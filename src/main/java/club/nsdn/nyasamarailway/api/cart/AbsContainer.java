package club.nsdn.nyasamarailway.api.cart;

import club.nsdn.nyasamarailway.item.tool.Item1N4148;
import club.nsdn.nyasamarailway.item.tool.Item74HC04;
import club.nsdn.nyasamarailway.item.tool.ItemNTP32Bit;
import club.nsdn.nyasamarailway.item.tool.ItemNTP8Bit;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by drzzm32 on 2019.4.2.
 */
public abstract class AbsContainer extends Entity {

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
    protected void entityInit() {  }

    @Override
    protected void readEntityFromNBT(@Nonnull NBTTagCompound tagCompound) {  }

    @Override
    protected void writeEntityToNBT(@Nonnull NBTTagCompound tagCompound) {  }

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
                if (
                        stack.getItem() instanceof Item74HC04 || stack.getItem() instanceof Item1N4148 ||
                        stack.getItem() instanceof ItemNTP8Bit || stack.getItem() instanceof ItemNTP32Bit
                ) {
                    return true;
                }
            }
            if (!this.world.isRemote) {
                player.startRiding(this);
            }

            return true;
        }
    }

    @Override
    protected boolean canFitPassenger(Entity entity) {
        return getPassengers().size() < getMaxPassengerSize();
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
                if (stack.getItem() instanceof Item74HC04) flag = true;
                if (stack.getItem() instanceof Item1N4148) flag = true;
            }
            if (flag) {
                this.removePassengers();

                if (flag && !this.hasCustomName()) {
                    this.setDead();
                }
            }

            return true;
        } else {
            return true;
        }
    }

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
