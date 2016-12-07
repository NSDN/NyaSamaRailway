package club.nsdn.nyasamarailway.TileEntities;

/**
 * Created by drzzm32 on 2016.5.5.
 */

import club.nsdn.nyasamarailway.Blocks.BlockPlatform;
import club.nsdn.nyasamarailway.Renderers.TileEntity.*;
import club.nsdn.nyasamarailway.Renderers.TileEntity.Rail.*;
import club.nsdn.nyasamarailway.TileEntities.Rail.RailBase;
import club.nsdn.nyasamarailway.TileEntities.Rail.RailMono;
import club.nsdn.nyasamarailway.TileEntities.Rail.RailMonoMagnet;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraft.client.model.ModelBase;

public class TileEntityModelBinder {

    public TileEntityModelBinder(FMLInitializationEvent event) {

        BlockPlatform.renderType = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(BlockPlatform.renderType, new PlatformRenderer());

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
                RailBase.TileEntityRail.class, new RailRenderer(new RailNoSleeperStraightModel(), "textures/rails/rail_ns_s.png"));

        ClientRegistry.bindTileEntitySpecialRenderer(
                RailMono.TileEntityRail.class, new RailMonoRenderer(new ModelBase[] { new RailMonoStraightSimpleModel(), new RailMonoSlopeSimpleModel(), new RailMonoTurnedSimpleModel() }, 0.0, 0.0, 0.0));

        ClientRegistry.bindTileEntitySpecialRenderer(
                RailMonoMagnet.TileEntityRail.class, new RailMonoRenderer(
                        new ModelBase[] { new RailMonoMagnetStraightModel(), new RailMonoMagnetSlopeModel(), new RailMonoMagnetTurnedModel() },
                        new String[] { "textures/rails/RailMonoMagnetStraight.png", "textures/rails/RailMonoMagnetSlope.png", "textures/rails/RailMonoMagnetTurned.png" },
                        0.0, 0.0, 0.0));
    }

}
