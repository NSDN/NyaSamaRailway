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
    public static Block blockTrackPlate;
    public static Block blockTrackShelf;
    public static Block blockTrackShelfLow;
    public static Block blockRailStoneSleeper;
    public static Block blockRailNoSleeper;
    public static Block blockRailProtectBody;
    public static Block blockRailProtectHead;
    public static Block blockRailProtectHeadAnti;

    private static void register(Block block, String name) {
        GameRegistry.registerBlock(block, name);
    }

    public BlockLoader(FMLPreInitializationEvent event) {
        blockSign = new BlockSign();
        register(blockSign, "nyasamarailway_block_sign");

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

        blockRailProtectHead = new BlockRailProtectHead();
        register(blockRailProtectHead, "block_rail_protect_head");

        blockRailProtectHeadAnti = new BlockRailProtectHeadAnti();
        register(blockRailProtectHeadAnti, "block_rail_protect_head_anti");

        blockRailProtectBody = new BlockRailProtectBody();
        register(blockRailProtectBody, "block_rail_protect_body");

    }

}