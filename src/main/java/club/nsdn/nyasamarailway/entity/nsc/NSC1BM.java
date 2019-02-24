package club.nsdn.nyasamarailway.entity.nsc;

import club.nsdn.nyasamarailway.api.cart.nsc.AbsNSCxBM;
import club.nsdn.nyasamarailway.api.cart.nsc.IRotaCart;
import club.nsdn.nyasamarailway.network.TrainPacket;
import club.nsdn.nyasamarailway.util.TrainController;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class NSC1BM extends AbsNSCxBM implements IRotaCart {

    public float angle;

    @Override
    public float getAngle() {
        return angle;
    }

    @Override
    public void setAngle(float v) {
        angle = v % 360.0F;
    }

    public NSC1BM(World world) {
        super(world);
    }

    public NSC1BM(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public float getMaxCartSpeedOnRail() {
        return 8.0F;
    }

    @Override
    public double getMountedYOffset() {
        return -0.1 + shiftY;
    }

    @Nonnull
    @Override
    public ItemStack getCartItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public void doMotion(TrainPacket packet, EntityMinecart cart) {
        TrainController.doMotionWithSlip(packet, cart, getMaxCartSpeedOnRail());
    }

}
