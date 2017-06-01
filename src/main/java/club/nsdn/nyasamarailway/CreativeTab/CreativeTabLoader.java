package club.nsdn.nyasamarailway.CreativeTab;

import club.nsdn.nyasamarailway.Blocks.BlockNyaSamaRailwayLogo;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.*;
import net.minecraft.creativetab.CreativeTabs;
import club.nsdn.nyasamarailway.Blocks.BlockLoader;

/**
 * Created by drzzm32 on 2017.5.21.
 */
public class CreativeTabLoader {

    public static CreativeTabs tabNyaSamaRailway;

    public CreativeTabLoader(FMLPreInitializationEvent event) {
        tabNyaSamaRailway = new CreativeTabs("tabNyaSamaRailway") {
            @Override
            public Item getTabIconItem() {
                return Item.getItemFromBlock(BlockLoader.blocks.get(new BlockNyaSamaRailwayLogo().getRegisterID()));
            }
        };
    }

}
