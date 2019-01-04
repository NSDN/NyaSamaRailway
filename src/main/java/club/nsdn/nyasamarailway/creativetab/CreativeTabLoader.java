package club.nsdn.nyasamarailway.creativetab;

/**
 * Created by drzzm32 on 2016.5.5.
 */

import net.minecraft.item.Item;
import cpw.mods.fml.common.event.*;
import net.minecraft.creativetab.CreativeTabs;
import club.nsdn.nyasamarailway.block.BlockLoader;

public class CreativeTabLoader {

    public static CreativeTabs tabNyaSamaRailway;
    public static CreativeTabs tabNSTest;

    public CreativeTabLoader(FMLPreInitializationEvent event) {
        tabNyaSamaRailway = new CreativeTabs("tabNyaSamaRailway") {
            @Override
            public Item getTabIconItem() {
                return Item.getItemFromBlock(BlockLoader.blockNSRLogo);
            }
        };
        tabNSTest = new CreativeTabs("tabNSTest") {
            @Override
            public Item getTabIconItem() {
                return Item.getItemFromBlock(BlockLoader.blockNSTest);
            }
        };
    }

}
