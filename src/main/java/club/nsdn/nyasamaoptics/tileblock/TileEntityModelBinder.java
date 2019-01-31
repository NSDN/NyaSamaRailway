package club.nsdn.nyasamaoptics.tileblock;

import club.nsdn.nyasamaoptics.NyaSamaOptics;
import club.nsdn.nyasamaoptics.renderer.tileblock.*;
import club.nsdn.nyasamaoptics.tileblock.holo.HoloJetRev;
import club.nsdn.nyasamaoptics.tileblock.holo.PillarHead;
import club.nsdn.nyasamaoptics.tileblock.light.*;
import club.nsdn.nyasamaoptics.tileblock.screen.*;
import club.nsdn.nyasamatelecom.api.render.AbsTileEntitySpecialRenderer;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.LinkedHashMap;

/**
 * Created by drzzm32 on 2019.1.30.
 */
public class TileEntityModelBinder {

    private static TileEntityModelBinder instance;
    public static TileEntityModelBinder instance() {
        if (instance == null) instance = new TileEntityModelBinder();
        return instance;
    }

    public static LinkedHashMap<Class<? extends TileEntityBase>, AbsTileEntitySpecialRenderer> renderers;

    private static void bind(AbsTileEntitySpecialRenderer renderer, Class<? extends TileEntityBase> tileEntity) {
        ClientRegistry.bindTileEntitySpecialRenderer(tileEntity, renderer);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void registerTileEntitySpecialRenderers(ModelRegistryEvent event) {
        NyaSamaOptics.logger.info("registering TESRs");
        for (Class<? extends TileEntityBase> tile : renderers.keySet()) {
            bind(renderers.get(tile), tile);
        }
    }

    public TileEntityModelBinder() {
        renderers = new LinkedHashMap<>();

        renderers.put(RGBLight.TileEntityRGBLight.class, new LightRenderer());
        renderers.put(PillarHead.TileEntityPillarHead.class, new PillarHeadRenderer());
        renderers.put(HoloJetRev.TileEntityHoloJetRev.class, new HoloJetRevRenderer());
        renderers.put(LEDPlate.TileEntityLEDPlate.class, new LEDPlateRenderer());
        renderers.put(PlatformPlateFull.TileEntityPlatformPlateFull.class, new PlatformPlateRenderer(false));
        renderers.put(PlatformPlateHalf.TileEntityPlatformPlateHalf.class, new PlatformPlateRenderer(true));
        renderers.put(StationLamp.TileEntityStationLamp.class, new StationLampRenderer());
    }

}
