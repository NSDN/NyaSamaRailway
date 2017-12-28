package club.nsdn.nyasamarailway.item.telecom;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.network.NetworkWrapper;
import club.nsdn.nyasamatelecom.api.tool.NGTablet;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;

/**
 * Created by drzzm32 on 2016.10.25.
 */
public class ItemNGT extends NGTablet {

    public ItemNGT() {
        super(NyaSamaRailway.MODID, NetworkWrapper.instance, "ItemNGT", "item_ngt");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Override
    public SimpleNetworkWrapper getWrapper() {
        return NetworkWrapper.instance;
    }

}
