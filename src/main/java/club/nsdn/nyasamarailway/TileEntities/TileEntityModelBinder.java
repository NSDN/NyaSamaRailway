package club.nsdn.nyasamarailway.TileEntities;

/**
 * Created by drzzm32 on 2016.5.5.
 */

import club.nsdn.nyasamarailway.Renderers.TileEntity.*;
import club.nsdn.nyasamarailway.Renderers.TileEntity.Rail.RailNoSleeperStraightModel;
import club.nsdn.nyasamarailway.Renderers.TileEntity.Rail.RailRenderer;
import club.nsdn.nyasamarailway.TileEntities.Rail.RailNoSleeperStraight;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;

public class TileEntityModelBinder {

    public TileEntityModelBinder(FMLInitializationEvent event) {

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityTrackPlate.TrackPlate.class,
                new BaseRenderer(new TrackPlateModel()));

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityTrackShelf.TrackShelf.class,
                new BaseRenderer(new TrackShelfModel(), "textures/blocks/BrushedAluminum.png"));

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityTrackShelfLow.TrackShelfLow.class,
                new BaseRenderer(new TrackShelfLowModel(), "textures/blocks/BrushedAluminum.png"));

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityTBridgeHead.TBridgeHead.class,
                new BaseRenderer(new TBridgeHeadModel(true)));

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityTBridgeHeadNoRib.TBridgeHeadNoRib.class,
                new BaseRenderer(new TBridgeHeadModel(false)));

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityTBridgeBody.TBridgeBody.class,
                new BaseRenderer(new TBridgeBodyModel(true)));

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityTBridgeBodyNoRib.TBridgeBodyNoRib.class,
                new BaseRenderer(new TBridgeBodyModel(false)));

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityTBridgeShoulder.TBridgeShoulder.class,
                new BaseRenderer(new TBridgeShoulderModel()));

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityHalfBlock.HalfBlock.class,
                new BaseRenderer(new HalfBlockModel()));

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityHalfHalfBlock.HalfHalfBlock.class,
                new BaseRenderer(new HalfHalfBlockModel()));

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityPlatform.Platform.class,
                new BaseRenderer(new PlatformModel()));

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityRailSignBody.RailSignBody.class,
                new BaseRenderer(new RailSignBodyModel(), "textures/blocks/BrushedAluminum.png"));

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityRailSignHeadBeep.RailSignHeadBeep.class,
                new BaseRenderer(new RailSignHeadModel(), "textures/blocks/rail_sign_beep.png"));

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityRailSignHeadCut.RailSignHeadCut.class,
                new BaseRenderer(new RailSignHeadModel(), "textures/blocks/rail_sign_cut.png"));

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityRailSignHeadJoe.RailSignHeadJoe.class,
                new BaseRenderer(new RailSignHeadModel(), "textures/blocks/rail_sign_joe.png"));

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityRailSignHeadLink.RailSignHeadLink.class,
                new BaseRenderer(new RailSignHeadModel(), "textures/blocks/rail_sign_link.png"));

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityRailSignHeadT.RailSignHeadT.class,
                new BaseRenderer(new RailSignHeadModel(), "textures/blocks/rail_sign_t.png"));

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityStationSign.StationSign.class, new StationSignRenderer());

        ClientRegistry.bindTileEntitySpecialRenderer(
                RailNoSleeperStraight.Rail.class, new RailRenderer(new RailNoSleeperStraightModel(), "textures/rails/rail_ns_s.png"));
    }

}
