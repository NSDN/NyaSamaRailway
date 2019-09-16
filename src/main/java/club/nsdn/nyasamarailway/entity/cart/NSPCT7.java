package club.nsdn.nyasamarailway.entity.cart;

import club.nsdn.nyasamarailway.api.cart.AbsMotoCart;
import club.nsdn.nyasamarailway.api.cart.CartUtil;
import club.nsdn.nyasamarailway.network.TrainPacket;
import club.nsdn.nyasamarailway.util.TrainController;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.2.11
 */
public class NSPCT7 extends AbsMotoCart {

    public NSPCT7(World world) {
        super(world);
        ignoreFrustumCheck = true;
        setSize(1.0F, 1.0F);
    }

    public NSPCT7(World world, double x, double y, double z) {
        super(world, x, y, z);
        ignoreFrustumCheck = true;
        setSize(1.0F, 1.0F);
    }

    @Override
    public float getMaxCartSpeedOnRail() {
        return 3.0F;
    }

    @Override
    public double getMountedYOffset() {
        return 0.925;
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
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
    public void doMotion(TrainPacket packet, EntityMinecart cart) {
        TrainController.doMotionWithAir(packet, cart);
    }

    @Override
    public int getMaxPassengerSize() {
        return 6;
    }

    @Override // Called by rider
    public void updatePassenger(Entity entity) {
        CartUtil.updatePassenger6(this, entity);
    }

}
