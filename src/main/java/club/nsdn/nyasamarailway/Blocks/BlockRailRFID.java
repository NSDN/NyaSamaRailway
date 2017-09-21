package club.nsdn.nyasamarailway.Blocks;

import club.nsdn.nyasamarailway.Entity.LocoBase;
import club.nsdn.nyasamarailway.TileEntities.Signals.TileEntityRailPassiveReceiver;
import club.nsdn.nyasamarailway.TileEntities.Signals.TileEntityRailReceiver;
import club.nsdn.nyasamarailway.Util.NSASM;
import club.nsdn.nyasamarailway.Util.Util;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

import java.util.LinkedHashMap;
import java.util.Random;

/**
 * Created by drzzm32 on 2016.9.20.
 */
public class BlockRailRFID extends BlockRailPoweredBase implements ITileEntityProvider {

    public static class TileEntityRailRFID extends TileEntityRailPassiveReceiver {

        public int P = 0;
        public int R = 10;

        @Override
        public void fromNBT(NBTTagCompound tagCompound) {
            P = tagCompound.getInteger("P");
            R = tagCompound.getInteger("R");
            super.fromNBT(tagCompound);
        }

        @Override
        public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
            tagCompound.setInteger("P", P);
            tagCompound.setInteger("R", R);
            return super.toNBT(tagCompound);
        }

    }

    public TileEntity createNewTileEntity(World world, int i) {
        return new TileEntityRailRFID();
    }

    public BlockRailRFID() {
        super("BlockRailRFID");
        setTextureName("rail_rfid");
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
            if (tileEntity instanceof TileEntityRailReceiver) {
                ((TileEntityRailReceiver) tileEntity).onDestroy();
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
            if (world.getTileEntity(x, y, z) instanceof TileEntityRailRFID) {
                TileEntityRailRFID rfid = (TileEntityRailRFID) world.getTileEntity(x, y, z);
                int meta = rfid.getBlockMetadata();

                if (!isRailPowered(world, x, y, z) && rfid.senderIsPowered()) {
                    world.setBlockMetadataWithNotify(x, y, z, meta | 0x8, 3);
                    world.notifyBlocksOfNeighborChange(x, y, z, this);
                    world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
                } else if (!world.isBlockIndirectlyGettingPowered(x, y, z) && !rfid.senderIsPowered()) {
                    world.setBlockMetadataWithNotify(x, y, z, meta & 0x7, 3);
                    world.notifyBlocksOfNeighborChange(x, y, z, this);
                    world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
                }
            }

            world.scheduleBlockUpdate(x, y, z, this, 1);
        }
    }

    @Override
    public void onMinecartPass(World world, EntityMinecart cart, int x, int y, int z) {
        if (world.getTileEntity(x, y, z) instanceof TileEntityRailRFID) {
            TileEntityRailRFID rfid = (TileEntityRailRFID) world.getTileEntity(x, y, z);

            if (cart instanceof LocoBase) {
                LocoBase loco = (LocoBase) cart;

                if (isRailPowered(world, x, y, z) || rfid.senderIsPowered()) {
                    loco.P = rfid.P;
                    loco.R = rfid.R;
                }
            }

        }
    }

    public void setPower(TileEntityRailRFID rfid, EntityPlayer player, int value) {
        rfid.P = value > 20 ? 20 : (value < 0 ? 0 : value);
        player.addChatComponentMessage(
                new ChatComponentTranslation("info.rfid.pwr", value));
    }

    public void setResistance(TileEntityRailRFID rfid, EntityPlayer player, int value) {
        rfid.R = value > 10 ? 10 : (value < 1 ? 1 : value);
        player.addChatComponentMessage(
                new ChatComponentTranslation("info.rfid.res", 10 - value));
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (world.getTileEntity(x, y, z) == null) return false;
        if (world.getTileEntity(x, y, z) instanceof TileEntityRailRFID) {
            TileEntityRailRFID rfid = (TileEntityRailRFID) world.getTileEntity(x, y, z);
            if (!world.isRemote) {
                ItemStack stack = player.getCurrentEquippedItem();
                if (stack != null) {

                    NBTTagList list = Util.getTagListFromBook(stack);
                    String[][] code = NSASM.getCode(list);
                    new NSASM(code) {
                        @Override
                        public void loadFunc(LinkedHashMap<String, Operator> funcList) {
                            funcList.put("pwr", ((dst, src) -> {
                                if (src != null) return Result.ERR;
                                if (dst == null) return Result.ERR;

                                if (dst.type == RegType.INT) {
                                    setPower(rfid, player, (int) dst.data);
                                    return Result.OK;
                                }
                                return Result.ERR;
                            }));
                            funcList.put("res", ((dst, src) -> {
                                if (src != null) return Result.ERR;
                                if (dst == null) return Result.ERR;

                                if (dst.type == RegType.INT) {
                                    setResistance(rfid, player, (int) dst.data);
                                    return Result.OK;
                                }
                                return Result.ERR;
                            }));

                            funcList.replace("prt", ((dst, src) -> {
                                if (src != null) return Result.ERR;
                                if (dst == null) return Result.ERR;
                                if (dst.type == RegType.STR) {
                                    player.addChatComponentMessage(new ChatComponentText(((String) dst.data).substring(dst.strPtr)));
                                } else player.addChatComponentMessage(new ChatComponentText(dst.data.toString()));
                                return Result.OK;
                            }));
                        }
                    }.run();

                }

            }
            return true;
        }

        return false;
    }
}
