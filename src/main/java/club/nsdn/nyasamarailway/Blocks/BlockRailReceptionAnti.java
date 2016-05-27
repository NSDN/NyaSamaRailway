package club.nsdn.nyasamarailway.Blocks;

import club.nsdn.nyasamarailway.Items.ItemTrainController32Bit;
import club.nsdn.nyasamarailway.Items.ItemTrainController8Bit;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.thewdj.physics.Dynamics;

/**
 * Created by drzzm32 on 2016.5.22.
 */

public class BlockRailReceptionAnti extends BlockRailPoweredBase {

    public BlockRailReceptionAnti() {
        super("BlockRailReceptionAnti");
        setTextureName("rail_reception_anti");
    }

    @Override
    public void onMinecartPass(World world, EntityMinecart cart, int x, int y, int z) {
        boolean riddenByPlayer = false;
        if (cart.riddenByEntity instanceof EntityPlayer) {
            riddenByPlayer = true;
            ItemStack stack = ((EntityPlayer) cart.riddenByEntity).getCurrentEquippedItem();
            if (stack != null) {
                if (stack.getItem() instanceof ItemTrainController8Bit ||
                        stack.getItem() instanceof ItemTrainController32Bit) {
                    return;
                }
            }
        }

        double maxV = 0.1;
        if ((world.getBlockMetadata(x, y, z) >= 8) != riddenByPlayer) {
            if (Math.abs(cart.motionX) < maxV || Math.abs(cart.motionZ) < maxV) {
                if (world.getBlockMetadata(x, y, z) == 8) {
                    if (cart.motionZ <= 0)
                        cart.motionZ = 0.005;
                    cart.motionZ = Dynamics.LocoMotions.calcVelocityUp(Math.abs(cart.motionZ), 0.1, 1.0, 0.1, 0.02);
                } else {
                    if (cart.motionX >= 0)
                        cart.motionX = -0.005;
                    cart.motionX = -Dynamics.LocoMotions.calcVelocityUp(Math.abs(cart.motionX), 0.1, 1.0, 0.1, 0.02);
                }
            }
        } else {
            if (Math.abs(cart.motionX) > maxV || Math.abs(cart.motionZ) > maxV) {
                cart.motionX = Math.signum(cart.motionX) * Dynamics.LocoMotions.calcVelocityDown(Math.abs(cart.motionX), 0.1, 1.0, 1.0, 1.0, 0.1, 0.02);
                cart.motionZ = Math.signum(cart.motionZ) * Dynamics.LocoMotions.calcVelocityDown(Math.abs(cart.motionZ), 0.1, 1.0, 1.0, 1.0, 0.1, 0.02);
            } else {
                if (cart.chunkCoordX == x || cart.chunkCoordZ == z) {
                    cart.posX = cart.chunkCoordX + 0.5;
                    cart.posZ = cart.chunkCoordZ + 0.5;
                    cart.setVelocity(0, 0, 0);
                }
            }
        }
    }
}
