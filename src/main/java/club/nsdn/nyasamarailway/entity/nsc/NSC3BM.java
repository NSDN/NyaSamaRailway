package club.nsdn.nyasamarailway.entity.nsc;

import club.nsdn.nyasamarailway.api.cart.nsc.AbsNSCxBM;
import club.nsdn.nyasamarailway.network.TrainPacket;
import club.nsdn.nyasamarailway.util.TrainController;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class NSC3BM extends AbsNSCxBM {

    public NSC3BM(World world) {
        super(world);
    }

    public NSC3BM(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public float getMaxCartSpeedOnRail() {
        return 3.0F;
    }

    @Override
    public double getMountedYOffset() {
        return 0.6 + shiftY;
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
