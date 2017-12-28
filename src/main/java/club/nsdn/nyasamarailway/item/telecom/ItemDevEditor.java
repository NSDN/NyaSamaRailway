package club.nsdn.nyasamarailway.item.telecom;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamatelecom.api.tool.DevEditor;

/**
 * Created by drzzm32 on 2016.9.12.
 */
public class ItemDevEditor extends DevEditor {

    public ItemDevEditor() {
        super(NyaSamaRailway.MODID, "ItemDevEditor", "device_editor");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

}
