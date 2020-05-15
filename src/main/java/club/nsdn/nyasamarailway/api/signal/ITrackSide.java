package club.nsdn.nyasamarailway.api.signal;

import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by drzzm32 on 2019.2.10
 */
public interface ITrackSide {

    boolean getSGNState();
    boolean getTXDState();
    boolean getRXDState();

    void setDir(EnumFacing dir);
    boolean hasInvert();
    boolean isInvert();
    default void setInvert(boolean value) {  }
    default void flipInvert() {  }
    default boolean getInvertForRender() { return isInvert(); }

    static EnumFacing.Axis getAxis() {
        return EnumFacing.Axis.Y; // check the left
    }

    static EnumFacing getRailOffset(EnumFacing dir) {
        return dir.rotateAround(getAxis()).getOpposite();
    }

    /**
     * Get block's direction
     * */
    static EnumFacing getDirByMeta(TileEntityBase tile) {
        if (tile instanceof ITrackSide) {
            int rot = tile.META & 0x3;
            EnumFacing dir = EnumFacing.DOWN;
            switch (rot) {
                case 0:
                    dir = EnumFacing.SOUTH; break;
                case 1:
                    dir = EnumFacing.WEST;  break;
                case 2:
                    dir = EnumFacing.NORTH; break;
                case 3:
                    dir = EnumFacing.EAST;  break;
            }
            ((ITrackSide) tile).setDir(dir);
            return dir;
        }
        return EnumFacing.DOWN;
    }

    static LinkedList<EntityMinecart> getMinecarts(World world, BlockPos pos) {
        float bBoxSize = 0.125F;
        List bBox = world.getEntitiesWithinAABB(
                EntityMinecart.class,
                new AxisAlignedBB(pos).shrink(bBoxSize)
        );
        LinkedList<EntityMinecart> carts = new LinkedList<>();
        for (Object i : bBox.toArray()) {
            if (i instanceof EntityMinecart)
                carts.add((EntityMinecart) i);
        }
        return carts;
    }

    static LinkedList<EntityMinecart> getMinecarts(TileEntityBase tile, EnumFacing dir, EnumFacing offset, int mul) {
        if (tile instanceof ITrackSide) {
            if (dir == null) dir = getDirByMeta(tile);
            EnumFacing railOffset = getRailOffset(dir);
            BlockPos pos = tile.getPos();
            BlockPos extOffset = new BlockPos(
                    offset.getFrontOffsetX() * mul,
                    1,
                    offset.getFrontOffsetZ() * mul
            );
            pos = pos.add(railOffset.getDirectionVec()).add(extOffset);

            LinkedList<EntityMinecart> carts = getMinecarts(tile.getWorld(), pos);
            if (!carts.isEmpty()) return carts;

            pos = pos.offset(EnumFacing.DOWN);
            carts = getMinecarts(tile.getWorld(), pos);
            return carts;
        }
        return null;
    }

    static LinkedList<EntityMinecart> getMinecarts(TileEntityBase tile, EnumFacing dir) {
        return getMinecarts(tile, dir, EnumFacing.DOWN, 0);
    }

    static LinkedList<EntityMinecart> getMinecarts(TileEntityBase tile, EnumFacing dir, EnumFacing offset) {
        LinkedList<EntityMinecart> carts = new LinkedList<>();
        LinkedList<EntityMinecart> buff;
        for (int i = 0; i <= 8; i++) { //Total: 9 blocks
            buff = getMinecarts(tile, dir, offset, i);
            if (buff != null) carts.addAll(buff);
        }
        return carts;
    }

    static LinkedList<EntityMinecart> sniffMinecarts(TileEntityBase tile, EnumFacing dir, EnumFacing offset, int dist) {
        LinkedList<EntityMinecart> carts = new LinkedList<>();
        LinkedList<EntityMinecart> buff;
        for (int i = 0; i <= dist; i++) {
            buff = getMinecarts(tile, dir, offset, i);
            if (buff != null) carts.addAll(buff);
        }
        return carts;
    }

    static EntityMinecart getMinecart(World world, BlockPos pos) {
        LinkedList<EntityMinecart> carts = getMinecarts(world, pos);
        if (carts.isEmpty()) return null;
        return carts.get(0);
    }

    static EntityMinecart getMinecart(TileEntityBase tile, EnumFacing dir, EnumFacing offset, int mul) {
        LinkedList<EntityMinecart> carts = getMinecarts(tile, dir, offset, mul);
        if (carts.isEmpty()) return null;
        return carts.get(0);
    }

    static EntityMinecart getMinecart(TileEntityBase tile, EnumFacing dir, EnumFacing offset) {
        for (int i = 0; i <= 8; i++) { //Total: 9 blocks
            if (getMinecart(tile, dir, offset, i) != null)
                return getMinecart(tile, dir, offset, i);
        }
        return null;
    }

    static EntityMinecart sniffMinecart(TileEntityBase tile, EnumFacing dir, EnumFacing offset, int dist) {
        for (int i = 0; i <= dist; i++) {
            if (getMinecart(tile, dir, offset, i) != null)
                return getMinecart(tile, dir, offset, i);
        }
        return null;
    }

    static EntityMinecart getMinecart(TileEntityBase tile, EnumFacing dir) {
        return getMinecart(tile, dir, EnumFacing.DOWN, 0);
    }

    static boolean hasMultiMinecart(TileEntityBase tile, EnumFacing dir) {
        LinkedList<EntityMinecart> carts = getMinecarts(tile, dir);
        return !carts.isEmpty();
    }

    static boolean hasMinecart(TileEntityBase tile, EnumFacing dir, EnumFacing offset) {
        return getMinecart(tile, dir, offset) != null;
    }

    static boolean hasMinecart(TileEntityBase tile, EnumFacing dir) {
        return getMinecart(tile, dir) != null;
    }

    static boolean hasPowered(TileEntityBase tile) {
        if (tile instanceof ITrackSidePowerable)
            return ((ITrackSidePowerable) tile).hasPowered();
        return false;
    }

    static boolean hasPowered(World world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityBase)
            return hasPowered((TileEntityBase) tileEntity);
        return false;
    }

    static boolean nearbyHasPowered(World world, BlockPos pos) {
        return hasPowered(world, pos.east()) || hasPowered(world, pos.west()) ||
                hasPowered(world, pos.north()) || hasPowered(world, pos.south());
    }

    static boolean nearbyHasPowered(TileEntityBase tile) {
        return nearbyHasPowered(tile.getWorld(), tile.getPos());
    }

    static void setPowered(World world, BlockPos pos, boolean state) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityBase)
            setPowered((TileEntityBase) tileEntity, state);
    }

    static void setPowered(TileEntityBase tile, boolean state) {
        if (tile instanceof ITrackSidePowerable)
            ((ITrackSidePowerable) tile).setPowered(state);
    }
}
