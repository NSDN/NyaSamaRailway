package club.nsdn.nyasamarailway.entity.nsc;

import club.nsdn.nyasamarailway.api.cart.nsc.AbsNSCxB;
import club.nsdn.nyasamarailway.network.TrainPacket;
import club.nsdn.nyasamarailway.util.TrainController;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.1.25.
 */
public class NSC3B extends AbsNSCxB {

    public NSC3B(World world) {
        super(world);
    }

    public NSC3B(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public float getMaxCartSpeedOnRail() {
        return 3.0F;
    }

    @Override
    public double getMountedYOffset() {
        return 0.3 + shiftY;
    }

    @Nonnull
    @Override
    public ItemStack getCartItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public void doMotion(TrainPacket packet, EntityMinecart cart) {
        TrainController.doMotionWithEuler(packet, cart, getMaxCartSpeedOnRail());
    }

}
