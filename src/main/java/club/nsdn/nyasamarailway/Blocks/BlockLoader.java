package club.nsdn.nyasamarailway.Blocks;

/**
 * Created by drzzm32 on 2016.5.5.
 */

import club.nsdn.nyasamarailway.TileEntities.Rail.*;
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

    /*------*/
    public static Block blockRailStoneSleeperDetector5s;
    public static Block blockRailNoSleeperDetector5s;
    public static Block blockRailStoneSleeperDetector15s;
    public static Block blockRailNoSleeperDetector15s;
    public static Block blockRailStoneSleeperDetector30s;
    public static Block blockRailNoSleeperDetector30s;
    /*------*/

    public static Block blockRailProtectBody;
    public static Block blockRailProtectHead;
    public static Block blockRailProtectHeadAnti;
    public static Block blockRailReception;
    public static Block blockRailReceptionAnti;
    public static Block blockRailSignalTransfer;
    public static Block blockRailSpeedLimit;
    public static Block blockRailDirectional;
    public static Block blockRailDirectionalAnti;
    public static Block blockRailBlocking;
    public static Block blockRailNoSleeperBlocking;

    public static Block blockBumperStoneSleeper;
    public static Block blockBumperNoSleeper;

    public static Block blockIronBars;
    public static Block blockIronWeb;
    public static Block blockTBridgeHead;
    public static Block blockTBridgeHeadNoRib;
    public static Block blockTBridgeBody;
    public static Block blockTBridgeBodyNoRib;
    public static Block blockTBridgeShoulder;
    public static Block blockHalfBlock;
    public static Block blockHalfHalfBlock;
    public static Block blockPlatform;
    public static Block blockEdge;
    public static Block blockRailSignBody;
    public static Block blockRailSignHeadBeep;
    public static Block blockRailSignHeadCut;
    public static Block blockRailSignHeadJoe;
    public static Block blockRailSignHeadLink;
    public static Block blockRailSignHeadCutLink;
    public static Block blockRailSignHeadT;

    public static Block blockRailSignVertical1;
    public static Block blockRailSignVertical2;
    public static Block blockRailSignVertical3;
    public static Block blockRailSignVertical4;
    public static Block blockRailSignVertical5;

    public static Block railNoSleeperStraight;
    public static Block railMono;
    public static Block railMonoBumper;
    public static Block railMonoMagnet;
    public static Block railMonoSwitch;
    public static Block railMonoMagnetPowered;
    public static Block railMonoMagnetDetector;
    public static Block railMonoMagnetDetector5s;
    public static Block railMonoMagnetDetector15s;
    public static Block railMonoMagnetDetector30s;
    public static Block railMonoMagnetReception;
    public static Block railMonoMagnetReceptionAnti;
    public static Block railMonoMagnetDirectional;
    public static Block railMonoMagnetDirectionalAnti;
    public static Block railMonoMagnetSpeedLimit;
    public static Block railMonoMagnetSignalTransfer;
    public static Block railMonoMagnetBlocking;

    public static Block blockStationSign;

    public static Block blockSignalLight;
    public static Block blockSignalBox;
    public static Block blockSignalBoxSender;
    public static Block blockTriStateSignalBox;

    public static Block blockGateBase;
    public static Block blockGateDoor;
    public static Block blockGateFront;
    public static Block blockGateFrontN;

    public static Block blockGlassShield;
    public static Block blockGlassShieldHalf;
    public static Block blockGlassShieldAl;
    public static Block blockGlassShieldAlHalf;

    public static Block blockTicketOnce;
    public static Block blockTicketCard;
    public static Block blockCoin;

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

        /*------*/
        blockRailStoneSleeperDetector5s = new BlockRailStoneSleeperDetector(5);
        register(blockRailStoneSleeperDetector5s, "block_rail_stone_sleeper_detector_5s");
        blockRailNoSleeperDetector5s = new BlockRailNoSleeperDetector(5);
        register(blockRailNoSleeperDetector5s, "block_rail_no_sleeper_detector_5s");
        blockRailStoneSleeperDetector15s = new BlockRailStoneSleeperDetector(15);
        register(blockRailStoneSleeperDetector15s, "block_rail_stone_sleeper_detector_15s");
        blockRailNoSleeperDetector15s = new BlockRailNoSleeperDetector(15);
        register(blockRailNoSleeperDetector15s, "block_rail_no_sleeper_detector_15s");
        blockRailStoneSleeperDetector30s = new BlockRailStoneSleeperDetector(30);
        register(blockRailStoneSleeperDetector30s, "block_rail_stone_sleeper_detector_30s");
        blockRailNoSleeperDetector30s = new BlockRailNoSleeperDetector(30);
        register(blockRailNoSleeperDetector30s, "block_rail_no_sleeper_detector_30s");
        /*------*/

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

        blockRailSignalTransfer = new BlockRailSignalTransfer();
        register(blockRailSignalTransfer, "block_rail_signal_transfer");

        blockRailSpeedLimit = new BlockRailSpeedLimit();
        register(blockRailSpeedLimit, "block_rail_speed_limit");

        blockRailDirectional = new BlockRailDirectional();
        register(blockRailDirectional, "block_rail_dir");

        blockRailDirectionalAnti = new BlockRailDirectionalAnti();
        register(blockRailDirectionalAnti, "block_rail_dir_anti");

        blockRailBlocking = new BlockRailBlocking();
        register(blockRailBlocking, "block_rail_blocking");

        blockRailNoSleeperBlocking = new BlockRailNoSleeperBlocking();
        register(blockRailNoSleeperBlocking, "block_rail_no_sleeper_blocking");


        blockBumperStoneSleeper = new TileEntityBumperStoneSleeper();
        register(blockBumperStoneSleeper, "block_bumper_stone_sleeper");

        blockBumperNoSleeper = new TileEntityBumperNoSleeper();
        register(blockBumperNoSleeper, "block_bumper_no_sleeper");


        blockIronBars = new BlockIronBars();
        register(blockIronBars, "rail_iron_bars");

        blockIronWeb = new BlockIronWeb();
        register(blockIronWeb, "rail_iron_web");

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

        blockPlatform = new BlockPlatform();
        register(blockPlatform, "block_platform");

        blockEdge = new BlockEdge();
        register(blockEdge, "block_edge");

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

        blockRailSignHeadCutLink = new TileEntityRailSignHeadCutLink();
        register(blockRailSignHeadCutLink, "block_rail_sign_head_cutlink");

        blockRailSignHeadT = new TileEntityRailSignHeadT();
        register(blockRailSignHeadT, "block_rail_sign_head_t");

        blockRailSignVertical1 = new TileEntityRailSignVertical1();
        register(blockRailSignVertical1, "block_rail_sign_vertical_1");

        blockRailSignVertical2 = new TileEntityRailSignVertical2();
        register(blockRailSignVertical2, "block_rail_sign_vertical_2");

        blockRailSignVertical3 = new TileEntityRailSignVertical3();
        register(blockRailSignVertical3, "block_rail_sign_vertical_3");

        blockRailSignVertical4 = new TileEntityRailSignVertical4();
        register(blockRailSignVertical4, "block_rail_sign_vertical_4");

        blockRailSignVertical5 = new TileEntityRailSignVertical5();
        register(blockRailSignVertical5, "block_rail_sign_vertical_5");

        //railNoSleeperStraight = new RailNoSleeperStraight();
        //register(railNoSleeperStraight, "rail_ns_s");

        blockStationSign = new TileEntityStationSign();
        register(blockStationSign, "block_station_sign");

        railMono = new RailMono();
        register(railMono, "rail_mono");

        railMonoBumper = new RailMonoBumper();
        register(railMonoBumper, "rail_mono_bumper");

        railMonoMagnet = new RailMonoMagnet();
        register(railMonoMagnet, "rail_mono_magnet");

        railMonoSwitch = new RailMonoSwitch();
        register(railMonoSwitch, "rail_mono_switch");

        railMonoMagnetDetector = new RailMonoMagnetDetector();
        register(railMonoMagnetDetector, "rail_mono_magnet_detector");

        railMonoMagnetDetector5s = new RailMonoMagnetDetector(5);
        register(railMonoMagnetDetector5s, "rail_mono_magnet_detector_5s");

        railMonoMagnetDetector15s = new RailMonoMagnetDetector(15);
        register(railMonoMagnetDetector15s, "rail_mono_magnet_detector_15s");

        railMonoMagnetDetector30s = new RailMonoMagnetDetector(30);
        register(railMonoMagnetDetector30s, "rail_mono_magnet_detector_30s");

        railMonoMagnetPowered = new RailMonoMagnetPowered();
        register(railMonoMagnetPowered, "rail_mono_magnet_powered");

        railMonoMagnetReception = new RailMonoMagnetReception();
        register(railMonoMagnetReception, "rail_mono_magnet_reception");

        railMonoMagnetReceptionAnti = new RailMonoMagnetReceptionAnti();
        register(railMonoMagnetReceptionAnti, "rail_mono_magnet_reception_anti");

        railMonoMagnetDirectional = new RailMonoMagnetDirectional();
        register(railMonoMagnetDirectional, "rail_mono_magnet_directional");

        railMonoMagnetDirectionalAnti = new RailMonoMagnetDirectionalAnti();
        register(railMonoMagnetDirectionalAnti, "rail_mono_magnet_directional_anti");

        railMonoMagnetSpeedLimit = new RailMonoMagnetSpeedLimit();
        register(railMonoMagnetSpeedLimit, "rail_mono_magnet_speed_limit");

        railMonoMagnetSignalTransfer = new RailMonoMagnetSignalTransfer();
        register(railMonoMagnetSignalTransfer, "rail_mono_magnet_signal_transfer");

        railMonoMagnetBlocking = new RailMonoMagnetBlocking();
        register(railMonoMagnetBlocking, "rail_mono_magnet_blocking");

        blockSignalLight = new TileEntitySignalLight();
        register(blockSignalLight, "block_signal_light");

        blockSignalBox = new TileEntitySignalBox();
        register(blockSignalBox, "block_signal_box");

        blockSignalBoxSender = new TileEntitySignalBoxSender();
        register(blockSignalBoxSender, "block_signal_box_sender");

        blockTriStateSignalBox = new TileEntityTriStateSignalBox();
        register(blockTriStateSignalBox, "block_tri_state_signal_box");

        blockGateBase = new TileEntityGateBase();
        register(blockGateBase, "block_gate_base");

        blockGateDoor = new TileEntityGateDoor();
        register(blockGateDoor, "block_gate_door");

        blockGateFront = new TileEntityGateFront();
        register(blockGateFront, "block_gate_front");

        blockGateFrontN = new TileEntityGateFrontN();
        register(blockGateFrontN, "block_gate_front_n");

        blockGlassShield = new TileEntityGlassShield();
        register(blockGlassShield, "block_glass_shield");

        blockGlassShieldHalf = new TileEntityGlassShieldHalf();
        register(blockGlassShieldHalf, "block_glass_shield_half");

        blockGlassShieldAl = new TileEntityGlassShieldAl();
        register(blockGlassShieldAl, "block_glass_shield_al");

        blockGlassShieldAlHalf = new TileEntityGlassShieldAlHalf();
        register(blockGlassShieldAlHalf, "block_glass_shield_al_half");

        blockTicketOnce = new TileEntityTicketBlockOnce();
        register(blockTicketOnce, "block_ticket_once");

        blockTicketCard = new TileEntityTicketBlockCard();
        register(blockTicketCard, "block_ticket_card");

        blockCoin = new TileEntityCoinBlock();
        register(blockCoin, "block_coin");

    }

}