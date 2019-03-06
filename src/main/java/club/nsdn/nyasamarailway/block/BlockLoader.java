package club.nsdn.nyasamarailway.block;

import club.nsdn.nyasamaoptics.api.LightBeam;
import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.tileblock.deco.*;
import club.nsdn.nyasamarailway.tileblock.func.*;
import club.nsdn.nyasamarailway.tileblock.rail.*;
import club.nsdn.nyasamarailway.tileblock.signal.deco.*;
import club.nsdn.nyasamarailway.tileblock.signal.light.*;
import club.nsdn.nyasamarailway.tileblock.signal.trackside.*;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
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
 * Created by drzzm32 on 2019.2.10
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
    public static Block nsTest;

    public static LightBeam light;
    public static LightBeam lineLight;

    public void getLightBeams() {
        light = club.nsdn.nyasamaoptics.block.BlockLoader.light;
        lineLight = club.nsdn.nyasamaoptics.block.BlockLoader.lineLight;
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        NyaSamaRailway.logger.info("registering Blocks");
        event.getRegistry().registerAll(blocks.toArray(new Block[0]));
    }

    @SubscribeEvent
    public void registerItemBlocks(RegistryEvent.Register<Item> event) {
        NyaSamaRailway.logger.info("registering ItemBlocks");
        for (Block b : blocks) {
            String regName = b.getUnlocalizedName().toLowerCase();
            if (b.getRegistryName() != null)
                regName = b.getRegistryName().toString().split(":")[1];
            itemBlocks.put(b, new ItemBlock(b).setRegistryName(NyaSamaRailway.MODID, regName));
        }
        event.getRegistry().registerAll(itemBlocks.values().toArray(new Item[0]));
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void registerItemBlockModels(ModelRegistryEvent event) {
        NyaSamaRailway.logger.info("registering ItemBlock Models (Block's Icon)");
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

        blocks.add(new BlockSign());
        blocks.add(new BlockNSDNLogo());
        logo = new BlockLogo();
        blocks.add(logo);
        nsTest = new BlockNSTest();
        blocks.add(nsTest);

        blocks.add(new BlockIronBars());
        blocks.add(new BlockIronWeb());
        blocks.add(new BlockPlatform());
        blocks.add(new BlockWireRailNode());

        blocks.add(new BlockGlassShield("GlassShieldAl", "glass_shield_al", 1, 1, 0.125));
        blocks.add(new BlockGlassShield("GlassShieldAlHalf", "glass_shield_al_half", 1, 0.5, 0.125));
        blocks.add(new BlockGlassShield("GlassShieldAlBase", "glass_shield_albase", 1, 1.5, 0.125));
        blocks.add(new BlockGlassShieldCorner("GlassShieldCorner", "glass_shield_corner", true));
        blocks.add(new BlockGlassShieldCorner("GlassShieldCornerHalf", "glass_shield_corner_half", false));

        blocks.add(new Pillar());
        blocks.add(new PillarBig());
        blocks.add(new PillarQuad());
        blocks.add(new RailSignBody());
        blocks.add(new RailSignHead("RailSignHeadBeep", "rail_sign_head_beep", "rail_sign_head_beep"));
        blocks.add(new RailSignHead("RailSignHeadCut", "rail_sign_head_cut", "rail_sign_head_cut"));
        blocks.add(new RailSignHead("RailSignHeadJoe", "rail_sign_head_joe", "rail_sign_head_joe"));
        blocks.add(new RailSignHead("RailSignHeadLink", "rail_sign_head_link", "rail_sign_head_link"));
        blocks.add(new RailSignHead("RailSignHeadCutLink", "rail_sign_head_cutlink", "rail_sign_head_cut", "rail_sign_head_link"));
        blocks.add(new RailSignHead("RailSignHeadT", "rail_sign_head_t", "rail_sign_head_t"));
        blocks.add(new RailSignVertical("RailSignVertical1", "rail_sign_vertical_head_1", "rail_sign_vertical_1"));
        blocks.add(new RailSignVertical("RailSignVertical2", "rail_sign_vertical_head_2", "rail_sign_vertical_2"));
        blocks.add(new RailSignVertical("RailSignVertical3", "rail_sign_vertical_head_3", "rail_sign_vertical_3"));
        blocks.add(new RailSignVertical("RailSignVertical4", "rail_sign_vertical_head_4", "rail_sign_vertical_4"));
        blocks.add(new RailSignVertical("RailSignVertical5", "rail_sign_vertical_head_5", "rail_sign_vertical_5"));
        blocks.add(new RailSignVertical("RailSignVertical6", "rail_sign_vertical_head_6", "rail_sign_vertical_6"));
        blocks.add(new RailSignVertical("RailSignVertical7", "rail_sign_vertical_head_7", "rail_sign_vertical_7"));
        blocks.add(new RailSignVertical("RailSignVertical8", "rail_sign_vertical_head_8", "rail_sign_vertical_8"));
        blocks.add(new RailSignVertical("RailSignVertical9", "rail_sign_vertical_head_9", "rail_sign_vertical_9"));
        blocks.add(new RailSignVertical("RailSignVertical10", "rail_sign_vertical_head_10", "rail_sign_vertical_10"));
        blocks.add(new RailSignVertical("RailSignVertical11", "rail_sign_vertical_head_11", "rail_sign_vertical_11"));
        blocks.add(new SignalPillar());

        blocks.add(new CoinBlock());
        blocks.add(new GateBase());
        blocks.add(new GateDoor());
        blocks.add(new GateFrontN());
        blocks.add(new PierTag());
        blocks.add(new TicketBlockCard());
        blocks.add(new TicketBlockOnce());

        blocks.add(new ConvWireMono());
        blocks.add(new MagnetRail());
        blocks.add(new MagnetSwitch());
        blocks.add(new MonoRailBase());
        blocks.add(new MonoRailSwitch());
        blocks.add(new Rail3rd());
        blocks.add(new Rail3rdSwitch());
        blocks.add(new RailBumper());
        blocks.add(new RailStoneSleeper());
        blocks.add(new RailNoSleeper());
        blocks.add(new RailTriSwitch());
        blocks.add(new WireRail());

        blocks.add(new GateFront());
        blocks.add(new GlassShield());
        blocks.add(new GlassShield1X1());
        blocks.add(new GlassShield3X1());
        blocks.add(new GlassShield3X1D5());
        blocks.add(new GlassShield1D5X1D5());
        blocks.add(new GlassShieldHalf());

        blocks.add(new BiSignalLight());
        blocks.add(new PillarSignalBi());
        blocks.add(new PillarSignalOne());
        blocks.add(new PillarSignalTri());
        blocks.add(new SignalLamp());
        blocks.add(new SignalLight());
        blocks.add(new SignalStick());
        blocks.add(new TriSignalLight());

        blocks.add(new TrackSideBlocking());
        blocks.add(new TrackSideBlockingHs());
        blocks.add(new TrackSideReception());
        blocks.add(new TrackSideRFID());
        blocks.add(new TrackSideRFIDHs());
        blocks.add(new TrackSideSniffer());
        blocks.add(new TrackSideSnifferHs());
    }

}