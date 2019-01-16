package club.nsdn.nyasamatelecom.creativetab;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.*;
import net.minecraft.creativetab.CreativeTabs;
import club.nsdn.nyasamatelecom.block.BlockLoader;

/**
 * Created by drzzm32 on 2018.12.12.
 */
public class CreativeTabLoader {

    public static CreativeTabs tabNyaSamaTelecom;

    public CreativeTabLoader(FMLPreInitializationEvent event) {
        tabNyaSamaTelecom = new CreativeTabs("tabNyaSamaTelecom") {
            @Override
            public ItemStack getTabIconItem() {
                return BlockLoader.itemBlocks.get(BlockLoader.logo).getDefaultInstance();
            }
        };
    }

}
