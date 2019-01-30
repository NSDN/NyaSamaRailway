package club.nsdn.nyasamatelecom.block;

import club.nsdn.nyasamatelecom.NyaSamaTelecom;
import club.nsdn.nyasamatelecom.tileblock.core.*;
import club.nsdn.nyasamatelecom.tileblock.redstone.*;
import club.nsdn.nyasamatelecom.tileblock.wireless.*;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import cn.ac.nya.nspga.*;

import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * Created by drzzm32 on 2018.12.12.
 */
public class BlockLoader {

    private static BlockLoader instance;
    public static BlockLoader instance() {
        if (instance == null) instance = new BlockLoader();
        return instance;
    }

    public static LinkedList<Block> blocks;
    public static LinkedHashMap<Block, Item> itemBlocks;
    public static Block logo;

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        NyaSamaTelecom.logger.info("registering Blocks");
        event.getRegistry().registerAll(blocks.toArray(new Block[0]));
    }

    @SubscribeEvent
    public void registerItemBlocks(RegistryEvent.Register<Item> event) {
        NyaSamaTelecom.logger.info("registering ItemBlocks");
        for (Block b : blocks) {
            String regName = b.getUnlocalizedName().toLowerCase();
            if (b.getRegistryName() != null)
                regName = b.getRegistryName().toString().split(":")[1];
            itemBlocks.put(b, new ItemBlock(b).setRegistryName(NyaSamaTelecom.MODID, regName));
        }
        event.getRegistry().registerAll(itemBlocks.values().toArray(new Item[0]));
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void registerItemBlockModels(ModelRegistryEvent event) {
        NyaSamaTelecom.logger.info("registering ItemBlock Models (Block's Icon)");
        for (Item i : itemBlocks.values()) {
            String regName = i.getUnlocalizedName().toLowerCase();
            if (i.getRegistryName() != null)
                regName = i.getRegistryName().toString();
            ModelLoader.setCustomModelResourceLocation(i, 0,
                    new ModelResourceLocation(regName, "inventory")
            );
        }
    }

    public BlockLoader() {
        blocks = new LinkedList<>();
        itemBlocks = new LinkedHashMap<>();

        logo = new BlockLogo();
        blocks.add(logo);

        blocks.add(new BlockSign());
        blocks.add(new BlockNSDNLogo());

        blocks.add(new BlockNSPGA(NSPGAT0C0.class, "BlockNSPGAT0C0", "nspga_t0c0i8o8r0"));
        blocks.add(new BlockNSPGA(NSPGAT4C4.class, "BlockNSPGAT4C4", "nspga_t4c4i8o8r0"));

        blocks.add(new BlockNSASMBox());
        blocks.add(new BlockSignalBox());
        blocks.add(new BlockSignalBoxSender());
        blocks.add(new BlockSignalBoxGetter());
        blocks.add(new BlockTriStateSignalBox());

        blocks.add(new BlockRedInput());
        blocks.add(new BlockRedOutput());

        blocks.add(new BlockWirelessRx());
        blocks.add(new BlockWirelessTx());

        blocks.add(new BlockRSLatch());
        blocks.add(new BlockTimer());
        blocks.add(new BlockDelayer());
    }

}