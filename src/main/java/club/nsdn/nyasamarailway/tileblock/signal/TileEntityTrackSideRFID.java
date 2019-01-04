package club.nsdn.nyasamarailway.tileblock.signal;

import club.nsdn.nyasamarailway.entity.*;
import club.nsdn.nyasamarailway.util.RailRFIDCore;
import club.nsdn.nyasamatelecom.api.util.NSASM;
import club.nsdn.nyasamatelecom.api.util.Util;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by drzzm32 on 2019.1.4.
 */
public class TileEntityTrackSideRFID extends TileEntityRailRFID implements ITrackSide {

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

    public ForgeDirection direction;

    @Override
    public void fromNBT(NBTTagCompound tagCompound) {
        direction = ForgeDirection.getOrientation(
                tagCompound.getInteger("direction")
        );
        super.fromNBT(tagCompound);
    }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
        if (direction == null) direction = ForgeDirection.UNKNOWN;
        tagCompound.setInteger("direction", direction.ordinal());
        return super.toNBT(tagCompound);
    }

    public static boolean configure(World world, int x, int y, int z, EntityPlayer player) {
        if (world.getTileEntity(x, y, z) == null) return false;
        if (world.getTileEntity(x, y, z) instanceof TileEntityTrackSideRFID) {
            TileEntityTrackSideRFID rfid = (TileEntityTrackSideRFID) world.getTileEntity(x, y, z);

            ItemStack stack = player.getCurrentEquippedItem();
            if (stack != null) {
                NBTTagList list = Util.getTagListFromNGT(stack);
                if (list == null) return false;

                if (!world.isRemote) {
                    String[][] code = NSASM.getCode(list);
                    new RailRFIDCore(code) {
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
                        public TileEntityRailRFID getRFID() {
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
        if (world.getTileEntity(x, y, z) instanceof TileEntityTrackSideRFID) {
            TileEntityTrackSideRFID rfid = (TileEntityTrackSideRFID) world.getTileEntity(x, y, z);

            int meta = world.getBlockMetadata(x, y, z);
            boolean hasPowered = ITrackSide.hasPowered(rfid);

            if (rfid.getSender() != null) {
                if (!hasPowered && rfid.senderIsPowered()) {
                    ITrackSide.setPowered(rfid, true);
                } else if (hasPowered && !rfid.senderIsPowered()) {
                    ITrackSide.setPowered(rfid, false);
                }
            }

            EntityMinecart cart = ITrackSide.getMinecart(rfid, rfid.direction);

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

            world.scheduleBlockUpdate(x, y, z, rfid.blockType, 1);
        }

    }

}
