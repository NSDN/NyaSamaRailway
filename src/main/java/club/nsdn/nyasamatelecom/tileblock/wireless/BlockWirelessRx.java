package club.nsdn.nyasamatelecom.tileblock.wireless;

import club.nsdn.nyasamatelecom.NyaSamaTelecom;
import club.nsdn.nyasamatelecom.api.device.SignalBoxSender;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityTransceiver;
import club.nsdn.nyasamatelecom.api.util.NSASM;
import club.nsdn.nyasamatelecom.api.util.Util;
import club.nsdn.nyasamatelecom.creativetab.CreativeTabLoader;
import club.nsdn.nyasamatelecom.util.TelecomProcessor;
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
import org.thewdj.telecom.IWirelessRx;

import java.util.LinkedHashMap;

/**
 * Created by drzzm32 on 2018.1.29.
 */
public class BlockWirelessRx extends SignalBoxSender {

    public static class TileEntityWirelessRx extends SignalBoxSender.TileEntitySignalBoxSender
        implements IWirelessRx<TileEntityTransceiver, TileEntityWirelessRx> {

        public String id = "null";
        public String key = "null";

        @Override
        public String id() {
            return id;
        }

        @Override
        public String key() {
            return key;
        }

        @Override
        public TileEntityWirelessRx me() {
            return this;
        }

        @Override
        public void setState(boolean state) {
            this.isEnabled = state;
        }

        @Override
        public void fromNBT(NBTTagCompound tagCompound) {
            this.id = tagCompound.getString("deviceID");
            this.key = tagCompound.getString("deviceKey");
            super.fromNBT(tagCompound);
        }

        @Override
        public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
            tagCompound.setString("deviceID", id);
            tagCompound.setString("deviceKey", key);
            return super.toNBT(tagCompound);
        }

        @Override
        public void updateSignal(World world, BlockPos pos) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity == null) return;
            if (tileEntity instanceof TileEntityWirelessRx) {
                TileEntityWirelessRx dev = (TileEntityWirelessRx) tileEntity;
                if (TelecomProcessor.instance().device(dev.id) == null)
                    TelecomProcessor.instance().register(dev);
            }
        }

    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityWirelessRx();
    }

    public BlockWirelessRx() {
        super(NyaSamaTelecom.MODID, "BlockWirelessRx", "signal_box_rx");
        setCreativeTab(CreativeTabLoader.tabNyaSamaTelecom);
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);

        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityWirelessRx) {
            TelecomProcessor.instance().register((TileEntityWirelessRx) tileEntity);
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)  {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityWirelessRx) {
            TileEntityWirelessRx box = (TileEntityWirelessRx) tileEntity;

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
                            funcList.put("dev", (dst, src) -> {
                                if (src != null) return Result.ERR;

                                if (dst == null) {
                                    Util.say(getPlayer(), "info.signal.box.wireless.id", box.id);
                                } else {
                                    if (dst.type != RegType.STR) return Result.ERR;
                                    box.id = dst.data.toString();
                                    Util.say(getPlayer(), "info.signal.box.wireless.set");
                                }

                                return Result.OK;
                            });

                            funcList.put("key", (dst, src) -> {
                                if (src != null) return Result.ERR;

                                if (dst == null) {
                                    Util.say(getPlayer(), "info.signal.box.wireless.key", box.key);
                                } else {
                                    if (dst.type != RegType.STR) return Result.ERR;
                                    box.key = dst.data.toString();
                                    Util.say(getPlayer(), "info.signal.box.wireless.set");
                                }

                                return Result.OK;
                            });
                        }
                    }.run();

                    TelecomProcessor.instance().register(box);
                }

                return true;
            }
        }

        return false;
    }

}
