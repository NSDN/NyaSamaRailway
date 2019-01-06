package club.nsdn.nyasamarailway.tileblock.signal.trackside;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.tileblock.signal.ITrackSide;
import club.nsdn.nyasamarailway.tileblock.signal.TileEntityTrackSideSniffer;
import club.nsdn.nyasamatelecom.api.util.Util;
import cn.ac.nya.nsasm.NSASM;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.LinkedHashMap;
import java.util.Random;

/**
 * Created by drzzm32 on 2019.1.6.
 */
public class TrackSideSnifferHs extends AbsTrackSide {

    public static class Sniffer extends TileEntityTrackSideSniffer {

        public static abstract class SnifferCore extends TileEntityTrackSideSniffer.SnifferCore<Sniffer> {

            public SnifferCore(String code) {
                super(code);
            }

            @Override
            public void loadFunc(LinkedHashMap<String, NSASM.Operator> funcList) {
                super.loadFunc(funcList);

                funcList.put("inv", ((dst, src) -> {
                    if (src != null) return NSASM.Result.ERR;
                    if (dst != null) return NSASM.Result.ERR;

                    getSniffer().invert = !getSniffer().invert;

                    return NSASM.Result.OK;
                }));
            }

            @Override
            public abstract Sniffer getSniffer();

        }

        protected boolean prevInv;

        @Override
        protected boolean hasChanged() {
            return super.hasChanged() || prevInv != isInvert();
        }

        @Override
        protected void updateChanged() {
            super.updateChanged();
            prevInv = isInvert();
        }

        @Override
        public boolean hasInvert() {
            return true;
        }

        @Override
        public boolean isInvert() {
            return invert;
        }

        public boolean invert = false;

        @Override
        public void fromNBT(NBTTagCompound tagCompound) {
            invert = tagCompound.getBoolean("invert");
            super.fromNBT(tagCompound);
        }

        @Override
        public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
            tagCompound.setBoolean("invert", invert);
            return super.toNBT(tagCompound);
        }

        public static boolean configure(World world, int x, int y, int z, EntityPlayer player) {
            if (world.getTileEntity(x, y, z) == null) return false;
            if (world.getTileEntity(x, y, z) instanceof Sniffer) {
                Sniffer sniffer = (Sniffer) world.getTileEntity(x, y, z);

                ItemStack stack = player.getCurrentEquippedItem();
                if (stack != null) {
                    NBTTagList list = Util.getTagListFromNGT(stack);
                    if (list == null) return false;

                    if (!world.isRemote) {
                        String code = club.nsdn.nyasamatelecom.api.util.NSASM.getCodeString(list);

                        if (code.contains("inv\n")) {
                            new SnifferCore(code) {
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
                                    return null;
                                }

                                @Override
                                public Sniffer getSniffer() {
                                    return sniffer;
                                }

                                @Override
                                public EntityMinecart getCart() {
                                    return null;
                                }
                            }.run();
                        } else {
                            sniffer.nsasmState = Sniffer.NSASM_IDLE;
                            sniffer.nsasmCode = code;

                            player.addChatComponentMessage(new ChatComponentTranslation("info.sniffer.set"));
                        }
                    }

                    return true;
                }
            }

            return false;
        }

        public static void tick(World world, int x, int y, int z) {
            if (world.isRemote) return;
            if (world.getTileEntity(x, y, z) == null) return;
            if (world.getTileEntity(x, y, z) instanceof Sniffer) {
                Sniffer sniffer = (Sniffer) world.getTileEntity(x, y, z);

                ForgeDirection offset = sniffer.isInvert() ? sniffer.direction.getOpposite() : sniffer.direction;
                boolean hasCart = ITrackSide.hasMinecart(sniffer, sniffer.direction, offset);
                boolean hasPowered = ITrackSide.hasPowered(sniffer);
                if (hasCart && sniffer.nsasmState == Sniffer.NSASM_IDLE) {
                    sniffer.nsasmState = Sniffer.NSASM_DONE;

                    EntityMinecart cart = ITrackSide.getMinecart(sniffer, sniffer.direction, offset);
                    EntityPlayer player;
                    if (cart != null) {
                        if (!(cart.riddenByEntity instanceof EntityPlayer))
                            player = null;
                        else player = (EntityPlayer) cart.riddenByEntity;
                    } else player = null;

                    new SnifferCore(sniffer.nsasmCode) {
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
                        public Sniffer getSniffer() {
                            return sniffer;
                        }

                        @Override
                        public EntityMinecart getCart() {
                            return cart;
                        }
                    }.run();

                }

                if (!hasCart && sniffer.nsasmState == Sniffer.NSASM_DONE) {
                    sniffer.nsasmState = Sniffer.NSASM_IDLE;
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

                if (sniffer.hasChanged()) {
                    sniffer.updateChanged();
                    world.markBlockForUpdate(x, y, z);
                }

                world.scheduleBlockUpdate(x, y, z, sniffer.getBlockType(), hasCart ? sniffer.keep : 1);
            }
        }

    }

    public TrackSideSnifferHs() {
        super("TrackSideSnifferHs", "track_side_sniffer_hs");
        setCreativeTab(CreativeTabLoader.tabNSTest);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new Sniffer();
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        Sniffer.tick(world, x, y, z);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        return Sniffer.configure(world, x, y, z, player);
    }

}
