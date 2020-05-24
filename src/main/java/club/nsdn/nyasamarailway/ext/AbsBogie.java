package club.nsdn.nyasamarailway.ext;

import club.nsdn.nyasamarailway.api.cart.AbsCartBase;
import club.nsdn.nyasamarailway.api.cart.IBogie;
import club.nsdn.nyasamarailway.util.TrainController;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;

import javax.annotation.Nonnull;

public class AbsBogie extends AbsCartBase implements IBogie {

    public float angle = 0;

    public AbsBogie(World world) {
        super(world);
        ignoreFrustumCheck = true;
        setSize(1.0F, 0.5F);
    }

    public AbsBogie(World world, double x, double y, double z) {
        super(world, x, y, z);
        ignoreFrustumCheck = true;
        setSize(1.0F, 0.5F);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        initEntity();
    }

    @Override
    protected void readEntityFromNBT(@Nonnull NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);
        fromNBT(tagCompound);
    }

    @Override
    protected void writeEntityToNBT(@Nonnull NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        toNBT(tagCompound);
    }

    public void initEntity() { }
    public void fromNBT(@Nonnull NBTTagCompound tag) { }
    public void toNBT(@Nonnull NBTTagCompound tag) { }

    public double getRadius() { return 0.21875; }

    @Override
    public float getMaxCartSpeedOnRail() {
        return 2.0F;
    }

    @Override
    public double getMountedYOffset() {
        return 0;
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }

    @Override
    public float getLinkageDistance(EntityMinecart cart) { return 4.0F; }

    @Override
    public float getOptimalDistance(EntityMinecart cart) { return 2.0F; }

    @Nonnull
    @Override
    public ItemStack getCartItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        return MinecraftForge.EVENT_BUS.post(new MinecartInteractEvent(this, player, hand));
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (world.isRemote) {
            double dir = 0, yaw = TrainController.calcYaw(this) * Math.PI / 180.0;
            if (this.motionX != 0)
                dir = Math.signum(this.motionX / Math.cos(yaw));
            else if (this.motionZ != 0)
                dir = Math.signum(this.motionZ / -Math.sin(yaw));
            this.angle += ((float) (getSpeed() / getRadius() * 180 / Math.PI) * (float) dir);
        }
    }

    @Override
    public int getMaxPassengerSize() {
        return 1;
    }

    @Override // Called by rider
    public void updatePassenger(Entity entity) { }

}
