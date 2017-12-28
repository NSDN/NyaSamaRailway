package club.nsdn.nyasamarailway.entity;

/**
 * Created by drzzm32 on 2016.5.24.
 */

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.entity.cart.*;
import club.nsdn.nyasamarailway.entity.loco.*;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;

public class EntityLoader {

    public EntityLoader(FMLInitializationEvent event) {

        EntityRegistry.registerModEntity(
                NSTCT1.class,
                "NSTCT1",
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
                NSPCT4.class,
                "NSPCT4",
                6,
                NyaSamaRailway.getInstance(), 256, 3, true);

        EntityRegistry.registerModEntity(
                NSPCT5.class,
                "NSPCT5",
                7,
                NyaSamaRailway.getInstance(), 256, 3, true);

        EntityRegistry.registerModEntity(
                NSPCT5L.class,
                "NSPCT5L",
                8,
                NyaSamaRailway.getInstance(), 256, 3, true);

        EntityRegistry.registerModEntity(
                NSPCT6.class,
                "NSPCT6",
                9,
                NyaSamaRailway.getInstance(), 256, 3, true);

        EntityRegistry.registerModEntity(
                NSET2.class,
                "NSET2",
                10,
                NyaSamaRailway.getInstance(), 256, 3, true);

        EntityRegistry.registerModEntity(
                NSPCT6L.class,
                "NSPCT6L",
                11,
                NyaSamaRailway.getInstance(), 256, 3, true);

        EntityRegistry.registerModEntity(
                NSPCT7.class,
                "NSPCT7",
                12,
                NyaSamaRailway.getInstance(), 256, 3, true);

        EntityRegistry.registerModEntity(
                NSPCT8.class,
                "NSPCT8",
                13,
                NyaSamaRailway.getInstance(), 256, 3, true);

        EntityRegistry.registerModEntity(
                NSPCT8M.class,
                "NSPCT8M",
                14,
                NyaSamaRailway.getInstance(), 256, 3, true);

        EntityRegistry.registerModEntity(
                NSPCT8J.class,
                "NSPCT8J",
                15,
                NyaSamaRailway.getInstance(), 256, 3, true);

        EntityRegistry.registerModEntity(
                NSPCT4M.class,
                "NSPCT4M",
                16,
                NyaSamaRailway.getInstance(), 256, 3, true);

        EntityRegistry.registerModEntity(
                TrainBase.class,
                "TrainBase",
                234,
                NyaSamaRailway.getInstance(), 256, 3, true);

    }

}
