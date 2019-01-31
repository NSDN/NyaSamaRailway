package club.nsdn.nyasamaoptics.item;

import club.nsdn.nyasamaoptics.NyaSamaOptics;
import club.nsdn.nyasamaoptics.block.BlockLoader;
import club.nsdn.nyasamaoptics.util.font.FontLoader;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.LinkedList;

/**
 * Created by drzzm32 on 2019.1.30.
 */
public class ItemLoader {

    private static ItemLoader instance;
    public static ItemLoader instance() {
        if (instance == null) instance = new ItemLoader();
        return instance;
    }

    public static LinkedList<Item> items;

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        NyaSamaOptics.logger.info("registering Items");
        event.getRegistry().registerAll(items.toArray(new Item[0]));
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void registerItemModels(ModelRegistryEvent event) {
        NyaSamaOptics.logger.info("registering ItemModels (Item's Icon)");
        for (Item i : items) {
            String regName = i.getUnlocalizedName().toLowerCase();
            if (i.getRegistryName() != null) regName = i.getRegistryName().toString();
            ModelLoader.setCustomModelResourceLocation(i, 0,
                    new ModelResourceLocation(regName, "inventory")
            );
        }
    }

    public ItemLoader() {
        items = new LinkedList<>();

        items.add(new NSOConv());
    }
}
