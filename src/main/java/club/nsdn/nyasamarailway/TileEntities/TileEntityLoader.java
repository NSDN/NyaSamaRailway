package club.nsdn.nyasamarailway.TileEntities;

/**
 * Created by drzzm32 on 2016.5.5.
 */

import club.nsdn.nyasamarailway.Blocks.*;
import club.nsdn.nyasamarailway.TileEntities.Rail.RailBase;
import club.nsdn.nyasamarailway.TileEntities.Rail.RailMono;
import club.nsdn.nyasamarailway.TileEntities.Rail.RailMonoMagnet;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;

public class TileEntityLoader {

    public TileEntityLoader(FMLInitializationEvent event) {

        GameRegistry.registerTileEntity(
                TileEntityTrackPlate.TrackPlate.class,
                "tileEntityTrackPlate");

        GameRegistry.registerTileEntity(
                TileEntityTrackShelf.TrackShelf.class,
                "tileEntityTrackShelf");

        GameRegistry.registerTileEntity(
                TileEntityTrackShelfLow.TrackShelfLow.class,
                "tileEntityTrackShelfLow");

        GameRegistry.registerTileEntity(
                TileEntityTBridgeHead.TBridgeHead.class,
                "tileEntityTBridgeHead");

        GameRegistry.registerTileEntity(
                TileEntityTBridgeHeadNoRib.TBridgeHeadNoRib.class,
                "tileEntityTBridgeHeadNoRib");

        GameRegistry.registerTileEntity(
                TileEntityTBridgeBody.TBridgeBody.class,
                "tileEntityTBridgeBody");

        GameRegistry.registerTileEntity(
                TileEntityTBridgeBodyNoRib.TBridgeBodyNoRib.class,
                "tileEntityTBridgeBodyNoRib");

        GameRegistry.registerTileEntity(
                TileEntityTBridgeShoulder.TBridgeShoulder.class,
                "tileEntityTBridgeShoulder");

        GameRegistry.registerTileEntity(
                TileEntityHalfBlock.HalfBlock.class,
                "tileEntityHalfBlock");

        GameRegistry.registerTileEntity(
                TileEntityHalfHalfBlock.HalfHalfBlock.class,
                "tileEntityHalfHalfBlock");

        GameRegistry.registerTileEntity(
                TileEntityRailSignBody.RailSignBody.class,
                "tileEntityRailSignBody");

        GameRegistry.registerTileEntity(
                TileEntityRailSignHeadBeep.RailSignHeadBeep.class,
                "tileEntityRailSignHeadBeep");

        GameRegistry.registerTileEntity(
                TileEntityRailSignHeadCut.RailSignHeadCut.class,
                "tileEntityRailSignHeadCut");

        GameRegistry.registerTileEntity(
                TileEntityRailSignHeadJoe.RailSignHeadJoe.class,
                "tileEntityRailSignHeadJoe");

        GameRegistry.registerTileEntity(
                TileEntityRailSignHeadLink.RailSignHeadLink.class,
                "tileEntityRailSignHeadLink");

        GameRegistry.registerTileEntity(
                TileEntityRailSignHeadT.RailSignHeadT.class,
                "tileEntityRailSignHeadT");

        GameRegistry.registerTileEntity(
                TileEntityStationSign.StationSign.class,
                "tileEntityStationSign");

        GameRegistry.registerTileEntity(
                BlockRailReception.TileEntityRailReception.class,
                "tileEntityRailReception");

        GameRegistry.registerTileEntity(
                BlockRailReceptionAnti.TileEntityRailReceptionAnti.class,
                "tileEntityRailReceptionAnti");

        GameRegistry.registerTileEntity(
                RailBase.TileEntityRail.class,
                "railNoSleeperStraight");

        GameRegistry.registerTileEntity(
                RailMono.TileEntityRail.class,
                "railMono");

        GameRegistry.registerTileEntity(
                RailMonoMagnet.TileEntityRail.class,
                "railMonoMagnet");
    }

}
