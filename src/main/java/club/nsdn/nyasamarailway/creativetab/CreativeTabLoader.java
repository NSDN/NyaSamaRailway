package club.nsdn.nyasamarailway.creativetab;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.*;
import net.minecraft.creativetab.CreativeTabs;
import club.nsdn.nyasamarailway.block.BlockLoader;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class CreativeTabLoader {

    public static CreativeTabs tabNyaSamaRailway;
    public static CreativeTabs tabNSTest;

    public CreativeTabLoader(FMLPreInitializationEvent event) {
        tabNyaSamaRailway = new CreativeTabs("tabNyaSamaRailway") {
            @Override
            public ItemStack getTabIconItem() {
                return BlockLoader.itemBlocks.get(BlockLoader.logo).getDefaultInstance();
            }
        };
        tabNSTest = new CreativeTabs("tabNSTest") {
            @Override
            public ItemStack getTabIconItem() {
                return BlockLoader.itemBlocks.get(BlockLoader.nsTest).getDefaultInstance();
            }
        };
    }

}
