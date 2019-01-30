package club.nsdn.nyasamatelecom.tileblock.core;

import club.nsdn.nyasamatelecom.NyaSamaTelecom;
import club.nsdn.nyasamatelecom.api.device.TriStateSignalBox;
import club.nsdn.nyasamatelecom.api.util.NSASM;
import club.nsdn.nyasamatelecom.api.util.Util;
import club.nsdn.nyasamatelecom.creativetab.CreativeTabLoader;
import club.nsdn.nyasamatelecom.network.NetworkWrapper;
import club.nsdn.nyasamatelecom.util.Utility;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.LinkedHashMap;

/**
 * Created by drzzm32 on 2019.1.29.
 */
public class BlockTriStateSignalBox extends TriStateSignalBox {

    public static class TileEntityTriStateSignalBox extends TriStateSignalBox.TileEntityTriStateSignalBox {
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityTriStateSignalBox();
    }

    public BlockTriStateSignalBox() {
        super(NyaSamaTelecom.MODID, "BlockTriStateSignalBox", "tri_state_signal_box");
        setCreativeTab(CreativeTabLoader.tabNyaSamaTelecom);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)  {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityTriStateSignalBox) {
            TileEntityTriStateSignalBox box = (TileEntityTriStateSignalBox) tileEntity;

            ItemStack stack = player.getHeldItem(hand);
            if (!stack.isEmpty()) {
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
                            return player;
                        }

                        @Override
                        public void loadFunc(LinkedHashMap<String, Operator> funcList) {
                            funcList.put("inv", (dst, src) -> {
                                if (dst != null) return Result.ERR;
                                if (src != null) return Result.ERR;

                                box.inverterEnabled = !box.inverterEnabled;
                                if (box.inverterEnabled)
                                    Util.say(getPlayer(), "info.signal.box.inverter.on");
                                else
                                    Util.say(getPlayer(), "info.signal.box.inverter.off");

                                return Result.OK;
                            });

                            funcList.put("tri", (dst, src) -> {
                                if (dst != null) return Result.ERR;
                                if (src != null) return Result.ERR;

                                box.triStateIsNeg = !box.triStateIsNeg;
                                if (box.triStateIsNeg)
                                    Util.say(getPlayer(), "info.signal.box.triState.neg");
                                else
                                    Util.say(getPlayer(), "info.signal.box.triState.pos");

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
