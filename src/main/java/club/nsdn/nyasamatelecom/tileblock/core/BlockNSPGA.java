package club.nsdn.nyasamatelecom.tileblock.core;

import club.nsdn.nyasamatelecom.NyaSamaTelecom;
import club.nsdn.nyasamatelecom.api.device.SignalBox;
import club.nsdn.nyasamatelecom.api.tool.NGTablet;
import club.nsdn.nyasamatelecom.api.util.NSASM;
import club.nsdn.nyasamatelecom.api.util.Util;
import club.nsdn.nyasamatelecom.creativetab.CreativeTabLoader;
import club.nsdn.nyasamatelecom.network.NetworkWrapper;
import club.nsdn.nyasamatelecom.util.TelecomProcessor;
import cn.ac.nya.nspga.INSPGA;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.LinkedHashMap;

/**
 * Created by drzzm32 on 2019.1.29.
 */
public class BlockNSPGA extends SignalBox {

    public final Class<? extends INSPGA> dev;

    public static class TileEntityNSPGA extends SignalBox.TileEntitySignalBox {

        public INSPGA device = null;
        public boolean configured = false;

        public int inputsLen = 0;
        public String[] inputs = new String[0];
        public int outputsLen = 0;
        public String[] outputs = new String[0];
        public int flashLen = 0;
        public int[] flash = new int[0];

        public TileEntityNSPGA() {
            setInfo(8, 0.875, 0.125, 0.875);
        }

        @Override
        public void fromNBT(NBTTagCompound tagCompound) {
            inputsLen = tagCompound.getInteger("inputsLen");
            if (inputsLen > 0) {
                inputs = new String[inputsLen];
                for (int i = 0; i < inputsLen; i++) {
                    inputs[i] = tagCompound.getString("input" + i);
                }
            }
            outputsLen = tagCompound.getInteger("outputsLen");
            if (outputsLen > 0) {
                outputs = new String[outputsLen];
                for (int i = 0; i < outputsLen; i++) {
                    outputs[i] = tagCompound.getString("output" + i);
                }
            }
            flashLen = tagCompound.getInteger("flashLen");
            if (flashLen > 0) {
                int[] tmp = tagCompound.getIntArray("flash");
                if (tmp.length == flashLen) flash = tmp;
            }
            super.fromNBT(tagCompound);
        }

        @Override
        public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
            if (inputsLen > 0) {
                tagCompound.setInteger("inputsLen", inputsLen);
                for (int i = 0; i < inputsLen; i++) {
                    tagCompound.setString("input" + i, inputs[i]);
                }
            }
            if (outputsLen > 0) {
                tagCompound.setInteger("outputsLen", outputsLen);
                for (int i = 0; i < outputsLen; i++) {
                    tagCompound.setString("output" + i, outputs[i]);
                }
            }
            if (flashLen > 0) {
                tagCompound.setInteger("flashLen", flashLen);
                tagCompound.setIntArray("flash", flash);
            }
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

        private boolean input(String dev) {
            TelecomProcessor.DeviceInfo info;
            info = TelecomProcessor.instance().device(dev);
            boolean result;
            if (info == null) return false;
            if (!TelecomProcessor.instance().isTx(info)) {
                return false;
            }
            result = TelecomProcessor.instance().get(info);
            return result;
        }

        private void output(String dev, boolean state) {
            TelecomProcessor.DeviceInfo info;
            info = TelecomProcessor.instance().device(dev);
            if (info == null) return;
            if (!TelecomProcessor.instance().isRx(info)) {
                return;
            }
            TelecomProcessor.instance().set(info, state);
        }

        public byte input() {
            byte result = 0x00;
            if (inputsLen == 0) {
                result = isEnabled ? (byte) 0xFF : (byte) 0x00;
            } else {
                for (int i = 0; i < inputsLen; i++) {
                    result |= ((input(inputs[i]) ? 0x1 : 0x0) << i);
                }
            }
            return result;
        }

        public void output(byte data) {
            if (outputsLen == 0) {
                boolean state = (data != 0);
                if (!tryControlFirst(state)) {
                    if (!tryControlSecond(state)) {
                        if (!setTargetSender(state)) {
                            if (!setTargetGetter(state)) {
                                if (getTarget() != null) {
                                    controlTarget(state);
                                }
                            }
                        }
                    }
                }
            } else {
                for (int i = 0; i < outputsLen; i++) {
                    output(outputs[i], ((data >> i) & 0x1) != 0);
                }
            }
        }

        @Override
        public void updateSignal(World world, BlockPos pos) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity == null) return;
            if (tileEntity instanceof TileEntityNSPGA) {
                TileEntityNSPGA dev = (TileEntityNSPGA) tileEntity;

                int meta = dev.META;
                int old = meta;
                boolean isEnabled;

                if (dev.getSender() == null) {
                    isEnabled = (meta & 0x8) != 0;
                    meta &= 0x7;
                } else {
                    isEnabled = dev.senderIsPowered();

                    if (isEnabled) meta |= 0x8;
                    else meta &= 0x7;
                }

                dev.isEnabled = isEnabled;

                if (dev.device == null && dev.getBlockType() instanceof BlockNSPGA)
                    dev.device = ((BlockNSPGA) dev.getBlockType()).createDevice();

                if (!dev.configured) {
                    if (dev.flashLen > 0) {
                        dev.device.configure(dev.flash);
                    }
                }

                byte input = dev.input();
                byte output = dev.device.output(input);
                dev.output(output);

                if (old != meta) {
                    dev.META = meta;
                    dev.refresh();
                }
            }
        }

    }

    public abstract static class NSPGALoader extends NSASM {

        public NSPGALoader(String[][] code) { super(code); }

        public NSPGALoader(String code) { super(code); }

        public abstract TileEntityNSPGA getNSPGA();

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

        private String getString(Map map, int index) {
            Register register = new Register();
            register.data = index;
            register.type = RegType.INT;
            register.readOnly = true;
            register.strPtr = 0;
            if (map.containsKey(register)) {
                register = map.get(register);
                if (register.type == RegType.STR) {
                    return register.data.toString();
                }
            }
            return "";
        }

        private int getInteger(Map map, int index) {
            Register register = new Register();
            register.data = index;
            register.type = RegType.INT;
            register.readOnly = true;
            register.strPtr = 0;
            if (map.containsKey(register)) {
                register = map.get(register);
                if (register.type == RegType.INT) {
                    return (int) register.data;
                }
            }
            return 0;
        }

        @Override
        public void loadFunc(LinkedHashMap<String, Operator> funcList) {
            funcList.replace("in", (dst, src) -> {
                if (dst == null) return Result.ERR;
                if (src != null) return Result.ERR;
                if (dst.type != RegType.MAP) return Result.ERR;

                if (dst.data instanceof Map && getNSPGA() != null) {
                    Map map = (Map) dst.data; TileEntityNSPGA dev = getNSPGA();
                    if (map.size() <= 8) {
                        dev.inputsLen = map.size();
                        dev.inputs = new String[dev.inputsLen];
                        for (int i = 0; i < dev.inputsLen; i++) {
                            dev.inputs[i] = getString(map, i);
                        }
                        return Result.OK;
                    }
                }

                return Result.ERR;
            });

            funcList.replace("out", (dst, src) -> {
                if (dst == null) return Result.ERR;
                if (src != null) return Result.ERR;
                if (dst.type != RegType.MAP) return Result.ERR;

                if (dst.data instanceof Map && getNSPGA() != null) {
                    Map map = (Map) dst.data; TileEntityNSPGA dev = getNSPGA();
                    if (map.size() <= 8) {
                        dev.outputsLen = map.size();
                        dev.outputs = new String[dev.outputsLen];
                        for (int i = 0; i < dev.outputsLen; i++) {
                            dev.outputs[i] = getString(map, i);
                        }
                        return Result.OK;
                    }
                }

                return Result.ERR;
            });

            funcList.put("flash", (dst, src) -> {
                if (dst == null) return Result.ERR;
                if (src != null) return Result.ERR;
                if (dst.type != RegType.MAP) return Result.ERR;

                if (dst.data instanceof Map && getNSPGA() != null) {
                    Map map = (Map) dst.data; TileEntityNSPGA dev = getNSPGA();
                    dev.flashLen = map.size();
                    dev.flash = new int[dev.flashLen];
                    for (int i = 0; i < dev.flashLen; i++) {
                        dev.flash[i] = getInteger(map, i) & 0xFFFF;
                    }
                    return Result.OK;
                }

                return Result.ERR;
            });

            funcList.replace("rst", (dst, src) -> {
                if (dst != null || src != null)
                    return Result.ERR;
                if (getNSPGA() != null) {
                    TileEntityNSPGA dev = getNSPGA();
                    if (dev.flashLen > 0) {
                        dev.device.configure(dev.flash);
                    }
                }
                return Result.OK;
            });
        }

    }

    @SideOnly(Side.CLIENT)
    public ResourceLocation texturePrint;

    public final String name;

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityNSPGA();
    }

    public BlockNSPGA(Class<? extends INSPGA> dev, String name, String id) {
        super(NyaSamaTelecom.MODID, name, id);
        setCreativeTab(CreativeTabLoader.tabNyaSamaTelecom);

        this.dev = dev; this.name = id;
    }

    public INSPGA createDevice() {
        try {
            return dev.newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)  {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityNSPGA) {
            TileEntityNSPGA dev = (TileEntityNSPGA) tileEntity;

            ItemStack stack;
            if (!world.isRemote && player.isSneaking()) {
                for (int i = 0; i < 9; i++) {
                    stack = player.inventory.mainInventory.get(i);
                    if (stack.isEmpty()) continue;
                    if (stack.getItem() == Items.AIR) continue;
                    if (stack.getItem() instanceof NGTablet) {

                        dev.inputsLen = 0; dev.inputs = new String[0];
                        dev.outputsLen = 0; dev.outputs = new String[0];
                        dev.flashLen = 0; dev.flash = new int[0];
                        dev.device = null; dev.configured = false;

                        Util.say(player, "info.nspga.reset");
                        return true;
                    }
                }
            }

            stack = player.getHeldItem(hand);
            if (!stack.isEmpty()) {
                NBTTagList list = Util.getTagListFromNGT(stack);
                if (list == null) return false;

                if (!world.isRemote) {
                    String[][] code = NSASM.getCode(list);
                    new NSPGALoader(code) {
                        @Override
                        public World getWorld() { return world; }
                        @Override
                        public EntityPlayer getPlayer() { return player; }
                        @Override
                        public double getX() { return pos.getX(); }
                        @Override
                        public double getY() { return pos.getY(); }
                        @Override
                        public double getZ() { return pos.getZ(); }
                        @Override
                        public TileEntityNSPGA getNSPGA() { return dev; }
                    }.run();

                    Util.say(player, "info.nspga.set");
                }

                return true;
            }
        }

        return false;
    }

}
