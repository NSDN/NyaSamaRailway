package club.nsdn.nyasamatelecom.tileblock.wireless;

import club.nsdn.nyasamatelecom.NyaSamaTelecom;
import club.nsdn.nyasamatelecom.api.device.SignalBoxGetter;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityTransceiver;
import club.nsdn.nyasamatelecom.api.util.NSASM;
import club.nsdn.nyasamatelecom.api.util.Util;
import club.nsdn.nyasamatelecom.creativetab.CreativeTabLoader;
import club.nsdn.nyasamatelecom.util.TelecomProcessor;
import club.nsdn.nyasamatelecom.network.NetworkWrapper;
import club.nsdn.nyasamatelecom.util.Utility;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.thewdj.telecom.IWirelessTx;

import java.util.LinkedHashMap;
import java.util.Random;

/**
 * Created by drzzm32 on 2017.12.29.
 */
public class BlockWirelessTx extends SignalBoxGetter {

    public static class TileEntityWirelessTx extends SignalBoxGetter.TileEntitySignalBoxGetter
        implements IWirelessTx<TileEntityTransceiver, TileEntityWirelessTx> {

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
        public TileEntityWirelessTx me() {
            return this;
        }

        @Override
        public boolean getState() {
            return isEnabled;
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

    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityWirelessTx();
    }

    public BlockWirelessTx() {
        super(NyaSamaTelecom.modid, "BlockWirelessTx", "signal_box_tx");
        setCreativeTab(CreativeTabLoader.tabNyaSamaTelecom);
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);

        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityWirelessTx) {
            TelecomProcessor.instance().register((TileEntityWirelessTx) tileEntity);
        }
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        super.updateTick(world, x, y, z, random);

        if (!world.isRemote) {
            TileEntity tileEntity = world.getTileEntity(x, y, z);
            if (tileEntity == null) return;
            if (tileEntity instanceof TileEntityWirelessTx) {
                TileEntityWirelessTx dev = (TileEntityWirelessTx) tileEntity;
                if (TelecomProcessor.instance().device(dev.id) == null)
                    TelecomProcessor.instance().register(dev);
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (world.getTileEntity(x, y, z) == null) return false;
        if (world.getTileEntity(x, y, z) instanceof TileEntityWirelessTx) {
            TileEntityWirelessTx box = (TileEntityWirelessTx) world.getTileEntity(x, y, z);

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
                            funcList.put("dev", (dst, src) -> {
                                if (src != null) return Result.ERR;

                                if (dst == null) {
                                    Utility.say(getPlayer(), "info.signal.box.wireless.id", box.id);
                                } else {
                                    if (dst.type != RegType.STR) return Result.ERR;
                                    box.id = dst.data.toString();
                                    Utility.say(getPlayer(), "info.signal.box.wireless.set");
                                }

                                return Result.OK;
                            });

                            funcList.put("key", (dst, src) -> {
                                if (src != null) return Result.ERR;

                                if (dst == null) {
                                    Utility.say(getPlayer(), "info.signal.box.wireless.key", box.key);
                                } else {
                                    if (dst.type != RegType.STR) return Result.ERR;
                                    box.key = dst.data.toString();
                                    Utility.say(getPlayer(), "info.signal.box.wireless.set");
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
