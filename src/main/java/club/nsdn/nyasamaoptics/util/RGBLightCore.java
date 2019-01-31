package club.nsdn.nyasamaoptics.util;

import club.nsdn.nyasamaoptics.network.NetworkWrapper;
import club.nsdn.nyasamaoptics.tileblock.light.RGBLight;
import club.nsdn.nyasamatelecom.api.util.NSASM;
import club.nsdn.nyasamatelecom.api.util.Util;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraft.entity.player.EntityPlayer;

import java.util.LinkedHashMap;

/**
 * Created by drzzm32 on 2019.1.30.
 */
public abstract class RGBLightCore extends NSASM {

    public RGBLightCore(String[][] code) {
        super(code);
    }

    @Override
    public SimpleNetworkWrapper getWrapper() {
        return NetworkWrapper.instance;
    }

    public void setColor(RGBLight.TileEntityRGBLight light, EntityPlayer player, int value) {
        light.color = value & 0xFFFFFF;
        light.refresh();
        Util.say(player, "info.light.color", Integer.toHexString(light.color).toUpperCase());
    }

    @Override
    public void loadFunc(LinkedHashMap<String, Operator> funcList) {
        funcList.put("clr", ((dst, src) -> {
            if (src != null) return Result.ERR;
            if (dst == null) return Result.ERR;

            if (dst.type == RegType.INT) {
                setColor(getLight(), getPlayer(), (int) dst.data);
                return Result.OK;
            }
            return Result.ERR;
        }));
    }

    public abstract RGBLight.TileEntityRGBLight getLight();

}
