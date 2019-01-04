package club.nsdn.nyasamarailway.item;

/**
 * Created by drzzm32 on 2016.5.5.
 */

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.item.cart.*;
import club.nsdn.nyasamarailway.item.loco.*;
import club.nsdn.nyasamarailway.item.telecom.Item74HC04;
import club.nsdn.nyasamarailway.item.telecom.ItemDevEditor;
import club.nsdn.nyasamarailway.item.telecom.ItemNGT;
import club.nsdn.nyasamarailway.item.tool.*;
import net.minecraft.item.Item;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import java.util.LinkedList;

public class ItemLoader {

    public static Item trainController8Bit;
    public static Item trainController32Bit;
    public static Item item74HC04;
    public static Item item1N4148;
    public static Item itemPierBuilder;
    public static Item itemDevEditor;
    public static Item itemNGT;
    public static Item itemStationSign;
    public static Item itemNSTCT1;
    public static Item itemTrainBase;
    public static Item itemNSPCT1;
    public static Item itemNSPCT2;
    public static Item itemNSPCT3;
    public static Item itemNSPCT4;
    public static Item itemNSPCT4M;
    public static Item itemNSPCT5;
    public static Item itemNSPCT5L;
    public static Item itemNSPCT6;
    public static Item itemNSPCT6C;
    public static Item itemNSPCT6W;
    public static Item itemNSPCT6L;
    public static Item itemNSPCT7;
    public static Item itemNSPCT8;
    public static Item itemNSPCT8C;
    public static Item itemNSPCT8W;
    public static Item itemNSPCT8M;
    public static Item itemNSPCT8J;
    public static Item itemNSPCT9;
    public static Item itemNSPCT9M;
    public static Item itemNSPCT10;
    public static Item itemNSPCT10M;
    public static Item itemNSPCT10J;
    public static Item itemNSBT1;
    public static Item itemNSET1;
    public static Item itemNSET2;

    public static Item itemTicketOnce;
    public static Item itemNyaCard;
    public static Item itemNyaCoin;
    public static LinkedList<ItemTicketStore> itemsNyaGift;

    private static void register(Item item, String name) {
        GameRegistry.registerItem(item, name);
    }

    public ItemLoader(FMLPreInitializationEvent event) {
        trainController8Bit = new ItemNTP8Bit();
        register(trainController8Bit, "train_controller_8bit");

        trainController32Bit = new ItemNTP32Bit();
        register(trainController32Bit, "train_controller_32bit");

        item74HC04 = new Item74HC04();
        register(item74HC04, "item_74hc04");

        item1N4148 = new Item1N4148();
        register(item1N4148, "item_1n4148");

        itemPierBuilder = new ItemPierBuilder();
        register(itemPierBuilder, "item_pier_builder");

        itemDevEditor = new ItemDevEditor();
        register(itemDevEditor, "item_dev_editor");

        itemNGT = new ItemNGT();
        register(itemNGT, "item_ngt");

        itemStationSign = new ItemStationSign();
        register(itemStationSign, "item_station_sign");

        itemNSTCT1 = new ItemNSTCT1();
        register(itemNSTCT1, "item_nstc_1");

        itemNSPCT1 = new ItemNSPCT1();
        register(itemNSPCT1, "item_nspc_1");

        itemNSPCT2 = new ItemNSPCT2();
        register(itemNSPCT2, "item_nspc_2");

        itemNSPCT3 = new ItemNSPCT3();
        register(itemNSPCT3, "item_nspc_3");

        itemNSPCT4 = new ItemNSPCT4();
        register(itemNSPCT4, "item_nspc_4");

        itemNSPCT4M = new ItemNSPCT4M();
        register(itemNSPCT4M, "item_nspc_4m");

        itemNSPCT5 = new ItemNSPCT5();
        register(itemNSPCT5, "item_nspc_5");

        itemNSPCT5L = new ItemNSPCT5L();
        register(itemNSPCT5L, "item_nspc_5l");

        itemNSPCT6 = new ItemNSPCT6();
        register(itemNSPCT6, "item_nspc_6");

        itemNSPCT6C = new ItemNSPCT6C();
        register(itemNSPCT6C, "item_nspc_6c");

        itemNSPCT6W = new ItemNSPCT6W();
        register(itemNSPCT6W, "item_nspc_6w");

        itemNSPCT6L = new ItemNSPCT6L();
        register(itemNSPCT6L, "item_nspc_6l");

        itemNSPCT7 = new ItemNSPCT7();
        register(itemNSPCT7, "item_nspc_7");

        itemNSPCT8 = new ItemNSPCT8();
        register(itemNSPCT8, "item_nspc_8");

        itemNSPCT8C = new ItemNSPCT8C();
        register(itemNSPCT8C, "item_nspc_8c");

        itemNSPCT8W = new ItemNSPCT8W();
        register(itemNSPCT8W, "item_nspc_8w");

        itemNSPCT8M = new ItemNSPCT8M();
        register(itemNSPCT8M, "item_nspc_8m");

        itemNSPCT8J = new ItemNSPCT8J();
        register(itemNSPCT8J, "item_nspc_8j");

        itemNSPCT9 = new ItemNSPCT9();
        register(itemNSPCT9, "item_nspc_9");

        itemNSPCT9M = new ItemNSPCT9M();
        register(itemNSPCT9M, "item_nspc_9m");

        itemNSPCT10 = new ItemNSPCT10();
        register(itemNSPCT10, "item_nspc_10");

        itemNSPCT10M = new ItemNSPCT10M();
        register(itemNSPCT10M, "item_nspc_10m");

        itemNSPCT10J = new ItemNSPCT10J();
        register(itemNSPCT10J, "item_nspc_10j");

        itemNSBT1 = new ItemNSBT1();
        register(itemNSBT1, "item_nsb_1");

        itemNSET1 = new ItemNSET1();
        register(itemNSET1, "item_nse_1");

        itemNSET2 = new ItemNSET2();
        register(itemNSET2, "item_nse_2");

        itemTicketOnce = new ItemTicketOnce();
        register(itemTicketOnce, "item_ticket_once");

        itemNyaCard = new ItemTicketStore("ItemNyaCard", "item_nyacard");
        register(itemNyaCard, "item_nyacard");

        itemNyaCoin = new ItemNyaCoin();
        register(itemNyaCoin, "item_nyacoin");

        itemsNyaGift = new LinkedList<>();
        itemsNyaGift.add(new ItemTicketStore("ItemNyaGift1", "item_nyagift_1","cards/item_giftcard_1"));
        itemsNyaGift.add(new ItemTicketStore("ItemNyaGift2", "item_nyagift_2","cards/item_giftcard_2"));
        itemsNyaGift.add(new ItemTicketStore("ItemNyaGift3", "item_nyagift_3","cards/item_giftcard_3"));
        itemsNyaGift.add(new ItemTicketStore("ItemNyaGift4", "item_nyagift_4","cards/item_giftcard_4"));
        itemsNyaGift.add(new ItemTicketStore("ItemNyaGift5", "item_nyagift_5","cards/item_giftcard_5"));
        itemsNyaGift.add(new ItemTicketStore("ItemNyaGift6", "item_nyagift_6","cards/item_giftcard_6"));
        itemsNyaGift.add(new ItemTicketStore("ItemNyaGift7", "item_nyagift_7","cards/item_giftcard_7"));
        for (ItemTicketStore i : itemsNyaGift) register(i, i.id);

        itemTrainBase = new ItemTrainBase();
        if (NyaSamaRailway.isDebug) register(itemTrainBase, "item_train_base");
    }
}
