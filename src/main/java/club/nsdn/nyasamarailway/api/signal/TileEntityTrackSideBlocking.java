package club.nsdn.nyasamarailway.api.signal;

import club.nsdn.nyasamatelecom.api.tileentity.TileEntityTransceiver;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class TileEntityTrackSideBlocking extends TileEntityTransceiver implements ITrackSide, ITrackSidePowerable {

    @Override
    protected void setBoundsByXYZ(double x1, double y1, double z1, double x2, double y2, double z2) {
        switch (META % METAMAX) {
            case 0:
                setBlockBounds(x1, y1, z1, x2, y2, z2);
                break;
            case 1:
                setBlockBounds(1.0 - z2, y1, x1, 1.0 - z1, y2, x2);
                break;
            case 2:
                setBlockBounds(1.0 - x2, y1, 1.0 - z2, 1.0 - x1, y2, 1.0 - z1);
                break;
            case 3:
                setBlockBounds(z1, y1, 1.0 - x2, z2, y2, 1.0 - x1);
                break;

            case 4:
                setBlockBounds(x1, 1.0 - y2, z1, x2, 1.0 - y1, z2);
                break;
            case 5:
                setBlockBounds(1.0 - z2, 1.0 - y2, x1, 1.0 - z1, 1.0 - y1, x2);
                break;
            case 6:
                setBlockBounds(1.0 - x2, 1.0 - y2, 1.0 - z2, 1.0 - x1, 1.0 - y1, 1.0 - z1);
                break;
            case 7:
                setBlockBounds(z1, 1.0 - y2, 1.0 - x2, z2, 1.0 - y1, 1.0 - x1);
                break;
        }
    }

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
    public void setDir(EnumFacing dir) {
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

    public EnumFacing direction;

    @Override
    public void fromNBT(NBTTagCompound tagCompound) {
        super.fromNBT(tagCompound);

        direction = EnumFacing.byName(
                tagCompound.getString("direction")
        );
    }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
        if (direction == null) direction = EnumFacing.DOWN;
        tagCompound.setString("direction", direction.getName());

        return super.toNBT(tagCompound);
    }

    @Override
    public boolean hasPowered() {
        return (META & 0x8) != 0;
    }

    @Override
    public void setPowered(boolean state) {
        META = state ? META | 0x8 : META & 0x7;
    }

    protected TileEntityTrackSideBlocking getNearby(EnumFacing dir) {
        BlockPos offset = new BlockPos(
                dir.getFrontOffsetX(),
                0,
                dir.getFrontOffsetZ()
        );
        TileEntity tileEntity = getWorld().getTileEntity(getPos().add(offset));
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

    @Override
    public void update() {
        tick(world, pos);
    }

    public void tick(World world, BlockPos pos) {
        if (world.isRemote) return;
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity == null) return;
        if (tileEntity instanceof TileEntityTrackSideBlocking) {
            TileEntityTrackSideBlocking blocking = (TileEntityTrackSideBlocking) tileEntity;

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
                blocking.refresh();
            }
        }
    }

}