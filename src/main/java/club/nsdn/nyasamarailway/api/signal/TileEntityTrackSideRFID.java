package club.nsdn.nyasamarailway.api.signal;

import club.nsdn.nyasamarailway.api.cart.*;
import club.nsdn.nyasamarailway.network.NetworkWrapper;
import club.nsdn.nyasamatelecom.api.device.SignalBox;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityReceiver;
import club.nsdn.nyasamatelecom.api.util.NSASM;
import club.nsdn.nyasamatelecom.api.util.Util;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import java.util.LinkedHashMap;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class TileEntityTrackSideRFID extends TileEntityReceiver implements ITrackSide, ITrackSidePowerable {

    public static void registerController() {
        SignalBox.TileEntitySignalBox.CONTROL_FUNCS.add((tileEntity, state) -> {
            if (tileEntity instanceof TileEntityTrackSideRFID) {
                ((TileEntityTrackSideRFID) tileEntity).setPowered(state);
            }
        });
    }

    public static abstract class RFIDCore<T extends TileEntityTrackSideRFID> extends NSASM {

        public RFIDCore(String[][] code) {
            super(code);
        }

        @Override
        public SimpleNetworkWrapper getWrapper() {
            return NetworkWrapper.instance;
        }

        private void prt(String format, Object... args) {
            if (getPlayer() != null)
                Util.say(getPlayer(), format, args);
        }

        public void setPower(TileEntityTrackSideRFID rfid, EntityPlayer player, int value) {
            if (rfid == null) return;
            rfid.P = value > 20 ? 20 : (value < 0 ? 0 : value);
            prt("info.rfid.pwr", rfid.P);
        }

        public void setBrake(TileEntityTrackSideRFID rfid, EntityPlayer player, int value) {
            if (rfid == null) return;
            value = 10 - value;
            rfid.R = value > 10 ? 10 : (value < 1 ? 1 : value);
            prt("info.rfid.brk", 10 - rfid.R);
        }

        public void setState(TileEntityTrackSideRFID rfid, EntityPlayer player, int value) {
            if (rfid == null) return;
            rfid.state = value > 0;
            prt("info.rfid.ste", rfid.state ? 1 : 0);
        }

        public void setHighSpeed(TileEntityTrackSideRFID rfid, EntityPlayer player, int value) {
            if (rfid == null) return;
            rfid.high = value > 0;
            prt("info.rfid.high", rfid.high ? 1 : 0);
        }

        public void setVelocity(TileEntityTrackSideRFID rfid, EntityPlayer player, double value) {
            if (rfid == null) return;
            rfid.vel = value;
            prt("info.rfid.vel", rfid.vel);
        }

        public void setMBlk(TileEntityTrackSideRFID rfid, EntityPlayer player, int value) {
            if (rfid == null) return;
            rfid.mblk = value > 0;
            prt("info.rfid.mblk", rfid.mblk ? 1 : 0);
        }

        @Override
        public void loadFunc(LinkedHashMap<String, Operator> funcList) {
            funcList.put("pwr", ((dst, src) -> {
                if (src != null) return Result.ERR;
                if (dst == null) return Result.ERR;

                if (dst.type == RegType.INT) {
                    setPower(getRFID(), getPlayer(), (int) dst.data);
                    return Result.OK;
                }
                return Result.ERR;
            }));
            funcList.put("brk", ((dst, src) -> {
                if (src != null) return Result.ERR;
                if (dst == null) return Result.ERR;

                if (dst.type == RegType.INT) {
                    setBrake(getRFID(), getPlayer(), (int) dst.data);
                    return Result.OK;
                }
                return Result.ERR;
            }));
            funcList.put("ste", ((dst, src) -> {
                if (src != null) return Result.ERR;
                if (dst == null) return Result.ERR;

                if (dst.type == RegType.INT) {
                    setState(getRFID(), getPlayer(), (int) dst.data);
                    return Result.OK;
                }
                return Result.ERR;
            }));
            funcList.put("vel", ((dst, src) -> {
                if (src != null) return Result.ERR;
                if (dst == null) return Result.ERR;

                if (dst.type == RegType.FLOAT || dst.type == RegType.INT) {
                    setVelocity(getRFID(), getPlayer(), Double.valueOf(dst.data.toString()));
                    return Result.OK;
                }
                return Result.ERR;
            }));

            funcList.put("high", ((dst, src) -> {
                if (src != null) return Result.ERR;
                if (dst == null) return Result.ERR;

                if (dst.type == RegType.INT) {
                    setHighSpeed(getRFID(), getPlayer(), (int) dst.data);
                    return Result.OK;
                }
                return Result.ERR;
            }));
            funcList.put("mblk", ((dst, src) -> {
                if (src != null) return Result.ERR;
                if (dst == null) return Result.ERR;

                if (dst.type == RegType.INT) {
                    setMBlk(getRFID(), getPlayer(), (int) dst.data);
                    return Result.OK;
                }
                return Result.ERR;
            }));

            funcList.put("side", ((dst, src) -> {
                if (src != null) return Result.ERR;
                if (dst == null) return Result.ERR;

                if (dst.type == RegType.STR) {
                    if (getRFID() != null) {
                        getRFID().cartSide = dst.data.toString();
                        prt("info.rfid.side", dst.data.toString());
                    }
                    return Result.OK;
                }
                return Result.ERR;
            }));
            funcList.put("jets", ((dst, src) -> {
                if (src != null) return Result.ERR;
                if (dst == null) return Result.ERR;

                if (dst.type == RegType.STR) {
                    if (getRFID() != null) {
                        getRFID().cartStr = dst.data.toString();
                        prt("info.rfid.str", dst.data.toString());
                    }
                    return Result.OK;
                }
                return Result.ERR;
            }));
            funcList.put("jet", ((dst, src) -> {
                if (src != null) return Result.ERR;
                if (dst == null) return Result.ERR;

                if (dst.type == RegType.STR) {
                    if (getRFID() != null) {
                        getRFID().cartJet = dst.data.toString();
                        prt("info.rfid.jet", dst.data.toString());
                    }
                    return Result.OK;
                }
                return Result.ERR;
            }));

        }

        public abstract T getRFID();

    }

    @Override
    protected void setBoundsByXYZ(double x1, double y1, double z1, double x2, double y2, double z2) {
        switch (META & 0x7) {
            case 0:
                setBlockBounds(x1, y1, z1, x2, y2, z2);
                break;
            case 1:
                setBlockBounds(1.0 - z2, y1, x1, 1.0 - z1, y2, x2);
                break;
            case 2:
                setBlockBounds(1.0 - x2, y1, 1.0 - z2, 1.0 - x1, y2, 1.0 - z1);
                break;
            case 3:
                setBlockBounds(z1, y1, 1.0 - x2, z2, y2, 1.0 - x1);
                break;

            case 4:
                setBlockBounds(x1, 1.0 - y2, z1, x2, 1.0 - y1, z2);
                break;
            case 5:
                setBlockBounds(1.0 - z2, 1.0 - y2, x1, 1.0 - z1, 1.0 - y1, x2);
                break;
            case 6:
                setBlockBounds(1.0 - x2, 1.0 - y2, 1.0 - z2, 1.0 - x1, 1.0 - y1, 1.0 - z1);
                break;
            case 7:
                setBlockBounds(z1, 1.0 - y2, 1.0 - x2, z2, 1.0 - y1, 1.0 - x1);
                break;
        }
    }

    @Override
    public boolean getSGNState() {
        return ITrackSide.hasPowered(this);
    }

    @Override
    public boolean getTXDState() {
        return false;
    }

    @Override
    public boolean getRXDState() {
        return getSender() != null;
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

    public int P = 0;
    public int R = 10;
    public double vel = 0;
    public boolean high = false;
    public boolean state = false;
    public boolean mblk = false;

    public String cartSide = "null", cartStr = "null", cartJet = "null";

    @Override
    public void fromNBT(NBTTagCompound tagCompound) {
        super.fromNBT(tagCompound);

        direction = EnumFacing.byName(
                tagCompound.getString("direction")
        );

        P = tagCompound.getInteger("P");
        R = tagCompound.getInteger("R");
        vel = tagCompound.getDouble("vel");
        high = tagCompound.getBoolean("high");
        state = tagCompound.getBoolean("state");
        mblk = tagCompound.getBoolean("mblk");

        cartSide = tagCompound.getString("cartSide");
        cartStr = tagCompound.getString("cartStr");
        cartJet = tagCompound.getString("cartJet");
    }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
        if (direction == null) direction = EnumFacing.DOWN;
        tagCompound.setString("direction", direction.getName());

        tagCompound.setInteger("P", P);
        tagCompound.setInteger("R", R);
        tagCompound.setDouble("vel", vel);
        tagCompound.setBoolean("high", high);
        tagCompound.setBoolean("state", state);
        tagCompound.setBoolean("mblk", mblk);

        tagCompound.setString("cartSide", cartSide);
        tagCompound.setString("cartStr", cartStr);
        tagCompound.setString("cartJet", cartJet);

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
        if (tileEntity instanceof TileEntityTrackSideRFID) {
            TileEntityTrackSideRFID rfid = (TileEntityTrackSideRFID) tileEntity;

            ItemStack stack = player.getHeldItemMainhand();
            if (!stack.isEmpty()) {
                NBTTagList list = Util.getTagListFromNGT(stack);
                if (list == null) return false;

                if (!world.isRemote) {
                    String[][] code = NSASM.getCode(list);
                    new RFIDCore(code) {
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
                        public TileEntityTrackSideRFID getRFID() {
                            return rfid;
                        }
                    }.run();
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public void update() {
        tick(world, pos);
    }

    protected static void controlCart(EntityMinecart cart, TileEntityTrackSideRFID rfid, boolean hasPowered) {
        if (cart instanceof AbsLocoBase) {
            AbsLocoBase loco = (AbsLocoBase) cart;

            if (hasPowered || rfid.senderIsPowered()) {
                loco.setEnginePower(rfid.P);
                loco.setEngineBrake(rfid.R);
            }
        } else if (cart instanceof IMotorCart) {
            IMotorCart motorCart = (IMotorCart) cart;

            if (hasPowered || rfid.senderIsPowered()) {
                motorCart.setMotorPower(rfid.P);
                motorCart.setMotorBrake(rfid.R);
                motorCart.setMotorState(rfid.state);
            }
        }

        if (cart instanceof ILimitVelCart) {
            ILimitVelCart limitVelCart = (ILimitVelCart) cart;

            if (hasPowered || rfid.senderIsPowered()) {
                limitVelCart.setMaxVelocity(rfid.vel);
            }
        }

        if (cart instanceof IHighSpeedCart) {
            IHighSpeedCart highSpeedCart = (IHighSpeedCart) cart;

            if (hasPowered || rfid.senderIsPowered()) {
                highSpeedCart.setHighSpeedMode(rfid.high);
            }
        }

        if (cart instanceof IMobileBlocking) {
            IMobileBlocking mblkCart = (IMobileBlocking) cart;

            if (hasPowered || rfid.senderIsPowered()) {
                mblkCart.setBlockingState(rfid.mblk);
            }
        }

        if (cart instanceof IExtendedInfoCart) {
            IExtendedInfoCart infoCart = (IExtendedInfoCart) cart;

            if (hasPowered || rfid.senderIsPowered()) {
                if (!rfid.cartSide.equals("null"))
                    infoCart.setExtendedInfo("side", rfid.cartSide);
                if (!rfid.cartStr.equals("null"))
                    infoCart.setExtendedInfo("str", rfid.cartStr);
                if (!rfid.cartJet.equals("null"))
                    infoCart.setExtendedInfo("jet", rfid.cartJet);
            }
        }
    }

    public void tick(World world, BlockPos pos) {
        if (world.isRemote) return;
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityTrackSideRFID) {
            TileEntityTrackSideRFID rfid = (TileEntityTrackSideRFID) tileEntity;

            boolean hasPowered = ITrackSide.hasPowered(rfid);

            if (rfid.getSender() != null) {
                if (!hasPowered && rfid.senderIsPowered()) {
                    ITrackSide.setPowered(rfid, true);
                } else if (hasPowered && !rfid.senderIsPowered()) {
                    ITrackSide.setPowered(rfid, false);
                }
            }

            EntityMinecart cart = ITrackSide.getMinecart(rfid, rfid.direction);

            controlCart(cart, rfid, hasPowered);

            if (rfid.hasChanged()) {
                rfid.updateChanged();
                rfid.refresh();
            }
        }

    }

}
