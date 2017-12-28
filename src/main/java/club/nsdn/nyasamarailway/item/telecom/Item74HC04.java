package club.nsdn.nyasamarailway.item.telecom;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.extmod.Railcraft;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityActuator;
import club.nsdn.nyasamatelecom.api.tool.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;

import java.util.UUID;

/**
 * Created by drzzm32 on 2016.6.7.
 */
public class Item74HC04 extends Connector {

    public Item74HC04() {
        super(NyaSamaRailway.MODID, "Item74HC04", "74hc04");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Override
    public boolean onConnectorUseLast(EntityPlayer player, UUID uuid, TileEntity tileEntity) {
        if (Railcraft.getInstance() != null) {
            if (Railcraft.getInstance().verifySwitch(tileEntity)) {
                if (receiverRails.containsKey(uuid)) {
                    if (receiverRails.get(uuid) instanceof TileEntityActuator) {
                        TileEntityActuator actuator = (TileEntityActuator) receiverRails.get(uuid);
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
        return false;
    }
}
