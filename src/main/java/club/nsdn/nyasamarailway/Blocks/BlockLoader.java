package club.nsdn.nyasamarailway.Blocks;

/**
 * Created by drzzm32 on 2016.5.5.
 */

import net.minecraft.block.Block;
import club.nsdn.nyasamarailway.TileEntities.*;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class BlockLoader {

    public static Block blockSign;
    public static Block blockNSDNLogo;
    public static Block blockNyaSamaRailwayLogo;
    public static Block blockTrackPlate;
    public static Block blockTrackShelf;
    public static Block blockTrackShelfLow;
    public static Block blockRailStoneSleeper;
    public static Block blockRailNoSleeper;
    public static Block blockRailStoneSleeperPowered;
    public static Block blockRailNoSleeperPowered;
    public static Block blockRailStoneSleeperDetector;
    public static Block blockRailNoSleeperDetector;
    public static Block blockRailProtectBody;
    public static Block blockRailProtectHead;
    public static Block blockRailProtectHeadAnti;
    public static Block blockRailReception;
    public static Block blockRailReceptionAnti;
    public static Block blockIronBars;
    public static Block blockTBridgeHead;
    public static Block blockTBridgeHeadNoRib;
    public static Block blockTBridgeBody;
    public static Block blockTBridgeBodyNoRib;
    public static Block blockTBridgeShoulder;
    public static Block blockHalfBlock;
    public static Block blockHalfHalfBlock;
    public static Block blockPlatform;
    public static Block blockRailSignBody;
    public static Block blockRailSignHeadBeep;
    public static Block blockRailSignHeadCut;
    public static Block blockRailSignHeadJoe;
    public static Block blockRailSignHeadLink;
    public static Block blockRailSignHeadT;

    private static void register(Block block, String name) {
        GameRegistry.registerBlock(block, name);
    }

    public BlockLoader(FMLPreInitializationEvent event) {
        blockSign = new BlockSign();
        register(blockSign, "nyasamarailway_block_sign");

        blockNSDNLogo = new BlockNSDNLogo();
        register(blockNSDNLogo, "nyasamarailway_nsdn_logo");

        blockNyaSamaRailwayLogo = new BlockNyaSamaRailwayLogo();
        register(blockNyaSamaRailwayLogo, "nyasamarailway_logo");

        blockTrackPlate = new TileEntityTrackPlate();
        register(blockTrackPlate, "block_track_plate");

        blockTrackShelf = new TileEntityTrackShelf();
        register(blockTrackShelf, "block_track_shelf");

        blockTrackShelfLow = new TileEntityTrackShelfLow();
        register(blockTrackShelfLow, "block_track_shelf_low");

        blockRailStoneSleeper = new BlockRailStoneSleeper();
        register(blockRailStoneSleeper, "block_rail_stone_sleeper");

        blockRailNoSleeper = new BlockRailNoSleeper();
        register(blockRailNoSleeper, "block_rail_no_sleeper");

        blockRailStoneSleeperPowered = new BlockRailStoneSleeperPowered();
        register(blockRailStoneSleeperPowered, "block_rail_stone_sleeper_powered");

        blockRailNoSleeperPowered = new BlockRailNoSleeperPowered();
        register(blockRailNoSleeperPowered, "block_rail_no_sleeper_powered");

        blockRailStoneSleeperDetector = new BlockRailStoneSleeperDetector();
        register(blockRailStoneSleeperDetector, "block_rail_stone_sleeper_detector");

        blockRailNoSleeperDetector = new BlockRailNoSleeperDetector();
        register(blockRailNoSleeperDetector, "block_rail_no_sleeper_detector");

        blockRailProtectHead = new BlockRailProtectHead();
        register(blockRailProtectHead, "block_rail_protect_head");

        blockRailProtectHeadAnti = new BlockRailProtectHeadAnti();
        register(blockRailProtectHeadAnti, "block_rail_protect_head_anti");

        blockRailProtectBody = new BlockRailProtectBody();
        register(blockRailProtectBody, "block_rail_protect_body");

        blockRailReception = new BlockRailReception();
        register(blockRailReception, "block_rail_reception");

        blockRailReceptionAnti = new BlockRailReceptionAnti();
        register(blockRailReceptionAnti, "block_rail_reception_anti");


        blockIronBars = new BlockIronBars();
        register(blockIronBars, "rail_iron_bars");

        blockTBridgeHead = new TileEntityTBridgeHead();
        register(blockTBridgeHead, "block_t_bridge_head");

        blockTBridgeHeadNoRib = new TileEntityTBridgeHeadNoRib();
        register(blockTBridgeHeadNoRib, "block_t_bridge_head_no_rib");

        blockTBridgeBody = new TileEntityTBridgeBody();
        register(blockTBridgeBody, "block_t_bridge_body");

        blockTBridgeBodyNoRib = new TileEntityTBridgeBodyNoRib();
        register(blockTBridgeBodyNoRib, "block_t_bridge_body_no_rib");

        blockTBridgeShoulder = new TileEntityTBridgeShoulder();
        register(blockTBridgeShoulder, "block_t_bridge_shoulder");

        blockHalfBlock = new TileEntityHalfBlock();
        register(blockHalfBlock, "block_half_block");

        blockHalfHalfBlock = new TileEntityHalfHalfBlock();
        register(blockHalfHalfBlock, "block_half_half_block");

        blockPlatform = new TileEntityPlatform();
        register(blockPlatform, "block_platform");

        blockRailSignBody = new TileEntityRailSignBody();
        register(blockRailSignBody, "block_rail_sign_body");

        blockRailSignHeadBeep = new TileEntityRailSignHeadBeep();
        register(blockRailSignHeadBeep, "block_rail_sign_head_beep");

        blockRailSignHeadCut = new TileEntityRailSignHeadCut();
        register(blockRailSignHeadCut, "block_rail_sign_head_cut");

        blockRailSignHeadJoe = new TileEntityRailSignHeadJoe();
        register(blockRailSignHeadJoe, "block_rail_sign_head_joe");

        blockRailSignHeadLink = new TileEntityRailSignHeadLink();
        register(blockRailSignHeadLink, "block_rail_sign_head_link");

        blockRailSignHeadT = new TileEntityRailSignHeadT();
        register(blockRailSignHeadT, "block_rail_sign_head_t");

    }

}