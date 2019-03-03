package club.nsdn.nyasamarailway.entity.train;

import club.nsdn.nyasamarailway.api.cart.AbsTrainBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.3.3
 */
public class NSRD2Main extends AbsTrainBase {

    public NSRD2Main(World world) {
        super(world);
        setSize(4.0F, 3.5F);
    }

    public NSRD2Main(World world, double x, double y, double z) {
        super(world, x, y, z);
        setSize(4.0F, 3.5F);
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }

    @Override
    public boolean processInitialInteract(@Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
        return false;
    }

    @Override
    public double getTrainYOffset() {
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public double getRenderFixOffset() {
        return -2.375;
    }

    @Override
    public int getMaxPassengerSize() {
        return 1;
    }

    @Override
    public void updatePassenger(@Nonnull Entity entity) {

    }

}
