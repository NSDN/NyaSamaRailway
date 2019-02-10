package club.nsdn.nyasamarailway.api.signal;

import club.nsdn.nyasamarailway.network.NetworkWrapper;
import club.nsdn.nyasamarailway.util.SoundUtil;
import club.nsdn.nyasamatelecom.api.network.ParticlePacket;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityMultiSender;
import club.nsdn.nyasamatelecom.api.util.NSASM;
import club.nsdn.nyasamatelecom.api.util.Util;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
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

/**
 * Created by drzzm32 on 2019.2.10
 */
public class TileEntityTrackSideSniffer extends TileEntityMultiSender implements ITrackSide, ITrackSidePowerable {

    public static abstract class SnifferCore<T extends TileEntityTrackSideSniffer> extends NSASM {

        public SnifferCore(String code) {
            super(code);
        }

        @Override
        public SimpleNetworkWrapper getWrapper() {
            return NetworkWrapper.instance;
        }

        @Override
        public void loadFunc(LinkedHashMap<String, Operator> funcList) {
            funcList.put("keep", ((dst, src) -> {
                if (src != null) return Result.ERR;
                if (dst == null) return Result.ERR;
                if (dst.type == RegType.STR) return Result.ERR;

                if (getSniffer() != null)
                    getSniffer().keep = Integer.valueOf(dst.data.toString());
                return Result.OK;
            }));
            funcList.put("enb", ((dst, src) -> {
                if (src != null) return Result.ERR;
                if (dst != null) return Result.ERR;

                if (getSniffer() != null)
                    getSniffer().enable = true;
                return Result.OK;
            }));
            funcList.put("rnd", ((dst, src) -> {
                if (src != null) return Result.ERR;
                if (dst == null) return Result.ERR;
                if (dst.readOnly) return Result.ERR;

                dst.type = RegType.INT;
                dst.data = Math.round(Math.random() * 255);
                return Result.OK;
            }));
            funcList.put("sum", ((dst, src) -> {
                if (src == null) return Result.ERR;
                if (dst == null) return Result.ERR;
                if (src.type != RegType.STR) return Result.ERR;
                if (src.data.toString().isEmpty()) return Result.ERR;
                if (dst.readOnly) return Result.ERR;

                dst.type = RegType.INT;
                dst.data = 0;
                for (char c : src.data.toString().toCharArray())
                    dst.data = (int) dst.data + (int) c;
                return Result.OK;
            }));
            funcList.put("cid", ((dst, src) -> {
                if (src != null) return Result.ERR;
                if (dst == null) return Result.ERR;
                if (dst.readOnly) return Result.ERR;

                dst.type = RegType.STR;
                if (getCart() == null) dst.data = "null";
                else dst.data = getCart().getCustomNameTag();
                return Result.OK;
            }));
            funcList.put("pid", ((dst, src) -> {
                if (src != null) return Result.ERR;
                if (dst == null) return Result.ERR;
                if (dst.readOnly) return Result.ERR;

                dst.type = RegType.STR;
                if (getPlayer() == null) dst.data = "null";
                else dst.data = getPlayer().getDisplayNameString();
                return Result.OK;
            }));

            funcList.put("cls", ((dst, src) -> {
                if (src != null) return Result.ERR;
                if (dst != null) return Result.ERR;

                if (getCart() != null)
                    SoundUtil.instance().playSound(getCart(), SoundUtil.instance().RECEPTION_CLOSE);
                return Result.OK;
            }));

            funcList.put("nya", ((dst, src) -> {
                if (src != null) return Result.ERR;
                if (dst == null) return Result.ERR;
                if (dst.type != RegType.STR) return Result.ERR;
                if (dst.data.toString().isEmpty()) return Result.ERR;

                String type = dst.data.toString();
                double tX, tY, tZ;
                if (regGroup[0].type != RegType.STR)
                    tX = Double.valueOf(regGroup[0].data.toString());
                else tX = 0;
                if (regGroup[1].type != RegType.STR)
                    tY = Double.valueOf(regGroup[1].data.toString());
                else tY = 0;
                if (regGroup[2].type != RegType.STR)
                    tZ = Double.valueOf(regGroup[2].data.toString());
                else tZ = 0;
                if (getPlayer() != null)
                    new ParticlePacket(
                            type, getX(), getY(), getZ(), tX, tY, tZ
                    ).send(
                            NetworkWrapper.instance, getPlayer().dimension
                    );
                else if (getCart() != null)
                    new ParticlePacket(
                            type, getX(), getY(), getZ(), tX, tY, tZ
                    ).send(
                            NetworkWrapper.instance, getCart().dimension
                    );

                return Result.OK;
            }));

            funcList.replace("nyaa", ((dst, src) -> {
                if (src != null) return Result.ERR;
                if (dst == null) return Result.ERR;
                if (dst.type != RegType.STR) return Result.ERR;
                if (dst.data.toString().isEmpty()) return Result.ERR;

                String type = dst.data.toString();
                double x, y, z, tX, tY, tZ;
                if (regGroup[0].type != RegType.STR)
                    x = Double.valueOf(regGroup[0].data.toString());
                else x = 0;
                if (regGroup[1].type != RegType.STR)
                    y = Double.valueOf(regGroup[1].data.toString());
                else y = 0;
                if (regGroup[2].type != RegType.STR)
                    z = Double.valueOf(regGroup[2].data.toString());
                else z = 0;
                if (regGroup[3].type != RegType.STR)
                    tX = Double.valueOf(regGroup[3].data.toString());
                else tX = 0;
                if (regGroup[4].type != RegType.STR)
                    tY = Double.valueOf(regGroup[4].data.toString());
                else tY = 0;
                if (regGroup[5].type != RegType.STR)
                    tZ = Double.valueOf(regGroup[5].data.toString());
                else tZ = 0;
                if (getPlayer() != null)
                    new ParticlePacket(
                            type, x, y, z, tX, tY, tZ
                    ).send(
                            NetworkWrapper.instance, getPlayer().dimension
                    );
                else if (getCart() != null)
                    new ParticlePacket(
                            type, x, y, z, tX, tY, tZ
                    ).send(
                            NetworkWrapper.instance, getCart().dimension
                    );

                return Result.OK;
            }));

        }

        public abstract T getSniffer();
        public abstract EntityMinecart getCart();

    }

    @Override
    public boolean getSGNState() {
        return ITrackSide.hasPowered(this);
    }

    @Override
    public boolean getTXDState() {
        return targetCount > 0;
    }

    @Override
    public boolean getRXDState() {
        return false;
    }

    protected boolean prevSGN, prevTXD, prevRXD;
    protected boolean hasChanged() {
        return prevSGN != getSGNState() || prevTXD != getTXDState() || prevRXD != getRXDState();
    }
    protected void updateChanged() {
        prevSGN = getSGNState(); prevTXD = getTXDState(); prevRXD = getRXDState();
    }

    @Override
    public void setDir(EnumFacing dir) {
        direction = dir;
    }

    @Override
    public boolean hasInvert() {
        return false;
    }

    @Override
    public boolean isInvert() {
        return false;
    }

    public EnumFacing direction;

    public static final boolean NSASM_IDLE = false;
    public static final boolean NSASM_DONE = true;
    public boolean nsasmState = NSASM_IDLE;
    public String nsasmCode = "";

    public boolean enable = false;
    public int keep = 20;

    @Override
    public void fromNBT(NBTTagCompound tagCompound) {
        direction = EnumFacing.byName(
                tagCompound.getString("direction")
        );

        nsasmState = tagCompound.getBoolean("nsasmState");
        nsasmCode = tagCompound.getString("nsasmCode");
        enable = tagCompound.getBoolean("enable");
        keep = tagCompound.getInteger("keep");
        super.fromNBT(tagCompound);
    }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
        if (direction == null) direction = EnumFacing.DOWN;
        tagCompound.setString("direction", direction.getName());

        tagCompound.setBoolean("nsasmState", nsasmState);
        tagCompound.setString("nsasmCode", nsasmCode);
        tagCompound.setBoolean("enable", enable);
        tagCompound.setInteger("keep", keep);
        return super.toNBT(tagCompound);
    }

    @Override
    public boolean hasPowered() {
        return (META & 0x8) != 0;
    }

    @Override
    public void setPowered(boolean state) {
        META = state ? META | 0x8 : META & 0x7;
    }

    public static boolean configure(World world, BlockPos pos, EntityPlayer player) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity == null) return false;
        if (tileEntity instanceof TileEntityTrackSideSniffer) {
            TileEntityTrackSideSniffer sniffer = (TileEntityTrackSideSniffer) tileEntity;

            ItemStack stack = player.getHeldItemMainhand();
            if (!stack.isEmpty()) {
                NBTTagList list = Util.getTagListFromNGT(stack);
                if (list == null) return false;

                if (!world.isRemote) {
                    String code = NSASM.getCodeString(list);

                    sniffer.nsasmState = TileEntityTrackSideSniffer.NSASM_IDLE;
                    sniffer.nsasmCode = code;

                    Util.say(player, "info.sniffer.set");
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public void update() { }

    public static void tick(World world, BlockPos pos) {
        if (world.isRemote) return;
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity == null) return;
        if (tileEntity instanceof TileEntityTrackSideSniffer) {
            TileEntityTrackSideSniffer sniffer = (TileEntityTrackSideSniffer) tileEntity;

            boolean hasCart = ITrackSide.hasMinecart(sniffer, sniffer.direction);
            boolean hasPowered = ITrackSide.hasPowered(sniffer);
            if (hasCart && sniffer.nsasmState == TileEntityTrackSideSniffer.NSASM_IDLE) {
                sniffer.nsasmState = TileEntityTrackSideSniffer.NSASM_DONE;

                EntityMinecart cart = ITrackSide.getMinecart(sniffer, sniffer.direction);
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
                    public TileEntityTrackSideSniffer getSniffer() {
                        return sniffer;
                    }

                    @Override
                    public EntityMinecart getCart() {
                        return cart;
                    }
                }.run();

            }

            if (!hasCart && sniffer.nsasmState == TileEntityTrackSideSniffer.NSASM_DONE) {
                sniffer.nsasmState = TileEntityTrackSideSniffer.NSASM_IDLE;
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
