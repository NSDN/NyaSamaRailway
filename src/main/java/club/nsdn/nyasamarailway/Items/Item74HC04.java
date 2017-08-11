package club.nsdn.nyasamarailway.Items;

import club.nsdn.nyasamarailway.ExtMod.Railcraft;
import club.nsdn.nyasamarailway.TileEntities.*;
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

    public static LinkedHashMap<UUID, TileEntityRailTransceiver> senderRails;
    public static LinkedHashMap<UUID, TileEntityRailReceiver> receiverRails;

    public Item74HC04() {
        super(ToolMaterial.IRON);
        setUnlocalizedName("Item74HC04");
        setTexName("74hc04");

        tmpRails = new LinkedHashMap<UUID, TileEntityRailTransceiver>();

        senderRails = new LinkedHashMap<UUID, TileEntityRailTransceiver>();
        receiverRails = new LinkedHashMap<UUID, TileEntityRailReceiver>();
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

        if (!world.isRemote) {
            if (player.isSneaking()) {
                UUID uuid = player.getUniqueID();
                TileEntity tileEntity = world.getTileEntity(x, y, z);
                if (tileEntity instanceof TileEntityRailTransceiver) {
                    TileEntityRailTransceiver railSender = (TileEntityRailTransceiver) tileEntity;
                    if (tmpRails.containsKey(uuid)) {
                        tmpRails.remove(uuid);
                        if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.abort"));
                    } else {
                        tmpRails.put(uuid, railSender);
                        if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.begin"));
                    }
                    return true;
                } else if (tileEntity instanceof TileEntityRailReceiver) {
                    TileEntityRailReceiver railReceiver = (TileEntityRailReceiver) tileEntity;
                    if (tmpRails.containsKey(uuid)) {
                        if (railReceiver.getSenderRail() != tmpRails.get(uuid)) {
                            if (tmpRails.get(uuid).getTransceiverRail() == null) {
                                if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.error"));
                            } else {
                                railReceiver.setSenderRail(tmpRails.get(uuid));
                                if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.connected"));
                                world.markBlockForUpdate(x, y, z);
                            }
                            tmpRails.remove(uuid);
                        } else {
                            if (railReceiver.getSenderRail() == tmpRails.get(uuid)) {
                                railReceiver.setSenderRail(null);
                                tmpRails.remove(uuid);
                                if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.disconnected"));
                                world.markBlockForUpdate(x, y, z);
                            }
                        }
                    } else {
                        if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.error"));
                    }
                    return true;
                }
            } else {
                UUID uuid = player.getUniqueID();
                TileEntity tileEntity = world.getTileEntity(x, y, z);
                if (tileEntity instanceof TileEntityRailSender) {
                    TileEntityRailSender sender = (TileEntityRailSender) tileEntity;
                    if (senderRails.containsKey(uuid)) {
                        if (senderRails.get(uuid) instanceof TileEntityRailSender) {
                            if (senderRails.get(uuid) == sender) {
                                if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.abort"));
                            } else {
                                if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.error"));
                            }
                        } else {
                            if (senderRails.get(uuid).getTransceiverRail() == sender) {
                                sender.setTransceiverRail(null);
                                senderRails.get(uuid).setTransceiverRail(null);
                                if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.disconnected"));
                                world.markBlockForUpdate(x, y, z);
                            } else {
                                sender.setTransceiverRail(senderRails.get(uuid));
                                senderRails.get(uuid).setTransceiverRail(sender);
                                if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.connected"));
                                world.markBlockForUpdate(x, y, z);
                            }
                        }
                        senderRails.remove(uuid);
                    } else {
                        senderRails.put(uuid, sender);
                        if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.begin"));
                    }
                    return true;
                } else if (tileEntity instanceof TileEntityRailTransceiver) {
                    TileEntityRailTransceiver transceiver = (TileEntityRailTransceiver) tileEntity;
                    if (senderRails.containsKey(uuid)) {
                        if (senderRails.get(uuid) instanceof TileEntityRailSender) {
                            if (senderRails.get(uuid).getTransceiverRail() == transceiver) {
                                transceiver.setTransceiverRail(null);
                                senderRails.get(uuid).setTransceiverRail(null);
                                if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.disconnected"));
                                world.markBlockForUpdate(x, y, z);
                            } else {
                                if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.error"));
                            }
                        } else {
                            if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.abort"));
                        }
                        senderRails.remove(uuid);
                    } else {
                        senderRails.put(uuid, transceiver);
                        if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.begin"));
                    }
                    return true;
                } else if (tileEntity instanceof TileEntityRailReceiver) {
                    TileEntityRailReceiver receiver = (TileEntityRailReceiver) tileEntity;
                    if (senderRails.containsKey(uuid)) {
                        if (receiver.getSenderRail() != senderRails.get(uuid)) {
                            receiver.setSenderRail(senderRails.get(uuid));
                            if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.connected"));
                            world.markBlockForUpdate(x, y, z);
                            senderRails.remove(uuid);
                        } else {
                            receiver.setSenderRail(null);
                            if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.disconnected"));
                            world.markBlockForUpdate(x, y, z);
                        }
                        senderRails.remove(uuid);
                    } else {
                        if (receiverRails.containsKey(uuid)) {
                            if (receiverRails.get(uuid) instanceof TileEntityRailActuator) {
                                TileEntityRailActuator actuator = (TileEntityRailActuator) receiverRails.get(uuid);
                                if (actuator.getTarget() != receiver) {
                                    actuator.setTarget(receiver);
                                    if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.connected"));
                                    world.markBlockForUpdate(x, y, z);
                                } else {
                                    actuator.setTarget(null);
                                    if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.disconnected"));
                                    world.markBlockForUpdate(x, y, z);
                                }
                            } else if (receiver instanceof TileEntityRailActuator) {
                                if (((TileEntityRailActuator) receiver).getTarget() != receiverRails.get(uuid)) {
                                    ((TileEntityRailActuator) receiver).setTarget(receiverRails.get(uuid));
                                    if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.connected"));
                                    world.markBlockForUpdate(x, y, z);
                                } else {
                                    ((TileEntityRailActuator) receiver).setTarget(null);
                                    if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.disconnected"));
                                    world.markBlockForUpdate(x, y, z);
                                }
                            }
                            receiverRails.remove(uuid);
                        } else {
                            if (receiver instanceof TileEntitySignalLight.SignalLight) {
                                if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.error"));
                            } else {
                                receiverRails.put(uuid, receiver);
                                if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.begin"));
                            }
                        }
                    }
                    return true;
                } else {
                    if (Railcraft.getInstance() != null) {
                        if (Railcraft.getInstance().verifySwitch(tileEntity)) {
                            if (receiverRails.containsKey(uuid)) {
                                if (receiverRails.get(uuid) instanceof TileEntityRailActuator) {
                                    TileEntityRailActuator actuator = (TileEntityRailActuator) receiverRails.get(uuid);
                                    if (actuator.getTarget() != tileEntity) {
                                        actuator.setTarget(tileEntity);
                                        if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.connected"));
                                        world.markBlockForUpdate(x, y, z);
                                    } else {
                                        actuator.setTarget(null);
                                        if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.disconnected"));
                                        world.markBlockForUpdate(x, y, z);
                                    }
                                }
                                receiverRails.remove(uuid);
                            } else {
                                if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.error"));
                            }
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }
}
