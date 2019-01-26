package club.nsdn.nyasamarailway.tileblock.signal;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by drzzm32 on 2019.1.4.
 */
public interface ITrackSide {

    boolean getSGNState();
    boolean getTXDState();
    boolean getRXDState();

    void setDir(ForgeDirection dir);
    boolean hasInvert();
    boolean isInvert();

    static ForgeDirection getAxis() {
        return ForgeDirection.DOWN; // check the left
    }

    static ForgeDirection getDirByMeta(TileEntity tile) {
        if (tile instanceof ITrackSide) {
            int rot = tile.getBlockMetadata() & 0x3;
            ForgeDirection dir = ForgeDirection.UNKNOWN;
            switch (rot) {
                case 0:
                    dir = ForgeDirection.SOUTH; break;
                case 1:
                    dir = ForgeDirection.WEST;  break;
                case 2:
                    dir = ForgeDirection.NORTH; break;
                case 3:
                    dir = ForgeDirection.EAST;  break;
            }
            ((ITrackSide) tile).setDir(dir);
            return dir;
        }
        return ForgeDirection.UNKNOWN;
    }

    static LinkedList<EntityMinecart> getMinecarts(World world, int x, int y, int z) {
        float bBoxSize = 0.125F;
        List bBox = world.getEntitiesWithinAABB(
                EntityMinecart.class,
                AxisAlignedBB.getBoundingBox((double) ((float) x + bBoxSize),
                        (double) y,
                        (double) ((float) z + bBoxSize),
                        (double) ((float) (x + 1) - bBoxSize),
                        (double) ((float) (y + 1) - bBoxSize),
                        (double) ((float) (z + 1) - bBoxSize))
        );
        LinkedList<EntityMinecart> carts = new LinkedList<>();
        for (Object i : bBox.toArray()) {
            if (i instanceof EntityMinecart)
                carts.add((EntityMinecart) i);
        }
        return carts;
    }

    static LinkedList<EntityMinecart> getMinecarts(TileEntity tile, ForgeDirection dir, ForgeDirection offset, int mul) {
        if (tile instanceof ITrackSide) {
            if (dir == null) dir = getDirByMeta(tile);
            ForgeDirection railPos = dir.getRotation(getAxis());
            LinkedList<EntityMinecart> carts = getMinecarts(
                    tile.getWorldObj(),
                    tile.xCoord + railPos.offsetX + offset.offsetX * mul,
                    tile.yCoord + 1,
                    tile.zCoord + railPos.offsetZ + offset.offsetZ * mul
            );
            if (!carts.isEmpty()) return carts;
            carts = getMinecarts(
                    tile.getWorldObj(),
                    tile.xCoord + railPos.offsetX + offset.offsetX * mul,
                    tile.yCoord,
                    tile.zCoord + railPos.offsetZ + offset.offsetZ * mul
            );
            return carts;
        }
        return null;
    }

    static LinkedList<EntityMinecart> getMinecarts(TileEntity tile, ForgeDirection dir) {
        return getMinecarts(tile, dir, ForgeDirection.UNKNOWN, 0);
    }

    static EntityMinecart getMinecart(World world, int x, int y, int z) {
        LinkedList<EntityMinecart> carts = getMinecarts(world, x, y, z);
        if (carts.isEmpty()) return null;
        return carts.get(0);
    }

    static EntityMinecart getMinecart(TileEntity tile, ForgeDirection dir, ForgeDirection offset, int mul) {
        LinkedList<EntityMinecart> carts = getMinecarts(tile, dir, offset, mul);
        if (carts.isEmpty()) return null;
        return carts.get(0);
    }

    static EntityMinecart getMinecart(TileEntity tile, ForgeDirection dir, ForgeDirection offset) {
        for (int i = 0; i <= 8; i++) { //Total: 9 blocks
            if (getMinecart(tile, dir, offset, i) != null)
                return getMinecart(tile, dir, offset, i);
        }
        return null;
    }

    static EntityMinecart getMinecart(TileEntity tile, ForgeDirection dir) {
        return getMinecart(tile, dir, ForgeDirection.UNKNOWN, 0);
    }

    static boolean hasMultiMinecart(TileEntity tile, ForgeDirection dir) {
        LinkedList<EntityMinecart> carts = getMinecarts(tile, dir);
        return !carts.isEmpty();
    }

    static boolean hasMinecart(TileEntity tile, ForgeDirection dir, ForgeDirection offset) {
        return getMinecart(tile, dir, offset) != null;
    }

    static boolean hasMinecart(TileEntity tile, ForgeDirection dir) {
        return getMinecart(tile, dir) != null;
    }

    static boolean hasPowered(World world, int x, int y, int z) {
        return (world.getBlockMetadata(x, y, z) & 8) != 0;
    }

    static boolean hasPowered(TileEntity tile) {
        return hasPowered(tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord);
    }

    static boolean nearbyHasPowered(World world, int x, int y, int z) {
        return hasPowered(world, x + 1, y, z) || hasPowered(world, x - 1, y, z) ||
                hasPowered(world, x, y, z + 1) || hasPowered(world, x, y, z - 1);
    }

    static boolean nearbyHasPowered(TileEntity tile) {
        return nearbyHasPowered(tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord);
    }

    static void setPowered(World world, int x, int y, int z, boolean state) {
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        if (state) {
            if ((meta & 8) == 0) {
                world.setBlockMetadataWithNotify(x, y, z, meta | 8, 3);
                world.notifyBlocksOfNeighborChange(x, y, z, block);
                world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
            }
        } else {
            if ((meta & 8) != 0) {
                world.setBlockMetadataWithNotify(x, y, z, meta & 7, 3);
                world.notifyBlocksOfNeighborChange(x, y, z, block);
                world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
            }
        }
        world.func_147453_f(x, y, z, block);
    }

    static void setPowered(TileEntity tile, boolean state) {
        setPowered(tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord, state);
    }
}
