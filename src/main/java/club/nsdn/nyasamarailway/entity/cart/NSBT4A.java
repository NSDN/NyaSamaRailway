package club.nsdn.nyasamarailway.entity.cart;

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

/**
 * Created by drzzm32 on 2019.3.1
 */
public class NSBT4A extends AbsCartBase implements IBogie {

    public final float R = 0.21875F;
    public float angle = 0;

    protected float linkDist = 1.0F;

    public NSBT4A(World world) {
        super(world);
        ignoreFrustumCheck = true;
        setSize(1.0F, 0.5F);
    }

    public NSBT4A(World world, double x, double y, double z) {
        super(world, x, y, z);
        ignoreFrustumCheck = true;
        setSize(1.0F, 0.5F);
    }

    public NSBT4A(World world, double x, double y, double z, float linkDist) {
        this(world, x, y, z);
        this.linkDist = linkDist;
    }

    @Override
    public float getMaxCartSpeedOnRail() {
        return 3.0F;
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
    public float getLinkageDistance(EntityMinecart cart) {
        if (cart instanceof NSBT4B)
            return linkDist * 1.5F;
        return 2.0F;
    }

    @Override
    public float getOptimalDistance(EntityMinecart cart) {
        if (cart instanceof NSBT4B)
            return linkDist;
        return 1.0F;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);

        linkDist = tagCompound.getFloat("linkDist");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);

        tagCompound.setFloat("linkDist", linkDist);
    }

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
            this.angle += ((float) (getSpeed() / R * 180 / Math.PI) * (float) dir);
        }
    }

    @Override
    public int getMaxPassengerSize() {
        return 1;
    }

    @Override // Called by rider
    public void updatePassenger(Entity entity) {

    }

}
