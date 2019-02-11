package club.nsdn.nyasamarailway.entity.cart;

import club.nsdn.nyasamarailway.api.cart.AbsMotoCart;
import club.nsdn.nyasamarailway.api.cart.AbsCartBase;
import club.nsdn.nyasamarailway.api.cart.CartUtil;
import club.nsdn.nyasamarailway.network.TrainPacket;
import club.nsdn.nyasamarailway.api.rail.IConvWireMono;
import club.nsdn.nyasamarailway.api.rail.IMonoRail;
import club.nsdn.nyasamarailway.api.rail.IWireRail;
import club.nsdn.nyasamarailway.util.TrainController;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class NSPCT8W extends AbsMotoCart {

    public static void doSpawn(World world, double x, double y, double z) {
        doSpawn(world, x, y, z, "");
    }

    public static void doSpawn(World world, double x, double y, double z, String name) {
        AbsCartBase head = new NSPCT8W(world, x, y, z);
        if (!name.isEmpty()) head.setCustomNameTag(name);
        world.spawnEntity(head);

        AbsCartBase container = new Container(world, x, y - 3.0, z);
        world.spawnEntity(container);

        container.startRiding(head);
    }

    public static class Container extends AbsCartBase {

        public Container(World world) {
            super(world);
            ignoreFrustumCheck = true;
            setSize(1.0F, 1.0F);
        }

        public Container(World world, double x, double y, double z) {
            super(world, x, y, z);
            ignoreFrustumCheck = true;
            setSize(1.0F, 1.0F);
        }

        @Override
        public double getMountedYOffset() {
            return 0.4;
        }

        @Nonnull
        @Override
        public ItemStack getCartItem() {
            return ItemStack.EMPTY;
        }

        @Override
        public void killMinecart(DamageSource source) {
            this.setDead();
            if (this.getRidingEntity() != null && this.getRidingEntity() instanceof NSPCT8W)
                ((NSPCT8W) this.getRidingEntity()).killMinecart(source);
        }

        @Override
        public int getMaxPassengerSize() {
            return 2;
        }

        @Override // Called by rider
        public void updatePassenger(Entity entity) {
            CartUtil.updatePassenger2(this, entity);
        }

    }

    public double shiftY = 0.0;
    public double shiftYCnt = 0.0;
    public static final double MONO = -1.0;
    public static final double WIRE = 0.0;

    private static final DataParameter<Float> SHIFT = EntityDataManager.createKey(NSPCT8W.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> CNT = EntityDataManager.createKey(NSPCT8W.class, DataSerializers.FLOAT);

    @Override
    protected void entityInit() {
        super.entityInit();

        dataManager.register(SHIFT, 0.0F);
        dataManager.register(CNT, 0.0F);
    }


    public float getShiftY() {
        return dataManager.get(SHIFT);
    }

    public void setShiftY(double shift) {
        this.shiftY = shift;
        dataManager.set(SHIFT, (float) shift);
    }

    public float getShiftYCnt() {
        return dataManager.get(CNT);
    }

    public void setShiftYCnt(double cnt) {
        this.shiftYCnt = cnt;
        dataManager.set(CNT, (float) cnt);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);

        setShiftY(tagCompound.getFloat("shiftY"));
        setShiftYCnt(tagCompound.getFloat("shiftYCnt"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);

        tagCompound.setFloat("shiftY", getShiftY());
        tagCompound.setFloat("shiftYCnt", getShiftYCnt());
    }

    public NSPCT8W(World world) {
        super(world);
        ignoreFrustumCheck = true;
    }

    public NSPCT8W(World world, double x, double y, double z) {
        super(world, x, y, z);
        ignoreFrustumCheck = true;
    }

    @Override
    public float getMaxCartSpeedOnRail() {
        return 2.0F;
    }

    @Override
    public double getMountedYOffset() {
        return -2.0;
    }

    @Override
    public float getLinkageDistance(EntityMinecart cart) {
        return 2.0F;
    }

    @Override
    public float getOptimalDistance(EntityMinecart cart) {
        return 1.6F;
    }

    @Nonnull
    @Override
    public ItemStack getCartItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public void killMinecart(DamageSource source) {
        if (this.getRidingEntity() != null && this.getRidingEntity() instanceof Container)
            this.getRidingEntity().setDead();
        this.setDead();
    }

    @Override
    public void doMotion(TrainPacket packet, EntityMinecart cart) {
        TrainController.doMotionWithAir(packet, cart);
    }

    @Override
    @Nonnull
    public Type getType() {
        return Type.FURNACE;
    }

    @Override
    public int getMaxPassengerSize() {
        return 1;
    }

    @Override
    protected boolean canFitPassenger(Entity entity) {
        return getPassengers().size() < getMaxPassengerSize() && entity instanceof Container;
    }

    @Override // Called by rider
    public void updatePassenger(Entity entity) {
        if (entity == getPassengers().get(0)) {
            double x = this.posX, y = this.posY, z = this.posZ;

            int bx = MathHelper.floor(x);
            int by = MathHelper.floor(y);
            int bz = MathHelper.floor(z);
            BlockPos pos = new BlockPos(bx, by, bz);
            Block block = world.getBlockState(pos).getBlock();
            int meta = block.getMetaFromState(world.getBlockState(pos));

            double len = -1.0 + getShiftYCnt();
            Vec3d mod = new Vec3d(0, len, 0);
            mod.rotatePitch(this.rotationPitch);
            mod.rotateYaw(this.rotationYaw);
            x += mod.x; y += mod.y; z += mod.z;

            entity.setPositionAndRotation(
                    x, y + this.getMountedYOffset(), z,
                    this.rotationYaw, 0.0F
            );
            if (entity instanceof Container && world.isRemote) {
                Container container = (Container) entity;
                container.setPositionAndRotationDirect(
                        x, y + this.getMountedYOffset(), z,
                        this.rotationYaw, 0.0F, -2,
                        false
                );

                boolean fix = true;
                if (block instanceof BlockRailBase) {
                    fix = !((BlockRailBase) block).isFlexibleRail(world, pos) || meta < 6 || meta > 9;
                }

                if ((((int) container.rotationYaw) % 90) != 0 && fix) {
                    container.prevRotationYaw = container.rotationYaw = (float) (MathHelper.floor(
                            (double) (this.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3
                    ) * 90.0F;
                }
            }
        }
    }

    @Override
    public void onUpdate() {
        if (!world.isRemote) {
            int px = MathHelper.floor(this.prevPosX);
            int py = MathHelper.floor(this.prevPosY);
            int pz = MathHelper.floor(this.prevPosZ);
            BlockPos ppos = new BlockPos(px, py, pz);
            Block pBlock = world.getBlockState(ppos).getBlock();

            int x = MathHelper.floor(this.posX);
            int y = MathHelper.floor(this.posY);
            int z = MathHelper.floor(this.posZ);
            BlockPos pos = new BlockPos(x, y, z);
            Block block = world.getBlockState(pos).getBlock();

            TileEntity rail = world.getTileEntity(pos);
            EnumFacing dir = EnumFacing.DOWN;
            if (rail instanceof IConvWireMono) {
                dir = ((IConvWireMono) rail).getDirection();
            }

            EnumFacing toDir = EnumFacing.DOWN;
            int yaw = MathHelper.floor((double) (this.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
            switch (yaw) {
                case 0: toDir = EnumFacing.SOUTH; break;
                case 1: toDir = EnumFacing.WEST; break;
                case 2: toDir = EnumFacing.NORTH; break;
                case 3: toDir = EnumFacing.EAST; break;
            }

            if (rail instanceof IConvWireMono && pBlock != block) {
                switch (dir) {
                    case SOUTH: // drop to North
                        if (toDir == EnumFacing.NORTH) shiftY = MONO;
                        else if (toDir == EnumFacing.SOUTH) shiftY = WIRE;
                        break;
                    case WEST: // drop to East
                        if (toDir == EnumFacing.EAST) shiftY = MONO;
                        else if (toDir == EnumFacing.WEST) shiftY = WIRE;
                        break;
                    case NORTH: // drop to South
                        if (toDir == EnumFacing.NORTH) shiftY = WIRE;
                        else if (toDir == EnumFacing.SOUTH) shiftY = MONO;
                        break;
                    case EAST: // drop to West
                        if (toDir == EnumFacing.EAST) shiftY = WIRE;
                        else if (toDir == EnumFacing.WEST) shiftY = MONO;
                        break;
                }
            } else if (rail instanceof IMonoRail) {
                shiftY = MONO;
            } else if (rail instanceof IWireRail) {
                shiftY = WIRE;
            }

            double v = Math.sqrt(motionX * motionX + motionZ * motionZ);
            if (v > 1.0) shiftYCnt = shiftY;
            else if (!(rail instanceof IConvWireMono) && v == 0) shiftYCnt = shiftY;
            else {
                if (shiftYCnt + v < shiftY) shiftYCnt += v;
                else if (shiftYCnt - v > shiftY) shiftYCnt -= v;
                else shiftYCnt = shiftY;
            }

            setShiftYCnt(shiftYCnt);
            setShiftY(shiftY);
        }

        super.onUpdate();
    }

}
