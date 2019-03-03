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
 * Created by drzzm32 on 2019.3.1
 */
public class NSRD1Shelf extends AbsTrainBase {

    public NSRD1Shelf(World world) {
        super(world);
        setSize(1.0F, 1.0F);
    }

    public NSRD1Shelf(World world, double x, double y, double z) {
        super(world, x, y, z);
        setSize(1.0F, 1.0F);
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
    @SideOnly(Side.CLIENT)
    public double getRenderFixOffset() {
        return -0.25;
    }

    @Override
    public int getMaxPassengerSize() {
        return 1;
    }

    @Override
    public void updatePassenger(@Nonnull Entity entity) {

    }

}
