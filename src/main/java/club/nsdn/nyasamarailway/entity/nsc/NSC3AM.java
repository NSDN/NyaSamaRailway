package club.nsdn.nyasamarailway.entity.nsc;

import club.nsdn.nyasamarailway.api.cart.nsc.AbsNSCxAM;
import club.nsdn.nyasamarailway.api.cart.IExtendedInfoCart;
import club.nsdn.nyasamarailway.api.cart.ILimitVelCart;
import club.nsdn.nyasamarailway.api.cart.AbsLocoBase;
import club.nsdn.nyasamarailway.network.TrainPacket;
import club.nsdn.nyasamarailway.util.TrainController;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.1.25.
 */
public class NSC3AM extends AbsNSCxAM {

    private final int INDEX_MV = 28;
    public double maxVelocity = 0;
    private int tmpEngineBrake = -1;

    private final int INDEX_EXT = 29;
    public String extenedInfo = "";

    public NSC3AM(World world) {
        super(world);
        ignoreFrustumCheck = true;
    }

    public NSC3AM(World world, double x, double y, double z) {
        super(world, x, y, z);
        ignoreFrustumCheck = true;
    }

    @Override
    public float getMaxCartSpeedOnRail() {
        return 3.0F;
    }

    @Override
    public double getMountedYOffset() {
        return -0.3;
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
