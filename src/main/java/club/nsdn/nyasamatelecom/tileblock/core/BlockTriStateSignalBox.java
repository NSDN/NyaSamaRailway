package club.nsdn.nyasamatelecom.tileblock.core;

import club.nsdn.nyasamatelecom.NyaSamaTelecom;
import club.nsdn.nyasamatelecom.api.device.TriStateSignalBox;
import club.nsdn.nyasamatelecom.api.util.NSASM;
import club.nsdn.nyasamatelecom.api.util.Util;
import club.nsdn.nyasamatelecom.creativetab.CreativeTabLoader;
import club.nsdn.nyasamatelecom.network.NetworkWrapper;
import club.nsdn.nyasamatelecom.util.Utility;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.LinkedHashMap;

/**
 * Created by drzzm32 on 2017.12.29.
 */
public class BlockTriStateSignalBox extends TriStateSignalBox {

    public static class TileEntityTriStateSignalBox extends TriStateSignalBox.TileEntityTriStateSignalBox {
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityTriStateSignalBox();
    }

    public BlockTriStateSignalBox() {
        super(NyaSamaTelecom.modid, "BlockTriStateSignalBox", "tri_state_signal_box");
        setCreativeTab(CreativeTabLoader.tabNyaSamaTelecom);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (world.getTileEntity(x, y, z) == null) return false;
        if (world.getTileEntity(x, y, z) instanceof TileEntityTriStateSignalBox) {
            TileEntityTriStateSignalBox box = (TileEntityTriStateSignalBox) world.getTileEntity(x, y, z);

            ItemStack stack = player.getCurrentEquippedItem();
            if (stack != null) {
                NBTTagList list = Util.getTagListFromNGT(stack);
                if (list == null) return false;

                if (!world.isRemote) {
                    String[][] code = NSASM.getCode(list);

                    new NSASM(code) {
                        @Override
                        public World getWorld() {
                            return world;
                        }

                        @Override
                        public SimpleNetworkWrapper getWrapper() {
                            return NetworkWrapper.instance;
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
                        public void loadFunc(LinkedHashMap<String, Operator> funcList) {
                            funcList.put("inv", (dst, src) -> {
                                if (dst != null) return Result.ERR;
                                if (src != null) return Result.ERR;

                                box.inverterEnabled = !box.inverterEnabled;
                                if (box.inverterEnabled)
                                    Utility.say(getPlayer(), "info.signal.box.inverter.on");
                                else
                                    Utility.say(getPlayer(), "info.signal.box.inverter.off");

                                return Result.OK;
                            });

                            funcList.put("tri", (dst, src) -> {
                                if (dst != null) return Result.ERR;
                                if (src != null) return Result.ERR;

                                box.triStateIsNeg = !box.triStateIsNeg;
                                if (box.triStateIsNeg)
                                    Utility.say(getPlayer(), "info.signal.box.triState.neg");
                                else
                                    Utility.say(getPlayer(), "info.signal.box.triState.pos");

                                return Result.OK;
                            });
                        }
                    }.run();
                }

                return true;
            }
        }

        return false;
    }

}
