package club.nsdn.nyasamarailway.tileblock.signal;

import club.nsdn.nyasamarailway.util.RailSnifferCore;
import club.nsdn.nyasamatelecom.api.util.NSASM;
import club.nsdn.nyasamatelecom.api.util.Util;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by drzzm32 on 2019.1.4.
 */
public class TileEntityTrackSideSniffer extends TileEntityRailSniffer implements ITrackSide {

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

    public static boolean configure(World world, int x, int y, int z, EntityPlayer player) {
        if (world.getTileEntity(x, y, z) == null) return false;
        if (world.getTileEntity(x, y, z) instanceof TileEntityTrackSideSniffer) {
            TileEntityTrackSideSniffer sniffer = (TileEntityTrackSideSniffer) world.getTileEntity(x, y, z);

            ItemStack stack = player.getCurrentEquippedItem();
            if (stack != null) {
                NBTTagList list = Util.getTagListFromNGT(stack);
                if (list == null) return false;

                if (!world.isRemote) {
                    String code = NSASM.getCodeString(list);

                    sniffer.nsasmState = TileEntityTrackSideSniffer.NSASM_IDLE;
                    sniffer.nsasmCode = code;

                    player.addChatComponentMessage(new ChatComponentTranslation("info.sniffer.set"));
                }

                return true;
            }
        }

        return false;
    }

    public static void tick(World world, int x, int y, int z) {
        if (world.isRemote) return;
        if (world.getTileEntity(x, y, z) == null) return;
        if (world.getTileEntity(x, y, z) instanceof TileEntityTrackSideSniffer) {
            TileEntityTrackSideSniffer sniffer = (TileEntityTrackSideSniffer) world.getTileEntity(x, y, z);

            boolean hasCart = ITrackSide.hasMinecart(sniffer, sniffer.direction);
            boolean hasPowered = ITrackSide.hasPowered(sniffer);
            if (hasCart && sniffer.nsasmState == TileEntityTrackSideSniffer.NSASM_IDLE) {
                sniffer.nsasmState = TileEntityTrackSideSniffer.NSASM_DONE;

                EntityMinecart cart = ITrackSide.getMinecart(sniffer, sniffer.direction);
                EntityPlayer player;
                if (!(cart.riddenByEntity instanceof EntityPlayer))
                    player = null;
                else player = (EntityPlayer) cart.riddenByEntity;
                TileEntityRailSniffer rail = sniffer;

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

            if (!hasCart && sniffer.nsasmState == TileEntityRailSniffer.NSASM_DONE) {
                sniffer.nsasmState = TileEntityRailSniffer.NSASM_IDLE;
                sniffer.enable = false;
            }

            if (hasCart && !hasPowered && !sniffer.enable) {
                ITrackSide.setPowered(sniffer, true);
                if (sniffer.getTransceiver() != null) ITrackSide.setPowered(sniffer.getTransceiver(), true);
            }
            if (!hasCart && hasPowered) {
                ITrackSide.setPowered(sniffer, false);
                if (sniffer.getTransceiver() != null) ITrackSide.setPowered(sniffer.getTransceiver(), false);
            }

            world.scheduleBlockUpdate(x, y, z, sniffer.blockType, hasCart ? sniffer.keep : 1);
        }
    }

}
