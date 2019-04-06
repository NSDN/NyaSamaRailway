package club.nsdn.nyasamarailway.tileblock.signal.trackside;

import club.nsdn.nyasamarailway.api.cart.*;
import club.nsdn.nyasamarailway.api.signal.ITrackSide;
import club.nsdn.nyasamarailway.api.signal.TileEntityTrackSideRFID;
import club.nsdn.nyasamatelecom.api.util.NSASM;
import club.nsdn.nyasamatelecom.api.util.Util;
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
import java.util.LinkedList;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class TrackSideRFIDHs extends AbsTrackSide {

    public static class TileEntityTrackSideRFIDHs extends TileEntityTrackSideRFID {

        public TileEntityTrackSideRFIDHs() {
            setInfo(13, 0.25, 0.3125, 1);
        }

        public static abstract class RFIDCore extends TileEntityTrackSideRFID.RFIDCore<TileEntityTrackSideRFIDHs> {

            public RFIDCore(String[][] code) {
                super(code);
            }

            @Override
            public void loadFunc(LinkedHashMap<String, Operator> funcList) {
                super.loadFunc(funcList);

                funcList.put("inv", ((dst, src) -> {
                    if (src != null) return Result.ERR;
                    if (dst != null) return Result.ERR;

                    getRFID().invert = !getRFID().invert;

                    return Result.OK;
                }));
            }

            @Override
            public abstract TileEntityTrackSideRFIDHs getRFID();

        }

        protected boolean prevInv;

        @Override
        protected boolean hasChanged() {
            return super.hasChanged() || prevInv != isInvert();
        }

        @Override
        protected void updateChanged() {
            super.updateChanged();
            prevInv = isInvert();
        }

        @Override
        public boolean hasInvert() {
            return true;
        }

        @Override
        public boolean isInvert() {
            return ((META & 0x4) != 0) ^ invert;
        }

        @Override
        public void flipInvert() {
            invert = !invert;
        }

        @Override
        public boolean getInvertForRender() {
            return invert;
        }

        @Override
        public void setInvert(boolean invert) {
            this.invert = invert;
        }

        public boolean invert = false;

        @Override
        public void fromNBT(NBTTagCompound tagCompound) {
            invert = tagCompound.getBoolean("invert");
            super.fromNBT(tagCompound);
        }

        @Override
        public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
            tagCompound.setBoolean("invert", invert);
            return super.toNBT(tagCompound);
        }

        public static boolean configure(World world, BlockPos pos, EntityPlayer player) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity == null) return false;
            if (tileEntity instanceof TileEntityTrackSideRFIDHs) {
                TileEntityTrackSideRFIDHs rfid = (TileEntityTrackSideRFIDHs) tileEntity;

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
                            public TileEntityTrackSideRFIDHs getRFID() {
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
        public void tick(World world, BlockPos pos) {
            if (world.isRemote) return;
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity == null) return;
            if (tileEntity instanceof TileEntityTrackSideRFIDHs) {
                TileEntityTrackSideRFIDHs rfid = (TileEntityTrackSideRFIDHs) tileEntity;

                boolean hasPowered = ITrackSide.hasPowered(rfid);

                if (rfid.getSender() != null) {
                    if (!hasPowered && rfid.senderIsPowered()) {
                        ITrackSide.setPowered(rfid, true);
                    } else if (hasPowered && !rfid.senderIsPowered()) {
                        ITrackSide.setPowered(rfid, false);
                    }
                }

                EnumFacing offset = rfid.isInvert() ? rfid.direction.getOpposite() : rfid.direction;
                LinkedList<EntityMinecart> carts = ITrackSide.getMinecarts(rfid, rfid.direction, offset);

                if (carts != null) {
                    for (EntityMinecart cart : carts) {
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
                }

                if (rfid.hasChanged()) {
                    rfid.updateChanged();
                    rfid.refresh();
                }
            }

        }

    }

    public TrackSideRFIDHs() {
        super("TrackSideRFIDHs", "track_side_rfid_hs");
    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityTrackSideRFIDHs();
    }

    @Override
    public boolean onConfigure(World world, BlockPos pos, EntityPlayer player) {
        return TileEntityTrackSideRFIDHs.configure(world, pos, player);
    }

}
