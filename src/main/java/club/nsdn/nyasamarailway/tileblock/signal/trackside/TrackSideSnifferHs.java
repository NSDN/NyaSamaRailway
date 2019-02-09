package club.nsdn.nyasamarailway.tileblock.signal.trackside;

import club.nsdn.nyasamarailway.api.signal.TileEntityTrackSideSniffer;
import club.nsdn.nyasamarailway.api.signal.ITrackSide;
import club.nsdn.nyasamatelecom.api.util.Util;
import cn.ac.nya.nsasm.NSASM;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.LinkedHashMap;
import java.util.Random;

/**
 * Created by drzzm32 on 2019.1.6.
 */
public class TrackSideSnifferHs extends AbsTrackSide {

    public static class TileEntityTrackSideSnifferHs extends TileEntityTrackSideSniffer {

        public TileEntityTrackSideSnifferHs() {
            setInfo(13, 0.25, 0.3125, 1);
        }

        public static abstract class SnifferCore extends TileEntityTrackSideSniffer.SnifferCore<TileEntityTrackSideSnifferHs> {

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
            public abstract TileEntityTrackSideSnifferHs getSniffer();

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

        @Override
        public void setInvert(boolean invert) {
            this.invert = invert;
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

        public static boolean configure(World world, BlockPos pos, EntityPlayer player) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity == null) return false;
            if (tileEntity instanceof TileEntityTrackSideSnifferHs) {
                TileEntityTrackSideSnifferHs sniffer = (TileEntityTrackSideSnifferHs) tileEntity;

                ItemStack stack = player.getHeldItemMainhand();
                if (!stack.isEmpty()) {
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
                                    return pos.getX();
                                }

                                @Override
                                public double getY() {
                                    return pos.getY();
                                }

                                @Override
                                public double getZ() {
                                    return pos.getZ();
                                }

                                @Override
                                public EntityPlayer getPlayer() {
                                    return null;
                                }

                                @Override
                                public TileEntityTrackSideSnifferHs getSniffer() {
                                    return sniffer;
                                }

                                @Override
                                public EntityMinecart getCart() {
                                    return null;
                                }
                            }.run();
                        } else {
                            sniffer.nsasmState = TileEntityTrackSideSnifferHs.NSASM_IDLE;
                            sniffer.nsasmCode = code;

                            Util.say(player, "info.sniffer.set");
                        }
                    }

                    return true;
                }
            }

            return false;
        }

        public static void tick(World world, BlockPos pos) {
            if (world.isRemote) return;
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity == null) return;
            if (tileEntity instanceof TileEntityTrackSideSnifferHs) {
                TileEntityTrackSideSnifferHs sniffer = (TileEntityTrackSideSnifferHs) tileEntity;

                EnumFacing offset = sniffer.isInvert() ? sniffer.direction.getOpposite() : sniffer.direction;
                boolean hasCart = ITrackSide.hasMinecart(sniffer, sniffer.direction, offset);
                boolean hasPowered = ITrackSide.hasPowered(sniffer);
                if (hasCart && sniffer.nsasmState == TileEntityTrackSideSnifferHs.NSASM_IDLE) {
                    sniffer.nsasmState = TileEntityTrackSideSnifferHs.NSASM_DONE;

                    EntityMinecart cart = ITrackSide.getMinecart(sniffer, sniffer.direction, offset);
                    EntityPlayer player = null;
                    if (cart != null) {
                        if (!cart.getPassengers().isEmpty()) { // TODO: for more players
                            if (!(cart.getPassengers().get(0) instanceof EntityPlayer))
                                player = null;
                            else player = (EntityPlayer) cart.getPassengers().get(0);
                        }
                    }

                    EntityPlayer thePlayer = player;

                    new SnifferCore(sniffer.nsasmCode) {
                        @Override
                        public World getWorld() {
                            return world;
                        }

                        @Override
                        public double getX() {
                            return pos.getX();
                        }

                        @Override
                        public double getY() {
                            return pos.getY();
                        }

                        @Override
                        public double getZ() {
                            return pos.getZ();
                        }

                        @Override
                        public EntityPlayer getPlayer() {
                            return thePlayer;
                        }

                        @Override
                        public TileEntityTrackSideSnifferHs getSniffer() {
                            return sniffer;
                        }

                        @Override
                        public EntityMinecart getCart() {
                            return cart;
                        }
                    }.run();

                }

                if (!hasCart && sniffer.nsasmState == TileEntityTrackSideSnifferHs.NSASM_DONE) {
                    sniffer.nsasmState = TileEntityTrackSideSnifferHs.NSASM_IDLE;
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
                    sniffer.refresh();
                }

                world.scheduleUpdate(pos, sniffer.getBlockType(), hasCart ? sniffer.keep : 1);
            }
        }

    }

    public TrackSideSnifferHs() {
        super("TrackSideSnifferHs", "track_side_sniffer_hs");
    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityTrackSideSnifferHs();
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
        world.scheduleUpdate(pos, this, 1);
    }

    @Override
    public int tickRate(World world) {
        return 20;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
        TileEntityTrackSideSnifferHs.tick(world, pos);
    }

    @Override
    public boolean onConfigure(World world, BlockPos pos, EntityPlayer player) {
        return TileEntityTrackSideSnifferHs.configure(world, pos, player);
    }

}
