package club.nsdn.nyasamarailway.Items;

import club.nsdn.nyasamarailway.TileEntities.Signals.TileEntityRailActuator;
import club.nsdn.nyasamarailway.TileEntities.Signals.TileEntityRailReceiver;
import club.nsdn.nyasamarailway.TileEntities.Signals.TileEntityRailTransceiver;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

import java.util.LinkedHashMap;
import java.util.UUID;

/**
 * Created by drzzm32 on 2016.9.12.
 */
public class ItemDevEditor extends ItemToolBase {

    public static LinkedHashMap<UUID, TileEntityRailTransceiver> sender;
    public static LinkedHashMap<UUID, TileEntity> target;

    public ItemDevEditor() {
        super(ToolMaterial.IRON);
        setUnlocalizedName("ItemDevEditor");
        setTexName("device_editor");

        sender = new LinkedHashMap<UUID, TileEntityRailTransceiver>();
        target = new LinkedHashMap<UUID, TileEntity>();
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        player.setItemInUse(itemStack, this.getMaxItemUseDuration(itemStack));
        return itemStack;
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        UUID uuid = player.getUniqueID();

        if (!world.isRemote) {
            if (tileEntity == null) {
                if (sender.containsKey(uuid)) sender.remove(uuid);
                if (target.containsKey(uuid)) target.remove(uuid);
                player.addChatComponentMessage(
                        new ChatComponentTranslation("info.editor.clear")
                );
                return true;
            }

            if (player.isSneaking()) {
                if (!target.containsKey(uuid)) {
                    if (tileEntity instanceof TileEntityRailActuator) {
                        target.put(uuid, ((TileEntityRailActuator) tileEntity).getTarget());
                        player.addChatComponentMessage(
                                new ChatComponentTranslation("info.editor.copy.target")
                        );
                    } else {
                        player.addChatComponentMessage(
                                new ChatComponentTranslation("info.editor.error")
                        );
                    }
                } else {
                    if (tileEntity instanceof TileEntityRailActuator) {
                        ((TileEntityRailActuator) tileEntity).setTarget(target.get(uuid));
                        world.markBlockForUpdate(x, y, z);
                        player.addChatComponentMessage(
                                new ChatComponentTranslation("info.editor.paste.target")
                        );
                    } else {
                        player.addChatComponentMessage(
                                new ChatComponentTranslation("info.editor.error")
                        );
                    }
                }
            } else {
                if (!sender.containsKey(uuid)) {
                    if (tileEntity instanceof TileEntityRailReceiver) {
                        sender.put(uuid, ((TileEntityRailActuator) tileEntity).getSender());
                        player.addChatComponentMessage(
                                new ChatComponentTranslation("info.editor.copy.sender")
                        );
                    } else {
                        player.addChatComponentMessage(
                                new ChatComponentTranslation("info.editor.error")
                        );
                    }
                } else {
                    if (tileEntity instanceof TileEntityRailReceiver) {
                        ((TileEntityRailActuator) tileEntity).setSender(sender.get(uuid));
                        world.markBlockForUpdate(x, y, z);
                        player.addChatComponentMessage(
                                new ChatComponentTranslation("info.editor.paste.sender")
                        );
                    } else {
                        player.addChatComponentMessage(
                                new ChatComponentTranslation("info.editor.error")
                        );
                    }
                }
            }

            return true;
        }

        return false;
    }
}
