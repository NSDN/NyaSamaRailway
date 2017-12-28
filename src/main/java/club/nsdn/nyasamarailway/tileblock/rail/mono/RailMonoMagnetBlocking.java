package club.nsdn.nyasamarailway.tileblock.rail.mono;

import club.nsdn.nyasamarailway.block.rail.IRailNoDelay;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityTransceiver;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by drzzm32 on 2017.6.17.
 */
public class RailMonoMagnetBlocking extends RailMonoMagnetDetector implements IRailNoDelay {

    public static LinkedHashMap<UUID, TileEntityRail> tmpRails;

    public static class TileEntityRail extends TileEntityTransceiver implements RailMonoMagnetPowerable {

        @Override
        @SideOnly(Side.CLIENT)
        public AxisAlignedBB getRenderBoundingBox()
        {
            return AxisAlignedBB
                    .getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1)
                    .expand(4, 4, 4);
        }
    }

    public TileEntity createNewTileEntity(World world, int i) {
        return new TileEntityRail();
    }

    public RailMonoMagnetBlocking() {
        super("RailMonoMagnetBlocking", "rail_mono_magnet_blocking");

        tmpRails = new LinkedHashMap<UUID, TileEntityRail>();
    }

    public RailMonoMagnetBlocking(String name, String texture) {
        super("RailMonoMagnetBlocking", "rail_mono_magnet_blocking");

        tmpRails = new LinkedHashMap<UUID, TileEntityRail>();
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

    public void setOutputSignal(TileEntityTransceiver rail, boolean state) {
        setOutputSignal(rail.getWorldObj(), rail.xCoord, rail.yCoord, rail.zCoord, state);
    }

    public boolean railHasPowered(World world, int x, int y, int z) {
        return world.getBlock(x, y, z) == this && (world.getBlockMetadata(x, y, z) & 8) != 0;
    }

    public boolean nearbyRailPowered(World world, int x, int y, int z) {
        return railHasPowered(world, x + 1, y, z) || railHasPowered(world, x - 1, y, z) ||
                railHasPowered(world, x, y, z + 1) || railHasPowered(world, x, y, z - 1);
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

    public boolean nearbyRailHasCart(World world, int x, int y, int z) {
        return (world.getBlock(x + 1, y, z) == this && railHasCart(world, x + 1, y, z)) ||
                (world.getBlock(x - 1, y, z) == this && railHasCart(world, x - 1, y, z)) ||
                (world.getBlock(x, y, z + 1) == this && railHasCart(world, x, y, z + 1)) ||
                (world.getBlock(x, y, z - 1) == this && railHasCart(world, x, y, z - 1));
    }

    public TileEntityRail[] getNearbyRail(World world, int x, int y, int z) {
        return new TileEntityRail[]{
                world.getTileEntity(x + 1, y, z) instanceof TileEntityRail ? (TileEntityRail) world.getTileEntity(x + 1, y, z) : null,
                world.getTileEntity(x - 1, y, z) instanceof TileEntityRail ? (TileEntityRail) world.getTileEntity(x - 1, y, z) : null,
                world.getTileEntity(x, y, z + 1) instanceof TileEntityRail ? (TileEntityRail) world.getTileEntity(x, y, z + 1) : null,
                world.getTileEntity(x, y, z - 1) instanceof TileEntityRail ? (TileEntityRail) world.getTileEntity(x, y, z - 1) : null,
        };
    }

    @Override
    public void setRailOutput(World world, int x, int y, int z, int meta) {
        TileEntityTransceiver thisRail = null;
        if (world.getTileEntity(x, y, z) instanceof TileEntityTransceiver)
            thisRail = (TileEntityTransceiver) world.getTileEntity(x, y, z);

        if (thisRail != null) {
            if (thisRail.getTransceiver() != null) {
                if (railHasCart(world, x, y, z) && !railHasPowered(world, x, y, z)) {
                    if (nearbyRailPowered(world, x, y, z)) {
                        setOutputSignal(thisRail, true);
                        setOutputSignal(thisRail.getTransceiver(), true);
                    }
                }
            } else {
                if (!railHasCart(world, x, y, z) && railHasPowered(world, x, y, z)) {
                    if (!nearbyRailHasCart(world, x, y, z)) {
                        TileEntityTransceiver[] rails = getNearbyRail(world, x, y, z);
                        for (TileEntityTransceiver rail : rails) {
                            if (rail != null) {
                                setOutputSignal(rail, false);
                                if (rail.getTransceiver() != null)
                                    setOutputSignal(rail.getTransceiver(), false);
                            }
                        }
                    }
                }
            }

            if (thisRail.getTransceiver() == null) {
                if (railHasCart(world, x, y, z) && !railHasPowered(world, x, y, z)) {
                    world.setBlockMetadataWithNotify(x, y, z, meta | 8, 3);
                    world.notifyBlocksOfNeighborChange(x, y, z, this);
                    world.notifyBlocksOfNeighborChange(x, y - 1, z, this);
                    world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
                }

                if (!railHasCart(world, x, y, z) && railHasPowered(world, x, y, z)) {
                    world.setBlockMetadataWithNotify(x, y, z, meta & 7, 3);
                    world.notifyBlocksOfNeighborChange(x, y, z, this);
                    world.notifyBlocksOfNeighborChange(x, y - 1, z, this);
                    world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
                }

                if (railHasCart(world, x, y, z)) {
                    world.scheduleBlockUpdate(x, y, z, this, 1);
                }

                world.func_147453_f(x, y, z, this);
            }
        }
    }

}
