package club.nsdn.nyasamaoptics.creativetab;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.*;
import net.minecraft.creativetab.CreativeTabs;
import club.nsdn.nyasamaoptics.block.BlockLoader;

/**
 * Created by drzzm32 on 2019.1.30.
 */
public class CreativeTabLoader {

    public static CreativeTabs tabNyaSamaOptics;

    public CreativeTabLoader(FMLPreInitializationEvent event) {
        tabNyaSamaOptics = new CreativeTabs("tabNyaSamaOptics") {
            @Override
            public ItemStack getTabIconItem() {
                return BlockLoader.itemBlocks.get(BlockLoader.logo).getDefaultInstance();
            }
        };
    }

}
