package club.nsdn.nyasamarailway.entity.cart;

import club.nsdn.nyasamarailway.api.cart.*;
import club.nsdn.nyasamarailway.api.cart.nsc.IMonoRailCart;
import club.nsdn.nyasamarailway.api.rail.TileEntityRailEndpoint;
import club.nsdn.nyasamarailway.network.TrainPacket;
import club.nsdn.nyasamarailway.api.rail.IConvWireMono;
import club.nsdn.nyasamarailway.api.rail.IMonoRail;
import club.nsdn.nyasamarailway.api.rail.IWireRail;
import club.nsdn.nyasamarailway.util.TrainController;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
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
public class NSPCT8W extends AbsMotoCart implements IMonoRailCart {

    public static void doSpawn(World world, double x, double y, double z) {
        doSpawn(world, x, y, z, "");
    }

    public static void doSpawn(World world, double x, double y, double z, String name) {
        AbsCartBase head = new NSPCT8W(world, x, y, z);
        if (!name.isEmpty()) head.setCustomNameTag(name);
        world.spawnEntity(head);

        AbsContainer container = new Container(world, x, y - 3.0, z);
        world.spawnEntity(container);

        container.startRiding(head);
    }

    public static class Container extends AbsContainer implements IContainer {

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

        @Override
        public int getMaxPassengerSize() {
            return 2;
        }

        @Override // Called by rider
        public void updatePassenger(Entity entity) {
            CartUtil.updatePassenger2(this, entity);
        }

        public void updateThis(Entity entity) {
            if (entity instanceof NSPCT8W) {
                NSPCT8W base = (NSPCT8W) entity;
                double x = base.posX, y = base.posY, z = base.posZ;


                double len = -1.0, shift = -1.0 + base.getShiftY();
                Vec3d mod = new Vec3d(0, len, 0);
                if (base.getCurved())
                    mod = CartUtil.rotatePitchFix(mod, (float) ((base.rotationPitch % 360) / 180 * Math.PI));
                else if (base.onSlope())
                    mod = CartUtil.rotatePitchFix(mod, (float) (Math.PI / 4));
                mod = mod.rotateYaw((float) ((180 - base.rotationYaw) / 180 * Math.PI));
                x += mod.x; y += mod.y; z += mod.z;
                y = y + base.getMountedYOffset() + shift;

                this.setPosition(x, y, z);

                x = MathHelper.floor(base.posX);
                y = MathHelper.floor(base.posY);
                z = MathHelper.floor(base.posZ);

                BlockPos pos = new BlockPos(x, y, z);
                IBlockState state = base.world.getBlockState(pos);
                double dx = this.posX - this.prevPosX;
                double dz = this.posZ - this.prevPosZ;
                if (dx * dx + dz * dz < 0.001D) {
                    if (state.getBlock() instanceof BlockRailBase) {
                        BlockRailBase railBase = (BlockRailBase) state.getBlock();
                        BlockRailBase.EnumRailDirection direction = railBase.getRailDirection(world, pos, state, base);
                        switch (direction) {
                            case EAST_WEST: this.rotationYaw = 0.0F; break;
                            case NORTH_SOUTH: this.rotationYaw = 90.0F; break;
                        }
                    }
                } else {
                    this.rotationYaw = base.rotationYaw;
                }

                this.setRotation(this.rotationYaw, 0.0F);
            }
        }

        @Override
        public void update() {
            Entity entity = this.getRidingEntity();
            if (!(entity instanceof NSPCT8W))
                this.setDead();
        }

    }

    protected boolean isBogie = false;

    public void setBogie(boolean state) { isBogie = state; }

    public double defShiftY = 0.0;
    public double shiftY = 0.0;
    public static final double MONO = DEFAULT_SHIFT;
    public static final double WIRE = 1.0;

    private static final DataParameter<Float> DEF_SHIFT = EntityDataManager.createKey(NSPCT8W.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> NOW_SHIFT = EntityDataManager.createKey(NSPCT8W.class, DataSerializers.FLOAT);

    private static final DataParameter<Float> NOW_YAW = EntityDataManager.createKey(NSPCT8W.class, DataSerializers.FLOAT);
    private static final DataParameter<Boolean> ON_SLOPE = EntityDataManager.createKey(NSPCT8W.class, DataSerializers.BOOLEAN);

    @Override
    protected void entityInit() {
        super.entityInit();

        dataManager.register(DEF_SHIFT, 0.0F);
        dataManager.register(NOW_SHIFT, 0.0F);
        dataManager.register(NOW_YAW, 0.0F);
        dataManager.register(ON_SLOPE, false);
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

    public float getYaw() {
        return dataManager.get(NOW_YAW);
    }

    public void setYaw(float yaw) {
        this.rotationYaw = yaw;
        dataManager.set(NOW_YAW, yaw);
    }

    public boolean onSlope() {
        return dataManager.get(ON_SLOPE);
    }

    public void onSlope(boolean val) {
        dataManager.set(ON_SLOPE, val);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);

        setDefShiftY(tagCompound.getDouble("defShiftY"));
        setShiftY(tagCompound.getDouble("shiftY"));
        setBogie(tagCompound.getBoolean("isBogie"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);

        tagCompound.setDouble("defShiftY", getDefShiftY());
        tagCompound.setDouble("shiftY", getShiftY());
        tagCompound.setBoolean("isBogie", isBogie);
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
        return 4.0F;
    }

    @Override
    public float getOptimalDistance(EntityMinecart cart) {
        return 2.0F;
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
        if (!isBogie) {
            TrainController.doMotionWithAir(packet, cart);
        }
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
        if (entity instanceof Container) {
            ((Container) entity).updateThis(this);
        }
    }

    @Override
    protected TileEntityRailEndpoint getNearbyEndpoint() {
        int x = MathHelper.floor(this.posX);
        int y = MathHelper.floor(this.posY);
        int z = MathHelper.floor(this.posZ);

        BlockPos pos = new BlockPos(x, y, z);
        TileEntityRailEndpoint endpoint = null;

        pos = pos.down();

        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityRailEndpoint)
            endpoint = (TileEntityRailEndpoint) tileEntity;

        return endpoint;
    }

    @Override
    protected void moveAlongCurvedTrack() {
        Vec3d pos = nowEndPoint.get(nowProgress);
        Vec3d vec = nowEndPoint.get(nowProgress + 0.005);
        vec = vec.subtract(pos).normalize();

        double yaw = Math.atan2(vec.z, vec.x) * 180 / Math.PI;
        double hlen = Math.sqrt(vec.x * vec.x + vec.z * vec.z);
        double pitch = Math.atan(vec.y / hlen) * 180 / Math.PI;

        double vx = pos.x - this.posX;
        double vy = pos.y - this.posY;
        double vz = pos.z - this.posZ;
        this.move(MoverType.SELF, vx, vy, vz);

        setRotation((float) yaw, (float) pitch);
        setPositionAndUpdate(pos.x, pos.y + CURVED_SHIFT, pos.z);
    }

    @Override
    public boolean hasPassenger() {
        if (!getPassengers().isEmpty()) {
            Entity e = getPassengers().get(0);
            if (e instanceof Container)
                return ((Container) e).hasPassenger();
        }
        return false;
    }

    @Override
    public boolean hasWatchDog() {
        return !isBogie;
    }

    @Override
    public boolean feedWatchDog() {
        if (getMotorState()) {
            return hasPassenger();
        } else if (isMoving()) {
            if (getMotorBrake() > 1)
                return hasPassenger();
        }
        return true;
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
                onSlope(meta >= 2 && meta <= 5);
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

            setYaw(this.rotationYaw);
        } else {
            this.rotationYaw = getYaw();
        }

        super.onUpdate();
    }

}
