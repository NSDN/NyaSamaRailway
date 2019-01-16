package club.nsdn.nyasamatelecom.block;

import club.nsdn.nyasamatelecom.NyaSamaTelecom;
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
        event.getRegistry().registerAll(blocks.toArray(new Block[0]));
    }

    @SubscribeEvent
    public void registerItemBlocks(RegistryEvent.Register<Item> event) {
        for (Block b : blocks) {
            String regName = b.getUnlocalizedName().toLowerCase();
            if (b instanceof IRegisterable)
                regName = ((IRegisterable) b).getID();
            itemBlocks.put(b, new ItemBlock(b).setRegistryName(NyaSamaTelecom.MODID, regName));
        }
        event.getRegistry().registerAll(itemBlocks.values().toArray(new Item[0]));
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void registerItemBlockModels(ModelRegistryEvent event) {
        for (Item i : itemBlocks.values()) {
            String regName = i.getUnlocalizedName().toLowerCase();
            if (i.getRegistryName() != null) regName = i.getRegistryName().toString();
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

        //blocks.put("nspga_t0c0", new BlockNSPGA(NSPGAT0C0.class, "BlockNSPGAT0C0", "nspga_t0c0i8o8r0"));
        //blocks.put("nspga_t4c4", new BlockNSPGA(NSPGAT4C4.class, "BlockNSPGAT4C4", "nspga_t4c4i8o8r0"));

        //blocks.put("nsasm_box", new BlockNSASMBox());
        //blocks.put("signal_box", new BlockSignalBox());
        //blocks.put("signal_box_sender", new BlockSignalBoxSender());
        //blocks.put("signal_box_getter", new BlockSignalBoxGetter());
        //blocks.put("tri_state_signal_box", new BlockTriStateSignalBox());

        //blocks.put("signal_box_input", new BlockRedInput());
        //blocks.put("signal_box_output", new BlockRedOutput());

        //blocks.put("signal_box_rx", new BlockWirelessRx());
        //blocks.put("signal_box_tx", new BlockWirelessTx());

        //blocks.put("rs_latch_box", new BlockRSLatch());
        //blocks.put("timer_box", new BlockTimer());
        //blocks.put("delayer_box", new BlockDelayer());
    }

}