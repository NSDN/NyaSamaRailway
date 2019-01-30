package club.nsdn.nyasamatelecom.tileblock.core;

import club.nsdn.nyasamatelecom.NyaSamaTelecom;
import club.nsdn.nyasamatelecom.api.device.SignalBox;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityReceiver;
import club.nsdn.nyasamatelecom.api.util.NSASM;
import club.nsdn.nyasamatelecom.api.util.Util;
import club.nsdn.nyasamatelecom.creativetab.CreativeTabLoader;
import club.nsdn.nyasamatelecom.network.NetworkWrapper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.thewdj.telecom.IPassive;

import java.util.LinkedHashMap;

/**
 * Created by drzzm32 on 2019.1.29.
 */
public class BlockDelayer extends SignalBox {

    public static class TileEntityDelayer extends SignalBox.TileEntitySignalBox {

        public int setTime = 20;
        public int tmpTime;

        @Override
        public void fromNBT(NBTTagCompound tagCompound) {
            super.fromNBT(tagCompound);
            setTime = tagCompound.getInteger("setTime");
        }

        @Override
        public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
            tagCompound.setInteger("setTime", setTime);
            return super.toNBT(tagCompound);
        }

        @Override
        public boolean tryControlFirst(boolean state) {
            return false;
        }

        @Override
        public boolean tryControlSecond(boolean state) {
            return false;
        }

        @Override
        public void updateSignal(World world, BlockPos pos) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity == null) return;
            if (tileEntity instanceof TileEntityDelayer) {
                TileEntityDelayer delayer = (TileEntityDelayer) tileEntity;

                int meta = delayer.META;
                int old = meta;
                boolean isEnabled;

                if (delayer.getSender() == null) {
                    isEnabled = (meta & 0x8) != 0;
                    meta &= 0x7;
                } else {
                    isEnabled = delayer.senderIsPowered();

                    if (isEnabled) meta |= 0x8;
                    else meta &= 0x7;
                }

                delayer.isEnabled = isEnabled;

                if (!isEnabled) delayer.tmpTime = 0;
                else {
                    delayer.tmpTime += 1;
                    if (delayer.tmpTime >= delayer.setTime) {
                        delayer.tmpTime = 0;

                        isEnabled = !delayer.inverterEnabled;
                        if (!delayer.tryControlFirst(isEnabled)) {
                            if (!delayer.tryControlSecond(isEnabled)) {
                                if (!delayer.setTargetSender(isEnabled)) {
                                    if (!delayer.setTargetGetter(isEnabled)) {
                                        if (delayer.getTarget() != null) {
                                            TileEntity target = delayer.getTarget();
                                            if (target instanceof TileEntityReceiver) {
                                                if (target instanceof IPassive) {
                                                    delayer.controlTarget(isEnabled);
                                                }
                                            } else {
                                                delayer.controlTarget(isEnabled);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (old != meta || delayer.prevInverterEnabled != delayer.inverterEnabled) {
                    delayer.prevInverterEnabled = delayer.inverterEnabled;
                    delayer.META = meta;
                    delayer.refresh();
                }
            }
        }

    }

    public abstract static class DelayerConfigurer extends NSASM {

        public DelayerConfigurer(String[][] code) { super(code); }

        public DelayerConfigurer(String code) { super(code); }

        public abstract TileEntityDelayer getDelayer();

        @Override
        public SimpleNetworkWrapper getWrapper() {
            return NetworkWrapper.instance;
        }

        private void prt(String str) {
            Register result = new Register();
            result.type = RegType.STR; result.strPtr = 0;
            result.readOnly = true;
            result.data = str;
            funcList.get("prt").run(result, null);
        }

        @Override
        public void loadFunc(LinkedHashMap<String, Operator> funcList) {
            funcList.put("set", (dst, src) -> {
                if (src != null) return Result.ERR;
                if (dst == null) {
                    if (getDelayer() != null) {
                        prt("[NST] Delay: " + String.valueOf(getDelayer().setTime));
                    }
                } else {
                    if (dst.type != RegType.INT) return Result.ERR;

                    int t = (int) dst.data;
                    if (getDelayer() != null) {
                        getDelayer().setTime = t < 0 ? 0 : t;
                        prt("[NST] Delay: " + String.valueOf(getDelayer().setTime));
                    }
                }

                return Result.OK;
            });
        }

    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityDelayer();
    }

    public BlockDelayer() {
        super(NyaSamaTelecom.MODID, "BlockDelayer", "delayer");
        setCreativeTab(CreativeTabLoader.tabNyaSamaTelecom);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)  {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityDelayer) {
            TileEntityDelayer delayer = (TileEntityDelayer) tileEntity;

            ItemStack stack = player.getHeldItem(hand);
            if (!stack.isEmpty()) {
                NBTTagList list = Util.getTagListFromNGT(stack);
                if (list == null) return false;

                if (!world.isRemote) {
                    String[][] code = NSASM.getCode(list);
                    new DelayerConfigurer(code) {
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
                            return player;
                        }

                        @Override
                        public TileEntityDelayer getDelayer() {
                            return delayer;
                        }
                    }.run();
                }

                return true;
            }
        }

        return false;
    }

}
