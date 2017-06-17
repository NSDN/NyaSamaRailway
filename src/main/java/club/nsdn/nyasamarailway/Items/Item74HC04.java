package club.nsdn.nyasamarailway.Items;

import club.nsdn.nyasamarailway.Blocks.*;
import club.nsdn.nyasamarailway.TileEntities.Rail.*;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

import java.util.LinkedHashMap;
import java.util.UUID;

/**
 * Created by drzzm32 on 2016.6.7.
 */
public class Item74HC04 extends ItemToolBase {

    public static LinkedHashMap<UUID, TileEntityRailTransceiver> tmpRails;

    public Item74HC04() {
        super(ToolMaterial.IRON);
        setUnlocalizedName("Item74HC04");
        setTexName("74hc04");

        tmpRails = new LinkedHashMap<UUID, TileEntityRailTransceiver>();
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        player.setItemInUse(itemStack, this.getMaxItemUseDuration(itemStack));
        return itemStack;
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        Block block = world.getBlock(x, y, z);

        if (block == null)
            return false;

        if (player.isSneaking()) {
            if (!world.isRemote) {
                UUID uuid = player.getUniqueID();
                TileEntity tileEntity = world.getTileEntity(x, y, z);
                if (tileEntity instanceof TileEntityRailTransceiver) {
                    TileEntityRailTransceiver railSender = (TileEntityRailTransceiver) tileEntity;
                    if (tmpRails.containsKey(uuid)) {
                        tmpRails.remove(uuid);
                        player.addChatComponentMessage(new ChatComponentTranslation("info.blocking.abort"));
                    } else {
                        tmpRails.put(uuid, railSender);
                        player.addChatComponentMessage(new ChatComponentTranslation("info.blocking.begin"));
                    }
                } else if (tileEntity instanceof TileEntityRailReceiver) {
                    TileEntityRailReceiver railReceiver = (TileEntityRailReceiver) tileEntity;
                    if (tmpRails.containsKey(uuid)) {
                        if (railReceiver.getSenderRail() == null) {
                            if (tmpRails.get(uuid).getTransceiverRail() == null) {
                                player.addChatComponentMessage(new ChatComponentTranslation("info.blocking.error"));
                            } else {
                                railReceiver.setSenderRail(tmpRails.get(uuid));
                                player.addChatComponentMessage(new ChatComponentTranslation("info.blocking.connected"));
                            }
                            tmpRails.remove(uuid);
                        } else {
                            if (railReceiver.getSenderRail() == tmpRails.get(uuid)) {
                                railReceiver.setSenderRail(null);
                                tmpRails.remove(uuid);
                                player.addChatComponentMessage(new ChatComponentTranslation("info.blocking.disconnected"));
                            }
                        }
                    } else {
                        player.addChatComponentMessage(new ChatComponentTranslation("info.blocking.error"));
                    }
                }
            }
        } else {
            if (block instanceof IRailDirectional) {
                if (((IRailDirectional) block).isForward()) {
                    if (block instanceof BlockRailReception) world.setBlock(x, y, z, BlockLoader.blockRailReceptionAnti);
                    if (block instanceof BlockRailProtectHead) world.setBlock(x, y, z, BlockLoader.blockRailProtectHeadAnti);
                    if ((block instanceof BlockRailDirectional)) world.setBlock(x, y, z, BlockLoader.blockRailDirectionalAnti);
                    if (block instanceof RailMonoMagnetReception) world.setBlock(x, y, z, BlockLoader.railMonoMagnetReceptionAnti);
                    if ((block instanceof RailMonoMagnetDirectional)) world.setBlock(x, y, z, BlockLoader.railMonoMagnetDirectionalAnti);
                } else {
                    if (block instanceof BlockRailReceptionAnti) world.setBlock(x, y, z, BlockLoader.blockRailReception);
                    if (block instanceof BlockRailProtectHeadAnti) world.setBlock(x, y, z, BlockLoader.blockRailProtectHead);
                    if ((block instanceof BlockRailDirectionalAnti)) world.setBlock(x, y, z, BlockLoader.blockRailDirectional);
                    if (block instanceof RailMonoMagnetReceptionAnti) world.setBlock(x, y, z, BlockLoader.railMonoMagnetReception);
                    if ((block instanceof RailMonoMagnetDirectionalAnti)) world.setBlock(x, y, z, BlockLoader.railMonoMagnetDirectional);
                }
                return !world.isRemote;
            } else if (block instanceof BlockRailDetectorBase) {
                int nowDelay = ((BlockRailDetectorBase) block).getDelaySecond();
                if (block instanceof BlockRailStoneSleeperDetector) {
                    switch (nowDelay) {
                        case 0:
                            world.setBlock(x, y, z, BlockLoader.blockRailStoneSleeperDetector5s);
                            break;
                        case 5:
                            world.setBlock(x, y, z, BlockLoader.blockRailStoneSleeperDetector15s);
                            break;
                        case 15:
                            world.setBlock(x, y, z, BlockLoader.blockRailStoneSleeperDetector30s);
                            break;
                        case 30:
                            world.setBlock(x, y, z, BlockLoader.blockRailStoneSleeperDetector);
                            break;
                    }
                } else if (block instanceof BlockRailNoSleeperDetector) {
                    switch (nowDelay) {
                        case 0:
                            world.setBlock(x, y, z, BlockLoader.blockRailNoSleeperDetector5s);
                            break;
                        case 5:
                            world.setBlock(x, y, z, BlockLoader.blockRailNoSleeperDetector15s);
                            break;
                        case 15:
                            world.setBlock(x, y, z, BlockLoader.blockRailNoSleeperDetector30s);
                            break;
                        case 30:
                            world.setBlock(x, y, z, BlockLoader.blockRailNoSleeperDetector);
                            break;
                    }
                }
                nowDelay = ((BlockRailDetectorBase) world.getBlock(x, y, z)).getDelaySecond();
                if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.74HC04.delay", nowDelay));
                return !world.isRemote;
            } else if (block instanceof RailMonoMagnetDetector) {
                int nowDelay = ((RailMonoMagnetDetector) block).getDelaySecond();
                switch (nowDelay) {
                    case 0:
                        world.setBlock(x, y, z, BlockLoader.railMonoMagnetDetector5s);
                        break;
                    case 5:
                        world.setBlock(x, y, z, BlockLoader.railMonoMagnetDetector15s);
                        break;
                    case 15:
                        world.setBlock(x, y, z, BlockLoader.railMonoMagnetDetector30s);
                        break;
                    case 30:
                        world.setBlock(x, y, z, BlockLoader.railMonoMagnetDetector);
                        break;
                }
                nowDelay = ((RailMonoMagnetDetector) world.getBlock(x, y, z)).getDelaySecond();
                if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.74HC04.delay", nowDelay));
                return !world.isRemote;
            }
        }

        return false;
    }
}
