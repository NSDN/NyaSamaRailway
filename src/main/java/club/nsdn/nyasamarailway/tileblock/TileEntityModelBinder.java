package club.nsdn.nyasamarailway.tileblock;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.renderer.tileentity.deco.*;
import club.nsdn.nyasamarailway.renderer.tileentity.func.*;
import club.nsdn.nyasamarailway.renderer.tileentity.rail.*;
import club.nsdn.nyasamarailway.renderer.tileentity.signal.*;
import club.nsdn.nyasamarailway.tileblock.deco.*;
import club.nsdn.nyasamarailway.tileblock.func.*;
import club.nsdn.nyasamarailway.tileblock.rail.*;
import club.nsdn.nyasamarailway.tileblock.signal.deco.*;
import club.nsdn.nyasamarailway.tileblock.signal.light.*;
import club.nsdn.nyasamarailway.tileblock.signal.trackside.*;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.LinkedHashMap;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class TileEntityModelBinder {

    private static TileEntityModelBinder instance;
    public static TileEntityModelBinder instance() {
        if (instance == null) instance = new TileEntityModelBinder();
        return instance;
    }

    public static LinkedHashMap<Class<? extends TileEntityBase>, TileEntitySpecialRenderer<? super TileEntityBase>> renderers;

    private static void bind(TileEntitySpecialRenderer<? super TileEntityBase> renderer, Class<? extends TileEntityBase> tileEntity) {
        ClientRegistry.bindTileEntitySpecialRenderer(tileEntity, renderer);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void registerTileEntitySpecialRenderers(ModelRegistryEvent event) {
        NyaSamaRailway.logger.info("registering TESRs");
        for (Class<? extends TileEntityBase> tile : renderers.keySet()) {
            bind(renderers.get(tile), tile);
        }
    }

    public TileEntityModelBinder() {
        renderers = new LinkedHashMap<>();

        renderers.put(Pillar.TileEntityPillar.class, new PillarRenderer());
        renderers.put(PillarBig.TileEntityPillarBig.class, new PillarBigRenderer());
        renderers.put(PillarQuad.TileEntityPillarQuad.class, new PillarQuadRenderer());
        renderers.put(RailSignBody.TileEntityRailSignBody.class, new RailSignBodyRenderer());
        renderers.put(RailSignHead.TileEntityRailSignHead.class, new RailSignHeadRenderer());
        renderers.put(RailSignVertical.TileEntityRailSignVertical.class, new RailSignVerticalRenderer());
        renderers.put(SignalPillar.TileEntitySignalPillar.class, new SignalPillarRenderer());

        renderers.put(CoinBlock.TileEntityCoinBlock.class, new TicketBlockRenderer(TicketBlockRenderer.COIN));
        renderers.put(GateBase.TileEntityGateBase.class, new GateRenderer(GateRenderer.GATE_BASE));
        renderers.put(GateDoor.TileEntityGateDoor.class, new GateRenderer(GateRenderer.GATE_DOOR));
        renderers.put(GateFrontN.TileEntityGateFrontN.class, new GateRenderer(GateRenderer.GATE_FRONT_N));
        renderers.put(PierTag.TileEntityPierTag.class, new PierTagRenderer());
        renderers.put(TicketBlockCard.TileEntityTicketBlockCard.class, new TicketBlockRenderer(TicketBlockRenderer.TICKET_CARD));
        renderers.put(TicketBlockOnce.TileEntityTicketBlockOnce.class, new TicketBlockRenderer(TicketBlockRenderer.TICKET_ONCE));

        renderers.put(ConvWireMono.TileEntityConvWireMono.class, new ConvWireMonoRenderer());
        renderers.put(MagnetRail.TileEntityMagnetRail.class, new MonoRailRenderer(
                new String[] {
                        "textures/rails/mono_rail_magnet_straight.png",
                        "textures/rails/mono_rail_magnet_slope.png",
                        "textures/rails/mono_rail_magnet_turned.png"
                }
        ));
        renderers.put(MagnetSwitch.TileEntityMagnetSwitch.class, new MonoRailSwitchRenderer(false));
        renderers.put(MonoRailBase.TileEntityMonoRailBase.class, new MonoRailRenderer());
        renderers.put(MonoRailSwitch.TileEntityMonoRailSwitch.class, new MonoRailSwitchRenderer());
        renderers.put(Rail3rd.TileEntityRail3rd.class, new Rail3rdRenderer());
        renderers.put(Rail3rdSwitch.TileEntityRail3rdSwitch.class, new MonoRailSwitchRenderer(true));
        renderers.put(RailBumper.TileEntityRailBumper.class, new RailBumperRenderer());
        //renderers.put(RailStoneSleeper.TileEntityRailStoneSleeper.class, new RailRenderer("rail"));
        //renderers.put(RailNoSleeper.TileEntityRailNoSleeper.class, new RailRenderer("rail_ns"));
        //renderers.put(RailTriSwitch.TileEntityRailTriSwitch.class, new RailTriSwitchRenderer());
        //renderers.put(RailTriSwitch.TileEntityRailTriSwitch.class, new RailTriSwitchRendererFast());
        renderers.put(WireRail.TileEntityWireRail.class, new RailRenderer("rail_wire"));

        renderers.put(GateFront.TileEntityGateFront.class, new GateRenderer(GateRenderer.GATE_FRONT));
        renderers.put(GlassShield.TileEntityGlassShield.class, new GlassShieldRenderer(GlassShieldRenderer.SHIELD));
        renderers.put(GlassShield1X1.TileEntityGlassShield1X1.class, new GlassShieldRenderer(GlassShieldRenderer.SHIELD_1X1));
        renderers.put(GlassShield3X1.TileEntityGlassShield3X1.class, new GlassShieldRenderer(GlassShieldRenderer.SHIELD_3X1));
        renderers.put(GlassShield3X1D5.TileEntityGlassShield3X1D5.class, new GlassShieldRenderer(GlassShieldRenderer.SHIELD_3X1D5));
        renderers.put(GlassShield1D5X1D5.TileEntityGlassShield1D5X1D5.class, new GlassShieldRenderer(GlassShieldRenderer.SHIELD_1D5X1D5));
        renderers.put(GlassShieldHalf.TileEntityGlassShieldHalf.class, new GlassShieldRenderer(GlassShieldRenderer.SHIELD_HALF));

        renderers.put(BiSignalLight.TileEntityBiSignalLight.class, new BiSignalLightRenderer("bi_signal_light"));
        renderers.put(PillarSignalBi.TileEntityPillarSignalBi.class, new BiSignalLightRenderer("signal_pillar_bi"));
        renderers.put(PillarSignalOne.TileEntityPillarSignalOne.class, new SignalLightRenderer("models/blocks/signal_pillar_one.obj"));
        renderers.put(PillarSignalTri.TileEntityPillarSignalTri.class, new TriSignalLightRenderer("signal_pillar_tri"));
        renderers.put(SignalLamp.TileEntitySignalLamp.class, new SignalLightRenderer("models/blocks/signal_lamp.obj"));
        renderers.put(SignalLight.TileEntitySignalLight.class, new SignalLightRenderer());
        renderers.put(SignalStick.TileEntitySignalStick.class, new SignalLightRenderer("models/blocks/signal_stick.obj"));
        renderers.put(TriSignalLight.TileEntityTriSignalLight.class, new TriSignalLightRenderer("tri_signal_light"));

        renderers.put(TrackSideBlocking.TileEntityTrackSideBlocking.class, new TrackSideRenderer("track_side_blocking_sign"));
        renderers.put(TrackSideBlockingHs.TileEntityTrackSideBlockingHs.class, new TrackSideRenderer("track_side_blocking_hs_sign"));
        renderers.put(TrackSideReception.TileEntityTrackSideReception.class, new TrackSideRenderer("track_side_reception_sign"));
        renderers.put(TrackSideRFID.TileEntityTrackSideRFID.class, new TrackSideRenderer("track_side_rfid_sign"));
        renderers.put(TrackSideRFIDHs.TileEntityTrackSideRFIDHs.class, new TrackSideRenderer("track_side_rfid_hs_sign"));
        renderers.put(TrackSideSniffer.TileEntityTrackSideSniffer.class, new TrackSideRenderer("track_side_sniffer_sign"));
        renderers.put(TrackSideSnifferHs.TileEntityTrackSideSnifferHs.class, new TrackSideRenderer("track_side_sniffer_hs_sign"));

        //renderers.put(TileEntityBuildEndpoint.class, new BuildRouteRenderer());
    }

}
