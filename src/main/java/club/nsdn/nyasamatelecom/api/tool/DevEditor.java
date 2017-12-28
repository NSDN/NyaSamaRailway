package club.nsdn.nyasamatelecom.api.tool;

import club.nsdn.nyasamatelecom.api.tileentity.TileEntityActuator;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityReceiver;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityTransceiver;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

import java.util.LinkedHashMap;
import java.util.UUID;

/**
 * Created by drzzm32 on 2017.12.28.
 */
public class DevEditor extends ToolBase {

    public static LinkedHashMap<UUID, TileEntityTransceiver> sender;
    public static LinkedHashMap<UUID, TileEntity> target;

    public DevEditor(String modid, String name, String icon) {
        super(ToolMaterial.IRON);
        setUnlocalizedName(name);
        setTextureName(modid.toLowerCase() + ":" + icon);

        sender = new LinkedHashMap<UUID, TileEntityTransceiver>();
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
                    if (tileEntity instanceof TileEntityActuator) {
                        target.put(uuid, ((TileEntityActuator) tileEntity).getTarget());
                        player.addChatComponentMessage(
                                new ChatComponentTranslation("info.editor.copy.target")
                        );
                    } else {
                        player.addChatComponentMessage(
                                new ChatComponentTranslation("info.editor.error")
                        );
                    }
                } else {
                    if (tileEntity instanceof TileEntityActuator) {
                        ((TileEntityActuator) tileEntity).setTarget(target.get(uuid));
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
                    if (tileEntity instanceof TileEntityReceiver) {
                        sender.put(uuid, ((TileEntityReceiver) tileEntity).getSender());
                        player.addChatComponentMessage(
                                new ChatComponentTranslation("info.editor.copy.sender")
                        );
                    } else {
                        player.addChatComponentMessage(
                                new ChatComponentTranslation("info.editor.error")
                        );
                    }
                } else {
                    if (tileEntity instanceof TileEntityReceiver) {
                        ((TileEntityReceiver) tileEntity).setSender(sender.get(uuid));
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
