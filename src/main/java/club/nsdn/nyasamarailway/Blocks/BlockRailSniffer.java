package club.nsdn.nyasamarailway.Blocks;

import club.nsdn.nyasamarailway.TileEntities.Signals.TileEntityRailTransceiver;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by drzzm32 on 2017.10.2.
 */
public class BlockRailSniffer extends BlockRailDetectorBase implements IRailNoDelay {

    public TileEntity createNewTileEntity(World world, int i) {
        return new TileEntityRailTransceiver();
    }

    public BlockRailSniffer() {
        super("BlockRailSniffer");
        setTextureName("rail_sniffer");
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

    public void setOutputSignal(TileEntityRailTransceiver rail, boolean state) {
        setOutputSignal(rail.getWorldObj(), rail.xCoord, rail.yCoord, rail.zCoord, state);
    }

    public boolean railHasPowered(World world, int x, int y, int z) {
        return world.getBlock(x, y, z) == this && (world.getBlockMetadata(x, y, z) & 8) != 0;
    }

    public boolean railHasCart(World world, int x, int y, int z) {
        float bBoxSize = 0.125F;
        List bBox = world.getEntitiesWithinAABB(
                EntityMinecart.class,
                AxisAlignedBB.getBoundingBox(
                        (double) ((float) x + bBoxSize),
                        (double) y,
                        (double) ((float) z + bBoxSize),
                        (double) ((float) (x + 1) - bBoxSize),
                        (double) ((float) (y + 1) - bBoxSize),
                        (double) ((float) (z + 1) - bBoxSize)
                )
        );
        return !bBox.isEmpty();
    }

    @Override
    public void setRailOutput(World world, int x, int y, int z, int meta) {
        TileEntityRailTransceiver thisRail = null;
        if (world.getTileEntity(x, y, z) instanceof TileEntityRailTransceiver)
            thisRail = (TileEntityRailTransceiver) world.getTileEntity(x, y, z);

        if (thisRail != null) {

            if (railHasCart(world, x, y, z) && !railHasPowered(world, x, y, z)) {
                setOutputSignal(thisRail, true);
                setOutputSignal(thisRail.getTransceiver(), true);
            }
            if (!railHasCart(world, x, y, z) && railHasPowered(world, x, y, z)) {
                setOutputSignal(thisRail, false);
                setOutputSignal(thisRail.getTransceiver(), false);
            }

            if (railHasCart(world, x, y, z)) {
                world.scheduleBlockUpdate(x, y, z, this, 60);
            }

        }
    }

}
