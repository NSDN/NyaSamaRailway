package club.nsdn.nyasamarailway.tileblock.signal;

import club.nsdn.nyasamatelecom.api.tileentity.TileEntityTransceiver;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by drzzm32 on 2019.1.4.
 */
public class TileEntityTrackSideBlocking extends TileEntityTransceiver implements ITrackSide {

    public ForgeDirection direction;

    public void fromNBT(NBTTagCompound tagCompound) {
        direction = ForgeDirection.getOrientation(
                tagCompound.getInteger("direction")
        );
        super.fromNBT(tagCompound);
    }

    public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
        if (direction == null) direction = ForgeDirection.UNKNOWN;
        tagCompound.setInteger("direction", direction.ordinal());
        return super.toNBT(tagCompound);
    }

    public static void tick(World world, int x, int y, int z) {
        if (world.isRemote) return;
        if (world.getTileEntity(x, y, z) == null) return;
        if (world.getTileEntity(x, y, z) instanceof TileEntityTrackSideSniffer) {
            TileEntityTrackSideBlocking blocking = (TileEntityTrackSideBlocking) world.getTileEntity(x, y, z);

            boolean hasCart = ITrackSide.hasMinecart(blocking, blocking.direction);
            boolean hasPowered = ITrackSide.hasPowered(blocking);
            int meta = blocking.getBlockMetadata();
            if (blocking.getTransceiver() != null) {
                if (hasCart && !hasPowered) {
                    if (ITrackSide.nearbyHasPowered(blocking)) {
                        ITrackSide.setPowered(blocking, true);
                        ITrackSide.setPowered(blocking.getTransceiver(), true);
                    }
                }
            } else {
                if (!hasCart && hasPowered) {
                    if (!ITrackSide.nearbyHasMinecart(blocking, blocking.direction)) {
                        TileEntity[] tiles = ITrackSide.getNearby(blocking, blocking.direction);
                        for (TileEntity tile : tiles) {
                            if (tile != null) {
                                ITrackSide.setPowered(tile, false);
                                if (tile instanceof TileEntityTransceiver) {
                                    TileEntityTransceiver rail = (TileEntityTransceiver) tile;
                                    if (rail.getTransceiver() != null)
                                        ITrackSide.setPowered(rail.getTransceiver(), false);
                                }
                            }
                        }
                    }
                }
            }

            if (blocking.getTransceiver() == null) {
                if (hasCart && !hasPowered) {
                    world.setBlockMetadataWithNotify(x, y, z, meta | 8, 3);
                    world.notifyBlocksOfNeighborChange(x, y, z, blocking.blockType);
                    world.notifyBlocksOfNeighborChange(x, y - 1, z, blocking.blockType);
                    world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
                }

                if (!hasCart && hasPowered) {
                    world.setBlockMetadataWithNotify(x, y, z, meta & 7, 3);
                    world.notifyBlocksOfNeighborChange(x, y, z, blocking.blockType);
                    world.notifyBlocksOfNeighborChange(x, y - 1, z, blocking.blockType);
                    world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
                }

                world.scheduleBlockUpdate(x, y, z, blocking.blockType, 1);
            }
        }
    }

}
