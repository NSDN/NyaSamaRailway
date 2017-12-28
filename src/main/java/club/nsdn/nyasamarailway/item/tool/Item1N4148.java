package club.nsdn.nyasamarailway.item.tool;

import club.nsdn.nyasamarailway.block.*;
import club.nsdn.nyasamarailway.block.rail.*;
import club.nsdn.nyasamarailway.block.rail.special.BlockRailReception;
import club.nsdn.nyasamarailway.block.rail.special.BlockRailReceptionAnti;
import club.nsdn.nyasamarailway.tileblock.functional.TileEntityCoinBlock;
import club.nsdn.nyasamarailway.tileblock.functional.TileEntityTicketBlockOnce;
import club.nsdn.nyasamarailway.tileblock.rail.mono.*;
import club.nsdn.nyasamarailway.tileblock.signal.core.BlockSignalBox;
import club.nsdn.nyasamarailway.tileblock.signal.core.BlockTriStateSignalBox;
import club.nsdn.nyasamarailway.tileblock.signal.block.BlockGateFront;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityTransceiver;
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
 * Created by drzzm32 on 2016.8.11.
 */
public class Item1N4148 extends ItemToolBase {

    public static LinkedHashMap<UUID, TileEntityTransceiver> tmpRails;

    public Item1N4148() {
        super(ToolMaterial.IRON);
        setUnlocalizedName("Item1N4148");
        setTexName("1n4148");

        tmpRails = new LinkedHashMap<UUID, TileEntityTransceiver>();
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

        if (block instanceof IRailDirectional) {
            if (player.isSneaking()) {
                TileEntity rail = world.getTileEntity(x, y, z);
                if (rail == null) return false;

                if (rail instanceof BlockRailReception.TileEntityRailReception) {
                    if (((BlockRailReception.TileEntityRailReception) rail).cartType.isEmpty()) {
                        ((BlockRailReception.TileEntityRailReception) rail).cartType = "loco";
                        if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.reception.loco"));
                    } else {
                        if (((BlockRailReception.TileEntityRailReception) rail).cartType.equals("loco")) {
                            ((BlockRailReception.TileEntityRailReception) rail).cartType = "";
                            if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.reception.reset"));
                        } else {
                            ((BlockRailReception.TileEntityRailReception) rail).cartType = "";
                            if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.reception.cleared"));
                        }
                    }
                    return !world.isRemote;
                } else if (rail instanceof BlockRailReceptionAnti.TileEntityRailReceptionAnti) {
                    if (((BlockRailReceptionAnti.TileEntityRailReceptionAnti) rail).cartType.isEmpty()) {
                        ((BlockRailReceptionAnti.TileEntityRailReceptionAnti) rail).cartType = "loco";
                        if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.reception.loco"));
                    } else {
                        if (((BlockRailReceptionAnti.TileEntityRailReceptionAnti) rail).cartType.equals("loco")) {
                            ((BlockRailReceptionAnti.TileEntityRailReceptionAnti) rail).cartType = "";
                            if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.reception.reset"));
                        } else {
                            ((BlockRailReceptionAnti.TileEntityRailReceptionAnti) rail).cartType = "";
                            if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.reception.cleared"));
                        }
                    }
                    return !world.isRemote;
                } else if (rail instanceof RailMonoMagnetReception.TileEntityRail) {
                    if (((RailMonoMagnetReception.TileEntityRail) rail).cartType.isEmpty()) {
                        ((RailMonoMagnetReception.TileEntityRail) rail).cartType = "loco";
                        if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.reception.loco"));
                    } else {
                        if (((RailMonoMagnetReception.TileEntityRail) rail).cartType.equals("loco")) {
                            ((RailMonoMagnetReception.TileEntityRail) rail).cartType = "";
                            if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.reception.reset"));
                        } else {
                            ((RailMonoMagnetReception.TileEntityRail) rail).cartType = "";
                            if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.reception.cleared"));
                        }
                    }
                    return !world.isRemote;
                } else if (rail instanceof RailMonoMagnetReceptionAnti.TileEntityRail) {
                    if (((RailMonoMagnetReceptionAnti.TileEntityRail) rail).cartType.isEmpty()) {
                        ((RailMonoMagnetReceptionAnti.TileEntityRail) rail).cartType = "loco";
                        if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.reception.loco"));
                    } else {
                        if (((RailMonoMagnetReceptionAnti.TileEntityRail) rail).cartType.equals("loco")) {
                            ((RailMonoMagnetReceptionAnti.TileEntityRail) rail).cartType = "";
                            if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.reception.reset"));
                        } else {
                            ((RailMonoMagnetReceptionAnti.TileEntityRail) rail).cartType = "";
                            if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.reception.cleared"));
                        }
                    }
                    return !world.isRemote;
                }
            }

            if (((IRailDirectional) block).isForward()) {
                if (block instanceof BlockRailReception) world.setBlock(x, y, z, BlockLoader.blockRailReceptionAnti);
                if (block instanceof BlockRailProtectHead) {
                    int meta = world.getBlockMetadata(x, y, z);
                    world.setBlock(x, y, z, BlockLoader.blockRailProtectHeadAnti);
                    world.setBlockMetadataWithNotify(x, y, z, meta, 1);
                }
                if ((block instanceof BlockRailDirectional)) world.setBlock(x, y, z, BlockLoader.blockRailDirectionalAnti);
                if (block instanceof RailMonoMagnetReception) world.setBlock(x, y, z, BlockLoader.railMonoMagnetReceptionAnti);
                if ((block instanceof RailMonoMagnetDirectional)) world.setBlock(x, y, z, BlockLoader.railMonoMagnetDirectionalAnti);
            } else {
                if (block instanceof BlockRailReceptionAnti) world.setBlock(x, y, z, BlockLoader.blockRailReception);
                if (block instanceof BlockRailProtectHeadAnti) {
                    int meta = world.getBlockMetadata(x, y, z);
                    world.setBlock(x, y, z, BlockLoader.blockRailProtectHead);
                    world.setBlockMetadataWithNotify(x, y, z, meta, 1);
                }
                if ((block instanceof BlockRailDirectionalAnti)) world.setBlock(x, y, z, BlockLoader.blockRailDirectional);
                if (block instanceof RailMonoMagnetReceptionAnti) world.setBlock(x, y, z, BlockLoader.railMonoMagnetReception);
                if ((block instanceof RailMonoMagnetDirectionalAnti)) world.setBlock(x, y, z, BlockLoader.railMonoMagnetDirectional);
            }
            return !world.isRemote;
        } else if (block instanceof BlockRailDetectorBase && !(block instanceof IRailNoDelay)) {
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
            if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.1N4148.delay", nowDelay));
            return !world.isRemote;
        } else if (block instanceof RailMonoMagnetDetector && !(block instanceof RailMonoMagnetBlocking)) {
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
            if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.1N4148.delay", nowDelay));
            return !world.isRemote;
        }

        TileEntity tileEntity = world.getTileEntity(x, y, z);

        if (tileEntity == null) return false;

        if (player.isSneaking()) {
            if (tileEntity instanceof TileEntityCoinBlock.CoinBlock) {
                TileEntityCoinBlock.CoinBlock coinBlock = (TileEntityCoinBlock.CoinBlock) tileEntity;
                if (coinBlock.value <= 0) coinBlock.value = 1;

                switch (coinBlock.value) {
                    case 1:
                        coinBlock.value = 5;
                        break;
                    case 5:
                        coinBlock.value = 10;
                        break;
                    case 10:
                        coinBlock.value = 20;
                        break;
                    case 20:
                        coinBlock.value = 50;
                        break;
                    case 50:
                        coinBlock.value = 1;
                        break;
                }
                if (player instanceof EntityPlayerMP) player.addChatComponentMessage(
                        new ChatComponentTranslation("info.coin.value", coinBlock.value)
                );
            } else if (tileEntity instanceof TileEntityTicketBlockOnce.TicketBlock) {
                TileEntityTicketBlockOnce.TicketBlock ticketBlock = (TileEntityTicketBlockOnce.TicketBlock) tileEntity;

                switch (ticketBlock.setOver) {
                    case 1:
                        ticketBlock.setOver = 5;
                        break;
                    case 5:
                        ticketBlock.setOver = 10;
                        break;
                    case 10:
                        ticketBlock.setOver = 20;
                        break;
                    case 20:
                        ticketBlock.setOver = 50;
                        break;
                    case 50:
                        ticketBlock.setOver = 1;
                        break;
                }
                if (player instanceof EntityPlayerMP) player.addChatComponentMessage(
                        new ChatComponentTranslation("info.ticket.over", ticketBlock.setOver)
                );
            } else if (tileEntity instanceof BlockGateFront.GateFront) {
                BlockGateFront.GateFront gateFront = (BlockGateFront.GateFront) tileEntity;

                switch (gateFront.setOver) {
                    case 1:
                        gateFront.setOver = 5;
                        break;
                    case 5:
                        gateFront.setOver = 10;
                        break;
                    case 10:
                        gateFront.setOver = 20;
                        break;
                    case 20:
                        gateFront.setOver = 50;
                        break;
                    case 50:
                        gateFront.setOver = 1;
                        break;
                }
                if (player instanceof EntityPlayerMP) player.addChatComponentMessage(
                        new ChatComponentTranslation("info.gate.over", gateFront.setOver)
                );
            }
        }

        if (tileEntity instanceof TileEntityTransceiver) {
            TileEntityTransceiver thisRail = (TileEntityTransceiver) tileEntity;

            if (player.isSneaking() && !world.isRemote) {
                UUID uuid = player.getUniqueID();
                if (tmpRails.containsKey(uuid)) {
                    if (tmpRails.get(uuid) == thisRail) {
                        thisRail.setTransceiver(null);
                        if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.blocking.abort"));
                    } else {
                        if (thisRail.getTransceiver() == tmpRails.get(uuid)) {
                            thisRail.getTransceiver().setTransceiver(null);
                            thisRail.setTransceiver(null);
                            if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.blocking.disconnected"));
                        } else {
                            thisRail.setTransceiver(tmpRails.get(uuid));
                            thisRail.getTransceiver().setTransceiver(thisRail);
                            if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.blocking.connected"));
                        }
                    }
                    tmpRails.remove(uuid);
                } else {
                    tmpRails.put(uuid, thisRail);
                    if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.blocking.begin"));
                }
                return true;
            }
        } else if (tileEntity instanceof BlockTriStateSignalBox.TileEntityTriStateSignalBox) {
            BlockTriStateSignalBox.TileEntityTriStateSignalBox signalBox = (BlockTriStateSignalBox.TileEntityTriStateSignalBox) tileEntity;

            if (player.isSneaking()) {
                signalBox.prevInverterEnabled = signalBox.inverterEnabled;
                if (signalBox.inverterEnabled) {
                    signalBox.inverterEnabled = false;
                    if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.box.inverter.off"));
                } else {
                    signalBox.inverterEnabled = true;
                    if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.box.inverter.on"));
                }
            } else {
                signalBox.prevTriStateIsNeg = signalBox.triStateIsNeg;
                if (signalBox.triStateIsNeg) {
                    signalBox.triStateIsNeg = false;
                    if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.box.triState.pos"));
                } else {
                    signalBox.triStateIsNeg = true;
                    if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.box.triState.neg"));
                }
            }
            return !world.isRemote;
        } else if (tileEntity instanceof BlockSignalBox.TileEntitySignalBox) {
            BlockSignalBox.TileEntitySignalBox signalBox = (BlockSignalBox.TileEntitySignalBox) tileEntity;

            if (player.isSneaking()) {
                signalBox.prevInverterEnabled = signalBox.inverterEnabled;
                if (signalBox.inverterEnabled) {
                    signalBox.inverterEnabled = false;
                    if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.box.inverter.off"));
                } else {
                    signalBox.inverterEnabled = true;
                    if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.box.inverter.on"));
                }
            }
            return !world.isRemote;
        } else if (tileEntity instanceof club.nsdn.nyasamarailway.tileblock.signal.TileEntitySignalLight) {
            club.nsdn.nyasamarailway.tileblock.signal.TileEntitySignalLight signalLight =
                    (club.nsdn.nyasamarailway.tileblock.signal.TileEntitySignalLight) tileEntity;

            if (player.isSneaking()) {
                signalLight.prevLightType = signalLight.lightType;
                if (signalLight.lightType.equals("none")) {
                    signalLight.lightType = "red&off";
                    if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.light.mode.red_off"));
                } else if (signalLight.lightType.equals("red&off")) {
                    signalLight.lightType = "yellow&off";
                    if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.light.mode.yellow_off"));
                } else if (signalLight.lightType.equals("yellow&off")) {
                    signalLight.lightType = "green&off";
                    if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.light.mode.green_off"));
                } else if (signalLight.lightType.equals("green&off")) {
                    signalLight.lightType = "red&yellow";
                    if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.light.mode.red_yellow"));
                } else if (signalLight.lightType.equals("red&yellow")) {
                    signalLight.lightType = "red&green";
                    if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.light.mode.red_green"));
                } else if (signalLight.lightType.equals("red&green")) {
                    signalLight.lightType = "yellow&green";
                    if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.light.mode.yellow_green"));
                } else if (signalLight.lightType.equals("yellow&green")) {
                    signalLight.lightType = "white&blue";
                    if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.light.mode.white_blue"));
                } else if (signalLight.lightType.equals("white&blue")) {
                    signalLight.lightType = "yellow&purple";
                    if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.light.mode.yellow_purple"));
                } else if (signalLight.lightType.equals("yellow&purple")) {
                    signalLight.lightType = "none";
                    if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.light.mode.none"));
                }
            }  else {
                if (signalLight.isBlinking) {
                    signalLight.isBlinking = false;
                    if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.light.blink.off"));
                } else {
                    signalLight.isBlinking = true;
                    if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.light.blink.on"));
                }
            }
            return !world.isRemote;
        }

        return false;
    }
}
