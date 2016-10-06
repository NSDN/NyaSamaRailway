package club.nsdn.nyasamarailway.Items;

/**
 * Created by drzzm32 on 2016.5.5.
 */

import club.nsdn.nyasamarailway.NyaSamaRailway;
import net.minecraft.item.Item;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ItemLoader {

    public static Item trainController8Bit;
    public static Item trainController32Bit;
    public static Item item74HC04;
    public static Item itemStationSign;
    public static Item itemMinecartBase;
    public static Item itemTrainBase;
    public static Item itemNSPCT1;
    public static Item itemNSPCT2;
    public static Item itemNSPCT3;
    public static Item itemNSBT1;
    public static Item itemNSET1;

    private static void register(Item item, String name) {
        GameRegistry.registerItem(item, name);
    }

    public ItemLoader(FMLPreInitializationEvent event) {
        trainController8Bit = new ItemTrainController8Bit();
        register(trainController8Bit, "train_controller_8bit");

        trainController32Bit = new ItemTrainController32Bit();
        register(trainController32Bit, "train_controller_32bit");

        item74HC04 = new Item74HC04();
        register(item74HC04, "item_74hc04");

        itemStationSign = new ItemStationSign();
        register(itemStationSign, "item_station_sign");

        itemMinecartBase = new ItemMinecartBase();
        register(itemMinecartBase, "item_minecart_base");

        itemNSPCT1 = new ItemNSPCT1();
        register(itemNSPCT1, "item_nspc_1");

        itemNSPCT2 = new ItemNSPCT2();
        register(itemNSPCT2, "item_nspc_2");

        itemNSPCT3 = new ItemNSPCT3();
        register(itemNSPCT3, "item_nspc_3");

        itemNSBT1 = new ItemNSBT1();
        register(itemNSBT1, "item_nsb_1");

        itemNSET1 = new ItemNSET1();
        register(itemNSET1, "item_nse_1");

        itemTrainBase = new ItemTrainBase();
        if (NyaSamaRailway.isDebug) register(itemTrainBase, "item_train_base");
    }
}
