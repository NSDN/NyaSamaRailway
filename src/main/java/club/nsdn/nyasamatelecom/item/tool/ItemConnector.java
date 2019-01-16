package club.nsdn.nyasamatelecom.item.tool;

import club.nsdn.nyasamatelecom.NyaSamaTelecom;
import club.nsdn.nyasamatelecom.api.tool.Connector;
import club.nsdn.nyasamatelecom.creativetab.CreativeTabLoader;

/**
 * Created by drzzm32 on 2018.12.13.
 */
public class ItemConnector extends Connector {

    public ItemConnector() {
        super(NyaSamaTelecom.MODID, "ItemConnector", "connector");
        setCreativeTab(CreativeTabLoader.tabNyaSamaTelecom);
    }

}
