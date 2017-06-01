package club.nsdn.nyasamarailway.Items;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.LinkedHashMap;

/**
 * Created by drzzm32 on 2017.5.21.
 */
public class ItemLoader {

    public static LinkedHashMap<String, Item> items;

    private static void register(Item item) {
        GameRegistry.register(item);
    }

    private static void addItem(Item item) {
        if (item instanceof IItemBase) {
            items.put(((IItemBase) item).getRegisterID(), item);
        } else {
            items.put(item.getUnlocalizedName(), item);
        }
    }

    public ItemLoader(FMLPreInitializationEvent event) {
        items = new LinkedHashMap<>();

        addItem(new ItemTrainController8Bit());
        addItem(new ItemTrainController32Bit());

        for (String id : items.keySet()) register(items.get(id));
    }
}
