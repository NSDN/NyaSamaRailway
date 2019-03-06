package club.nsdn.nyasamarailway.tileblock;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.tileblock.deco.*;
import club.nsdn.nyasamarailway.tileblock.func.*;
import club.nsdn.nyasamarailway.tileblock.rail.*;
import club.nsdn.nyasamarailway.tileblock.signal.deco.*;
import club.nsdn.nyasamarailway.tileblock.signal.light.*;
import club.nsdn.nyasamarailway.tileblock.signal.trackside.*;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.LinkedList;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class TileEntityLoader {

    private static TileEntityLoader instance;
    public static TileEntityLoader instance() {
        if (instance == null) instance = new TileEntityLoader();
        return instance;
    }

    public static LinkedList<Class<? extends TileEntity>> tileEntities;

    private static void register(Class<? extends TileEntity> tileEntity) {
        String name = tileEntity.getSimpleName().replace("TileEntity", "tile");
        GameRegistry.registerTileEntity(tileEntity, new ResourceLocation(NyaSamaRailway.MODID, name));
    }

    @SubscribeEvent
    public void registerTileEntities(RegistryEvent.Register<Block> event) {
        NyaSamaRailway.logger.info("registering TileEntities");
        for (Class<? extends TileEntity> t : tileEntities)
            register(t);
    }

    public TileEntityLoader() {
        tileEntities = new LinkedList<>();

        tileEntities.add(Pillar.TileEntityPillar.class);
        tileEntities.add(PillarBig.TileEntityPillarBig.class);
        tileEntities.add(PillarQuad.TileEntityPillarQuad.class);
        tileEntities.add(RailSignBody.TileEntityRailSignBody.class);
        tileEntities.add(RailSignHead.TileEntityRailSignHead.class);
        tileEntities.add(RailSignVertical.TileEntityRailSignVertical.class);
        tileEntities.add(SignalPillar.TileEntitySignalPillar.class);

        tileEntities.add(CoinBlock.TileEntityCoinBlock.class);
        tileEntities.add(GateBase.TileEntityGateBase.class);
        tileEntities.add(GateDoor.TileEntityGateDoor.class);
        tileEntities.add(GateFrontN.TileEntityGateFrontN.class);
        tileEntities.add(PierTag.TileEntityPierTag.class);
        tileEntities.add(TicketBlockCard.TileEntityTicketBlockCard.class);
        tileEntities.add(TicketBlockOnce.TileEntityTicketBlockOnce.class);

        tileEntities.add(ConvWireMono.TileEntityConvWireMono.class);
        tileEntities.add(MagnetRail.TileEntityMagnetRail.class);
        tileEntities.add(MagnetSwitch.TileEntityMagnetSwitch.class);
        tileEntities.add(MonoRailBase.TileEntityMonoRailBase.class);
        tileEntities.add(MonoRailSwitch.TileEntityMonoRailSwitch.class);
        tileEntities.add(Rail3rd.TileEntityRail3rd.class);
        tileEntities.add(Rail3rdSwitch.TileEntityRail3rdSwitch.class);
        tileEntities.add(RailBumper.TileEntityRailBumper.class);
        tileEntities.add(RailStoneSleeper.TileEntityRailStoneSleeper.class);
        tileEntities.add(RailNoSleeper.TileEntityRailNoSleeper.class);
        tileEntities.add(RailTriSwitch.TileEntityRailTriSwitch.class);
        tileEntities.add(WireRail.TileEntityWireRail.class);

        tileEntities.add(GateFront.TileEntityGateFront.class);
        tileEntities.add(GlassShield.TileEntityGlassShield.class);
        tileEntities.add(GlassShield1X1.TileEntityGlassShield1X1.class);
        tileEntities.add(GlassShield3X1.TileEntityGlassShield3X1.class);
        tileEntities.add(GlassShield3X1D5.TileEntityGlassShield3X1D5.class);
        tileEntities.add(GlassShield1D5X1D5.TileEntityGlassShield1D5X1D5.class);
        tileEntities.add(GlassShieldHalf.TileEntityGlassShieldHalf.class);

        tileEntities.add(BiSignalLight.TileEntityBiSignalLight.class);
        tileEntities.add(PillarSignalBi.TileEntityPillarSignalBi.class);
        tileEntities.add(PillarSignalOne.TileEntityPillarSignalOne.class);
        tileEntities.add(PillarSignalTri.TileEntityPillarSignalTri.class);
        tileEntities.add(SignalLamp.TileEntitySignalLamp.class);
        tileEntities.add(SignalLight.TileEntitySignalLight.class);
        tileEntities.add(SignalStick.TileEntitySignalStick.class);
        tileEntities.add(TriSignalLight.TileEntityTriSignalLight.class);

        tileEntities.add(TrackSideBlocking.TileEntityTrackSideBlocking.class);
        tileEntities.add(TrackSideBlockingHs.TileEntityTrackSideBlockingHs.class);
        tileEntities.add(TrackSideReception.TileEntityTrackSideReception.class);
        tileEntities.add(TrackSideRFID.TileEntityTrackSideRFID.class);
        tileEntities.add(TrackSideRFIDHs.TileEntityTrackSideRFIDHs.class);
        tileEntities.add(TrackSideSniffer.TileEntityTrackSideSniffer.class);
        tileEntities.add(TrackSideSnifferHs.TileEntityTrackSideSnifferHs.class);
    }

}
