package club.nsdn.nyasamarailway.CreativeTab;

/**
 * Created by drzzm32 on 2016.5.5.
 */

import net.minecraft.item.Item;
import cpw.mods.fml.common.event.*;
import net.minecraft.creativetab.CreativeTabs;
import club.nsdn.nyasamarailway.Blocks.BlockLoader;

public class CreativeTabLoader {

    public static CreativeTabs tabNyaSamaRailway;

    public CreativeTabLoader(FMLPreInitializationEvent event) {
        tabNyaSamaRailway = new CreativeTabs("tabNyaSamaRailway") {
            @Override
            public Item getTabIconItem() {
                return Item.getItemFromBlock(BlockLoader.blockSign);
            }
        };
    }

}
