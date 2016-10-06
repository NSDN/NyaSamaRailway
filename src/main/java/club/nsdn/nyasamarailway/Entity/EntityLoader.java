package club.nsdn.nyasamarailway.Entity;

/**
 * Created by drzzm32 on 2016.5.24.
 */

import club.nsdn.nyasamarailway.NyaSamaRailway;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;

public class EntityLoader {

    public EntityLoader(FMLInitializationEvent event) {

        EntityRegistry.registerModEntity(
                MinecartBase.class,
                "MinecartBase",
                233,
                NyaSamaRailway.getInstance(), 256, 3, true);

        EntityRegistry.registerModEntity(
                NSPCT1.class,
                "NSPCT1",
                1,
                NyaSamaRailway.getInstance(), 256, 3, true);

        EntityRegistry.registerModEntity(
                NSBT1.class,
                "NSBT1",
                2,
                NyaSamaRailway.getInstance(), 256, 3, true);

        EntityRegistry.registerModEntity(
                NSET1.class,
                "NSET1",
                3,
                NyaSamaRailway.getInstance(), 256, 3, true);

        EntityRegistry.registerModEntity(
                NSPCT2.class,
                "NSPCT2",
                4,
                NyaSamaRailway.getInstance(), 256, 3, true);

        EntityRegistry.registerModEntity(
                NSPCT3.class,
                "NSPCT3",
                5,
                NyaSamaRailway.getInstance(), 256, 3, true);

        EntityRegistry.registerModEntity(
                TrainBase.class,
                "TrainBase",
                234,
                NyaSamaRailway.getInstance(), 256, 3, true);

    }

}
