package club.nsdn.nyasamarailway.block.rail.special;

import club.nsdn.nyasamarailway.block.rail.BlockRailPoweredBase;
import club.nsdn.nyasamarailway.block.rail.IRailDirectional;
import club.nsdn.nyasamarailway.block.rail.IRailReception;
import club.nsdn.nyasamarailway.entity.MinecartBase;
import club.nsdn.nyasamarailway.entity.cart.*;
import club.nsdn.nyasamarailway.item.tool.ItemNTP32Bit;
import club.nsdn.nyasamarailway.item.tool.ItemNTP8Bit;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

/**
 * Created by drzzm32 on 2016.5.22.
 */

public class BlockRailReception extends BlockRailPoweredBase implements IRailDirectional, ITileEntityProvider, IRailReception {

    public static class TileEntityRailReception extends club.nsdn.nyasamarailway.tileblock.signal.TileEntityRailReception {

        @Override
        public void spawn(World world, int x, int y, int z) {
            TileEntityRailReception rail = null;
            if (world.getTileEntity(x, y, z) instanceof TileEntityRailReception) {
                rail = (TileEntityRailReception) world.getTileEntity(x, y, z);
            }
            if (rail != null) {
                if (rail.cartType.isEmpty()) return;
                if (rail.cartType.equals("loco")) return;

                if (rail.cartType.equals(NSTCT1.class.getName())) {
                    MinecartBase cart = new NSTCT1(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);
                    world.spawnEntityInWorld(cart);
                } else if (rail.cartType.equals(NSBT1.class.getName())) {
                    MinecartBase cart = new NSBT1(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);
                    world.spawnEntityInWorld(cart);
                } else if (rail.cartType.equals(NSPCT1.class.getName())) {
                    MinecartBase cart = new NSPCT1(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);
                    world.spawnEntityInWorld(cart);
                } else if (rail.cartType.equals(NSPCT2.class.getName())) {
                    MinecartBase cart = new NSPCT2(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);
                    world.spawnEntityInWorld(cart);
                } else if (rail.cartType.equals(NSPCT3.class.getName())) {
                    MinecartBase cart = new NSPCT3(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);
                    world.spawnEntityInWorld(cart);
                } else if (rail.cartType.equals(NSPCT5L.class.getName())) {
                    MinecartBase cart = new NSPCT5L(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);
                    world.spawnEntityInWorld(cart);
                } else if (rail.cartType.equals(NSPCT6.class.getName())) {
                    MinecartBase cart = new NSPCT6(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);
                    world.spawnEntityInWorld(cart);
                } else if (rail.cartType.equals(NSPCT8.class.getName())) {
                    MinecartBase cart = new NSPCT8(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);
                    world.spawnEntityInWorld(cart);
                } else if (rail.cartType.equals(NSPCT9.class.getName())) {
                    NSPCT9 cart = new NSPCT9(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);
                    cart.setExtendedInfo(rail.extInfo);
                    world.spawnEntityInWorld(cart);
                } else {
                    rail.cartType = "stock";
                    EntityMinecart cart = EntityMinecartEmpty.createMinecart(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5, -1);
                    world.spawnEntityInWorld(cart);
                }
            }
        }

    }

    public TileEntity createNewTileEntity(World world, int i) {
        return new TileEntityRailReception();
    }

    public BlockRailReception() {
        super("BlockRailReception");
        setTextureName("rail_reception");
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        world.scheduleBlockUpdate(x, y, z, this, 1);
    }

    @Override
    public int tickRate(World world) {
        return 20;
    }

    public boolean isForward() {
        return true;
    }

    public boolean checkNearbySameRail(World world, int x, int y, int z) {
        return world.getBlock(x + 1, y, z) == this || world.getBlock(x - 1, y, z) == this ||
                world.getBlock(x, y, z + 1) == this || world.getBlock(x, y, z - 1) == this;
    }

    public boolean timeExceed(World world, int x, int y, int z) {
        if (world.getTileEntity(x, y, z) instanceof TileEntityRailReception) {
            TileEntityRailReception rail = (TileEntityRailReception) world.getTileEntity(x, y, z);
            return rail.delay >= rail.setDelay * 20;
        }
        return false;
    }

    public void setRailState(World world, int x, int y, int z, boolean state) {
        int meta = world.getBlockMetadata(x, y, z);
        world.setBlockMetadataWithNotify(x, y, z, state ? (meta | 0x8) : (meta & 0x7), 3);
        world.notifyBlocksOfNeighborChange(x, y, z, this);
        world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (!world.isRemote) {
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

            TileEntityRailReception rail = null;
            if (world.getTileEntity(x, y, z) instanceof TileEntityRailReception) {
                rail = (TileEntityRailReception) world.getTileEntity(x, y, z);
            }
            boolean playerDetectable = false;
            if (!checkNearbySameRail(world, x, y, z)) playerDetectable = true;

            if (rail != null) {
                TileEntityRailReception.tick(!isForward(), bBox, rail, playerDetectable);
            }

            world.scheduleBlockUpdate(x, y, z, this, 1);
        }
        super.updateTick(world, x, y, z, random);
    }

    @Override
    public void onMinecartPass(World world, EntityMinecart cart, int x, int y, int z) {
        boolean playerDetectable = false;
        EntityPlayer player = null;

        if (!checkNearbySameRail(world, x, y, z)) playerDetectable = true;
        if (cart.riddenByEntity instanceof EntityPlayer) {
            player = (EntityPlayer) cart.riddenByEntity;
            ItemStack stack = ((EntityPlayer) cart.riddenByEntity).getCurrentEquippedItem();
            if (stack != null) {
                if (stack.getItem() instanceof ItemNTP8Bit ||
                        stack.getItem() instanceof ItemNTP32Bit) {
                    return;
                }
            }
        } else if (cart.riddenByEntity instanceof EntityMinecart) {
            EntityMinecart ncart = (EntityMinecart) cart.riddenByEntity;

            if (ncart.riddenByEntity instanceof EntityPlayer) {
                player = (EntityPlayer) ncart.riddenByEntity;
                ItemStack stack = ((EntityPlayer) ncart.riddenByEntity).getCurrentEquippedItem();
                if (stack != null) {
                    if (stack.getItem() instanceof ItemNTP8Bit ||
                            stack.getItem() instanceof ItemNTP32Bit) {
                        return;
                    }
                }
            }
        }

        if (world.getTileEntity(x, y, z) instanceof TileEntityRailReception) {
            TileEntityRailReception rail = (TileEntityRailReception) world.getTileEntity(x, y, z);

            TileEntityRailReception.cart(!isForward(), cart, rail, player, playerDetectable);
        }
    }

    @Override
    public void onRailPowered(World world, int x, int y, int z, int meta, boolean hasCart) {
        boolean playerDetectable = false;
        if (!checkNearbySameRail(world, x, y, z)) playerDetectable = true;
        EntityMinecart cart = getMinecart(world, x, y, z);
        TileEntityRailReception rail = null;
        if (world.getTileEntity(x, y, z) instanceof TileEntityRailReception) {
            rail = (TileEntityRailReception) world.getTileEntity(x, y, z);
        }

        TileEntityRailReception.powered(!isForward(), cart, rail, playerDetectable);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        return TileEntityRailReception.configure(world, x, y, z, player);
    }

}
