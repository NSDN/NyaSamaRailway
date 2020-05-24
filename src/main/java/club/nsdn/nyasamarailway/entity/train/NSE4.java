package club.nsdn.nyasamarailway.entity.train;

import club.nsdn.nyasamarailway.api.cart.AbsTrainBase;
import club.nsdn.nyasamarailway.api.cart.CartUtil;
import club.nsdn.nyasamarailway.api.cart.IInspectionCart;
import club.nsdn.nyasamarailway.api.item.IController;
import club.nsdn.nyasamarailway.block.BlockPlatform;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.3.3
 */
public class NSE4 extends AbsTrainBase implements IInspectionCart {

    public NSE4(World world) {
        super(world);
    }

    public NSE4(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public boolean shouldRiderSit() {
        return true;
    }

    @Override
    public double getMountedYOffset() {
        return -0.25;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public double getRenderFixOffset() {
        return -0.25;
    }

    @Override
    public int getMaxPassengerSize() {
        return 2;
    }

    @Override // Called by rider
    public void updatePassenger(@Nonnull Entity entity) {
        CartUtil.updatePassenger2CE(this, entity);
    }

    @Override
    public boolean processInitialInteract(@Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
        if (player.isSneaking()) {
            return false;
        } else if (!canFitPassenger(player)) {
            return true;
        } else {
            ItemStack stack = player.getHeldItemMainhand();
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof IController)
                    return true;
            }
            if (!this.world.isRemote) {
                player.startRiding(this);
            }

            return true;
        }
    }

    @Override
    protected void removePassenger(Entity entity) {
        BlockPos pos = this.getPosition();
        EnumFacing facing = getHorizontalFacing(); // Engine is the front
        if (world.getBlockState(pos.down().offset(facing.rotateYCCW())).getBlock() instanceof BlockPlatform)
            pos = pos.offset(facing.rotateYCCW(), 2);
        else if (world.getBlockState(pos.down().offset(facing.rotateY())).getBlock() instanceof BlockPlatform)
            pos = pos.offset(facing.rotateY(), 2);
        else {
            super.removePassenger(entity);
            return;
        }

        super.removePassenger(entity);
        entity.setPositionAndUpdate(
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5
        );
    }

}
