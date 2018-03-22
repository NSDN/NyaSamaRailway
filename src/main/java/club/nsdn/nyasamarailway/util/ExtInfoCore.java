package club.nsdn.nyasamarailway.util;

import club.nsdn.nyasamarailway.entity.IExtendedInfoCart;
import club.nsdn.nyasamarailway.network.NetworkWrapper;
import club.nsdn.nyasamarailway.tileblock.signal.TileEntityRailRFID;
import club.nsdn.nyasamatelecom.api.util.NSASM;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;

import java.util.LinkedHashMap;

/**
 * Created by drzzm32 on 2018.3.21.
 */
public abstract class ExtInfoCore extends NSASM {

    public ExtInfoCore(String[][] code) {
        super(code);
    }

    @Override
    public SimpleNetworkWrapper getWrapper() {
        return NetworkWrapper.instance;
    }

    public abstract IExtendedInfoCart getCart();

    @Override
    public void loadFunc(LinkedHashMap<String, Operator> funcList) {
        funcList.put("side", ((dst, src) -> {
            if (src != null) return Result.ERR;
            if (dst == null) return Result.ERR;

            if (dst.type == RegType.STR) {
                if (getCart() != null)
                    getCart().setExtendedInfo("side", dst.data.toString());
                return Result.OK;
            }
            return Result.ERR;
        }));
        funcList.put("jets", ((dst, src) -> {
            if (src != null) return Result.ERR;
            if (dst == null) return Result.ERR;

            if (dst.type == RegType.STR) {
                if (getCart() != null)
                    getCart().setExtendedInfo("str", dst.data.toString());
                return Result.OK;
            }
            return Result.ERR;
        }));
        funcList.put("jet", ((dst, src) -> {
            if (src != null) return Result.ERR;
            if (dst == null) return Result.ERR;

            if (dst.type == RegType.STR) {
                if (getCart() != null)
                    getCart().setExtendedInfo("jet", dst.data.toString());
                return Result.OK;
            }
            return Result.ERR;
        }));

    }

}
