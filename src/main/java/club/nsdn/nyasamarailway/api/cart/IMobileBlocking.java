package club.nsdn.nyasamarailway.api.cart;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by drzzm32 on 2019.11.19
 */
public interface IMobileBlocking {
    boolean getBlockingState();
    void setBlockingState(boolean value);

    static Tuple<BlockPos, EnumFacing> findNextRail(World world, Tuple<BlockPos, EnumFacing> now) {
        return findNextRail(world, null, now);
    }

    static Tuple<BlockPos, EnumFacing> findNextRail(World world, EntityMinecart cart, Tuple<BlockPos, EnumFacing> now) {
        if (now == null) return null;

        IBlockState state = world.getBlockState(now.getFirst());
        Block block = state.getBlock();
        if (!(block instanceof BlockRailBase))
            return null;
        BlockRailBase rail = (BlockRailBase) block;
        BlockRailBase.EnumRailDirection railDir = rail.getRailDirection(world, now.getFirst(), state, cart);
        switch (railDir) {
            case NORTH_SOUTH:
                switch (now.getSecond()) {
                    case NORTH:
                        if (!BlockRailBase.isRailBlock(world, now.getFirst().north()))
                            return new Tuple<>(now.getFirst().down().north(), now.getSecond());
                        return new Tuple<>(now.getFirst().north(), now.getSecond());
                    case SOUTH:
                        if (!BlockRailBase.isRailBlock(world, now.getFirst().south()))
                            return new Tuple<>(now.getFirst().down().south(), now.getSecond());
                        return new Tuple<>(now.getFirst().south(), now.getSecond());
                    default:
                        return null;
                }
            case EAST_WEST:
                switch (now.getSecond()) {
                    case EAST:
                        if (!BlockRailBase.isRailBlock(world, now.getFirst().east()))
                            return new Tuple<>(now.getFirst().down().east(), now.getSecond());
                        return new Tuple<>(now.getFirst().east(), now.getSecond());
                    case WEST:
                        if (!BlockRailBase.isRailBlock(world, now.getFirst().west()))
                            return new Tuple<>(now.getFirst().down().west(), now.getSecond());
                        return new Tuple<>(now.getFirst().west(), now.getSecond());
                    default:
                        return null;
                }
            case ASCENDING_NORTH:
                switch (now.getSecond()) {
                    case NORTH:
                        return new Tuple<>(now.getFirst().up().north(), now.getSecond());
                    case SOUTH:
                        if (BlockRailBase.isRailBlock(world, now.getFirst().south()))
                            return new Tuple<>(now.getFirst().south(), now.getSecond());
                        return new Tuple<>(now.getFirst().down().south(), now.getSecond());
                    default:
                        return null;
                }
            case ASCENDING_SOUTH:
                switch (now.getSecond()) {
                    case NORTH:
                        if (BlockRailBase.isRailBlock(world, now.getFirst().north()))
                            return new Tuple<>(now.getFirst().north(), now.getSecond());
                        return new Tuple<>(now.getFirst().down().north(), now.getSecond());
                    case SOUTH:
                        return new Tuple<>(now.getFirst().up().south(), now.getSecond());
                    default:
                        return null;
                }
            case ASCENDING_EAST:
                switch (now.getSecond()) {
                    case EAST:
                        return new Tuple<>(now.getFirst().up().east(), now.getSecond());
                    case WEST:
                        if (BlockRailBase.isRailBlock(world, now.getFirst().west()))
                            return new Tuple<>(now.getFirst().west(), now.getSecond());
                        return new Tuple<>(now.getFirst().down().west(), now.getSecond());
                    default:
                        return null;
                }
            case ASCENDING_WEST:
                switch (now.getSecond()) {
                    case EAST:
                        if (BlockRailBase.isRailBlock(world, now.getFirst().east()))
                            return new Tuple<>(now.getFirst().east(), now.getSecond());
                        return new Tuple<>(now.getFirst().down().east(), now.getSecond());
                    case WEST:
                        return new Tuple<>(now.getFirst().up().west(), now.getSecond());
                    default:
                        return null;
                }
            case NORTH_EAST:
                switch (now.getSecond()) {
                    case NORTH:
                    case WEST:
                        if (!BlockRailBase.isRailBlock(world, now.getFirst().north()))
                            return new Tuple<>(now.getFirst().down().north(), EnumFacing.NORTH);
                        return new Tuple<>(now.getFirst().north(), EnumFacing.NORTH);
                    case SOUTH:
                    case EAST:
                        if (!BlockRailBase.isRailBlock(world, now.getFirst().east()))
                            return new Tuple<>(now.getFirst().down().east(), EnumFacing.EAST);
                        return new Tuple<>(now.getFirst().east(), EnumFacing.EAST);
                    default:
                        return null;
                }
            case NORTH_WEST:
                switch (now.getSecond()) {
                    case NORTH:
                    case EAST:
                        if (!BlockRailBase.isRailBlock(world, now.getFirst().north()))
                            return new Tuple<>(now.getFirst().down().north(), EnumFacing.NORTH);
                        return new Tuple<>(now.getFirst().north(), EnumFacing.NORTH);
                    case SOUTH:
                    case WEST:
                        if (!BlockRailBase.isRailBlock(world, now.getFirst().west()))
                            return new Tuple<>(now.getFirst().down().west(), EnumFacing.WEST);
                        return new Tuple<>(now.getFirst().west(), EnumFacing.WEST);
                    default:
                        return null;
                }
            case SOUTH_EAST:
                switch (now.getSecond()) {
                    case NORTH:
                    case EAST:
                        if (!BlockRailBase.isRailBlock(world, now.getFirst().east()))
                            return new Tuple<>(now.getFirst().down().east(), EnumFacing.EAST);
                        return new Tuple<>(now.getFirst().east(), EnumFacing.EAST);
                    case SOUTH:
                    case WEST:
                        if (!BlockRailBase.isRailBlock(world, now.getFirst().south()))
                            return new Tuple<>(now.getFirst().down().south(), EnumFacing.SOUTH);
                        return new Tuple<>(now.getFirst().south(), EnumFacing.SOUTH);
                    default:
                        return null;
                }
            case SOUTH_WEST:
                switch (now.getSecond()) {
                    case NORTH:
                    case WEST:
                        if (!BlockRailBase.isRailBlock(world, now.getFirst().west()))
                            return new Tuple<>(now.getFirst().down().west(), EnumFacing.WEST);
                        return new Tuple<>(now.getFirst().west(), EnumFacing.WEST);
                    case SOUTH:
                    case EAST:
                        if (!BlockRailBase.isRailBlock(world, now.getFirst().south()))
                            return new Tuple<>(now.getFirst().down().south(), EnumFacing.SOUTH);
                        return new Tuple<>(now.getFirst().south(), EnumFacing.SOUTH);
                    default:
                        return null;
                }
            default:
                return null;
        }
    }

    static boolean hasCart(World world, EntityMinecart me, BlockPos pos) {
        float bBoxSize = 0.125F;
        List bBox = world.getEntitiesWithinAABB(
                EntityMinecart.class,
                new AxisAlignedBB(pos).shrink(bBoxSize)
        );
        if (!bBox.isEmpty()) {
            Object obj = bBox.get(0);
            if (obj instanceof AbsMotoCart) {
                AbsMotoCart cart = (AbsMotoCart) obj;
                if (cart.equals(me))
                    return false;
                return !cart.getPassengers().isEmpty();
            }
            return !obj.equals(me);
        }
        return false;
    }

    static double getNearestCartDist(World world, EntityMinecart cart, int maxDist) {
        BlockPos pos = cart.getPosition();
        if (!BlockRailBase.isRailBlock(world, pos))
            return Double.NaN;

        EnumFacing facing = EnumFacing.DOWN;
        if (cart instanceof AbsCartBase) {
            facing = ((AbsCartBase) cart).facing;
        } else {
            if (cart.motionX > 0)
                facing = EnumFacing.EAST;
            else if (cart.motionX < 0)
                facing = EnumFacing.WEST;
            else if (cart.motionZ > 0)
                facing = EnumFacing.SOUTH;
            else if (cart.motionZ < 0)
                facing = EnumFacing.NORTH;
            else if (cart.motionX  == 0 && cart.motionZ == 0)
                facing = cart.getHorizontalFacing();
        }

        Tuple<BlockPos, EnumFacing> now = new Tuple<>(pos, facing);
        if (findNextRail(world, cart, now) == null)
            return Double.NaN;

        for (int i = 0; i < maxDist; i++) {
            now = findNextRail(world, cart, now);
            if (now == null)
                return i;
            if (hasCart(world, cart, now.getFirst()))
                return i;
        }

        return Double.NaN;
    }

    static boolean hasCartFrom32(World world, EntityMinecart cart) {
        double dist = getNearestCartDist(world, cart, 32);
        if (Double.isNaN(dist)) return false;
        return dist < 32;
    }

    static boolean hasCartFrom16(World world, EntityMinecart cart) {
        double dist = getNearestCartDist(world, cart, 16);
        if (Double.isNaN(dist)) return false;
        return dist < 16;
    }
}
