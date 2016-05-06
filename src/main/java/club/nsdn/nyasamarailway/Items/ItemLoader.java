package club.nsdn.nyasamarailway.Items;

/**
 * Created by drzzm32 on 2016.5.5.
 */

import net.minecraft.item.Item;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ItemLoader {

    public static Item insulatorSquareSmall;

    private static void register(Item item, String name) {
        GameRegistry.registerItem(item, name);
    }

    public ItemLoader(FMLPreInitializationEvent event) {
        insulatorSquareSmall = new ItemInsulatorSquareSmall();
        register(insulatorSquareSmall, "insulator_square_small_item");

    }
}
