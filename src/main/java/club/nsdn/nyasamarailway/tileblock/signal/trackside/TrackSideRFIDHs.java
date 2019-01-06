package club.nsdn.nyasamarailway.tileblock.signal.trackside;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.entity.*;
import club.nsdn.nyasamarailway.network.NetworkWrapper;
import club.nsdn.nyasamarailway.tileblock.signal.ITrackSide;
import club.nsdn.nyasamarailway.tileblock.signal.TileEntityTrackSideRFID;
import club.nsdn.nyasamatelecom.api.util.NSASM;
import club.nsdn.nyasamatelecom.api.util.Util;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.LinkedHashMap;
import java.util.Random;

/**
 * Created by drzzm32 on 2019.1.6.
 */
public class TrackSideRFIDHs extends AbsTrackSide {

    public static class RFID extends TileEntityTrackSideRFID {

        public static abstract class RFIDCore extends TileEntityTrackSideRFID.RFIDCore<RFID> {

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
            public abstract RFID getRFID();

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
            return invert;
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

        public static boolean configure(World world, int x, int y, int z, EntityPlayer player) {
            if (world.getTileEntity(x, y, z) == null) return false;
            if (world.getTileEntity(x, y, z) instanceof RFID) {
                RFID rfid = (RFID) world.getTileEntity(x, y, z);

                ItemStack stack = player.getCurrentEquippedItem();
                if (stack != null) {
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
                            public RFID getRFID() {
                                return rfid;
                            }
                        }.run();
                    }

                    return true;
                }
            }

            return false;
        }

        public static void tick(World world, int x, int y, int z) {
            if (world.isRemote) return;
            if (world.getTileEntity(x, y, z) instanceof RFID) {
                RFID rfid = (RFID) world.getTileEntity(x, y, z);

                boolean hasPowered = ITrackSide.hasPowered(rfid);

                if (rfid.getSender() != null) {
                    if (!hasPowered && rfid.senderIsPowered()) {
                        ITrackSide.setPowered(rfid, true);
                    } else if (hasPowered && !rfid.senderIsPowered()) {
                        ITrackSide.setPowered(rfid, false);
                    }
                }

                ForgeDirection offset = rfid.isInvert() ? rfid.direction.getOpposite() : rfid.direction;
                EntityMinecart cart = ITrackSide.getMinecart(rfid, rfid.direction, offset);

                if (cart instanceof LocoBase) {
                    LocoBase loco = (LocoBase) cart;

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

                if (rfid.hasChanged()) {
                    rfid.updateChanged();
                    world.markBlockForUpdate(x, y, z);
                }

                world.scheduleBlockUpdate(x, y, z, rfid.getBlockType(), 1);
            }

        }

    }

    public TrackSideRFIDHs() {
        super("TrackSideRFIDHs", "track_side_rfid_hs");
        setCreativeTab(CreativeTabLoader.tabNSTest);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new RFID();
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        RFID.tick(world, x, y, z);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        return RFID.configure(world, x, y, z, player);
    }

}
