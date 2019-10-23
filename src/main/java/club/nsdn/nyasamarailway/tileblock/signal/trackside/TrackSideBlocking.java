package club.nsdn.nyasamarailway.tileblock.signal.trackside;

import club.nsdn.nyasamarailway.api.signal.ITrackSide;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class TrackSideBlocking extends AbsTrackSide {

    public static class TileEntityTrackSideBlocking extends club.nsdn.nyasamarailway.api.signal.TileEntityTrackSideBlocking {

        public TileEntityTrackSideBlocking() {
            setInfo(13, 0.25, 0.3125, 1);
        }

        protected boolean hasCart(BlockPos start, Vec3i vec) {
            float bBoxSize = 0.125F;
            List bBox = world.getEntitiesWithinAABB(
                    EntityMinecart.class,
                    new AxisAlignedBB(start).expand(vec.getX(), vec.getY(), vec.getZ()).shrink(bBoxSize)
            );
            return !bBox.isEmpty();
        }

        @Override
        public void tick(World world, BlockPos pos) {
            if (world.isRemote) return;
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity == null) return;
            if (tileEntity instanceof TileEntityTrackSideBlocking) {
                TileEntityTrackSideBlocking blocking = (TileEntityTrackSideBlocking) tileEntity;

                boolean hasCart = ITrackSide.hasMinecart(blocking, blocking.direction);
                boolean hasPowered = ITrackSide.hasPowered(blocking);
                if (blocking.getTransceiver() != null) {
                    TileEntity[] tiles = blocking.getNearby();
                    int nearbyCount = 0;
                    for (TileEntity tile : tiles) {
                        if (tile instanceof TileEntityTrackSideBlocking)
                            nearbyCount += 1;
                    }
                    if (nearbyCount == 0) {
                        TileEntity tile = blocking.getTransceiver();
                        if (tile instanceof TileEntityTrackSideBlocking) {
                            TileEntityTrackSideBlocking next = (TileEntityTrackSideBlocking) tile;
                            if (blocking.direction.equals(next.direction)) {
                                EnumFacing offset = ITrackSide.getRailOffset(blocking.direction);
                                Vec3i vec = tile.getPos().subtract(blocking.getPos());
                                BlockPos start = blocking.getPos().offset(offset);
                                boolean state = hasCart(start, vec);
                                ITrackSide.setPowered(blocking, state);
                                ITrackSide.setPowered(blocking.getTransceiver(), state);
                            }
                        }
                    } else if (hasCart && !hasPowered) {
                        if (ITrackSide.nearbyHasPowered(blocking)) {
                            ITrackSide.setPowered(blocking, true);
                            ITrackSide.setPowered(blocking.getTransceiver(), true);
                        }
                    }
                } else {
                    if (hasCart && !hasPowered) {
                        ITrackSide.setPowered(blocking, true);
                    }

                    if (!hasCart && hasPowered) {
                        ITrackSide.setPowered(blocking, false);

                        if (!blocking.nearbyHasMinecart()) {
                            TileEntity[] tiles = blocking.getNearby();
                            for (TileEntity tile : tiles) {
                                if (tile instanceof TileEntityTrackSideBlocking) {
                                    TileEntityTrackSideBlocking tileBlocking = (TileEntityTrackSideBlocking) tile;
                                    ITrackSide.setPowered(tileBlocking, false);
                                    if (tileBlocking.getTransceiver() != null)
                                        ITrackSide.setPowered(tileBlocking.getTransceiver(), false);
                                }
                            }
                        }
                    }
                }

                if (blocking.hasChanged()) {
                    blocking.updateChanged();
                    blocking.refresh();
                }
            }
        }

    }

    public TrackSideBlocking() {
        super("TrackSideBlocking", "track_side_blocking");
    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityTrackSideBlocking();
    }

    @Override
    public boolean onConfigure(World world, BlockPos pos, EntityPlayer player) {
        return false;
    }

}
