package club.nsdn.nyasamatelecom.api.tool;

import club.nsdn.nyasamatelecom.api.tileentity.TileEntityActuator;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityReceiver;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityTransceiver;
import club.nsdn.nyasamatelecom.api.util.Util;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.LinkedHashMap;
import java.util.UUID;

/**
 * Created by drzzm32 on 2018.12.13.
 */
public class DevEditor extends ToolBase {

    public static LinkedHashMap<UUID, TileEntityTransceiver> sender;
    public static LinkedHashMap<UUID, TileEntity> target;

    public DevEditor(String modid, String name, String id) {
        super(ToolMaterial.IRON);
        setUnlocalizedName(name);
        setRegistryName(modid, id);

        sender = new LinkedHashMap<>();
        target = new LinkedHashMap<>();
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, EnumHand hand) {
        TileEntity tileEntity = world.getTileEntity(pos);
        UUID uuid = player.getUniqueID();

        if (!world.isRemote) {
            if (tileEntity == null) {
                if (sender.containsKey(uuid)) sender.remove(uuid);
                if (target.containsKey(uuid)) target.remove(uuid);
                Util.say(player, "info.editor.clear");
                return EnumActionResult.SUCCESS;
            }

            if (player.isSneaking()) {
                if (!target.containsKey(uuid)) {
                    if (tileEntity instanceof TileEntityActuator) {
                        target.put(uuid, ((TileEntityActuator) tileEntity).getTarget());
                        Util.say(player, "info.editor.copy.target");
                    } else {
                        Util.say(player, "info.editor.error");
                    }
                } else {
                    if (tileEntity instanceof TileEntityActuator) {
                        ((TileEntityActuator) tileEntity).setTarget(target.get(uuid));
                        updateTileEntity(tileEntity);
                        Util.say(player, "info.editor.paste.target");
                    } else {
                        Util.say(player, "info.editor.error");
                    }
                }
            } else {
                if (!sender.containsKey(uuid)) {
                    if (tileEntity instanceof TileEntityReceiver) {
                        sender.put(uuid, ((TileEntityReceiver) tileEntity).getSender());
                        Util.say(player, "info.editor.copy.sender");
                    } else {
                        Util.say(player, "info.editor.error");
                    }
                } else {
                    if (tileEntity instanceof TileEntityReceiver) {
                        ((TileEntityReceiver) tileEntity).setSender(sender.get(uuid));
                        updateTileEntity(tileEntity);
                        Util.say(player, "info.editor.paste.sender");
                    } else {
                        Util.say(player, "info.editor.error");
                    }
                }
            }

            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.FAIL;
    }
}
