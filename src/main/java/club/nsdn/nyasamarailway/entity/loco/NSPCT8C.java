package club.nsdn.nyasamarailway.entity.loco;

import club.nsdn.nyasamaelectricity.tileblock.wire.BlockWire;
import club.nsdn.nyasamarailway.entity.ILimitVelCart;
import club.nsdn.nyasamarailway.entity.LocoBase;
import club.nsdn.nyasamarailway.entity.MinecartBase;
import club.nsdn.nyasamarailway.item.ItemLoader;
import club.nsdn.nyasamarailway.network.TrainPacket;
import club.nsdn.nyasamarailway.tileblock.rail.ConvWireMono;
import club.nsdn.nyasamarailway.tileblock.rail.mono.RailMonoMagnetBase;
import club.nsdn.nyasamarailway.util.TrainController;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by drzzm32 on 2018.2.24.
 */
public class NSPCT8C extends LocoBase implements ILimitVelCart {

    public static void doSpawn(World world, int x, int y, int z, String name) {
        LocoBase head = new NSPCT8C(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);
        if (!name.isEmpty()) head.setMinecartName(name);
        world.spawnEntityInWorld(head);

        MinecartBase container = new NSPCT8C.Container(world, (double) x + 0.5, (double) y + 0.5 - 3.0, (double) z + 0.5);
        world.spawnEntityInWorld(container);

        container.mountEntity(head);
    }

    public static class Container extends MinecartBase {

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
        public AxisAlignedBB getBoundingBox() {
            return boundingBox;
        }

        public boolean canMakePlayerTurn() {
            return false;
        }

        @Override
        public double getMountedYOffset() {
            return 0.1;
        }

        @Override
        protected void entityInit() {
            super.entityInit();
        }

        @Override
        protected void readEntityFromNBT(NBTTagCompound tagCompound) {
            super.readEntityFromNBT(tagCompound);
        }

        @Override
        protected void writeEntityToNBT(NBTTagCompound tagCompound) {
            super.writeEntityToNBT(tagCompound);
        }

        @Override
        public void killMinecart(DamageSource source) {
            this.setDead();
            if (this.ridingEntity != null && this.ridingEntity instanceof NSPCT8C)
                ((NSPCT8C) this.ridingEntity).killMinecart(source);
        }

    }

    private final int INDEX_MV = 28;
    public double maxVelocity = 0;
    private int tmpEngineBrake = -1;

    private final int INDEX_SHIFT = 29;
    public double shiftY = 0.0;
    private final int INDEX_CNT = 30;
    public double shiftYCnt = 0.0;
    public static final double MONO = -1.0;
    public static final double WIRE = 0.0;

    @Override
    public double getMaxVelocity() {
        return this.dataWatcher.getWatchableObjectFloat(INDEX_MV);
    }

    @Override
    public void setMaxVelocity(double value) {
        this.maxVelocity = (float) value;
        this.dataWatcher.updateObject(INDEX_MV, (float) value);
    }

    public void setShiftY(double shift) {
        this.shiftY = shift;
        this.dataWatcher.updateObject(INDEX_SHIFT, (float) shift);
    }

    public float getShiftYCnt() {
        return this.dataWatcher.getWatchableObjectFloat(INDEX_CNT);
    }

    public void setShiftYCnt(double shift) {
        this.shiftYCnt = shift;
        this.dataWatcher.updateObject(INDEX_CNT, (float) shift);
    }

    public float getShiftY() {
        return this.dataWatcher.getWatchableObjectFloat(INDEX_SHIFT);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(INDEX_MV, Float.valueOf("0"));
        this.dataWatcher.addObject(INDEX_SHIFT, Float.valueOf("0"));
        this.dataWatcher.addObject(INDEX_CNT, Float.valueOf("0"));
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);
        setMaxVelocity(tagCompound.getDouble("LocoMV"));
        setShiftY(tagCompound.getFloat("shiftY"));
        setShiftYCnt(tagCompound.getFloat("shiftYCnt"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setDouble("LocoMV", getMaxVelocity());
        tagCompound.setFloat("shiftY", getShiftY());
        tagCompound.setFloat("shiftYCnt", getShiftYCnt());
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
    protected boolean isHighSpeed() {
        return true;
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

    @Override
    protected void doEngine() {
        tmpPacket = new TrainPacket(getEnginePower(), getEngineBrake(), getEngineDir());
        tmpPacket.highSpeed = isHighSpeed();
        tmpPacket.Velocity = this.Velocity;
        if (this.maxVelocity > 0) {
            if (this.Velocity > this.maxVelocity && tmpEngineBrake == -1) {
                tmpEngineBrake = getEngineBrake();
                setEngineBrake(1);
            } else if (this.Velocity > this.maxVelocity && tmpEngineBrake != -1) {
                setEngineBrake(1);
            } else if (this.Velocity <= this.maxVelocity && tmpEngineBrake != -1) {
                setEngineBrake(tmpEngineBrake);
                tmpEngineBrake = -1;
            }
        }
        TrainController.doMotionWithAir(tmpPacket, this);
        setEnginePrevVel(this.Velocity);
        setEngineVel(tmpPacket.Velocity);
    }

    @Override
    public void killMinecart(DamageSource source) {
        this.setDead();
        if (this.riddenByEntity != null && this.riddenByEntity instanceof Container)
            this.riddenByEntity.setDead();
        ItemStack itemstack = new ItemStack(ItemLoader.itemNSPCT8C, 1);
        itemstack.setStackDisplayName(itemstack.getDisplayName());
        if (!source.damageType.equals("nsr")) this.entityDropItem(itemstack, 0.0F);
    }

    @Override
    public void updateRiderPosition() {
        if (this.riddenByEntity != null) {
            double x = this.posX, y = this.posY, z = this.posZ;

            int bx = MathHelper.floor_double(x);
            int by = MathHelper.floor_double(y);
            int bz = MathHelper.floor_double(z);
            Block block = worldObj.getBlock(bx, by, bz);
            int meta = worldObj.getBlockMetadata(bx, by, bz);

            double len = -1.0 + getShiftYCnt();
            Vec3 mod = Vec3.createVectorHelper(0, len, 0);
            mod.rotateAroundX(this.rotationPitch);
            mod.rotateAroundY(this.rotationYaw);
            x += mod.xCoord; y += mod.yCoord; z += mod.zCoord;

            this.riddenByEntity.setPositionAndRotation(
                    x, y + this.getMountedYOffset(), z,
                    this.rotationYaw, 0.0F
            );
            if (this.riddenByEntity instanceof Container && worldObj.isRemote) {
                Container container = (Container) this.riddenByEntity;
                container.setPositionAndRotation2(
                    x, y + this.getMountedYOffset(), z,
                    this.rotationYaw, 0.0F, -2
                );

                boolean fix = true;
                if (block instanceof BlockRailBase) {
                    fix = ((BlockRailBase) block).isPowered() || meta < 6 || meta > 9;
                }

                if ((((int) container.rotationYaw) % 90) != 0 && fix) {
                    container.prevRotationYaw = container.rotationYaw = (float) (MathHelper.floor_double(
                        (double) (this.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3
                    ) * 90.0F;
                }
            }
        }
    }

    @Override
    public void onUpdate() {
        if (!worldObj.isRemote) {
            int px = MathHelper.floor_double(this.prevPosX);
            int py = MathHelper.floor_double(this.prevPosY);
            int pz = MathHelper.floor_double(this.prevPosZ);
            Block pBlock = worldObj.getBlock(px, py, pz);

            int x = MathHelper.floor_double(this.posX);
            int y = MathHelper.floor_double(this.posY);
            int z = MathHelper.floor_double(this.posZ);
            Block block = worldObj.getBlock(x, y, z);
            ForgeDirection dir = ForgeDirection.UNKNOWN;
            if (worldObj.getTileEntity(x, y, z) instanceof ConvWireMono.Conv) {
                dir = ((ConvWireMono.Conv) worldObj.getTileEntity(x, y, z)).direction;
            }

            ForgeDirection toDir = ForgeDirection.UNKNOWN;
            int yaw = MathHelper.floor_double((double) (this.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
            switch (yaw) {
                case 0: toDir = ForgeDirection.SOUTH; break;
                case 1: toDir = ForgeDirection.WEST; break;
                case 2: toDir = ForgeDirection.NORTH; break;
                case 3: toDir = ForgeDirection.EAST; break;
            }

            if (block instanceof ConvWireMono && pBlock != block) {
                switch (dir) {
                    case SOUTH: // drop to North
                        if (toDir == ForgeDirection.NORTH) shiftY = MONO;
                        else if (toDir == ForgeDirection.SOUTH) shiftY = WIRE;
                        break;
                    case WEST: // drop to East
                        if (toDir == ForgeDirection.EAST) shiftY = MONO;
                        else if (toDir == ForgeDirection.WEST) shiftY = WIRE;
                        break;
                    case NORTH: // drop to South
                        if (toDir == ForgeDirection.NORTH) shiftY = WIRE;
                        else if (toDir == ForgeDirection.SOUTH) shiftY = MONO;
                        break;
                    case EAST: // drop to West
                        if (toDir == ForgeDirection.EAST) shiftY = WIRE;
                        else if (toDir == ForgeDirection.WEST) shiftY = MONO;
                        break;
                }
            } else if (block instanceof RailMonoMagnetBase) {
                shiftY = MONO;
            } else if (block instanceof BlockWire) {
                shiftY = WIRE;
            }

            double v = Math.sqrt(motionX * motionX + motionZ * motionZ);
            if (v > 1.0) shiftYCnt = shiftY;
            else if (!(block instanceof ConvWireMono) && v == 0) shiftYCnt = shiftY;
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
