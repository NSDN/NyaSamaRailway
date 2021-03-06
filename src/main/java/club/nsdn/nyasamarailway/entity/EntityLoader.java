package club.nsdn.nyasamarailway.entity;

/**
 * Created by drzzm32 on 2016.5.24.
 */

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.entity.cart.*;
import club.nsdn.nyasamarailway.entity.loco.*;
import club.nsdn.nyasamarailway.entity.nsc.*;
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
                NSPCT6C.class,
                "NSPCT6C",
                17,
                NyaSamaRailway.getInstance(), 256, 3, true);

        EntityRegistry.registerModEntity(
                NSPCT6C.Container.class,
                "NSPCT6CContainer",
                18,
                NyaSamaRailway.getInstance(), 256, 3, true);

        EntityRegistry.registerModEntity(
                NSPCT6W.class,
                "NSPCT6W",
                19,
                NyaSamaRailway.getInstance(), 256, 3, true);

        EntityRegistry.registerModEntity(
                NSPCT6W.Container.class,
                "NSPCT6WContainer",
                20,
                NyaSamaRailway.getInstance(), 256, 3, true);

        EntityRegistry.registerModEntity(
                NSPCT8C.class,
                "NSPCT8C",
                21,
                NyaSamaRailway.getInstance(), 256, 3, true);

        EntityRegistry.registerModEntity(
                NSPCT8C.Container.class,
                "NSPCT8CContainer",
                22,
                NyaSamaRailway.getInstance(), 256, 3, true);

        EntityRegistry.registerModEntity(
                NSPCT8W.class,
                "NSPCT8W",
                23,
                NyaSamaRailway.getInstance(), 256, 3, true);

        EntityRegistry.registerModEntity(
                NSPCT8W.Container.class,
                "NSPCT8WContainer",
                24,
                NyaSamaRailway.getInstance(), 256, 3, true);

        EntityRegistry.registerModEntity(
                NSPCT9.class,
                "NSPCT9",
                25,
                NyaSamaRailway.getInstance(), 256, 3, true);

        EntityRegistry.registerModEntity(
                NSPCT9M.class,
                "NSPCT9M",
                26,
                NyaSamaRailway.getInstance(), 256, 3, true);

        EntityRegistry.registerModEntity(
                NSPCT10.class,
                "NSPCT10",
                27,
                NyaSamaRailway.getInstance(), 256, 3, true);

        EntityRegistry.registerModEntity(
                NSPCT10M.class,
                "NSPCT10M",
                28,
                NyaSamaRailway.getInstance(), 256, 3, true);

        EntityRegistry.registerModEntity(
                NSPCT10J.class,
                "NSPCT10J",
                29,
                NyaSamaRailway.getInstance(), 256, 3, true);

        EntityRegistry.registerModEntity(
                NSC1A.class,
                "NSC1A",
                30,
                NyaSamaRailway.getInstance(), 256, 3, true);

        EntityRegistry.registerModEntity(
                NSC1AM.class,
                "NSC1AM",
                31,
                NyaSamaRailway.getInstance(), 256, 3, true);

        EntityRegistry.registerModEntity(
                NSC1B.class,
                "NSC1B",
                32,
                NyaSamaRailway.getInstance(), 256, 3, true);

        EntityRegistry.registerModEntity(
                NSC1BM.class,
                "NSC1BM",
                33,
                NyaSamaRailway.getInstance(), 256, 3, true);

        EntityRegistry.registerModEntity(
                NSC2A.class,
                "NSC2A",
                34,
                NyaSamaRailway.getInstance(), 256, 3, true);

        EntityRegistry.registerModEntity(
                NSC2AM.class,
                "NSC2AM",
                35,
                NyaSamaRailway.getInstance(), 256, 3, true);

        EntityRegistry.registerModEntity(
                NSC2B.class,
                "NSC2B",
                36,
                NyaSamaRailway.getInstance(), 256, 3, true);

        EntityRegistry.registerModEntity(
                NSC2BM.class,
                "NSC2BM",
                37,
                NyaSamaRailway.getInstance(), 256, 3, true);

        EntityRegistry.registerModEntity(
                NSC3A.class,
                "NSC3A",
                38,
                NyaSamaRailway.getInstance(), 256, 3, true);

        EntityRegistry.registerModEntity(
                NSC3AM.class,
                "NSC3AM",
                39,
                NyaSamaRailway.getInstance(), 256, 3, true);

        EntityRegistry.registerModEntity(
                NSC3B.class,
                "NSC3B",
                40,
                NyaSamaRailway.getInstance(), 256, 3, true);

        EntityRegistry.registerModEntity(
                NSC3BM.class,
                "NSC3BM",
                41,
                NyaSamaRailway.getInstance(), 256, 3, true);

        EntityRegistry.registerModEntity(
                TrainBase.class,
                "TrainBase",
                234,
                NyaSamaRailway.getInstance(), 256, 3, true);

    }

}
