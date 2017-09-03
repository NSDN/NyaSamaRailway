package club.nsdn.nyasamarailway.Items;

import club.nsdn.nyasamarailway.Blocks.*;
import club.nsdn.nyasamarailway.TileEntities.Rail.*;
import club.nsdn.nyasamarailway.TileEntities.TileEntityRailTransceiver;
import club.nsdn.nyasamarailway.TileEntities.TileEntitySignalBox;
import club.nsdn.nyasamarailway.TileEntities.TileEntitySignalLight;
import club.nsdn.nyasamarailway.TileEntities.TileEntityTriStateSignalBox;
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

    public static LinkedHashMap<UUID, TileEntityRailTransceiver> tmpRails;

    public Item1N4148() {
        super(ToolMaterial.IRON);
        setUnlocalizedName("Item1N4148");
        setTexName("1n4148");

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
        } else if (block instanceof BlockRailDetectorBase && !(block instanceof BlockRailBlocking)) {
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

        if (tileEntity instanceof TileEntityRailTransceiver) {
            TileEntityRailTransceiver thisRail = (TileEntityRailTransceiver) tileEntity;

            if (player.isSneaking() && !world.isRemote) {
                UUID uuid = player.getUniqueID();
                if (tmpRails.containsKey(uuid)) {
                    if (tmpRails.get(uuid) == thisRail) {
                        thisRail.setTransceiverRail(null);
                        if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.blocking.abort"));
                    } else {
                        if (thisRail.getTransceiverRail() == tmpRails.get(uuid)) {
                            thisRail.getTransceiverRail().setTransceiverRail(null);
                            thisRail.setTransceiverRail(null);
                            if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.blocking.disconnected"));
                        } else {
                            thisRail.setTransceiverRail(tmpRails.get(uuid));
                            thisRail.getTransceiverRail().setTransceiverRail(thisRail);
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
        } else if (tileEntity instanceof TileEntityTriStateSignalBox.TriStateSignalBox) {
            TileEntityTriStateSignalBox.TriStateSignalBox signalBox = (TileEntityTriStateSignalBox.TriStateSignalBox) tileEntity;

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
        } else if (tileEntity instanceof TileEntitySignalBox.SignalBox) {
            TileEntitySignalBox.SignalBox signalBox = (TileEntitySignalBox.SignalBox) tileEntity;

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
        } else if (tileEntity instanceof TileEntitySignalLight.SignalLight) {
            TileEntitySignalLight.SignalLight signalLight = (TileEntitySignalLight.SignalLight) tileEntity;

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
            } else {
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
