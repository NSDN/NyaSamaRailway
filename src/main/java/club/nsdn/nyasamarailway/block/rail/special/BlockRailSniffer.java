package club.nsdn.nyasamarailway.block.rail.special;

import club.nsdn.nyasamarailway.block.rail.BlockRailDetectorBase;
import club.nsdn.nyasamarailway.block.rail.IRailNoDelay;
import club.nsdn.nyasamarailway.tileblock.signal.TileEntityRailSniffer;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityTransceiver;
import club.nsdn.nyasamatelecom.api.util.NSASM;
import club.nsdn.nyasamarailway.util.RailSnifferCore;
import club.nsdn.nyasamatelecom.api.util.Util;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by drzzm32 on 2017.10.2.
 */
public class BlockRailSniffer extends BlockRailDetectorBase implements IRailNoDelay {

    public static class RailSniffer extends TileEntityRailSniffer {
    }

    public TileEntity createNewTileEntity(World world, int i) {
        return new RailSniffer();
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

    public void setOutputSignal(TileEntityTransceiver rail, boolean state) {
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

    public EntityMinecart getCart(World world, int x, int y, int z) {
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
        if (bBox.isEmpty()) return null;
        if (!(bBox.get(0) instanceof EntityMinecart)) return null;
        return (EntityMinecart) bBox.get(0);
    }

    @Override
    public void setRailOutput(World world, int x, int y, int z, int meta) {
        RailSniffer sniffer = null;
        if (world.getTileEntity(x, y, z) instanceof RailSniffer)
            sniffer = (RailSniffer) world.getTileEntity(x, y, z);

        if (sniffer != null) {

            if (railHasCart(world, x, y, z) && sniffer.nsasmState == RailSniffer.NSASM_IDLE) {
                sniffer.nsasmState = RailSniffer.NSASM_DONE;

                EntityMinecart cart = getCart(world, x, y, z);
                EntityPlayer player;
                if (!(cart.riddenByEntity instanceof EntityPlayer))
                    player = null;
                else player = (EntityPlayer) cart.riddenByEntity;
                RailSniffer rail = sniffer;

                new RailSnifferCore(rail.nsasmCode) {
                    @Override
                    public World getWorld() {
                        return world;
                    }

                    @Override
                    public double getX() {
                        return x;
                    }

                    @Override
                    public double getY() {
                        return y;
                    }

                    @Override
                    public double getZ() {
                        return z;
                    }

                    @Override
                    public EntityPlayer getPlayer() {
                        return player;
                    }

                    @Override
                    public TileEntityRailSniffer getRail() {
                        return rail;
                    }

                    @Override
                    public EntityMinecart getCart() {
                        return cart;
                    }
                }.run();

            }

            if (!railHasCart(world, x, y, z) && sniffer.nsasmState == RailSniffer.NSASM_DONE) {
                sniffer.nsasmState = RailSniffer.NSASM_IDLE;
                sniffer.enable = false;
            }

            if (railHasCart(world, x, y, z) && !railHasPowered(world, x, y, z) && !sniffer.enable) {
                setOutputSignal(sniffer, true);
                if (sniffer.getTransceiver() != null) setOutputSignal(sniffer.getTransceiver(), true);
            }
            if (!railHasCart(world, x, y, z) && railHasPowered(world, x, y, z)) {
                setOutputSignal(sniffer, false);
                if (sniffer.getTransceiver() != null) setOutputSignal(sniffer.getTransceiver(), false);
            }

            if (railHasCart(world, x, y, z)) {
                world.scheduleBlockUpdate(x, y, z, this, sniffer.keep);
            }

        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (world.getTileEntity(x, y, z) == null) return false;
        if (world.getTileEntity(x, y, z) instanceof RailSniffer) {
            RailSniffer sniffer = (RailSniffer) world.getTileEntity(x, y, z);
            if (!world.isRemote) {
                ItemStack stack = player.getCurrentEquippedItem();
                if (stack != null) {

                    NBTTagList list = Util.getTagListFromNGT(stack);
                    if (list == null) return true;
                    String code = NSASM.getCodeString(list);

                    sniffer.nsasmState = RailSniffer.NSASM_IDLE;
                    sniffer.nsasmCode = code;

                    player.addChatComponentMessage(new ChatComponentTranslation("info.sniffer.set"));
                }
            }
            return true;
        }

        return false;
    }

}
