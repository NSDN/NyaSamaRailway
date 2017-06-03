package club.nsdn.nyasamarailway.Items;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

    @SideOnly(Side.CLIENT)
    public static void preLoadModels() {
        for (String id : items.keySet()) {
            ModelBakery.registerItemVariants(items.get(id), new ResourceLocation(NyaSamaRailway.modid, id));
        }
    }

    @SideOnly(Side.CLIENT)
    public static void loadModels() {
        for (String id : items.keySet()) {
            Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(
                    items.get(id), 0,
                    new ModelResourceLocation(NyaSamaRailway.modid + ":" + id, "inventory")
            );

        }

    }
}
