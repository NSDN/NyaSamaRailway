package club.nsdn.nyasamarailway.tileblock;

/**
 * Created by drzzm32 on 2016.5.5.
 */

import club.nsdn.nyasamarailway.block.rail.*;
import club.nsdn.nyasamarailway.block.rail.special.*;
import club.nsdn.nyasamarailway.tileblock.decoration.*;
import club.nsdn.nyasamarailway.tileblock.decoration.sign.*;
import club.nsdn.nyasamarailway.tileblock.functional.*;
import club.nsdn.nyasamarailway.tileblock.rail.*;
import club.nsdn.nyasamarailway.tileblock.rail.mono.*;
import club.nsdn.nyasamarailway.tileblock.signal.block.*;
import club.nsdn.nyasamarailway.tileblock.signal.core.BlockSignalBox;
import club.nsdn.nyasamarailway.tileblock.signal.core.BlockSignalBoxSender;
import club.nsdn.nyasamarailway.tileblock.signal.core.BlockTriStateSignalBox;
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
                TileEntityRailSignHeadCutLink.RailSignHeadCutLink.class,
                "tileEntityRailSignHeadCutLink");

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
                BlockRailRFID.TileEntityRailRFID.class,
                "TileEntityRailRFID");

        GameRegistry.registerTileEntity(
                BlockRailNoSleeperRFID.TileEntityRailRFID.class,
                "TileEntityRailNoSleeperRFID");

        GameRegistry.registerTileEntity(
                BlockRailSniffer.RailSniffer.class,
                "tileEntityRailSniffer");

        GameRegistry.registerTileEntity(
                RailBase.TileEntityRail.class,
                "railNoSleeperStraight");

        GameRegistry.registerTileEntity(
                RailMono.TileEntityRail.class,
                "railMono");

        GameRegistry.registerTileEntity(
                RailMonoBumper.Bumper.class,
                "railMonoBumper");

        GameRegistry.registerTileEntity(
                RailMonoMagnet.TileEntityRail.class,
                "railMonoMagnet");

        GameRegistry.registerTileEntity(
                RailMonoMagnetDetector.TileEntityRail.class,
                "railMonoMagnetDetector");

        GameRegistry.registerTileEntity(
                RailMonoMagnetDirectional.TileEntityRail.class,
                "railMonoMagnetDirectional");

        GameRegistry.registerTileEntity(
                RailMonoMagnetDirectionalAnti.TileEntityRail.class,
                "railMonoMagnetDirectionalAnti");

        GameRegistry.registerTileEntity(
                RailMonoMagnetPowered.TileEntityRail.class,
                "railMonoMagnetPowered");

        GameRegistry.registerTileEntity(
                RailMonoMagnetReception.TileEntityRail.class,
                "railMonoMagnetReception");

        GameRegistry.registerTileEntity(
                RailMonoMagnetReceptionAnti.TileEntityRail.class,
                "railMonoMagnetReceptionAnti");

        GameRegistry.registerTileEntity(
                RailMonoMagnetSignalTransfer.TileEntityRail.class,
                "railMonoMagnetSignalTransfer");

        GameRegistry.registerTileEntity(
                RailMonoMagnetSpeedLimit.TileEntityRail.class,
                "railMonoMagnetSpeedLimit");

        GameRegistry.registerTileEntity(
                RailMonoMagnetBlocking.TileEntityRail.class,
                "railMonoMagnetBlocking");

        GameRegistry.registerTileEntity(
                RailMonoMagnetRFID.RailRFID.class,
                "railMonoMagnetRFID");

        GameRegistry.registerTileEntity(
                RailMonoMagnetSniffer.RailSniffer.class,
                "railMonoMagnetSniffer");

        GameRegistry.registerTileEntity(
                BlockRailBlocking.RailBlocking.class,
                "tileEntityRailBlocking");

        GameRegistry.registerTileEntity(
                BlockRailDetectorBase.RailDetector.class,
                "tileEntityRailDetector");

        GameRegistry.registerTileEntity(
                BlockSignalLight.SignalLight.class,
                "tileEntitySignalLight");

        GameRegistry.registerTileEntity(
                BlockSignalBox.TileEntitySignalBox.class,
                "tileEntitySignalBox");

        GameRegistry.registerTileEntity(
                BlockSignalBoxSender.TileEntitySignalBoxSender.class,
                "tileEntitySignalBoxSender");

        GameRegistry.registerTileEntity(
                BumperStoneSleeper.Bumper.class,
                "tileEntityBumperStoneSleeper");

        GameRegistry.registerTileEntity(
                BumperNoSleeper.Bumper.class,
                "tileEntityBumperNoSleeper");

        GameRegistry.registerTileEntity(
                BlockTriStateSignalBox.TileEntityTriStateSignalBox.class,
                "tileEntityTriStateSignalBox");

        GameRegistry.registerTileEntity(
                RailMonoSwitch.MonoSwitch.class,
                "railMonoSwitch");

        GameRegistry.registerTileEntity(
                TileEntityGateBase.GateBase.class,
                "tileEntityGateBase");

        GameRegistry.registerTileEntity(
                TileEntityGateDoor.GateDoor.class,
                "tileEntityGateDoor");

        GameRegistry.registerTileEntity(
                BlockGateFront.GateFront.class,
                "tileEntityGateFront");

        GameRegistry.registerTileEntity(
                TileEntityGateFrontN.GateFrontN.class,
                "tileEntityGateFrontN");

        GameRegistry.registerTileEntity(
                TileEntityTicketBlockOnce.TicketBlock.class,
                "tileEntityTicketBlockOnce");

        GameRegistry.registerTileEntity(
                TileEntityTicketBlockCard.TicketBlock.class,
                "tileEntityTicketBlockCard");

        GameRegistry.registerTileEntity(
                TileEntityCoinBlock.CoinBlock.class,
                "tileEntityCoinBlock");

        GameRegistry.registerTileEntity(
                BlockGlassShield.GlassShield.class,
                "tileEntityGlassShield");

        GameRegistry.registerTileEntity(
                BlockGlassShieldHalf.GlassShield.class,
                "tileEntityGlassShieldHalf");

        GameRegistry.registerTileEntity(
                BlockGlassShield1X1.GlassShield.class,
                "tileEntityGlassShield1X1");

        GameRegistry.registerTileEntity(
                BlockGlassShield3X1.GlassShield.class,
                "tileEntityGlassShield3X1");

        GameRegistry.registerTileEntity(
                BlockGlassShield3X1D5.GlassShield.class,
                "tileEntityGlassShield3X1D5");

        GameRegistry.registerTileEntity(
                TileEntityGlassShieldAl.GlassShieldAl.class,
                "tileEntityGlassShieldAl");

        GameRegistry.registerTileEntity(
                TileEntityGlassShieldAlHalf.GlassShieldAl.class,
                "tileEntityGlassShieldAlHalf");

        GameRegistry.registerTileEntity(
                TileEntityGlassShieldAlBase.GlassShieldAl.class,
                "tileEntityGlassShieldAlBase");

        GameRegistry.registerTileEntity(
                TileEntityGlassShieldCorner.GlassShieldAl.class,
                "tileEntityGlassShieldCorner");

        GameRegistry.registerTileEntity(
                TileEntityGlassShieldCornerHalf.GlassShieldAl.class,
                "tileEntityGlassShieldCornerHalf");

        GameRegistry.registerTileEntity(
                TileEntityRailSignVertical1.RailSignVertical.class,
                "tileEntityRailSignVertical1");

        GameRegistry.registerTileEntity(
                TileEntityRailSignVertical2.RailSignVertical.class,
                "tileEntityRailSignVertical2");

        GameRegistry.registerTileEntity(
                TileEntityRailSignVertical3.RailSignVertical.class,
                "tileEntityRailSignVertical3");

        GameRegistry.registerTileEntity(
                TileEntityRailSignVertical4.RailSignVertical.class,
                "tileEntityRailSignVertical4");

        GameRegistry.registerTileEntity(
                TileEntityRailSignVertical5.RailSignVertical.class,
                "tileEntityRailSignVertical5");

        GameRegistry.registerTileEntity(
                TileEntityRailSignVertical6.RailSignVertical.class,
                "tileEntityRailSignVertical6");

        GameRegistry.registerTileEntity(
                TileEntityRailSignVertical7.RailSignVertical.class,
                "tileEntityRailSignVertical7");

        GameRegistry.registerTileEntity(
                RailTriSwitch.TriSwitch.class,
                "tileEntityRailTriSwitch");

        GameRegistry.registerTileEntity(
                TileEntityPierTag.PierTag.class,
                "tileEntityPierTag");

        GameRegistry.registerTileEntity(
                TileEntityPillar.Pillar.class,
                "tileEntityPillar");

        GameRegistry.registerTileEntity(
                BlockBiSignalLight.BiSignalLight.class,
                "tileBiSignalLight");

        GameRegistry.registerTileEntity(
                BlockTriSignalLight.TriSignalLight.class,
                "tileTriSignalLight");

        GameRegistry.registerTileEntity(
                BlockSignalLamp.SignalLight.class,
                "tileEntitySignalLamp");

        GameRegistry.registerTileEntity(
                BlockSignalStick.SignalLight.class,
                "tileEntitySignalStick");

        GameRegistry.registerTileEntity(
                Rail3rd.TileEntityRail.class,
                "tileEntityRail3rd");

        GameRegistry.registerTileEntity(
                Rail3rdSwitch.MonoSwitch.class,
                "tileEntityRail3rdSwitch");

        GameRegistry.registerTileEntity(
                RailMagnetSwitch.MagnetSwitch.class,
                "tileEntityRailMagnetSwitch");
    }

}
