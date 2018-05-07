package club.nsdn.nyasamarailway.block.rail.special;

import club.nsdn.nyasamarailway.block.rail.BlockRailDetectorBase;
import club.nsdn.nyasamarailway.block.rail.IRailNoDelay;
import club.nsdn.nyasamarailway.item.tool.ItemToolBase;
import club.nsdn.nyasamatelecom.api.device.SignalBoxSender;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by drzzm32 on 2018.5.7.
 */
public class BlockRailRedStone extends BlockRailDetectorBase implements IRailNoDelay {

    public static class RailRedStone extends SignalBoxSender.TileEntitySignalBoxSender {
    }

    public TileEntity createNewTileEntity(World world, int i) {
        return new RailRedStone();
    }

    public BlockRailRedStone() {
        super("BlockRailRedStone");
        setTextureName("rail_rs");
    }

    @Override
    public float getRailMaxSpeed(World world, EntityMinecart cart, int x, int y, int z) {
        return 8.0F;
    }

    public void setOutputSignal(World world, int x, int y, int z, boolean state) {
        Block block = world.getBlock(x, y, z);
        if (block != this) return;
        int meta = world.getBlockMetadata(x, y, z);
        if (state) {
            if ((meta & 8) == 0) {
                world.setBlockMetadataWithNotify(x, y, z, meta | 8, 3);
                world.notifyBlocksOfNeighborChange(x, y, z, block);
                world.notifyBlocksOfNeighborChange(x, y - 1, z, block);
                world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
            }
        } else {
            if ((meta & 8) != 0) {
                world.setBlockMetadataWithNotify(x, y, z, meta & 7, 3);
                world.notifyBlocksOfNeighborChange(x, y, z, block);
                world.notifyBlocksOfNeighborChange(x, y - 1, z, block);
                world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
            }
        }
        world.func_147453_f(x, y, z, block);
    }

    public void setOutputSignal(RailRedStone rail, boolean state) {
        setOutputSignal(rail.getWorldObj(), rail.xCoord, rail.yCoord, rail.zCoord, state);
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        world.scheduleBlockUpdate(x, y, z, this, 1);
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (!world.isRemote) {
            updateSignal(world, x, y, z);
        }
    }

    public void updateSignal(World world, int x , int y, int z) {
        if (world.getTileEntity(x, y, z) == null) return;
        if (world.getTileEntity(x, y, z) instanceof RailRedStone) {
            RailRedStone rail = (RailRedStone) world.getTileEntity(x, y, z);

            boolean isEnabled = rail.isEnabled;

            if (rail.getTransceiver() != null) {
                isEnabled = isEnabled && rail.transceiverIsPowered();
            }

            setOutputSignal(rail, isEnabled);

            world.scheduleBlockUpdate(x, y, z, this, 1);
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        RailRedStone rail;
        if (world.getTileEntity(x, y, z) == null) return false;
        if (world.getTileEntity(x, y, z) instanceof RailRedStone) {
            rail = (RailRedStone) world.getTileEntity(x, y, z);
            if (!world.isRemote && player.getCurrentEquippedItem() != null) {

                if (!(player.getCurrentEquippedItem().getItem() instanceof ItemToolBase))
                    return false;

                rail.prevIsEnabled = rail.isEnabled;
                if (rail.isEnabled) {
                    rail.isEnabled = false;
                } else {
                    rail.isEnabled = true;
                }
                world.playSoundEffect(
                        (double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D,
                        "random.click", 0.3F, 0.5F
                );

                return true;
            }
        }

        return false;
    }

}
