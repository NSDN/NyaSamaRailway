package club.nsdn.nyasamarailway.tileblock.rail.mono;

import club.nsdn.nyasamarailway.entity.*;
import club.nsdn.nyasamarailway.tileblock.signal.TileEntityRailRFID;
import club.nsdn.nyasamarailway.util.RailRFIDCore;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityReceiver;
import club.nsdn.nyasamatelecom.api.util.NSASM;
import club.nsdn.nyasamatelecom.api.util.Util;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by drzzm32 on 2017.12.28.
 */
public class RailMonoMagnetRFID extends RailMonoMagnetPowered {

    public static class RailRFID extends TileEntityRailRFID implements RailMonoMagnetPowerable {

        @Override
        @SideOnly(Side.CLIENT)
        public AxisAlignedBB getRenderBoundingBox()
        {
            return AxisAlignedBB
                    .getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1)
                    .expand(4, 4, 4);
        }

    }

    @Override
    public TileEntity createNewTileEntity(World world, int i) {
        return new RailRFID();
    }

    public RailMonoMagnetRFID() {
        super("RailMonoMagnetRFID", "rail_mono_magnet_rfid");
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        world.scheduleBlockUpdate(x, y, z, this, 1);
    }

    @Override
    public void onBlockPreDestroy(World world, int x, int y, int z, int meta) {
        super.onBlockPreDestroy(world, x, y, z, meta);
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity != null) {
            if (tileEntity instanceof TileEntityReceiver) {
                ((TileEntityReceiver) tileEntity).onDestroy();
            }
        }
    }

    @Override
    public int tickRate(World world) {
        return 20;
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (!world.isRemote) {
            if (world.getTileEntity(x, y, z) instanceof RailRFID) {
                RailRFID rfid = (RailRFID) world.getTileEntity(x, y, z);
                int meta = world.getBlockMetadata(x, y, z);

                if (rfid.getSender() != null) {
                    if (!isRailPowered(world, x, y, z) && rfid.senderIsPowered()) {
                        world.setBlockMetadataWithNotify(x, y, z, meta | 0x8, 3);
                        world.notifyBlocksOfNeighborChange(x, y, z, this);
                        world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
                    } else if (isRailPowered(world, x, y, z) && !rfid.senderIsPowered()) {
                        world.setBlockMetadataWithNotify(x, y, z, meta & 0x7, 3);
                        world.notifyBlocksOfNeighborChange(x, y, z, this);
                        world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
                    }
                }
            }

            world.scheduleBlockUpdate(x, y, z, this, 1);
        }
        super.updateTick(world, x, y, z, random);
    }

    @Override
    public void onMinecartPass(World world, EntityMinecart cart, int x, int y, int z) {
        if (world.getTileEntity(x, y, z) instanceof RailRFID) {
            RailRFID rfid = (RailRFID) world.getTileEntity(x, y, z);

            if (cart instanceof LocoBase) {
                LocoBase loco = (LocoBase) cart;

                if (isRailPowered(world, x, y, z) || rfid.senderIsPowered()) {
                    loco.setEnginePower(rfid.P);
                    loco.setEngineBrake(rfid.R);
                }
            } else if (cart instanceof IMotorCart) {
                IMotorCart motorCart = (IMotorCart) cart;

                if (isRailPowered(world, x, y, z) || rfid.senderIsPowered()) {
                    motorCart.setMotorPower(rfid.P);
                    motorCart.setMotorBrake(rfid.R);
                    motorCart.setMotorState(rfid.state);
                }
            }

            if (cart instanceof ILimitVelCart) {
                ILimitVelCart limitVelCart = (ILimitVelCart) cart;

                if (isRailPowered(world, x, y, z) || rfid.senderIsPowered()) {
                    limitVelCart.setMaxVelocity(rfid.vel);
                }
            }

            if (cart instanceof IHighSpeedCart) {
                IHighSpeedCart highSpeedCart = (IHighSpeedCart) cart;

                if (isRailPowered(world, x, y, z) || rfid.senderIsPowered()) {
                    highSpeedCart.setHighSpeedMode(rfid.high);
                }
            }

            if (cart instanceof IExtendedInfoCart) {
                IExtendedInfoCart infoCart = (IExtendedInfoCart) cart;

                if (isRailPowered(world, x, y, z) || rfid.senderIsPowered()) {
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

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (world.getTileEntity(x, y, z) == null) return false;
        if (world.getTileEntity(x, y, z) instanceof RailRFID) {
            RailRFID rfid = (RailRFID) world.getTileEntity(x, y, z);

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

}
