package club.nsdn.nyasamatelecom.api.tool;

import club.nsdn.nyasamatelecom.api.tileentity.*;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityActuator;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityReceiver;
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
 * Created by drzzm32 on 2017.12.28.
 */
public class Connector extends ToolBase {

    public static LinkedHashMap<UUID, TileEntityTransceiver> tmpRails;

    public static LinkedHashMap<UUID, TileEntityTransceiver> senderRails;
    public static LinkedHashMap<UUID, TileEntityReceiver> receiverRails;

    public Connector(String modid, String name, String icon) {
        super(ToolMaterial.IRON);
        setUnlocalizedName(name);
        setTextureName(modid.toLowerCase() + ":" + icon);

        tmpRails = new LinkedHashMap<UUID, TileEntityTransceiver>();

        senderRails = new LinkedHashMap<UUID, TileEntityTransceiver>();
        receiverRails = new LinkedHashMap<UUID, TileEntityReceiver>();
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        player.setItemInUse(itemStack, this.getMaxItemUseDuration(itemStack));
        return itemStack;
    }

    public boolean onConnectorUseLast(EntityPlayer player, UUID uuid, TileEntity tileEntity) {
        return false;
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
                if (tileEntity instanceof TileEntityTransceiver) {
                    TileEntityTransceiver railSender = (TileEntityTransceiver) tileEntity;
                    if (tmpRails.containsKey(uuid)) {
                        tmpRails.remove(uuid);
                        if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.abort"));
                    } else {
                        tmpRails.put(uuid, railSender);
                        if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.begin"));
                    }
                    return true;
                } else if (tileEntity instanceof TileEntityReceiver) {
                    TileEntityReceiver railReceiver = (TileEntityReceiver) tileEntity;
                    if (tmpRails.containsKey(uuid)) {
                        if (railReceiver.getSender() != tmpRails.get(uuid)) {
                            if (tmpRails.get(uuid).getTransceiver() == null) {
                                if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.error"));
                            } else {
                                railReceiver.setSender(tmpRails.get(uuid));
                                if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.connected"));
                                updateTileEntity(tmpRails.get(uuid));
                                updateTileEntity(railReceiver);
                            }
                            tmpRails.remove(uuid);
                        } else {
                            if (railReceiver.getSender() == tmpRails.get(uuid)) {
                                railReceiver.setSender(null);
                                if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.disconnected"));
                                updateTileEntity(tmpRails.get(uuid));
                                updateTileEntity(railReceiver);
                                tmpRails.remove(uuid);
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
                if (tileEntity instanceof TileEntityMultiSender) {
                    TileEntityMultiSender sender = (TileEntityMultiSender) tileEntity;
                    if (senderRails.containsKey(uuid)) {
                        if (senderRails.get(uuid) instanceof TileEntityMultiSender) {
                            if (senderRails.get(uuid) == sender) {
                                if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.abort"));
                            } else {
                                if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.error"));
                            }
                        } else {
                            if (sender.getTransceiver() == senderRails.get(uuid)) {
                                sender.getTransceiver().setTransceiver(null);
                                sender.setTransceiver(null);
                                if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.disconnected"));
                                updateTileEntity(senderRails.get(uuid));
                                updateTileEntity(sender);
                            } else {
                                sender.setTransceiver(senderRails.get(uuid));
                                sender.getTransceiver().setTransceiver(sender);
                                if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.connected"));
                                updateTileEntity(senderRails.get(uuid));
                                updateTileEntity(sender);
                            }
                        }
                        senderRails.remove(uuid);
                    } else {
                        if (receiverRails.containsKey(uuid)) { //Here is actuator to transceiver, 1 to 1
                            if (receiverRails.get(uuid) instanceof TileEntityActuator) {
                                TileEntityActuator actuator = (TileEntityActuator) receiverRails.get(uuid);
                                if (actuator.getTarget() != sender) {
                                    actuator.setTarget(sender);
                                    if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.connected"));
                                    updateTileEntity(actuator);
                                    updateTileEntity(sender);
                                } else {
                                    actuator.setTarget(null);
                                    if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.disconnected"));
                                    updateTileEntity(actuator);
                                    updateTileEntity(sender);
                                }
                            } else {
                                if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.error"));
                            }
                            receiverRails.remove(uuid);
                        } else {
                            senderRails.put(uuid, sender);
                            if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.begin"));
                        }
                    }
                    return true;
                } else if (tileEntity instanceof TileEntityTransceiver) {
                    TileEntityTransceiver transceiver = (TileEntityTransceiver) tileEntity;
                    if (senderRails.containsKey(uuid)) {
                        if (senderRails.get(uuid) instanceof TileEntityMultiSender) {
                            if (transceiver.getTransceiver() == senderRails.get(uuid)) {
                                transceiver.getTransceiver().setTransceiver(null);
                                transceiver.setTransceiver(null);
                                if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.disconnected"));
                                updateTileEntity(senderRails.get(uuid));
                                updateTileEntity(transceiver);
                            } else {
                                if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.error"));
                            }
                        } else {
                            //Here is blocking rail, link with 1n4148 instead of this item, it may be dangerous
                            if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.abort"));
                        }
                        senderRails.remove(uuid);
                    } else {
                        senderRails.put(uuid, transceiver);
                        if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.begin"));
                    }
                    return true;
                } else if (tileEntity instanceof TileEntityReceiver) {
                    TileEntityReceiver receiver = (TileEntityReceiver) tileEntity;
                    if (senderRails.containsKey(uuid)) {
                        if (receiver.getSender() != senderRails.get(uuid)) {
                            receiver.setSender(senderRails.get(uuid));
                            if (senderRails.get(uuid) instanceof TileEntityMultiSender) {
                                ((TileEntityMultiSender) senderRails.get(uuid)).incTarget();
                            }
                            if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.connected"));
                            updateTileEntity(senderRails.get(uuid));
                            updateTileEntity(receiver);
                        } else {
                            receiver.setSender(null);
                            if (senderRails.get(uuid) instanceof TileEntityMultiSender) {
                                ((TileEntityMultiSender) senderRails.get(uuid)).decTarget();
                            }
                            if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.disconnected"));
                            updateTileEntity(senderRails.get(uuid));
                            updateTileEntity(receiver);
                        }
                        senderRails.remove(uuid);
                    } else {
                        if (receiverRails.containsKey(uuid)) {
                            if (receiverRails.get(uuid) instanceof TileEntityActuator) {
                                TileEntityActuator actuator = (TileEntityActuator) receiverRails.get(uuid);
                                if (actuator == receiver) {
                                    if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.abort"));
                                } else if (actuator.getTarget() != receiver) {
                                    actuator.setTarget(receiver);
                                    if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.connected"));
                                    updateTileEntity(actuator);
                                    updateTileEntity(receiver);
                                } else {
                                    actuator.setTarget(null);
                                    if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.disconnected"));
                                    updateTileEntity(actuator);
                                    updateTileEntity(receiver);
                                }
                            } else if (receiver instanceof TileEntityActuator) {
                                if (((TileEntityActuator) receiver).getTarget() != receiverRails.get(uuid)) {
                                    ((TileEntityActuator) receiver).setTarget(receiverRails.get(uuid));
                                    if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.connected"));
                                    updateTileEntity(receiverRails.get(uuid));
                                    updateTileEntity(receiver);
                                } else {
                                    ((TileEntityActuator) receiver).setTarget(null);
                                    if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.disconnected"));
                                    updateTileEntity(receiverRails.get(uuid));
                                    updateTileEntity(receiver);
                                }
                            }
                            receiverRails.remove(uuid);
                        } else {
                            if (receiver instanceof TileEntityPassiveReceiver) {
                                if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.error"));
                            } else {
                                receiverRails.put(uuid, receiver);
                                if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.begin"));
                            }
                        }
                    }
                    return true;
                } else {
                    onConnectorUseLast(player, uuid, tileEntity);
                }
            }
        }

        return false;
    }
}
