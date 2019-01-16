package club.nsdn.nyasamatelecom.item.tool;

import club.nsdn.nyasamatelecom.NyaSamaTelecom;
import club.nsdn.nyasamatelecom.api.tool.NGTablet;
import club.nsdn.nyasamatelecom.creativetab.CreativeTabLoader;
import club.nsdn.nyasamatelecom.network.NetworkWrapper;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

/**
 * Created by drzzm32 on 2018.12.13.
 */
public class ItemNGTablet extends NGTablet {

    public ItemNGTablet() {
        super(NyaSamaTelecom.MODID, NetworkWrapper.instance, "ItemNGTablet", "item_ngt");
        setCreativeTab(CreativeTabLoader.tabNyaSamaTelecom);
    }

    @Override
    public SimpleNetworkWrapper getNetworkWrapper() {
        return NetworkWrapper.instance;
    }

}
