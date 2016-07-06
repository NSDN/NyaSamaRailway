package club.nsdn.nyasamarailway.Blocks;

import club.nsdn.nyasamarailway.Items.ItemTrainController32Bit;
import club.nsdn.nyasamarailway.Items.ItemTrainController8Bit;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.thewdj.physics.Dynamics;

/**
 * Created by drzzm32 on 2016.5.22.
 */

public class BlockRailReception extends BlockRailPoweredBase implements IRailDirectional {

    public BlockRailReception() {
        super("BlockRailReception");
        setTextureName("rail_reception");
    }

    public boolean isForward() {
        return true;
    }

    public boolean checkNearbySameRail(World world, int x, int y, int z) {
        return world.getBlock(x + 1, y, z) == this || world.getBlock(x - 1, y, z) == this ||
                world.getBlock(x, y, z + 1) == this || world.getBlock(x, y, z - 1) == this;
    }

    public void setRailVerticalOutput(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        if (getRailDirection(world, x, y, z) == RailDirection.NS) {
            world.setBlockMetadataWithNotify(x - 1, y, z, meta | 8, 3);
            world.setBlockMetadataWithNotify(x + 1, y, z, meta | 8, 3);
            world.setBlockMetadataWithNotify(x, y - 1, z, meta | 8, 3);
        } else {
            world.setBlockMetadataWithNotify(x, y, z - 1, meta | 8, 3);
            world.setBlockMetadataWithNotify(x, y, z + 1, meta | 8, 3);
            world.setBlockMetadataWithNotify(x, y - 1, z, meta | 8, 3);
        }

    }

    @Override
    public void onMinecartPass(World world, EntityMinecart cart, int x, int y, int z) {
        boolean playerDetectable = false;
        if (cart.riddenByEntity instanceof EntityPlayer) {
            if (!checkNearbySameRail(world, x, y, z)) playerDetectable = true;
            ItemStack stack = ((EntityPlayer) cart.riddenByEntity).getCurrentEquippedItem();
            if (stack != null) {
                if (stack.getItem() instanceof ItemTrainController8Bit ||
                    stack.getItem() instanceof ItemTrainController32Bit) {
                    return;
                }
            }
        }

        double maxV = 0.1;
        if ((world.getBlockMetadata(x, y, z) >= 8) != playerDetectable) {
            if (Math.abs(cart.motionX) < maxV && Math.abs(cart.motionZ) < maxV) {
                if (getRailDirection(world, x, y, z) == RailDirection.NS) {
                    if (cart.motionZ >= 0)
                        cart.motionZ = -0.005;
                    cart.motionZ = -Dynamics.LocoMotions.calcVelocityUp(Math.abs(cart.motionZ), 0.1, 1.0, 0.1, 0.02);
                } else {
                    if (cart.motionX <= 0)
                        cart.motionX = 0.005;
                    cart.motionX = Dynamics.LocoMotions.calcVelocityUp(Math.abs(cart.motionX), 0.1, 1.0, 0.1, 0.02);
                }
            }
        } else {
            if (Math.abs(cart.motionX) > maxV || Math.abs(cart.motionZ) > maxV) {
                cart.motionX = Math.signum(cart.motionX) * Dynamics.LocoMotions.calcVelocityDown(Math.abs(cart.motionX), 0.1, 1.0, 1.0, 1.0, 0.05, 0.02);
                cart.motionZ = Math.signum(cart.motionZ) * Dynamics.LocoMotions.calcVelocityDown(Math.abs(cart.motionZ), 0.1, 1.0, 1.0, 1.0, 0.05, 0.02);
            } else {
                if (getRailDirection(world, x, y, z) == RailDirection.NS) {
                    if (cart.motionZ <= 0) {
                        cart.motionZ = 0;
                    }
                } else {
                    if (cart.motionX >= 0) {
                        cart.motionX = 0;
                    }
                }
            }
        }
    }
}
