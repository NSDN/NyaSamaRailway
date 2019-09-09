package club.nsdn.nyasamarailway.entity.loco;

import club.nsdn.nyasamarailway.api.cart.AbsLimLoco;
import club.nsdn.nyasamarailway.api.cart.IBogie;
import club.nsdn.nyasamarailway.network.TrainPacket;
import club.nsdn.nyasamarailway.util.TrainController;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.9.9.
 */
public class NSBT3GM extends AbsLimLoco implements IBogie {

    public final float R = 0.109375F;
    public float angle = 0;

    public NSBT3GM(World world) {
        super(world);
        ignoreFrustumCheck = true;
        setSize(2.0F, 1.5F);
    }

    public NSBT3GM(World world, double x, double y, double z) {
        super(world, x, y, z);
        ignoreFrustumCheck = true;
        setSize(2.0F, 1.5F);
    }

    @Override
    public float getMaxCartSpeedOnRail() {
        return 3.0F;
    }

    @Override
    public double getRenderFixOffset() {
        return 0.1875 * 2;
    }

    @Override
    public double getRenderYOffset() {
        return 0.5312;
    }

    @Override
    public float getLinkageDistance(EntityMinecart cart) {
        return 6.0F;
    }

    @Override
    public float getOptimalDistance(EntityMinecart cart) {
        return 4.0F;
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
    public void doMotion(TrainPacket packet, EntityMinecart cart) {
        TrainController.doMotionWithAir(packet, cart);
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
            this.angle += ((float) (getEngineVel() / R * 180 / Math.PI) * (float) dir);
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
