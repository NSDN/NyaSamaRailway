package club.nsdn.nyasamatelecom.api.tool;

import club.nsdn.nyasamatelecom.api.tileentity.*;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityActuator;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityReceiver;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityTransceiver;
import club.nsdn.nyasamatelecom.api.util.Util;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.UUID;

/**
 * Created by drzzm32 on 2018.12.13.
 */
public class Connector extends ToolBase {

    public static LinkedHashMap<UUID, TileEntityTransceiver> tmpRails;

    public static LinkedHashMap<UUID, TileEntityTransceiver> senderRails;
    public static LinkedHashMap<UUID, TileEntityReceiver> receiverRails;

    public Connector(String modid, String name, String id) {
        super(ToolMaterial.IRON);
        setUnlocalizedName(name);
        setRegistryName(modid, id);

        tmpRails = new LinkedHashMap<>();

        senderRails = new LinkedHashMap<>();
        receiverRails = new LinkedHashMap<>();
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    public boolean onConnectorUseLast(EntityPlayer player, UUID uuid, TileEntity tileEntity) {
        return false;
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, EnumHand hand) {
        Block block = world.getBlockState(pos).getBlock();

        if (!world.isRemote) {
            if (player.isSneaking()) {
                UUID uuid = player.getUniqueID();
                TileEntity tileEntity = world.getTileEntity(pos);
                if (tileEntity instanceof TileEntityTransceiver) {
                    TileEntityTransceiver railSender = (TileEntityTransceiver) tileEntity;
                    if (tmpRails.containsKey(uuid)) {
                        tmpRails.remove(uuid);
                        Util.say(player, "info.signal.abort");
                    } else {
                        tmpRails.put(uuid, railSender);
                        Util.say(player, "info.signal.begin");
                    }
                    return EnumActionResult.SUCCESS;
                } else if (tileEntity instanceof TileEntityReceiver) {
                    TileEntityReceiver railReceiver = (TileEntityReceiver) tileEntity;
                    if (tmpRails.containsKey(uuid)) {
                        if (railReceiver.getSender() != tmpRails.get(uuid)) {
                            if (tmpRails.get(uuid).getTransceiver() == null) {
                                Util.say(player, "info.signal.error");
                            } else {
                                railReceiver.setSender(tmpRails.get(uuid));
                                Util.say(player, "info.signal.connected");
                                updateTileEntity(tmpRails.get(uuid));
                                updateTileEntity(railReceiver);
                            }
                            tmpRails.remove(uuid);
                        } else {
                            if (railReceiver.getSender() == tmpRails.get(uuid)) {
                                railReceiver.setSender(null);
                                Util.say(player, "info.signal.disconnected");
                                updateTileEntity(tmpRails.get(uuid));
                                updateTileEntity(railReceiver);
                                tmpRails.remove(uuid);
                            }
                        }
                    } else {
                        Util.say(player, "info.signal.error");
                    }
                    return EnumActionResult.SUCCESS;
                }
            } else {
                UUID uuid = player.getUniqueID();
                TileEntity tileEntity = world.getTileEntity(pos);
                if (tileEntity instanceof TileEntityMultiSender) {
                    TileEntityMultiSender sender = (TileEntityMultiSender) tileEntity;
                    if (senderRails.containsKey(uuid)) {
                        if (senderRails.get(uuid) instanceof TileEntityMultiSender) {
                            if (senderRails.get(uuid) == sender) {
                                Util.say(player, "info.signal.abort");
                            } else {
                                Util.say(player, "info.signal.error");
                            }
                        } else {
                            if (sender.getTransceiver() == senderRails.get(uuid)) {
                                sender.getTransceiver().setTransceiver(null);
                                sender.setTransceiver(null);
                                Util.say(player, "info.signal.disconnected");
                                updateTileEntity(senderRails.get(uuid));
                                updateTileEntity(sender);
                            } else {
                                sender.setTransceiver(senderRails.get(uuid));
                                sender.getTransceiver().setTransceiver(sender);
                                Util.say(player, "info.signal.connected");
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
                                    Util.say(player, "info.signal.connected");
                                    updateTileEntity(actuator);
                                    updateTileEntity(sender);
                                } else {
                                    actuator.setTarget(null);
                                    Util.say(player, "info.signal.disconnected");
                                    updateTileEntity(actuator);
                                    updateTileEntity(sender);
                                }
                            } else {
                                Util.say(player, "info.signal.error");
                            }
                            receiverRails.remove(uuid);
                        } else {
                            senderRails.put(uuid, sender);
                            Util.say(player, "info.signal.begin");
                        }
                    }
                    return EnumActionResult.SUCCESS;
                } else if (tileEntity instanceof TileEntityTransceiver) {
                    TileEntityTransceiver transceiver = (TileEntityTransceiver) tileEntity;
                    if (senderRails.containsKey(uuid)) {
                        if (senderRails.get(uuid) instanceof TileEntityMultiSender) {
                            if (transceiver.getTransceiver() == senderRails.get(uuid)) {
                                transceiver.getTransceiver().setTransceiver(null);
                                transceiver.setTransceiver(null);
                                Util.say(player, "info.signal.disconnected");
                                updateTileEntity(senderRails.get(uuid));
                                updateTileEntity(transceiver);
                            } else {
                                Util.say(player, "info.signal.error");
                            }
                        } else {
                            //Here is blocking rail, link with 1n4148 instead of this item, it may be dangerous
                            Util.say(player, "info.signal.abort");
                        }
                        senderRails.remove(uuid);
                    } else {
                        senderRails.put(uuid, transceiver);
                        Util.say(player, "info.signal.begin");
                    }
                    return EnumActionResult.SUCCESS;
                } else if (tileEntity instanceof TileEntityReceiver) {
                    TileEntityReceiver receiver = (TileEntityReceiver) tileEntity;
                    if (senderRails.containsKey(uuid)) {
                        if (receiver.getSender() != senderRails.get(uuid)) {
                            receiver.setSender(senderRails.get(uuid));
                            if (senderRails.get(uuid) instanceof TileEntityMultiSender) {
                                ((TileEntityMultiSender) senderRails.get(uuid)).incTarget();
                            }
                            Util.say(player, "info.signal.connected");
                            updateTileEntity(senderRails.get(uuid));
                            updateTileEntity(receiver);
                        } else {
                            receiver.setSender(null);
                            if (senderRails.get(uuid) instanceof TileEntityMultiSender) {
                                ((TileEntityMultiSender) senderRails.get(uuid)).decTarget();
                            }
                            Util.say(player, "info.signal.disconnected");
                            updateTileEntity(senderRails.get(uuid));
                            updateTileEntity(receiver);
                        }
                        senderRails.remove(uuid);
                    } else {
                        if (receiverRails.containsKey(uuid)) {
                            if (receiverRails.get(uuid) instanceof TileEntityActuator) {
                                TileEntityActuator actuator = (TileEntityActuator) receiverRails.get(uuid);
                                if (actuator == receiver) {
                                    Util.say(player, "info.signal.abort");
                                } else if (actuator.getTarget() != receiver) {
                                    actuator.setTarget(receiver);
                                    Util.say(player, "info.signal.connected");
                                    updateTileEntity(actuator);
                                    updateTileEntity(receiver);
                                } else {
                                    actuator.setTarget(null);
                                    Util.say(player, "info.signal.disconnected");
                                    updateTileEntity(actuator);
                                    updateTileEntity(receiver);
                                }
                            } else if (receiver instanceof TileEntityActuator) {
                                if (((TileEntityActuator) receiver).getTarget() != receiverRails.get(uuid)) {
                                    ((TileEntityActuator) receiver).setTarget(receiverRails.get(uuid));
                                    Util.say(player, "info.signal.connected");
                                    updateTileEntity(receiverRails.get(uuid));
                                    updateTileEntity(receiver);
                                } else {
                                    ((TileEntityActuator) receiver).setTarget(null);
                                    Util.say(player, "info.signal.disconnected");
                                    updateTileEntity(receiverRails.get(uuid));
                                    updateTileEntity(receiver);
                                }
                            }
                            receiverRails.remove(uuid);
                        } else {
                            if (receiver instanceof TileEntityPassiveReceiver) {
                                Util.say(player, "info.signal.error");
                            } else {
                                receiverRails.put(uuid, receiver);
                                Util.say(player, "info.signal.begin");
                            }
                        }
                    }
                    return EnumActionResult.SUCCESS;
                } else {
                    onConnectorUseLast(player, uuid, tileEntity);
                }
            }
        }

        return EnumActionResult.FAIL;
    }
}
