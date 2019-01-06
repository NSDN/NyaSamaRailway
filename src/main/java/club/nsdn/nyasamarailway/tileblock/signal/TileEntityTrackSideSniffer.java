package club.nsdn.nyasamarailway.tileblock.signal;

import club.nsdn.nyasamarailway.network.NetworkWrapper;
import club.nsdn.nyasamatelecom.api.network.ParticlePacket;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityMultiSender;
import club.nsdn.nyasamatelecom.api.util.NSASM;
import club.nsdn.nyasamatelecom.api.util.Util;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.LinkedHashMap;

/**
 * Created by drzzm32 on 2019.1.4.
 */
public class TileEntityTrackSideSniffer extends TileEntityMultiSender implements ITrackSide {

    public static abstract class SnifferCore extends NSASM {

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
                else dst.data = getCart().getCommandSenderName();
                return Result.OK;
            }));
            funcList.put("pid", ((dst, src) -> {
                if (src != null) return Result.ERR;
                if (dst == null) return Result.ERR;
                if (dst.readOnly) return Result.ERR;

                dst.type = RegType.STR;
                if (getPlayer() == null) dst.data = "null";
                else dst.data = getPlayer().getDisplayName();
                return Result.OK;
            }));

            funcList.replace("nya", ((dst, src) -> {
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

        public abstract TileEntityTrackSideSniffer getSniffer();
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
    public void setDir(ForgeDirection dir) {
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

    public ForgeDirection direction;

    public static final boolean NSASM_IDLE = false;
    public static final boolean NSASM_DONE = true;
    public boolean nsasmState = NSASM_IDLE;
    public String nsasmCode = "";

    public boolean enable = false;
    public int keep = 20;

    @Override
    public void fromNBT(NBTTagCompound tagCompound) {
        direction = ForgeDirection.getOrientation(
                tagCompound.getInteger("direction")
        );

        nsasmState = tagCompound.getBoolean("nsasmState");
        nsasmCode = tagCompound.getString("nsasmCode");
        enable = tagCompound.getBoolean("enable");
        keep = tagCompound.getInteger("keep");
        super.fromNBT(tagCompound);
    }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
        if (direction == null) direction = ForgeDirection.UNKNOWN;
        tagCompound.setInteger("direction", direction.ordinal());

        tagCompound.setBoolean("nsasmState", nsasmState);
        tagCompound.setString("nsasmCode", nsasmCode);
        tagCompound.setBoolean("enable", enable);
        tagCompound.setInteger("keep", keep);
        return super.toNBT(tagCompound);
    }

    public static boolean configure(World world, int x, int y, int z, EntityPlayer player) {
        if (world.getTileEntity(x, y, z) == null) return false;
        if (world.getTileEntity(x, y, z) instanceof TileEntityTrackSideSniffer) {
            TileEntityTrackSideSniffer sniffer = (TileEntityTrackSideSniffer) world.getTileEntity(x, y, z);

            ItemStack stack = player.getCurrentEquippedItem();
            if (stack != null) {
                NBTTagList list = Util.getTagListFromNGT(stack);
                if (list == null) return false;

                if (!world.isRemote) {
                    String code = NSASM.getCodeString(list);

                    sniffer.nsasmState = TileEntityTrackSideSniffer.NSASM_IDLE;
                    sniffer.nsasmCode = code;

                    player.addChatComponentMessage(new ChatComponentTranslation("info.sniffer.set"));
                }

                return true;
            }
        }

        return false;
    }

    public static void tick(World world, int x, int y, int z) {
        if (world.isRemote) return;
        if (world.getTileEntity(x, y, z) == null) return;
        if (world.getTileEntity(x, y, z) instanceof TileEntityTrackSideSniffer) {
            TileEntityTrackSideSniffer sniffer = (TileEntityTrackSideSniffer) world.getTileEntity(x, y, z);

            boolean hasCart = ITrackSide.hasMinecart(sniffer, sniffer.direction);
            boolean hasPowered = ITrackSide.hasPowered(sniffer);
            if (hasCart && sniffer.nsasmState == TileEntityTrackSideSniffer.NSASM_IDLE) {
                sniffer.nsasmState = TileEntityTrackSideSniffer.NSASM_DONE;

                EntityMinecart cart = ITrackSide.getMinecart(sniffer, sniffer.direction);
                EntityPlayer player;
                if (!(cart.riddenByEntity instanceof EntityPlayer))
                    player = null;
                else player = (EntityPlayer) cart.riddenByEntity;

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
                    public TileEntityTrackSideSniffer getSniffer() {
                        return sniffer;
                    }

                    @Override
                    public EntityMinecart getCart() {
                        return cart;
                    }
                }.run();

            }

            if (!hasCart && sniffer.nsasmState == TileEntityRailSniffer.NSASM_DONE) {
                sniffer.nsasmState = TileEntityRailSniffer.NSASM_IDLE;
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
