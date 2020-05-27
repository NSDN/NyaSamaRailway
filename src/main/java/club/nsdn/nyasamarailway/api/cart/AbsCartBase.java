package club.nsdn.nyasamarailway.api.cart;

import club.nsdn.nyasamarailway.api.item.IController;
import club.nsdn.nyasamarailway.api.item.IWand;
import club.nsdn.nyasamarailway.api.rail.IBaseRail;
import club.nsdn.nyasamarailway.api.rail.IVirtualRail;
import club.nsdn.nyasamarailway.api.rail.TileEntityRailEndpoint;
import club.nsdn.nyasamarailway.block.BlockPlatform;
import club.nsdn.nyasamarailway.item.ItemLoader;
import club.nsdn.nyasamarailway.api.signal.TileEntityTrackSideReception;
import club.nsdn.nyasamatelecom.api.tool.ToolBase;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.util.*;
import org.thewdj.linkage.api.ILinkableCart;
import net.minecraft.block.BlockRailPowered;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMinecart;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;

import net.minecraftforge.event.entity.minecart.MinecartUpdateEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by drzzm32 on 2019.2.10
 */
public abstract class AbsCartBase extends EntityMinecart implements ILinkableCart, IPartParent {

    /** Minecart rotational logic matrix */
    public static final int[][][] MATRIX = new int[][][]{
            {{0, 0, -1}, {0, 0, 1}}, {{-1, 0, 0}, {1, 0, 0}}, {{-1, -1, 0}, {1, 0, 0}},
            {{-1, 0, 0}, {1, -1, 0}}, {{0, 0, -1}, {0, -1, 1}}, {{0, -1, -1}, {0, 0, 1}},
            {{0, 0, 1}, {1, 0, 0}}, {{0, 0, 1}, {-1, 0, 0}}, {{0, 0, -1}, {-1, 0, 0}},
            {{0, 0, -1}, {1, 0, 0}}
    };
    /** appears to be the progress of the turn */
    public boolean isInReverse;

    public EnumFacing facing = EnumFacing.DOWN;

    private static final DataParameter<Boolean> CURVED = EntityDataManager.createKey(AbsCartBase.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Float> BLOCKING = EntityDataManager.createKey(AbsCartBase.class, DataSerializers.FLOAT);

    public TileEntityRailEndpoint nowEndPoint = null;
    public double nowProgress = 0;
    public double progressDir = 0;

    public int virtualCounter = 0;
    public boolean inVirtual = false;
    public Vec3d virtualVec = Vec3d.ZERO;

    public AbsCartBase(World world) {
        super(world);
    }

    public AbsCartBase(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    protected void entityInit() {
        super.entityInit();

        dataManager.register(CURVED, false);
        dataManager.register(BLOCKING, Float.NaN);
    }

    public void setCurved(boolean state) { dataManager.set(CURVED, state); }

    public boolean getCurved() { return dataManager.get(CURVED); }

    public void scanBlocking() {
        int max = IMobileBlocking.distByCart(this, IMobileBlocking.BLOCKING_DIST_LIM * 2);
        double dist = IMobileBlocking.getNearestCartDist(world, this, max);
        if (Double.isNaN(dist))
            dist = IMobileBlocking.INF_DIST;
        else if (dist > max) dist = max;
        dataManager.set(BLOCKING, (float) dist);
    }

    public float getBlocking() {
        return dataManager.get(BLOCKING);
    }

    public boolean canRunVirtually() {
        return getVirtualCounter() < 600;
    }

    public int getVirtualCounter() { return virtualCounter; }

    public void incVirtualCounter() { virtualCounter += 1; }

    public void resetVirtualCounter() { virtualCounter = 0; }

    @Nonnull
    public abstract ItemStack getCartItem();

    @Override
    public void killMinecart(DamageSource source) {
        this.setDead();
        ItemStack stack = getCartItem();
        if (stack.isEmpty()) {
            Item item = ItemLoader.itemCarts.get(getClass());
            if (item == null) item = Items.AIR;
            stack = new ItemStack(item, 1);
        }
        stack.setStackDisplayName(stack.getDisplayName());
        if (!source.damageType.equals("nsr")) this.entityDropItem(stack, 0.0F);
    }

    @Override
    public boolean processInitialInteract(@Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
        if (super.processInitialInteract(player, hand)) {
            return true;
        } else if (player.isSneaking()) {
            return false;
        } else if (!canFitPassenger(player)) {
            return true;
        } else {
            ItemStack stack = player.getHeldItemMainhand();
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof IController)
                    return true;
                if (stack.getItem() instanceof ItemMinecart)
                    return true;
            }
            if (!this.world.isRemote) {
                player.startRiding(this);
            }

            return true;
        }
    }

    public int getMaxPassengerSize() {
        return 1;
    }

    @Override
    protected boolean canFitPassenger(Entity entity) {
        return getPassengers().size() < getMaxPassengerSize();
    }

    @Override // Called by rider
    public void updatePassenger(Entity entity) {
        double x = this.posX, z = this.posZ;
        double y = this.posY + this.getMountedYOffset() + entity.getYOffset();
        if (this.isPassenger(entity)) {
            if (getPassengers().size() >= 1) {
                entity.setPosition(x, y, z);
            }
        }
    }

    @Override
    @Nonnull
    public Type getType() {
        return Type.RIDEABLE;
    }

    @Override
    public double getMountedYOffset() {
        return 0;
    }

    @Override
    public float getMaxCartSpeedOnRail() {
        return 1.0F;
    }

    public double getPlayerPushVel() {
        return 0.2;
    }

    @Override
    public float getLinkageDistance(EntityMinecart cart) {
        return 1.5F;
    }

    @Override
    public float getOptimalDistance(EntityMinecart cart) {
        return 0.75F;
    }

    @Override
    public boolean isLinkable() {
        return true;
    }

    @Override
    public boolean canLink(EntityMinecart cart) {
        return true;
    }

    @Override
    public boolean hasTwoLinks() {
        return true;
    }

    @Override
    public boolean canBeAdjusted(EntityMinecart cart) {
        return true;
    }

    @Override
    public void onLinkCreated(EntityMinecart cart) {
    }

    @Override
    public void onLinkBroken(EntityMinecart cart) {
    }

    @Override
    protected void removePassenger(Entity entity) {
        BlockPos pos = this.getPosition();

        if (world.getBlockState(pos.north()).getBlock() instanceof BlockPlatform)
            pos = pos.north(2).up();
        else if (world.getBlockState(pos.south()).getBlock() instanceof BlockPlatform)
            pos = pos.south(2).up();
        else if (world.getBlockState(pos.west()).getBlock() instanceof BlockPlatform)
            pos = pos.west(2).up();
        else if (world.getBlockState(pos.east()).getBlock() instanceof BlockPlatform)
            pos = pos.east(2).up();
        else {
            super.removePassenger(entity);
            return;
        }

        super.removePassenger(entity);
        entity.setPositionAndUpdate(
                pos.getX() + 0.5,
                pos.getY() + 0.1,
                pos.getZ() + 0.5
        );
    }

    public double getSpeed() {
        return Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
    }

    @Nullable
    @Override
    public Entity[] getParts() {
        return hasMultiPart() ? getMultiPart().toArray(new Entity[0]) : null;
    }

    public boolean hasMultiPart() { return false; }

    public List<CartPart> getMultiPart() { return Collections.emptyList(); }

    @Override
    public Vec3d getOffset(Vec3d vec) {
        return vec.rotateYaw((float) ((180 - this.rotationYaw) / 180 * Math.PI)).add(this.getPositionVector());
    }

    public boolean hasSpecialUpdate() { return false; }

    public void specialUpdate() {

    }

    public boolean isMoving() {
        return motionX * motionX + motionZ * motionZ > 0.001;
    }

    public boolean hasPassenger() {
        return !getPassengers().isEmpty();
    }

    public boolean hasWatchDog() { return false; }

    public boolean feedWatchDog() {
        return true;
    }

    protected TileEntityRailEndpoint getNearbyEndpoint() {
        int x = MathHelper.floor(this.posX);
        int y = MathHelper.floor(this.posY);
        int z = MathHelper.floor(this.posZ);

        BlockPos pos = new BlockPos(x, y, z);
        TileEntityRailEndpoint endpoint = null;

        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityRailEndpoint)
            endpoint = (TileEntityRailEndpoint) tileEntity;

        return endpoint;
    }

    protected void moveAlongCurvedTrack() {
        Vec3d pos = nowEndPoint.get(nowProgress);
        Vec3d vec = nowEndPoint.get(nowProgress + 0.005);
        vec = vec.subtract(pos).normalize();

        double yaw = Math.atan2(vec.z, vec.x) * 180 / Math.PI;
        double hlen = Math.sqrt(vec.x * vec.x + vec.z * vec.z);
        double pitch = Math.atan(vec.y / hlen) * 180 / Math.PI;

        pos = pos.subtract(0, 0.5 - 0.0625, 0);

        double vx = pos.x - this.posX;
        double vy = pos.y - this.posY;
        double vz = pos.z - this.posZ;
        this.move(MoverType.SELF, vx, vy, vz);

        setPosition(pos.x, pos.y, pos.z);
        setRotation((float) yaw, (float) pitch);
    }

    private void moveAlongCurvedTrackCore(IBlockState state) {
        if (!(state.getBlock() instanceof BlockRailBase)) {
            TileEntityRailEndpoint endpoint = getNearbyEndpoint();

            if (endpoint != null) {
                if (endpoint.len() > 0 && nowEndPoint != endpoint) {
                    setCurved(true);
                    nowEndPoint = endpoint;
                    nowProgress = 0;
                    progressDir = 1;
                }

                TileEntity target = endpoint.getTarget();
                if (target instanceof TileEntityRailEndpoint) {
                    endpoint = (TileEntityRailEndpoint) target;
                    if (endpoint.len() > 0 && nowEndPoint != endpoint) {
                        setCurved(true);
                        nowEndPoint = endpoint;
                        nowProgress = endpoint.len();
                        progressDir = -1;
                    }
                }
            }

            if (getCurved() && nowEndPoint != null) {
                if (nowEndPoint.len() == 0) {
                    nowEndPoint = null;
                } else {
                    if (nowProgress > nowEndPoint.len()) {
                        TileEntityRailEndpoint next = nowEndPoint.parseNext();
                        if (next != null) {
                            if (next.len() > 0 && nowEndPoint != next) {
                                setCurved(true);
                                nowEndPoint = next;
                                nowProgress = 0;
                                progressDir = 1;
                            }
                        } else {
                            Vec3d now = nowEndPoint.get(nowProgress);
                            Vec3d vec = nowEndPoint.get(nowProgress + 0.005);
                            vec = vec.subtract(now).normalize();
                            motionX = Math.abs(motionX) * Math.signum(vec.x);
                            motionZ = Math.abs(motionZ) * Math.signum(vec.z);

                            setCurved(false);
                            nowEndPoint = null;
                            nowProgress = 0;
                            progressDir = 0;
                            return;
                        }
                    }

                    moveAlongCurvedTrack();

                    double dir = progressDir;
                    double velSq = motionX * motionX + motionZ * motionZ;
                    double vel = Math.sqrt(velSq), dProgress = vel;

                    Vec3d now = nowEndPoint.get(nowProgress);
                    Vec3d vec = nowEndPoint.get(nowProgress + 0.005);
                    vec = vec.subtract(now).normalize();
                    motionX = vel * vec.x;
                    motionZ = vel * vec.z;

                    for (double i = 0; i <= vel; i += vel / 16) {
                        vec = nowEndPoint.get(nowProgress + i);
                        vec = vec.subtract(now);
                        double lenSq = vec.lengthSquared();
                        if (lenSq >= velSq) {
                            dProgress = i;
                            break;
                        }
                    }
                    nowProgress += dProgress * dir;

                    applyDrag();
                }
            }
        } else {
            setCurved(false);
            nowEndPoint = null;
            nowProgress = 0;
            progressDir = 0;
        }
    }

    void moveAlongTrackCore(BlockPos pos, IBlockState state) {
        this.fallDistance = 0.0F;
        Vec3d vec3d = this.getPos(this.posX, this.posY, this.posZ);
        this.posY = (double)pos.getY();
        boolean isRailPowered = false;
        boolean slowDown = false;
        BlockRailBase blockrailbase = (BlockRailBase)state.getBlock();
        if (blockrailbase == Blocks.GOLDEN_RAIL) {
            isRailPowered = (Boolean)state.getValue(BlockRailPowered.POWERED);
            slowDown = !isRailPowered;
        }

        double slopeAdjustment = this.getSlopeAdjustment();
        BlockRailBase.EnumRailDirection direction = blockrailbase.getRailDirection(this.world, pos, state, this);
        switch(direction) {
            case ASCENDING_EAST:
                this.motionX -= slopeAdjustment;
                ++this.posY;
                break;
            case ASCENDING_WEST:
                this.motionX += slopeAdjustment;
                ++this.posY;
                break;
            case ASCENDING_NORTH:
                this.motionZ += slopeAdjustment;
                ++this.posY;
                break;
            case ASCENDING_SOUTH:
                this.motionZ -= slopeAdjustment;
                ++this.posY;
        }

        int[][] matrix = MATRIX[direction.getMetadata()];
        double u = (double)(matrix[1][0] - matrix[0][0]);
        double v = (double)(matrix[1][2] - matrix[0][2]);
        double L = Math.sqrt(u * u + v * v);
        double w = this.motionX * u + this.motionZ * v;
        if (w < 0.0D) {
            u = -u;
            v = -v;
        }

        /**
         * Modify for higher speed
         */
        double vel = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
        if (vel > getMaxCartSpeedOnRail()) {
            vel = getMaxCartSpeedOnRail();
        }

        this.motionX = vel * u / L;
        this.motionZ = vel * v / L;
        /**
         * pushed by player
         * */
        Entity entity = this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
        double a, b, c, d;
        if (entity instanceof EntityLivingBase) {
            a = ((EntityLivingBase)entity).moveForward;
            if (a > 0.0D && ((EntityLivingBase) entity).getHeldItemMainhand().getItem() instanceof IWand) {
                b = -Math.sin((double)(entity.rotationYaw * 0.017453292F));
                c = Math.cos((double)(entity.rotationYaw * 0.017453292F));
                d = this.motionX * this.motionX + this.motionZ * this.motionZ;
                if (d < 0.01D) {
                    this.motionX += b * getPlayerPushVel();
                    this.motionZ += c * getPlayerPushVel();
                    slowDown = false;
                }
            }
        }

        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            ItemStack stack = player.getHeldItemMainhand();
            if (!((stack.getItem() instanceof ToolBase)))
                stack = player.getHeldItemOffhand();
            if (stack.getItem() instanceof ToolBase)
                this.scanBlocking();
        }

        if (slowDown && this.shouldDoRailFunctions()) {
            a = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            if (a < 0.03D) {
                this.motionX *= 0.0D;
                this.motionY *= 0.0D;
                this.motionZ *= 0.0D;
            } else {
                this.motionX *= 0.5D;
                this.motionY *= 0.0D;
                this.motionZ *= 0.5D;
            }
        }

        a = (double)pos.getX() + 0.5D + (double)matrix[0][0] * 0.5D;
        b = (double)pos.getZ() + 0.5D + (double)matrix[0][2] * 0.5D;
        c = (double)pos.getX() + 0.5D + (double)matrix[1][0] * 0.5D;
        d = (double)pos.getZ() + 0.5D + (double)matrix[1][2] * 0.5D;
        u = c - a;
        v = d - b;
        double d10;
        if (u == 0.0D) {
            this.posX = (double)pos.getX() + 0.5D;
            d10 = this.posZ - (double)pos.getZ();
        } else if (v == 0.0D) {
            this.posZ = (double)pos.getZ() + 0.5D;
            d10 = this.posX - (double)pos.getX();
        } else {
            double d11 = this.posX - a;
            double d12 = this.posZ - b;
            d10 = (d11 * u + d12 * v) * 2.0D;
        }

        this.posX = a + u * d10;
        this.posZ = b + v * d10;
        this.setPosition(this.posX, this.posY, this.posZ);
        this.moveMinecartOnRail(pos);
        if (matrix[0][1] != 0 && MathHelper.floor(this.posX) - pos.getX() == matrix[0][0] && MathHelper.floor(this.posZ) - pos.getZ() == matrix[0][2]) {
            this.setPosition(this.posX, this.posY + (double)matrix[0][1], this.posZ);
        } else if (matrix[1][1] != 0 && MathHelper.floor(this.posX) - pos.getX() == matrix[1][0] && MathHelper.floor(this.posZ) - pos.getZ() == matrix[1][2]) {
            this.setPosition(this.posX, this.posY + (double)matrix[1][1], this.posZ);
        }

        this.applyDrag();
        Vec3d vec3d1 = this.getPos(this.posX, this.posY, this.posZ);
        if (vec3d1 != null && vec3d != null) {
            double d14 = (vec3d.y - vec3d1.y) * 0.05D;
            vel = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            if (vel > 0.0D) {
                this.motionX = this.motionX / vel * (vel + d14);
                this.motionZ = this.motionZ / vel * (vel + d14);
            }

            this.setPosition(this.posX, vec3d1.y, this.posZ);
        }

        /** HOLY SHIT! THE CODE CAUSES BUG!
         int j = MathHelper.floor(this.posX);
         int i = MathHelper.floor(this.posZ);
         if (j != pos.getX() || i != pos.getZ()) {
         vel = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         this.motionX = vel * (double)(j - pos.getX());
         this.motionZ = vel * (double)(i - pos.getZ());
         }
         */

        if (this.shouldDoRailFunctions()) {
            ((BlockRailBase)state.getBlock()).onMinecartPass(this.world, this, pos);
        }

        if (isRailPowered && this.shouldDoRailFunctions()) {
            double d15 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            if (d15 > 0.01D) {
                double d16 = 0.06D;
                this.motionX += this.motionX / d15 * 0.06D;
                this.motionZ += this.motionZ / d15 * 0.06D;
            } else if (direction == BlockRailBase.EnumRailDirection.EAST_WEST) {
                if (this.world.getBlockState(pos.west()).isNormalCube()) {
                    this.motionX = 0.02D;
                } else if (this.world.getBlockState(pos.east()).isNormalCube()) {
                    this.motionX = -0.02D;
                }
            } else if (direction == BlockRailBase.EnumRailDirection.NORTH_SOUTH) {
                if (this.world.getBlockState(pos.north()).isNormalCube()) {
                    this.motionZ = 0.02D;
                } else if (this.world.getBlockState(pos.south()).isNormalCube()) {
                    this.motionZ = -0.02D;
                }
            }
        }
    }

    @Override
    protected void moveAlongTrack(BlockPos pos, IBlockState state) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityTrackSideReception) {
            TileEntityTrackSideReception reception = (TileEntityTrackSideReception) tileEntity;
            if (getPassengers().isEmpty() && !reception.cartType.isEmpty()) {
                if (!reception.cartType.equals("loco"))
                    return;
            }
        }

        moveAlongTrackCore(pos, state);
    }

    @Override
    public void moveMinecartOnRail(BlockPos pos) {
        double mX = this.motionX;
        double mZ = this.motionZ;

        double max = this.getMaxSpeed();
        mX = MathHelper.clamp(mX, -max, max);
        mZ = MathHelper.clamp(mZ, -max, max);

        this.move(MoverType.SELF, mX, 0.0D, mZ);
    }

    @Override
    protected void applyDrag() {
        //Do engine code

    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float damage) {
        if (!this.world.isRemote && !this.isDead) {
            if (this.isEntityInvulnerable(source)) {
                return false;
            } else {
                this.setRollingDirection(-this.getRollingDirection());
                this.setRollingAmplitude(10);
                this.markVelocityChanged();
                this.setDamage(this.getDamage() + damage * 10.0F);
                boolean flag = false;
                if (source.getTrueSource() instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) source.getTrueSource();
                    ItemStack stack = player.getHeldItemMainhand();
                    if (stack.isEmpty()) return false;
                    if (stack.getItem() instanceof IWand) flag = true;
                }
                if (flag || this.getDamage() > 40.0F) {
                    onKilled();
                    this.removePassengers();
                    if (flag && !this.hasCustomName()) {
                        this.setDead();
                    } else {
                        this.killMinecart(source);
                    }
                }

                return true;
            }
        } else {
            return true;
        }
    }

    public void onKilled() { }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);

        if (tagCompound.hasKey("railEndpoint")) {
            nowEndPoint = new TileEntityRailEndpoint();
            nowEndPoint.setWorld(world);
            nowEndPoint.fromNBT(tagCompound);
            setCurved(true);
        } else {
            setCurved(false);
        }
        nowProgress = tagCompound.getDouble("nowProgress");
        progressDir = tagCompound.getDouble("progressDir");
        virtualCounter = tagCompound.getInteger("virtualCounter");
        inVirtual = tagCompound.getBoolean("inVirtual");
        double x = tagCompound.getDouble("virtualX");
        double y = tagCompound.getDouble("virtualY");
        double z = tagCompound.getDouble("virtualZ");
        virtualVec = new Vec3d(x, y, z);
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);

        if (nowEndPoint != null) {
            nowEndPoint.toNBT(tagCompound);
        }
        tagCompound.setDouble("nowProgress", nowProgress);
        tagCompound.setDouble("progressDir", progressDir);
        tagCompound.setInteger("virtualCounter", virtualCounter);
        tagCompound.setBoolean("inVirtual", inVirtual);
        if (virtualVec == null)
            virtualVec = Vec3d.ZERO;
        tagCompound.setDouble("virtualX", virtualVec.x);
        tagCompound.setDouble("virtualY", virtualVec.y);
        tagCompound.setDouble("virtualZ", virtualVec.z);
    }

    public void update() { }

    /*******************************************************************************************************************/

    @Override
    public void onUpdate() {
        if (this.getRollingAmplitude() > 0) {
            this.setRollingAmplitude(this.getRollingAmplitude() - 1);
        }

        if (this.getDamage() > 0.0F) {
            this.setDamage(this.getDamage() - 1.0F);
        }

        if (this.posY < -64.0D) {
            this.outOfWorld();
        }

        int x, y, z;
        if (!this.world.isRemote && this.world instanceof WorldServer) {
            this.world.profiler.startSection("portal");
            MinecraftServer minecraftserver = this.world.getMinecraftServer();
            y = this.getMaxInPortalTime();
            if (this.inPortal) {
                if (minecraftserver.getAllowNether()) {
                    if (!this.isRiding() && this.portalCounter++ >= y) {
                        this.portalCounter = y;
                        this.timeUntilPortal = this.getPortalCooldown();
                        byte j;
                        if (this.world.provider.getDimensionType().getId() == -1) {
                            j = 0;
                        } else {
                            j = -1;
                        }

                        this.changeDimension(j);
                    }

                    this.inPortal = false;
                }
            } else {
                if (this.portalCounter > 0) {
                    this.portalCounter -= 4;
                }

                if (this.portalCounter < 0) {
                    this.portalCounter = 0;
                }
            }

            if (this.timeUntilPortal > 0) {
                --this.timeUntilPortal;
            }

            this.world.profiler.endSection();
        }

        if (this.world.isRemote) {
            if (hasSpecialUpdate())
                specialUpdate();

            super.onUpdate();
        } else {
            if ((this.posX - this.prevPosX) > 0)
                this.facing = EnumFacing.EAST;
            else if ((this.posX - this.prevPosX) < 0)
                this.facing = EnumFacing.WEST;
            else if ((this.posZ - this.prevPosZ) > 0)
                this.facing = EnumFacing.SOUTH;
            else if ((this.posZ - this.prevPosZ) < 0)
                this.facing = EnumFacing.NORTH;

            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;

            if (hasWatchDog()) {
                if (!feedWatchDog()) {
                    this.setDead();
                    return;
                }
            }

            if (hasSpecialUpdate()) {
                specialUpdate();
            } else {
                x = Math.round((float) this.posX);
                y = Math.round((float) this.posY);
                z = Math.round((float) this.posZ);
                BlockPos pos = new BlockPos(x, y, z);
                IBlockState state = this.world.getBlockState(pos);

                moveAlongCurvedTrackCore(state);

                if (nowEndPoint == null) {
                    if (!this.hasNoGravity()) {
                        this.motionY -= 0.03999999910593033D;
                    }

                    x = MathHelper.floor(this.posX);
                    y = MathHelper.floor(this.posY);
                    z = MathHelper.floor(this.posZ);

                    if (!BlockRailBase.isRailBlock(this.world, new BlockPos(x, y, z)) && BlockRailBase.isRailBlock(this.world, new BlockPos(x, y - 1, z))) {
                        --y;

                        pos = new BlockPos(x, y, z);
                        TileEntity tile = this.world.getTileEntity(pos);
                        if (tile instanceof IBaseRail)
                            ++y;
                    }

                    pos = new BlockPos(x, y, z);
                    state = this.world.getBlockState(pos);
                    if (this.canUseRail() && BlockRailBase.isRailBlock(state)) {
                        this.moveAlongTrack(pos, state);
                        if (state.getBlock() == Blocks.ACTIVATOR_RAIL) {
                            this.onActivatorRailPass(x, y, z, state.getValue(BlockRailPowered.POWERED));
                        }
                        resetVirtualCounter();
                        this.inVirtual = false;
                    } else if ((state.getBlock() instanceof IVirtualRail && canRunVirtually()) || this.inVirtual) {
                        if (state.getBlock() instanceof IVirtualRail) {
                            resetVirtualCounter();

                            if (Math.abs(this.motionY) > getMaxCartSpeedOnRail())
                                this.motionY = 0;
                            double speed = Math.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
                            IVirtualRail rail = (IVirtualRail) state.getBlock();
                            Vec3d vec = rail.getTargetDirection(this.world, pos);
                            vec = vec.scale(speed);
                            this.virtualVec = new Vec3d(vec.x, vec.y, vec.z);
                        }
                        if (this.virtualVec != null) {
                            this.motionX = this.virtualVec.x;
                            this.motionY = this.virtualVec.y;
                            this.motionZ = this.virtualVec.z;
                        }
                        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);

                        incVirtualCounter();
                        this.inVirtual = canRunVirtually();
                    } else {
                        this.moveDerailedMinecart();
                        resetVirtualCounter();
                        this.inVirtual = false;
                    }

                    this.doBlockCollisions();
                    this.rotationPitch = 0.0F;
                    double dx = this.posX - this.prevPosX;
                    double dz = this.posZ - this.prevPosZ;
                    if (dx * dx + dz * dz > 0.001D) {
                        this.rotationYaw = (float)(MathHelper.atan2(dz, dx) * 180.0D / 3.141592653589793D);
                        if (this.isInReverse) {
                            this.rotationYaw += 180.0F;
                        }
                    }
                    if (this.inVirtual) {
                        this.rotationYaw = (float) (Math.atan2(this.virtualVec.z, this.virtualVec.x) * 180 / Math.PI);
                        this.rotationPitch = (float) (Math.atan(this.virtualVec.y /
                                Math.sqrt(this.virtualVec.x * this.virtualVec.x + this.virtualVec.z * this.virtualVec.z)) * 180 / Math.PI);
                    }

                    double dYaw = (double)MathHelper.wrapDegrees(this.rotationYaw - this.prevRotationYaw);
                    if (dYaw < -170.0D || dYaw >= 170.0D) {
                        this.rotationYaw += 180.0F;
                        this.isInReverse = !this.isInReverse;
                    }

                    this.setRotation(this.rotationYaw, this.rotationPitch);
                }
            }

            AxisAlignedBB box;
            if (getCollisionHandler() != null) {
                box = getCollisionHandler().getMinecartCollisionBox(this);
            } else {
                box = this.getEntityBoundingBox().grow(0.20000000298023224D, 0.0D, 0.20000000298023224D);
            }

            /** modified for Automatic Train Control */
            if (this.canBeRidden() && this.motionX * this.motionX + this.motionZ * this.motionZ > 0.01D) {
                List<Entity> list = this.world.getEntitiesInAABBexcluding(this, box, EntitySelectors.getTeamCollisionPredicate(this));
                if (!list.isEmpty()) {
                    for (int i = 0; i < list.size(); ++i) {
                        Entity target = list.get(i);
                        if (target instanceof EntityLivingBase) {
                            EntityLivingBase living = (EntityLivingBase) target;
                            Vec3d src = this.getPositionVector();
                            Vec3d dst = target.getPositionVector();
                            Vec3d vec = dst.subtract(src);

                            vec = vec.normalize().scale(this.getSpeed() * (1.5 + rand.nextDouble()));
                            vec = vec.addVector(0, 1 + rand.nextDouble(), 0);

                            if (living instanceof EntityPlayer) {
                                EntityPlayer player = (EntityPlayer) living;
                                ItemStack stack = player.getHeldItemMainhand();
                                if (!(stack.getItem() instanceof IWand) && !this.isPassenger(player)) {
                                    PotionType type;
                                    type = PotionType.getPotionTypeForName("weakness");
                                    if (type != null)
                                        for (PotionEffect effect : type.getEffects())
                                            effect.getPotion().affectEntity(null, null, player, effect.getAmplifier(), 1.0F);
                                    type = PotionType.getPotionTypeForName("harming");
                                    if (type != null)
                                        for (PotionEffect effect : type.getEffects())
                                            effect.getPotion().affectEntity(null, null, player, effect.getAmplifier(), 1.0F);
                                    player.addVelocity(vec.x, vec.y, vec.z);
                                } else {
                                    player.applyEntityCollision(this);
                                }
                            } else {
                                living.setHealth(living.getHealth() / 2.0F);
                                PotionType type;
                                type = PotionType.getPotionTypeForName("long_weakness");
                                if (type != null)
                                    for (PotionEffect effect : type.getEffects())
                                        effect.getPotion().affectEntity(null, null, living, effect.getAmplifier(), 1.0F);
                                type = PotionType.getPotionTypeForName("strong_harming");
                                if (type != null)
                                    for (PotionEffect effect : type.getEffects())
                                        effect.getPotion().affectEntity(null, null, living, effect.getAmplifier(), 1.0F);
                                living.addVelocity(vec.x, vec.y, vec.z);
                            }
                        } else {
                            target.applyEntityCollision(this);
                        }
                    }
                }
            } else {
                Iterator iterator = this.world.getEntitiesWithinAABBExcludingEntity(this, box).iterator();

                while(iterator.hasNext()) {
                    Entity entity = (Entity)iterator.next();
                    if (!this.isPassenger(entity) && entity.canBePushed() && entity instanceof EntityMinecart) {
                        entity.applyEntityCollision(this);
                    }
                }
            }

            this.handleWaterMovement();
            MinecraftForge.EVENT_BUS.post(new MinecartUpdateEvent(this, this.getCurrentRailPosition()));
        }

        update();
    }

    @Override
    public void applyEntityCollision(@Nonnull Entity entity) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            ItemStack stack = player.getHeldItemMainhand();
            if (stack.getItem() instanceof IWand)
                super.applyEntityCollision(entity);
        }
    }

    protected BlockPos getCurrentRailPosition() {
        int x = MathHelper.floor(this.posX);
        int y = MathHelper.floor(this.posY);
        int z = MathHelper.floor(this.posZ);
        if (BlockRailBase.isRailBlock(this.world, new BlockPos(x, y - 1, z))) {
            --y;
        }

        return new BlockPos(x, y, z);
    }

    @Override
    protected double getMaxSpeed() {
        if (!this.canUseRail()) {
            return this.getMaximumSpeed();
        } else {
            BlockPos pos = this.getCurrentRailPosition();
            IBlockState state = this.world.getBlockState(pos);
            if (!BlockRailBase.isRailBlock(state)) {
                return this.getMaximumSpeed();
            } else {
                float railMaxSpeed = ((BlockRailBase)state.getBlock()).getRailMaxSpeed(this.world, this, pos);
                return (double)Math.min(railMaxSpeed, this.getCurrentCartSpeedCapOnRail());
            }
        }
    }

    @Override
    @Nullable
    @SideOnly(Side.CLIENT)
    public Vec3d getPosOffset(double doubleX, double doubleY, double doubleZ, double v) {
        int x = MathHelper.floor(doubleX);
        int y = MathHelper.floor(doubleY);
        int z = MathHelper.floor(doubleZ);
        if (!BlockRailBase.isRailBlock(this.world, new BlockPos(x, y, z)) && BlockRailBase.isRailBlock(this.world, new BlockPos(x, y - 1, z))) {
            --y;
        }

        IBlockState iblockstate = this.world.getBlockState(new BlockPos(x, y, z));
        if (BlockRailBase.isRailBlock(iblockstate)) {
            BlockRailBase.EnumRailDirection direction = ((BlockRailBase)iblockstate.getBlock()).getRailDirection(this.world, new BlockPos(x, y, z), iblockstate, this);
            doubleY = (double)y;
            if (direction.isAscending()) {
                doubleY = (double)(y + 1);
            }

            int[][] aint = MATRIX[direction.getMetadata()];
            double d0 = (double)(aint[1][0] - aint[0][0]);
            double d1 = (double)(aint[1][2] - aint[0][2]);
            double d2 = Math.sqrt(d0 * d0 + d1 * d1);
            d0 /= d2;
            d1 /= d2;
            doubleX += d0 * v;
            doubleZ += d1 * v;
            if (aint[0][1] != 0 && MathHelper.floor(doubleX) - x == aint[0][0] && MathHelper.floor(doubleZ) - z == aint[0][2]) {
                doubleY += (double)aint[0][1];
            } else if (aint[1][1] != 0 && MathHelper.floor(doubleX) - x == aint[1][0] && MathHelper.floor(doubleZ) - z == aint[1][2]) {
                doubleY += (double)aint[1][1];
            }

            return this.getPos(doubleX, doubleY, doubleZ);
        } else {
            return null;
        }
    }

    @Override
    @Nullable
    public Vec3d getPos(double doubleX, double doubleY, double doubleZ) {
        int x = MathHelper.floor(doubleX);
        int y = MathHelper.floor(doubleY);
        int z = MathHelper.floor(doubleZ);
        if (!BlockRailBase.isRailBlock(this.world, new BlockPos(x, y, z)) && BlockRailBase.isRailBlock(this.world, new BlockPos(x, y - 1, z))) {
            --y;
        }

        IBlockState iblockstate = this.world.getBlockState(new BlockPos(x, y, z));
        if (BlockRailBase.isRailBlock(iblockstate)) {
            BlockRailBase.EnumRailDirection direction = ((BlockRailBase)iblockstate.getBlock()).getRailDirection(this.world, new BlockPos(x, y, z), iblockstate, this);
            int[][] aint = MATRIX[direction.getMetadata()];
            double d0 = (double)x + 0.5D + (double)aint[0][0] * 0.5D;
            double d1 = (double)y + 0.0625D + (double)aint[0][1] * 0.5D;
            double d2 = (double)z + 0.5D + (double)aint[0][2] * 0.5D;
            double d3 = (double)x + 0.5D + (double)aint[1][0] * 0.5D;
            double d4 = (double)y + 0.0625D + (double)aint[1][1] * 0.5D;
            double d5 = (double)z + 0.5D + (double)aint[1][2] * 0.5D;
            double d6 = d3 - d0;
            double d7 = (d4 - d1) * 2.0D;
            double d8 = d5 - d2;
            double d9;
            if (d6 == 0.0D) {
                d9 = doubleZ - (double)z;
            } else if (d8 == 0.0D) {
                d9 = doubleX - (double)x;
            } else {
                double d10 = doubleX - d0;
                double d11 = doubleZ - d2;
                d9 = (d10 * d6 + d11 * d8) * 2.0D;
            }

            doubleX = d0 + d6 * d9;
            doubleY = d1 + d7 * d9;
            doubleZ = d2 + d8 * d9;
            if (d7 < 0.0D) {
                ++doubleY;
            }

            if (d7 > 0.0D) {
                doubleY += 0.5D;
            }

            return new Vec3d(doubleX, doubleY, doubleZ);
        } else {
            return null;
        }
    }

}
