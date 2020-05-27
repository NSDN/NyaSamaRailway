package club.nsdn.nyasamarailway.api.cart;

import club.nsdn.nyasamarailway.api.item.IController;
import club.nsdn.nyasamarailway.api.item.IWand;
import club.nsdn.nyasamarailway.block.BlockPlatform;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Created by drzzm32 on 2019.4.2.
 */
public abstract class AbsContainer extends Entity implements IPartParent {

    private static final DataParameter<Integer> BASE = EntityDataManager.createKey(AbsContainer.class, DataSerializers.VARINT);

    public UUID baseUUID = UUID.randomUUID();

    public int lerpSteps;
    public double lerpX;
    public double lerpY;
    public double lerpZ;
    public double lerpYaw;
    public double lerpPitch;

    public AbsContainer(World world) {
        super(world);
        this.preventEntitySpawning = true;
        this.setSize(1.0F, 1.0F);
    }

    public AbsContainer(World world, double x, double y, double z) {
        this(world);
        this.setPosition(x, y, z);
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
    }

    @Override
    protected void entityInit() {
        dataManager.register(BASE, -1);
    }

    @Override
    protected void readEntityFromNBT(@Nonnull NBTTagCompound tagCompound) {
        dataManager.set(BASE, tagCompound.getInteger("Base"));
        this.baseUUID = tagCompound.getUniqueId("baseUUID");
    }

    @Override
    protected void writeEntityToNBT(@Nonnull NBTTagCompound tagCompound) {
        tagCompound.setInteger("Base", dataManager.get(BASE));
        tagCompound.setUniqueId("baseUUID", this.baseUUID);
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

    public AbsContainer setBase(Entity base) {
        if (base != null) {
            if (!world.isRemote)
                this.baseUUID = base.getUniqueID();
            dataManager.set(BASE, base.getEntityId());
        }
        return this;
    }

    public Entity getBase() {
        Entity entity = world.getEntityByID(dataManager.get(BASE));
        if (!world.isRemote && world instanceof WorldServer) {
            Entity serverEntity = ((WorldServer) world).getEntityFromUuid(this.baseUUID);
            if (serverEntity != null) {
                if (!serverEntity.equals(entity)) {
                    dataManager.set(BASE, serverEntity.getEntityId());
                }
                return serverEntity;
            } else {
                this.baseUUID = UUID.randomUUID();
                dataManager.set(BASE, -1);
                return null;
            }
        }
        return entity;
    }

    @Override
    @Nonnull
    public AxisAlignedBB getEntityBoundingBox() {
        return super.getEntityBoundingBox();
    }

    @Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox() {
        return this.getEntityBoundingBox();
    }

    @Override
    @Nonnull
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        AxisAlignedBB aabb = this.getEntityBoundingBox();
        return aabb.expand(2, 1, 2).expand(-2, -1, -2);
    }

    @Override
    public void applyEntityCollision(@Nonnull Entity entity) {
        if (entity instanceof AbsContainer) {
            if (entity.getEntityBoundingBox().minY < this.getEntityBoundingBox().maxY) {
                super.applyEntityCollision(entity);
            }
        } else if (entity.getEntityBoundingBox().minY <= this.getEntityBoundingBox().minY) {
            super.applyEntityCollision(entity);
        }
    }

    @Override
    public boolean canBeCollidedWith() {
        return !this.isDead;
    }

    @Override
    public boolean processInitialInteract(@Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
        if (player.isSneaking()) {
            return false;
        } else if (!canFitPassenger(player)) {
            return true;
        } else {
            ItemStack stack = player.getHeldItemMainhand();
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof IController)
                    return true;
            }
            if (!this.world.isRemote) {
                player.startRiding(this);
            }

            return true;
        }
    }

    @Override
    protected boolean canFitPassenger(@Nonnull Entity entity) {
        return getPassengers().size() < getMaxPassengerSize() && !isPassenger(entity);
    }

    public abstract int getMaxPassengerSize();

    @Override // Called by rider
    public abstract void updatePassenger(@Nonnull Entity entity);

    @Override
    public boolean attackEntityFrom(@Nonnull DamageSource source, float v) {
        if (!this.world.isRemote && !this.isDead) {
            this.markVelocityChanged();
            boolean flag = false;
            if (source.getTrueSource() instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) source.getTrueSource();
                ItemStack stack = player.getHeldItemMainhand();
                if (stack.isEmpty()) return false;
                if (stack.getItem() instanceof IWand) flag = true;
            }
            if (flag) {
                onKilled();
                this.removePassengers();
                this.setDead();
                for (CartPart e : getMultiPart())
                    e.setDead();
                return true;
            }
        }
        return false;
    }

    public void onKilled() { }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    public void setPosition(double x, double y, double z) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        float w = this.width / 2.0F;
        float h = this.height;
        this.setEntityBoundingBox(new AxisAlignedBB(x - (double)w, y, z - (double)w, x + (double)w, y + (double)h, z + (double)w));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int i, boolean b) {
        this.lerpX = x;
        this.lerpY = y;
        this.lerpZ = z;
        this.lerpYaw = (double) yaw;
        this.lerpPitch = (double) pitch;
        this.lerpSteps = 10;
    }

    public void tickLerp() {
        if (this.lerpSteps > 0 && !this.canPassengerSteer()) {
            double x = this.posX + (this.lerpX - this.posX) / (double)this.lerpSteps;
            double y = this.posY + (this.lerpY - this.posY) / (double)this.lerpSteps;
            double z = this.posZ + (this.lerpZ - this.posZ) / (double)this.lerpSteps;
            double yaw = MathHelper.wrapDegrees(this.lerpYaw - (double)this.rotationYaw);
            this.rotationYaw = (float)((double)this.rotationYaw + yaw / (double)this.lerpSteps);
            this.rotationPitch = (float)((double)this.rotationPitch + (this.lerpPitch - (double)this.rotationPitch) / (double)this.lerpSteps);
            --this.lerpSteps;
            this.setPosition(x, y, z);
            this.setRotation(this.rotationYaw, this.rotationPitch);
        }
    }

    @Override
    protected void addPassenger(Entity entity) {
        super.addPassenger(entity);
        if (this.canPassengerSteer() && this.lerpSteps > 0) {
            this.lerpSteps = 0;
            this.posX = this.lerpX;
            this.posY = this.lerpY;
            this.posZ = this.lerpZ;
            this.rotationYaw = (float)this.lerpYaw;
            this.rotationPitch = (float)this.lerpPitch;
        }
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

    public abstract void update();

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        super.onUpdate();
        this.tickLerp();

        this.update();
    }

}
