package club.nsdn.nyasamarailway.TileEntities;

/**
 * Created by drzzm32 on 2016.5.5.
 */

import club.nsdn.nyasamarailway.Blocks.BlockEdge;
import club.nsdn.nyasamarailway.Blocks.BlockPlatform;
import club.nsdn.nyasamarailway.Renderers.TileEntity.*;
import club.nsdn.nyasamarailway.Renderers.TileEntity.Rail.*;
import club.nsdn.nyasamarailway.TileEntities.Rail.*;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraft.client.model.ModelBase;

public class TileEntityModelBinder {

    public TileEntityModelBinder(FMLInitializationEvent event) {

        BlockPlatform.renderType = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(BlockPlatform.renderType, new PlatformRenderer());

        BlockEdge.renderType = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(BlockEdge.renderType, new EdgeRenderer());

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
                TileEntityRailSignHeadCutLink.RailSignHeadCutLink.class,
                new BaseRenderer(new RailSignHeadModel(), "textures/blocks/rail_sign_cutlink.png"));

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityRailSignHeadT.RailSignHeadT.class,
                new BaseRenderer(new RailSignHeadModel(), "textures/blocks/rail_sign_t.png"));

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityStationSign.StationSign.class, new StationSignRenderer());

        ClientRegistry.bindTileEntitySpecialRenderer(
                RailBase.TileEntityRail.class, new RailRenderer(new RailNoSleeperStraightModel(), "textures/rails/rail_ns_s.png"));

        ClientRegistry.bindTileEntitySpecialRenderer(
                RailMono.TileEntityRail.class,
                new RailMonoRenderer()
        );

        ClientRegistry.bindTileEntitySpecialRenderer(
                RailMonoBumper.Bumper.class,
                new RailMonoBumperRenderer()
        );

        ClientRegistry.bindTileEntitySpecialRenderer(
                RailMonoMagnet.TileEntityRail.class,
                new RailMonoRenderer(new String[] { "textures/rails/RailMonoMagnetStraight.png", "textures/rails/RailMonoMagnetSlope.png", "textures/rails/RailMonoMagnetTurned.png" })
        );

        ClientRegistry.bindTileEntitySpecialRenderer(
                RailMonoMagnetDetector.TileEntityRail.class,
                new RailMonoRenderer(new String[] { "textures/rails/RailMonoMagnetStraightDetector.png", "textures/rails/RailMonoMagnetSlopeDetector.png", "textures/rails/RailMonoMagnetTurnedDetector.png" })
        );

        ClientRegistry.bindTileEntitySpecialRenderer(
                RailMonoMagnetDirectional.TileEntityRail.class,
                new RailMonoRenderer(new String[] { "textures/rails/RailMonoMagnetStraightDirectional.png", "textures/rails/RailMonoMagnetSlopeDirectional.png", "textures/rails/RailMonoMagnetTurnedDirectional.png" })
        );

        ClientRegistry.bindTileEntitySpecialRenderer(
                RailMonoMagnetDirectionalAnti.TileEntityRail.class,
                new RailMonoRenderer(new String[] { "textures/rails/RailMonoMagnetStraightDirectionalAnti.png", "textures/rails/RailMonoMagnetSlopeDirectionalAnti.png", "textures/rails/RailMonoMagnetTurnedDirectionalAnti.png" })
        );

        ClientRegistry.bindTileEntitySpecialRenderer(
                RailMonoMagnetPowered.TileEntityRail.class,
                new RailMonoRenderer(new String[] { "textures/rails/RailMonoMagnetStraightPowered.png", "textures/rails/RailMonoMagnetSlopePowered.png", "textures/rails/RailMonoMagnetTurnedPowered.png" })
        );

        ClientRegistry.bindTileEntitySpecialRenderer(
                RailMonoMagnetReception.TileEntityRail.class,
                new RailMonoRenderer(new String[] { "textures/rails/RailMonoMagnetStraightReception.png", "textures/rails/RailMonoMagnetSlopeReception.png", "textures/rails/RailMonoMagnetTurnedReception.png" })
        );

        ClientRegistry.bindTileEntitySpecialRenderer(
                RailMonoMagnetReceptionAnti.TileEntityRail.class,
                new RailMonoRenderer(new String[] { "textures/rails/RailMonoMagnetStraightReceptionAnti.png", "textures/rails/RailMonoMagnetSlopeReceptionAnti.png", "textures/rails/RailMonoMagnetTurnedReceptionAnti.png" })
        );

        ClientRegistry.bindTileEntitySpecialRenderer(
                RailMonoMagnetSignalTransfer.TileEntityRail.class,
                new RailMonoRenderer(new String[] { "textures/rails/RailMonoMagnetStraightSignalTransfer.png", "textures/rails/RailMonoMagnetSlopeSignalTransfer.png", "textures/rails/RailMonoMagnetTurnedSignalTransfer.png" })
        );

        ClientRegistry.bindTileEntitySpecialRenderer(
                RailMonoMagnetSpeedLimit.TileEntityRail.class,
                new RailMonoRenderer(new String[] { "textures/rails/RailMonoMagnetStraightSpeedLimit.png", "textures/rails/RailMonoMagnetSlopeSpeedLimit.png", "textures/rails/RailMonoMagnetTurnedSpeedLimit.png" })
        );

        ClientRegistry.bindTileEntitySpecialRenderer(
                RailMonoMagnetBlocking.TileEntityRail.class,
                new RailMonoRenderer(new String[] { "textures/rails/RailMonoMagnetStraightBlocking.png", "textures/rails/RailMonoMagnetSlopeBlocking.png", "textures/rails/RailMonoMagnetTurnedBlocking.png" })
        );

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntitySignalLight.SignalLight.class, new SignalLightRenderer());

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntitySignalBox.SignalBox.class, new SignalBoxRenderer(false));

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntitySignalBoxSender.SignalBoxSender.class, new SignalBoxRenderer(true));

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityBumperStoneSleeper.Bumper.class, new BumperRenderer("rail"));

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityBumperNoSleeper.Bumper.class, new BumperRenderer("rail_ns"));

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityTriStateSignalBox.TriStateSignalBox.class, new TriStateSignalBoxRenderer());

        ClientRegistry.bindTileEntitySpecialRenderer(
                RailMonoSwitch.MonoSwitch.class, new RailMonoSwitchRenderer());

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityGateBase.GateBase.class, new GateRenderer(GateRenderer.GATE_BASE));

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityGateDoor.GateDoor.class, new GateRenderer(GateRenderer.GATE_DOOR));

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityGateFront.GateFront.class, new GateRenderer(GateRenderer.GATE_FRONT));

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityGateFrontN.GateFrontN.class, new GateRenderer(GateRenderer.GATE_FRONT_N));

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityTicketBlockOnce.TicketBlock.class,
                new TicketBlockRenderer(TicketBlockRenderer.TICKET_ONCE)
        );

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityTicketBlockCard.TicketBlock.class,
                new TicketBlockRenderer(TicketBlockRenderer.TICKET_CARD)
        );

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityCoinBlock.CoinBlock.class,
                new TicketBlockRenderer(TicketBlockRenderer.COIN)
        );

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityGlassShield.GlassShield.class,
                new GlassShieldRenderer(GlassShieldRenderer.SHIELD)
        );

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityGlassShieldHalf.GlassShield.class,
                new GlassShieldRenderer(GlassShieldRenderer.SHIELD_HALF)
        );

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityGlassShield1X1.GlassShield.class,
                new GlassShieldRenderer(GlassShieldRenderer.SHIELD_1X1)
        );

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityGlassShield3X1.GlassShield.class,
                new GlassShieldRenderer(GlassShieldRenderer.SHIELD_3X1)
        );

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityGlassShield3X1D5.GlassShield.class,
                new GlassShieldRenderer(GlassShieldRenderer.SHIELD_3X1D5)
        );

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityGlassShieldAl.GlassShieldAl.class,
                new GlassShieldRenderer(GlassShieldRenderer.SHIELD_AL)
        );

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityGlassShieldAlHalf.GlassShieldAl.class,
                new GlassShieldRenderer(GlassShieldRenderer.SHIELD_AL_HALF)
        );

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityRailSignVertical1.RailSignVertical.class,
                new RailSignVerticalRenderer("rail_sign_vertical_1")
        );

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityRailSignVertical2.RailSignVertical.class,
                new RailSignVerticalRenderer("rail_sign_vertical_2")
        );

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityRailSignVertical3.RailSignVertical.class,
                new RailSignVerticalRenderer("rail_sign_vertical_3")
        );

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityRailSignVertical4.RailSignVertical.class,
                new RailSignVerticalRenderer("rail_sign_vertical_4")
        );

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityRailSignVertical5.RailSignVertical.class,
                new RailSignVerticalRenderer("rail_sign_vertical_5")
        );

    }

}
