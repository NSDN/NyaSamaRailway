package club.nsdn.nyasamarailway.entity.loco;

import club.nsdn.nyasamarailway.api.cart.AbsLimLoco;
import club.nsdn.nyasamarailway.api.cart.AbsCartBase;
import club.nsdn.nyasamarailway.api.cart.CartUtil;
import club.nsdn.nyasamarailway.api.cart.IContainer;
import club.nsdn.nyasamarailway.api.cart.nsc.IMonoRailCart;
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
public class NSPCT8C extends AbsLimLoco implements IMonoRailCart {

    public static void doSpawn(World world, double x, double y, double z) {
        doSpawn(world, x, y, z, "");
    }

    public static void doSpawn(World world, double x, double y, double z, String name) {
        AbsLimLoco head = new NSPCT8C(world, x, y, z);
        if (!name.isEmpty()) head.setCustomNameTag(name);
        world.spawnEntity(head);

        AbsCartBase container = new Container(world, x, y - 3.0, z);
        world.spawnEntity(container);

        container.startRiding(head);
    }

    public static class Container extends AbsCartBase implements IContainer {

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
        public boolean hasPassenger() {
            return !getPassengers().isEmpty();
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
            Entity entity = this.getRidingEntity();
            if (entity instanceof NSPCT8C) {
                this.dismountRidingEntity();
                ((NSPCT8C) entity).killMinecart(source);
            }
        }

        @Override
        public int getMaxPassengerSize() {
            return 2;
        }

        @Override // Called by rider
        public void updatePassenger(Entity entity) {
            CartUtil.updatePassenger2(this, entity);
        }

        @Override
        public boolean hasSpecialUpdate() {
            return getRidingEntity() instanceof NSPCT8C;
        }

        @Override
        public void specialUpdate() {
            Entity entity = getRidingEntity();
            if (entity instanceof NSPCT8C) {
                NSPCT8C bogie = (NSPCT8C) entity;

                this.prevPosX = this.posX;
                this.prevPosY = this.posY;
                this.prevPosZ = this.posZ;
                this.prevRotationYaw = this.rotationYaw;
                this.prevRotationPitch = this.rotationPitch;

                double x = this.posX, y = this.posY, z = this.posZ;

                double len = -2.0 + bogie.getShiftY();
                Vec3d mod = new Vec3d(0, len, 0);
                mod = mod.rotatePitch((float) (bogie.rotationPitch / 180 * Math.PI));
                mod = mod.rotateYaw((float) ((180 - bogie.rotationYaw) / 180 * Math.PI));
                x += mod.x; y += mod.y; z += mod.z;

                this.setRotation(bogie.rotationYaw, 0.0F);
                this.setPositionAndUpdate(x, y + bogie.getMountedYOffset(), z);
            }
        }

    }

    public boolean onSlope;

    public double defShiftY = 0.0;
    public double shiftY = 0.0;
    public static final double MONO = DEFAULT_SHIFT;
    public static final double WIRE = 1.0;

    private static final DataParameter<Float> DEF_SHIFT = EntityDataManager.createKey(NSPCT8C.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> NOW_SHIFT = EntityDataManager.createKey(NSPCT8C.class, DataSerializers.FLOAT);

    @Override
    protected void entityInit() {
        super.entityInit();

        dataManager.register(DEF_SHIFT, 0.0F);
        dataManager.register(NOW_SHIFT, 0.0F);
    }

    public double getDefShiftY() {
        return dataManager.get(DEF_SHIFT);
    }

    public void setDefShiftY(double shift) {
        this.defShiftY = shift;
        dataManager.set(DEF_SHIFT, (float) shift);
    }

    public double getShiftY() {
        return dataManager.get(NOW_SHIFT);
    }

    public void setShiftY(double shift) {
        this.shiftY = shift;
        dataManager.set(NOW_SHIFT, (float) shift);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);

        setDefShiftY(tagCompound.getDouble("defShiftY"));
        setShiftY(tagCompound.getDouble("shiftY"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);

        tagCompound.setDouble("defShiftY", getDefShiftY());
        tagCompound.setDouble("shiftY", getShiftY());
    }

    public NSPCT8C(World world) {
        super(world);
        ignoreFrustumCheck = true;
    }

    public NSPCT8C(World world, double x, double y, double z) {
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
        if (!getPassengers().isEmpty()) {
            for (Entity e : getPassengers()) {
                if (e instanceof Container)
                    e.setDead();
            }
        }
        super.killMinecart(source);
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
        double x = this.posX, y = this.posY, z = this.posZ;

        double len = -2.0 + getShiftY();
        Vec3d mod = new Vec3d(0, len, 0);
        if (onSlope) mod = mod.rotatePitch((float) (Math.PI / 4));
        mod = mod.rotateYaw((float) ((180 - rotationYaw) / 180 * Math.PI));
        x += mod.x; y += mod.y; z += mod.z;

        entity.setPosition(x, y + this.getMountedYOffset(), z);
    }

    @Override
    protected void moveAlongCurvedTrack() {
        Vec3d pos = nowEndPoint.get(nowProgress);
        Vec3d vec = nowEndPoint.get(nowProgress + 0.005);
        vec = vec.subtract(pos).normalize();

        double yaw = Math.atan2(vec.z, vec.x) * 180 / Math.PI;
        double hlen = Math.sqrt(vec.x * vec.x + vec.z * vec.z);
        double pitch = Math.atan(vec.y / hlen) * 180 / Math.PI;
        setRotation((float) yaw, (float) pitch);
        setPositionAndUpdate(pos.x, pos.y + CURVED_SHIFT, pos.z);
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
                        if (toDir == EnumFacing.NORTH) defShiftY = MONO;
                        else if (toDir == EnumFacing.SOUTH) defShiftY = WIRE;
                        break;
                    case WEST: // drop to East
                        if (toDir == EnumFacing.EAST) defShiftY = MONO;
                        else if (toDir == EnumFacing.WEST) defShiftY = WIRE;
                        break;
                    case NORTH: // drop to South
                        if (toDir == EnumFacing.NORTH) defShiftY = WIRE;
                        else if (toDir == EnumFacing.SOUTH) defShiftY = MONO;
                        break;
                    case EAST: // drop to West
                        if (toDir == EnumFacing.EAST) defShiftY = WIRE;
                        else if (toDir == EnumFacing.WEST) defShiftY = MONO;
                        break;
                }
            } else if (rail instanceof IMonoRail) {
                int meta = ((IMonoRail) rail).getMeta();
                meta &= 0x7;
                onSlope = (meta >= 2 && meta <= 5);
                defShiftY = getDefaultShiftY(meta);
            } else if (rail instanceof IWireRail) {
                defShiftY = WIRE;
            }

            double v = Math.sqrt(motionX * motionX + motionZ * motionZ);
            if (v > 1.0) shiftY = defShiftY;
            else if (!(rail instanceof IConvWireMono) && v == 0) shiftY = defShiftY;
            else {
                if (shiftY + v < defShiftY) shiftY += v;
                else if (shiftY - v > defShiftY) shiftY -= v;
                else shiftY = defShiftY;
            }

            setShiftY(shiftY);
            setDefShiftY(defShiftY);
        }

        super.onUpdate();
    }

}
