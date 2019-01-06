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

    @Override
    public boolean getSGNState() {
        return ITrackSide.hasPowered(this);
    }

    @Override
    public boolean getTXDState() {
        return getTransceiver() != null;
    }

    @Override
    public boolean getRXDState() {
        return false;
    }

    protected boolean prevSGN, prevTXD, prevRXD;
    protected boolean hasChanged() {
        return prevSGN != getSGNState() || prevTXD != getTXDState() || prevRXD != getRXDState();
    }
    protected void updateChanged() {
        prevSGN = getSGNState(); prevTXD = getTXDState(); prevRXD = getRXDState();
    }

    @Override
    public void setDir(ForgeDirection dir) {
        direction = dir;
    }

    @Override
    public boolean hasInvert() {
        return false;
    }

    @Override
    public boolean isInvert() {
        return false;
    }

    public ForgeDirection direction;

    @Override
    public void fromNBT(NBTTagCompound tagCompound) {
        direction = ForgeDirection.getOrientation(
                tagCompound.getInteger("direction")
        );
        super.fromNBT(tagCompound);
    }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
        if (direction == null) direction = ForgeDirection.UNKNOWN;
        tagCompound.setInteger("direction", direction.ordinal());
        return super.toNBT(tagCompound);
    }

    protected TileEntityTrackSideBlocking getNearby(ForgeDirection dir) {
        TileEntity tileEntity = getWorldObj().getTileEntity(
            xCoord + dir.offsetX, yCoord, zCoord + dir.offsetZ
        );
        if (tileEntity instanceof TileEntityTrackSideBlocking)
            return (TileEntityTrackSideBlocking) tileEntity;
        return null;
    }

    public boolean nearbyHasMinecart() {
        boolean result = false;

        TileEntityTrackSideBlocking blocking = getNearby(direction);
        if (blocking != null) {
            result = ITrackSide.hasMinecart(blocking, blocking.direction);
        }
        blocking = getNearby(direction.getOpposite());
        if (blocking != null) {
            result |= ITrackSide.hasMinecart(blocking, blocking.direction);
        }

        return result;
    }

    public TileEntity[] getNearby() {
        return new TileEntity[] {
                getNearby(direction),
                getNearby(direction.getOpposite())
        };
    }

    public static void tick(World world, int x, int y, int z) {
        if (world.isRemote) return;
        if (world.getTileEntity(x, y, z) == null) return;
        if (world.getTileEntity(x, y, z) instanceof TileEntityTrackSideBlocking) {
            TileEntityTrackSideBlocking blocking = (TileEntityTrackSideBlocking) world.getTileEntity(x, y, z);

            boolean hasCart = ITrackSide.hasMinecart(blocking, blocking.direction);
            boolean hasPowered = ITrackSide.hasPowered(blocking);
            if (blocking.getTransceiver() != null) {
                if (hasCart && !hasPowered) {
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
                world.markBlockForUpdate(x, y, z);
            }

            world.scheduleBlockUpdate(x, y, z, blocking.getBlockType(), 1);
        }
    }

}
