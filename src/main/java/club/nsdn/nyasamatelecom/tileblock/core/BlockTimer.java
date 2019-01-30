package club.nsdn.nyasamatelecom.tileblock.core;

import club.nsdn.nyasamatelecom.NyaSamaTelecom;
import club.nsdn.nyasamatelecom.api.device.SignalBoxSender;
import club.nsdn.nyasamatelecom.api.tileentity.ITriStateReceiver;
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

import java.util.LinkedHashMap;

/**
 * Created by drzzm32 on 2019.1.29.
 */
public class BlockTimer extends SignalBoxSender {

    public static class TileEntityTimer extends SignalBoxSender.TileEntitySignalBoxSender implements ITriStateReceiver {

        public int setTime = 20;
        public int tmpTime;
        public boolean autoReload = true;

        private int prevSetTime, prevTmpTime;
        private boolean prevAutoReload;

        public static final int STATE_POS = 1;
        public static final int STATE_ZERO = 0;
        public static final int STATE_NEG = -1;

        public int state;
        private int prevState;

        public void setStatePos() {
            state = state == STATE_NEG ? STATE_ZERO : STATE_POS;
        }

        public void setStateNeg() {
            state = state == STATE_POS ? STATE_ZERO : STATE_NEG;
        }

        public boolean hasChanged() {
            return (prevIsEnabled != isEnabled) || (prevSetTime != setTime) ||
                    (prevTmpTime != tmpTime) || (prevAutoReload != autoReload) ||
                    (prevState != state);
        }

        public void updateChanged() {
            prevIsEnabled = isEnabled;
            prevSetTime = setTime;
            prevTmpTime = tmpTime;
            prevAutoReload = autoReload;
            prevState = state;
        }

        @Override
        public void fromNBT(NBTTagCompound tagCompound) {
            setTime = tagCompound.getInteger("setTime");
            tmpTime = tagCompound.getInteger("tmpTime");
            autoReload = tagCompound.getBoolean("autoReload");
            state = tagCompound.getInteger("state");
            super.fromNBT(tagCompound);
        }

        @Override
        public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
            tagCompound.setInteger("setTime", setTime);
            tagCompound.setInteger("tmpTime", tmpTime);
            tagCompound.setBoolean("autoReload", autoReload);
            tagCompound.setInteger("state", state);
            return super.toNBT(tagCompound);
        }

        @Override
        public void updateSignal(World world, BlockPos pos) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity == null) return;
            if (tileEntity instanceof TileEntityTimer) {
                TileEntityTimer timer = (TileEntityTimer) tileEntity;

                int meta = timer.META;
                int old = meta;

                switch (timer.state) {
                    case TileEntityTimer.STATE_POS:
                        if (timer.tmpTime < timer.setTime)
                            timer.tmpTime += 1;
                        break;
                    case TileEntityTimer.STATE_NEG:
                        timer.tmpTime = 0;
                        break;
                    case TileEntityTimer.STATE_ZERO:
                        break;
                    default:
                        break;
                }

                if (timer.tmpTime >= timer.setTime) {
                    timer.isEnabled = true;
                    if (timer.autoReload) timer.tmpTime = 0;
                } else {
                    timer.isEnabled = false;
                }

                boolean isEnabled = timer.isEnabled;

                if (timer.getTransceiver() != null) {
                    isEnabled = isEnabled && timer.transceiverIsPowered();
                }

                if (isEnabled) meta |= 0x8;
                else meta &= 0x7;

                if (old != meta) {
                    timer.META = meta;
                }
                if (timer.hasChanged()) {
                    timer.updateChanged();
                    timer.refresh();
                }

                timer.state = TileEntityTimer.STATE_ZERO;
            }
        }

    }

    public abstract static class TimerConfigurer extends NSASM {

        public TimerConfigurer(String[][] code) { super(code); }

        public TimerConfigurer(String code) { super(code); }

        public abstract TileEntityTimer getTimer();

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
                if (dst == null) return Result.ERR;
                if (dst.type != RegType.INT) return Result.ERR;

                int t = (int) dst.data;
                if (getTimer() != null) {
                    getTimer().setTime = t < 0 ? 0 : t;
                }
                return Result.OK;
            });

            funcList.put("auto", (dst, src) -> {
                if (src != null) return Result.ERR;
                if (dst == null) return Result.ERR;
                if (dst.type != RegType.INT) return Result.ERR;

                int auto = (int) dst.data;
                if (getTimer() != null) {
                    getTimer().autoReload = auto > 0;
                }
                return Result.OK;
            });

            funcList.replace("rst", (dst, src) -> {
                if (dst != null || src != null)
                    return Result.ERR;
                if (getTimer() != null) {
                    getTimer().tmpTime = 0;
                }
                return Result.OK;
            });
        }

    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityTimer();
    }

    public BlockTimer() {
        super(NyaSamaTelecom.MODID, "BlockTimer", "timer");
        setCreativeTab(CreativeTabLoader.tabNyaSamaTelecom);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)  {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityTimer) {
            TileEntityTimer timer = (TileEntityTimer) tileEntity;

            ItemStack stack = player.getHeldItem(hand);
            if (!stack.isEmpty()) {
                NBTTagList list = Util.getTagListFromNGT(stack);
                if (list == null) return false;

                if (!world.isRemote) {
                    String[][] code = NSASM.getCode(list);
                    new TimerConfigurer(code) {
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
                        public TileEntityTimer getTimer() {
                            return timer;
                        }
                    }.run();
                }

                return true;
            }
        }

        return false;
    }

}
