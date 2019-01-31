package club.nsdn.nyasamaoptics.tileblock;

import club.nsdn.nyasamaoptics.NyaSamaOptics;
import club.nsdn.nyasamaoptics.tileblock.holo.HoloJetRev;
import club.nsdn.nyasamaoptics.tileblock.holo.PillarHead;
import club.nsdn.nyasamaoptics.tileblock.light.*;
import club.nsdn.nyasamaoptics.tileblock.screen.*;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraft.tileentity.TileEntity;

import java.util.LinkedList;

/**
 * Created by drzzm32 on 2019.1.30.
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
        GameRegistry.registerTileEntity(tileEntity, new ResourceLocation(NyaSamaOptics.MODID, name));
    }

    @SubscribeEvent
    public void registerTileEntities(RegistryEvent.Register<Block> event) {
        NyaSamaOptics.logger.info("registering TileEntities");
        for (Class<? extends TileEntity> t : tileEntities)
            register(t);
    }

    public TileEntityLoader() {
        tileEntities = new LinkedList<>();

        tileEntities.add(RGBLight.TileEntityRGBLight.class);
        tileEntities.add(PillarHead.TileEntityPillarHead.class);
        tileEntities.add(HoloJetRev.TileEntityHoloJetRev.class);
        tileEntities.add(LEDPlate.TileEntityLEDPlate.class);
        tileEntities.add(PlatformPlateFull.TileEntityPlatformPlateFull.class);
        tileEntities.add(PlatformPlateHalf.TileEntityPlatformPlateHalf.class);
        tileEntities.add(StationLamp.TileEntityStationLamp.class);
    }

}
