package club.nsdn.nyasamarailway.item;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.api.cart.AbsTrainBase;
import club.nsdn.nyasamarailway.item.cart.*;
import club.nsdn.nyasamarailway.item.loco.*;
import club.nsdn.nyasamarailway.item.misc.*;
import club.nsdn.nyasamarailway.item.nsc.*;
import club.nsdn.nyasamarailway.item.tool.*;
import club.nsdn.nyasamarailway.item.train.*;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class ItemLoader {

    private static ItemLoader instance;
    public static ItemLoader instance() {
        if (instance == null) instance = new ItemLoader();
        return instance;
    }

    public static LinkedList<Item> items;

    public static LinkedHashMap<Class<? extends EntityMinecart>, AbsItemCart> itemCarts;
    public static LinkedHashMap<Class<? extends AbsTrainBase>, AbsItemTrain> itemTrains;

    public static ItemNyaCoin nyaCoin;
    public static ItemTicketOnce oneCard;
    public static ItemTicketStore nyaCard;
    public static LinkedList<ItemTicketStore> itemNyaGifts;

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        NyaSamaRailway.logger.info("registering Items");
        event.getRegistry().registerAll(items.toArray(new Item[0]));

        for (Item i : items) {
            if (i instanceof AbsItemCart)
                itemCarts.put(((AbsItemCart) i).cartClass, ((AbsItemCart) i));
            if (i instanceof AbsItemTrain)
                itemTrains.put(((AbsItemTrain) i).trainClass, ((AbsItemTrain) i));
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void registerItemModels(ModelRegistryEvent event) {
        NyaSamaRailway.logger.info("registering ItemModels (Item's Icon)");
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
        itemCarts = new LinkedHashMap<>();
        itemTrains = new LinkedHashMap<>();

        items.add(new Item74HC04());
        items.add(new Item1N4148());
        items.add(new ItemNTP8Bit());
        items.add(new ItemNTP32Bit());
        items.add(new ItemPierBuilder());

        nyaCoin = new ItemNyaCoin();
        items.add(nyaCoin);
        oneCard = new ItemTicketOnce();
        items.add(oneCard);
        nyaCard = new ItemTicketStore("ItemNyaCard", "item_nyacard");
        items.add(nyaCard);

        items.add(new ItemNSPCT4());
        items.add(new ItemNSPCT7());
        items.add(new ItemNSPCT8());
        items.add(new ItemNSPCT8W());
        items.add(new ItemNSPCT9());
        items.add(new ItemNSPCT10());

        items.add(new ItemNSET2());
        items.add(new ItemNSET3());
        items.add(new ItemNSPCT4M());
        items.add(new ItemNSPCT8C());
        items.add(new ItemNSPCT8J());
        items.add(new ItemNSPCT8M());
        items.add(new ItemNSPCT9M());
        items.add(new ItemNSPCT10J());
        items.add(new ItemNSPCT10M());

        items.add(new ItemNSC1A());
        items.add(new ItemNSC1AM());
        items.add(new ItemNSC1B());
        items.add(new ItemNSC1BM());
        items.add(new ItemNSC2A());
        items.add(new ItemNSC2AM());
        items.add(new ItemNSC2B());
        items.add(new ItemNSC2BM());
        items.add(new ItemNSC3A());
        items.add(new ItemNSC3AM());
        items.add(new ItemNSC3B());
        items.add(new ItemNSC3BM());

        items.add(new ItemNSBT2());
        items.add(new ItemNSBT2M());
        items.add(new ItemNSRM1());
        items.add(new ItemNSRM1T());
        items.add(new ItemNSBT3());
        items.add(new ItemNSBT3M());
        items.add(new ItemNSRM2());
        items.add(new ItemNSRM2T());

        items.add(new ItemNSRM3());
        items.add(new ItemNSRM3T());

        items.add(new ItemNSE4());

        items.add(new ItemNSRA1());
        items.add(new ItemNSRA2());

        items.add(new ItemNSRD1());
        items.add(new ItemNSRD2());

        itemNyaGifts = new LinkedList<>();
        itemNyaGifts.add(new ItemTicketStore("ItemNyaGift1", "item_nyagift_1","item_nyagift_1"));
        itemNyaGifts.add(new ItemTicketStore("ItemNyaGift2", "item_nyagift_2","item_nyagift_2"));
        itemNyaGifts.add(new ItemTicketStore("ItemNyaGift3", "item_nyagift_3","item_nyagift_3"));
        itemNyaGifts.add(new ItemTicketStore("ItemNyaGift4", "item_nyagift_4","item_nyagift_4"));
        itemNyaGifts.add(new ItemTicketStore("ItemNyaGift5", "item_nyagift_5","item_nyagift_5"));
        itemNyaGifts.add(new ItemTicketStore("ItemNyaGift6", "item_nyagift_6","item_nyagift_6"));
        itemNyaGifts.add(new ItemTicketStore("ItemNyaGift7", "item_nyagift_7","item_nyagift_7"));
        items.addAll(itemNyaGifts);
    }

}
