package club.nsdn.nyasamarailway.Items;

import club.nsdn.nyasamarailway.ExtMod.Railcraft;
import club.nsdn.nyasamarailway.TileEntities.Signals.*;
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
                if (tileEntity instanceof TileEntityRailMultiSender) {
                    TileEntityRailMultiSender sender = (TileEntityRailMultiSender) tileEntity;
                    if (senderRails.containsKey(uuid)) {
                        if (senderRails.get(uuid) instanceof TileEntityRailMultiSender) {
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
                        senderRails.put(uuid, sender);
                        if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.begin"));
                    }
                    return true;
                } else if (tileEntity instanceof TileEntityRailTransceiver) {
                    TileEntityRailTransceiver transceiver = (TileEntityRailTransceiver) tileEntity;
                    if (senderRails.containsKey(uuid)) {
                        if (senderRails.get(uuid) instanceof TileEntityRailMultiSender) {
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
                } else if (tileEntity instanceof TileEntityRailReceiver) {
                    TileEntityRailReceiver receiver = (TileEntityRailReceiver) tileEntity;
                    if (senderRails.containsKey(uuid)) {
                        if (receiver.getSender() != senderRails.get(uuid)) {
                            receiver.setSender(senderRails.get(uuid));
                            if (senderRails.get(uuid) instanceof TileEntityRailMultiSender) {
                                ((TileEntityRailMultiSender) senderRails.get(uuid)).incTarget();
                            }
                            if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.connected"));
                            updateTileEntity(senderRails.get(uuid));
                            updateTileEntity(receiver);
                        } else {
                            receiver.setSender(null);
                            if (senderRails.get(uuid) instanceof TileEntityRailMultiSender) {
                                ((TileEntityRailMultiSender) senderRails.get(uuid)).decTarget();
                            }
                            if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.disconnected"));
                            updateTileEntity(senderRails.get(uuid));
                            updateTileEntity(receiver);
                        }
                        senderRails.remove(uuid);
                    } else {
                        if (receiverRails.containsKey(uuid)) {
                            if (receiverRails.get(uuid) instanceof TileEntityRailActuator) {
                                TileEntityRailActuator actuator = (TileEntityRailActuator) receiverRails.get(uuid);
                                if (actuator == receiver) {
                                    if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.error"));
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
                            } else if (receiver instanceof TileEntityRailActuator) {
                                if (((TileEntityRailActuator) receiver).getTarget() != receiverRails.get(uuid)) {
                                    ((TileEntityRailActuator) receiver).setTarget(receiverRails.get(uuid));
                                    if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.connected"));
                                    updateTileEntity(receiverRails.get(uuid));
                                    updateTileEntity(receiver);
                                } else {
                                    ((TileEntityRailActuator) receiver).setTarget(null);
                                    if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.disconnected"));
                                    updateTileEntity(receiverRails.get(uuid));
                                    updateTileEntity(receiver);
                                }
                            }
                            receiverRails.remove(uuid);
                        } else {
                            if (receiver instanceof TileEntityRailPassiveReceiver) {
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
                                        updateTileEntity(actuator);
                                        updateTileEntity(tileEntity);
                                    } else {
                                        actuator.setTarget(null);
                                        if (player instanceof EntityPlayerMP) player.addChatComponentMessage(new ChatComponentTranslation("info.signal.disconnected"));
                                        updateTileEntity(actuator);
                                        updateTileEntity(tileEntity);
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
