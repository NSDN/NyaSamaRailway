package club.nsdn.nyasamarailway.item.tool;

import club.nsdn.nyasamarailway.block.*;
import club.nsdn.nyasamarailway.block.rail.*;
import club.nsdn.nyasamarailway.block.rail.special.BlockRailReception;
import club.nsdn.nyasamarailway.block.rail.special.BlockRailReceptionAnti;
import club.nsdn.nyasamarailway.tileblock.functional.BlockCoinBlock;
import club.nsdn.nyasamarailway.tileblock.functional.BlockTicketBlockOnce;
import club.nsdn.nyasamarailway.tileblock.rail.mono.*;
import club.nsdn.nyasamarailway.tileblock.signal.TileEntityRailReception;
import club.nsdn.nyasamarailway.tileblock.signal.TileEntityTrackSideReception;
import club.nsdn.nyasamarailway.tileblock.signal.core.BlockSignalBox;
import club.nsdn.nyasamarailway.tileblock.signal.core.BlockTriStateSignalBox;
import club.nsdn.nyasamarailway.tileblock.signal.deco.BlockGateFront;
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
    
    private void say(EntityPlayer player, String id) {
        if (player instanceof EntityPlayerMP)
            player.addChatComponentMessage(new ChatComponentTranslation(id));
    }

    private void say(EntityPlayer player, String id, Object... args) {
        if (player instanceof EntityPlayerMP)
            player.addChatComponentMessage(new ChatComponentTranslation(id, args));
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        Block block = world.getBlock(x, y, z);

        if (block == null)
            return false;

        if (world.getTileEntity(x, y, z) instanceof TileEntityTrackSideReception) {
            TileEntityTrackSideReception reception = (TileEntityTrackSideReception) world.getTileEntity(x, y, z);

            if (reception.cartType.isEmpty()) {
                reception.cartType = "loco";
                say(player, "info.reception.loco");
            } else {
                if (reception.cartType.equals("loco")) {
                    reception.cartType = "";
                    say(player, "info.reception.reset");
                } else {
                    reception.cartType = "";
                    reception.extInfo = "";
                    say(player, "info.reception.cleared");
                }
            }

            return !world.isRemote;
        }

        if (block instanceof IRailDirectional) {
            if (player.isSneaking()) {
                TileEntity rail = world.getTileEntity(x, y, z);
                if (rail == null) return false;

                if (rail instanceof TileEntityRailReception) {
                    if (((TileEntityRailReception) rail).cartType.isEmpty()) {
                        ((TileEntityRailReception) rail).cartType = "loco";
                        say(player, "info.reception.loco");
                    } else {
                        if (((TileEntityRailReception) rail).cartType.equals("loco")) {
                            ((TileEntityRailReception) rail).cartType = "";
                            say(player, "info.reception.reset");
                        } else {
                            ((TileEntityRailReception) rail).cartType = "";
                            ((TileEntityRailReception) rail).extInfo = "";
;                            say(player, "info.reception.cleared");
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
            say(player, "info.1N4148.delay", nowDelay);
            return !world.isRemote;
        } else if (block instanceof RailMonoMagnetDetector && !(block instanceof IRailNoDelay)) {
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
            say(player, "info.1N4148.delay", nowDelay);
            return !world.isRemote;
        }

        TileEntity tileEntity = world.getTileEntity(x, y, z);

        if (tileEntity == null) return false;

        if (player.isSneaking()) {
            if (tileEntity instanceof BlockCoinBlock.CoinBlock) {
                BlockCoinBlock.CoinBlock coinBlock = (BlockCoinBlock.CoinBlock) tileEntity;
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
            } else if (tileEntity instanceof BlockTicketBlockOnce.TicketBlock) {
                BlockTicketBlockOnce.TicketBlock ticketBlock = (BlockTicketBlockOnce.TicketBlock) tileEntity;

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
                        say(player, "info.blocking.abort");
                    } else {
                        if (thisRail.getTransceiver() == tmpRails.get(uuid)) {
                            thisRail.getTransceiver().setTransceiver(null);
                            thisRail.setTransceiver(null);
                            say(player, "info.blocking.disconnected");

                        } else {
                            thisRail.setTransceiver(tmpRails.get(uuid));
                            thisRail.getTransceiver().setTransceiver(thisRail);
                            say(player, "info.blocking.connected");
                        }
                    }
                    tmpRails.remove(uuid);
                } else {
                    tmpRails.put(uuid, thisRail);
                    say(player, "info.blocking.begin");
                }
                return true;
            }
        } else if (tileEntity instanceof BlockTriStateSignalBox.TileEntityTriStateSignalBox) {
            BlockTriStateSignalBox.TileEntityTriStateSignalBox signalBox = (BlockTriStateSignalBox.TileEntityTriStateSignalBox) tileEntity;

            if (player.isSneaking()) {
                signalBox.prevInverterEnabled = signalBox.inverterEnabled;
                if (signalBox.inverterEnabled) {
                    signalBox.inverterEnabled = false;
                    say(player, "info.signal.box.inverter.off");
                } else {
                    signalBox.inverterEnabled = true;
                    say(player, "info.signal.box.inverter.on");
                }
            } else {
                signalBox.prevTriStateIsNeg = signalBox.triStateIsNeg;
                if (signalBox.triStateIsNeg) {
                    signalBox.triStateIsNeg = false;
                    say(player, "info.signal.box.triState.pos");
                } else {
                    signalBox.triStateIsNeg = true;
                    say(player, "info.signal.box.triState.neg");
                }
            }
            return !world.isRemote;
        } else if (tileEntity instanceof BlockSignalBox.TileEntitySignalBox) {
            BlockSignalBox.TileEntitySignalBox signalBox = (BlockSignalBox.TileEntitySignalBox) tileEntity;

            if (player.isSneaking()) {
                if (signalBox.inverterEnabled) {
                    signalBox.inverterEnabled = false;
                    say(player, "info.signal.box.inverter.off");
                } else {
                    signalBox.inverterEnabled = true;
                    say(player, "info.signal.box.inverter.on");
                }
            }
            return !world.isRemote;
        } else if (tileEntity instanceof club.nsdn.nyasamarailway.tileblock.signal.TileEntitySignalLight) {
            club.nsdn.nyasamarailway.tileblock.signal.TileEntitySignalLight signalLight =
                    (club.nsdn.nyasamarailway.tileblock.signal.TileEntitySignalLight) tileEntity;

            if (player.isSneaking()) {
                signalLight.prevLightType = signalLight.lightType;
                switch (signalLight.lightType) {
                    case "none":
                        signalLight.lightType = "red&off";
                        say(player, "info.signal.light.mode.red_off");
                        break;
                    case "red&off":
                        signalLight.lightType = "yellow&off";
                        say(player, "info.signal.light.mode.yellow_off");
                        break;
                    case "yellow&off":
                        signalLight.lightType = "green&off";
                        say(player, "info.signal.light.mode.green_off");
                        break;
                    case "green&off":
                        signalLight.lightType = "white&off";
                        say(player, "info.signal.light.mode.white_off");
                        break;
                    case "white&off":
                        signalLight.lightType = "blue&off";
                        say(player, "info.signal.light.mode.blue_off");
                        break;
                    case "blue&off":
                        signalLight.lightType = "purple&off";
                        say(player, "info.signal.light.mode.purple_off");
                        break;
                    case "purple&off":
                        signalLight.lightType = "red&yellow";
                        say(player, "info.signal.light.mode.red_yellow");
                        break;
                    case "red&yellow":
                        signalLight.lightType = "red&green";
                        say(player, "info.signal.light.mode.red_green");
                        break;
                    case "red&green":
                        signalLight.lightType = "yellow&green";
                        say(player, "info.signal.light.mode.yellow_green");
                        break;
                    case "yellow&green":
                        signalLight.lightType = "white&blue";
                        say(player, "info.signal.light.mode.white_blue");
                        break;
                    case "white&blue":
                        signalLight.lightType = "yellow&purple";
                        say(player, "info.signal.light.mode.yellow_purple");
                        break;
                    case "yellow&purple":
                        signalLight.lightType = "none";
                        say(player, "info.signal.light.mode.none");
                        break;
                    default:
                        break;
                }
            } else {
                if (signalLight.isBlinking) {
                    signalLight.isBlinking = false;
                    say(player, "info.signal.light.blink.off");
                } else {
                    signalLight.isBlinking = true;
                    say(player, "info.signal.light.blink.on");
                }
            }
            return !world.isRemote;
        }

        return false;
    }
}
