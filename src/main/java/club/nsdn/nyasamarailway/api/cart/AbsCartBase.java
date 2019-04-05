package club.nsdn.nyasamarailway.api.cart;

import club.nsdn.nyasamarailway.api.rail.TileEntityRailEndpoint;
import club.nsdn.nyasamarailway.block.BlockPlatform;
import club.nsdn.nyasamarailway.item.ItemLoader;
import club.nsdn.nyasamarailway.item.tool.*;
import club.nsdn.nyasamarailway.api.signal.TileEntityTrackSideReception;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import org.thewdj.linkage.api.ILinkableCart;
import net.minecraft.block.BlockRailPowered;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMinecart;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumHand;
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
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;

import net.minecraftforge.event.entity.minecart.MinecartUpdateEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

/**
 * Created by drzzm32 on 2019.2.10
 */
public abstract class AbsCartBase extends EntityMinecart implements ILinkableCart {

    /** Minecart rotational logic matrix */
    public static final int[][][] MATRIX = new int[][][]{
            {{0, 0, -1}, {0, 0, 1}}, {{-1, 0, 0}, {1, 0, 0}}, {{-1, -1, 0}, {1, 0, 0}},
            {{-1, 0, 0}, {1, -1, 0}}, {{0, 0, -1}, {0, -1, 1}}, {{0, -1, -1}, {0, 0, 1}},
            {{0, 0, 1}, {1, 0, 0}}, {{0, 0, 1}, {-1, 0, 0}}, {{0, 0, -1}, {-1, 0, 0}},
            {{0, 0, -1}, {1, 0, 0}}
    };
    /** appears to be the progress of the turn */
    public boolean isInReverse;

    private static final DataParameter<Boolean> CURVED = EntityDataManager.createKey(AbsCartBase.class, DataSerializers.BOOLEAN);

    public TileEntityRailEndpoint nowEndPoint = null;
    public double nowProgress = 0;
    public double progressDir = 0;

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
    }

    public void setCurved(boolean state) { dataManager.set(CURVED, state); }

    public boolean getCurved() { return dataManager.get(CURVED); }

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
                if (
                        stack.getItem() instanceof Item74HC04 || stack.getItem() instanceof Item1N4148 ||
                        stack.getItem() instanceof ItemNTP8Bit || stack.getItem() instanceof ItemNTP32Bit
                ) {
                    return true;
                }
                if (stack.getItem() instanceof ItemMinecart) return true;
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

    public boolean hasSpecialUpdate() { return false; }

    public void specialUpdate() {

    }

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
        setPositionAndUpdate(pos.x, pos.y, pos.z);
    }

    protected void moveAlongTrackCore(BlockPos pos, IBlockState state) {
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

        int[][] aint = MATRIX[direction.getMetadata()];
        double d1 = (double)(aint[1][0] - aint[0][0]);
        double d2 = (double)(aint[1][2] - aint[0][2]);
        double d3 = Math.sqrt(d1 * d1 + d2 * d2);
        double d4 = this.motionX * d1 + this.motionZ * d2;
        if (d4 < 0.0D) {
            d1 = -d1;
            d2 = -d2;
        }

        /**
         * Modify for higher speed
         */
        double vel = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
        if (vel > getMaxCartSpeedOnRail()) {
            vel = getMaxCartSpeedOnRail();
        }

        this.motionX = vel * d1 / d3;
        this.motionZ = vel * d2 / d3;
        Entity entity = this.getPassengers().isEmpty() ? null : (Entity)this.getPassengers().get(0);
        double d18;
        double d19;
        double d8;
        double d9;
        if (entity instanceof EntityLivingBase) {
            d18 = (double)((EntityLivingBase)entity).moveForward;
            if (d18 > 0.0D) {
                d19 = -Math.sin((double)(entity.rotationYaw * 0.017453292F));
                d8 = Math.cos((double)(entity.rotationYaw * 0.017453292F));
                d9 = this.motionX * this.motionX + this.motionZ * this.motionZ;
                if (d9 < 0.01D) {
                    this.motionX += d19 * getPlayerPushVel();
                    this.motionZ += d8 * getPlayerPushVel();
                    slowDown = false;
                }
            }
        }

        if (slowDown && this.shouldDoRailFunctions()) {
            d18 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            if (d18 < 0.03D) {
                this.motionX *= 0.0D;
                this.motionY *= 0.0D;
                this.motionZ *= 0.0D;
            } else {
                this.motionX *= 0.5D;
                this.motionY *= 0.0D;
                this.motionZ *= 0.5D;
            }
        }

        d18 = (double)pos.getX() + 0.5D + (double)aint[0][0] * 0.5D;
        d19 = (double)pos.getZ() + 0.5D + (double)aint[0][2] * 0.5D;
        d8 = (double)pos.getX() + 0.5D + (double)aint[1][0] * 0.5D;
        d9 = (double)pos.getZ() + 0.5D + (double)aint[1][2] * 0.5D;
        d1 = d8 - d18;
        d2 = d9 - d19;
        double d10;
        if (d1 == 0.0D) {
            this.posX = (double)pos.getX() + 0.5D;
            d10 = this.posZ - (double)pos.getZ();
        } else if (d2 == 0.0D) {
            this.posZ = (double)pos.getZ() + 0.5D;
            d10 = this.posX - (double)pos.getX();
        } else {
            double d11 = this.posX - d18;
            double d12 = this.posZ - d19;
            d10 = (d11 * d1 + d12 * d2) * 2.0D;
        }

        this.posX = d18 + d1 * d10;
        this.posZ = d19 + d2 * d10;
        this.setPosition(this.posX, this.posY, this.posZ);
        this.moveMinecartOnRail(pos);
        if (aint[0][1] != 0 && MathHelper.floor(this.posX) - pos.getX() == aint[0][0] && MathHelper.floor(this.posZ) - pos.getZ() == aint[0][2]) {
            this.setPosition(this.posX, this.posY + (double)aint[0][1], this.posZ);
        } else if (aint[1][1] != 0 && MathHelper.floor(this.posX) - pos.getX() == aint[1][0] && MathHelper.floor(this.posZ) - pos.getZ() == aint[1][2]) {
            this.setPosition(this.posX, this.posY + (double)aint[1][1], this.posZ);
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
        if (this.isBeingRidden()) {
            mX *= 0.75D;
            mZ *= 0.75D;
        }

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
                    if (stack.getItem() instanceof Item74HC04) flag = true;
                    if (stack.getItem() instanceof Item1N4148) flag = true;
                }
                if (flag || this.getDamage() > 40.0F) {
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
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);

        if (nowEndPoint != null) {
            nowEndPoint.toNBT(tagCompound);
        }
        tagCompound.setDouble("nowProgress", nowProgress);
        tagCompound.setDouble("progressDir", progressDir);
    }

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

        int y;
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
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;

            int x = MathHelper.floor(this.posX);
                y = MathHelper.floor(this.posY);
            int z = MathHelper.floor(this.posZ);

            BlockPos pos = new BlockPos(x, y, z);
            IBlockState state = this.world.getBlockState(pos);

            if (hasSpecialUpdate()) {
                specialUpdate();
            } else {
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
                                }
                            }
                            moveAlongCurvedTrack();

                            double dir = progressDir;

                            EnumFacing.Axis axis = nowEndPoint.axis();
                            if (axis == EnumFacing.Axis.X)
                                nowProgress += Math.abs(motionX) * dir;
                            else if (axis == EnumFacing.Axis.Z)
                                nowProgress += Math.abs(motionZ) * dir;

                            motionX = Math.abs(motionX) * Math.signum(posX - prevPosX);
                            motionZ = Math.abs(motionZ) * Math.signum(posZ - prevPosZ);

                            applyDrag();
                        }
                    }
                } else {
                    setCurved(false);
                    nowEndPoint = null;
                    nowProgress = 0;
                    progressDir = 0;
                }

                if (nowEndPoint == null) {
                    if (!this.hasNoGravity()) {
                        this.motionY -= 0.03999999910593033D;
                    }

                    if (!BlockRailBase.isRailBlock(this.world, new BlockPos(x, y, z)) && BlockRailBase.isRailBlock(this.world, new BlockPos(x, y - 1, z))) {
                        --y;
                    }

                    BlockPos blockpos = new BlockPos(x, y, z);
                    IBlockState iblockstate = this.world.getBlockState(blockpos);
                    if (this.canUseRail() && BlockRailBase.isRailBlock(iblockstate)) {
                        this.moveAlongTrack(blockpos, iblockstate);
                        if (iblockstate.getBlock() == Blocks.ACTIVATOR_RAIL) {
                            this.onActivatorRailPass(x, y, z, (Boolean)iblockstate.getValue(BlockRailPowered.POWERED));
                        }
                    } else {
                        this.moveDerailedMinecart();
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

            if (this.canBeRidden() && this.motionX * this.motionX + this.motionZ * this.motionZ > 0.01D) {
                List<Entity> list = this.world.getEntitiesInAABBexcluding(this, box, EntitySelectors.getTeamCollisionPredicate(this));
                if (!list.isEmpty()) {
                    for(int j1 = 0; j1 < list.size(); ++j1) {
                        Entity entity1 = (Entity)list.get(j1);
                        if (!(entity1 instanceof EntityPlayer) && !(entity1 instanceof EntityIronGolem) && !(entity1 instanceof EntityMinecart) && canFitPassenger(entity1) && !entity1.isRiding()) {
                            entity1.startRiding(this);
                        } else {
                            entity1.applyEntityCollision(this);
                        }
                    }
                }
            } else {
                Iterator var13 = this.world.getEntitiesWithinAABBExcludingEntity(this, box).iterator();

                while(var13.hasNext()) {
                    Entity entity = (Entity)var13.next();
                    if (!this.isPassenger(entity) && entity.canBePushed() && entity instanceof EntityMinecart) {
                        entity.applyEntityCollision(this);
                    }
                }
            }

            this.handleWaterMovement();
            MinecraftForge.EVENT_BUS.post(new MinecartUpdateEvent(this, this.getCurrentRailPosition()));
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
