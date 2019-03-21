package club.nsdn.nyasamarailway.entity;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.entity.cart.*;
import club.nsdn.nyasamarailway.entity.loco.*;
import club.nsdn.nyasamarailway.entity.nsc.*;
import club.nsdn.nyasamarailway.entity.train.*;
import club.nsdn.nyasamarailway.renderer.entity.*;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.LinkedHashMap;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class EntityModelBinder {

    private static EntityModelBinder instance;
    public static EntityModelBinder instance() {
        if (instance == null) instance = new EntityModelBinder();
        return instance;
    }

    public static LinkedHashMap<Class<? extends Entity>, IRenderFactory<? extends Entity>> renderers;

    private static void bind(Class entity, IRenderFactory factory) {
        RenderingRegistry.registerEntityRenderingHandler(entity, factory);
    }

    @SubscribeEvent
    public void registerTileEntities(ModelRegistryEvent event) {
        NyaSamaRailway.logger.info("registering Entity Models");
        for (Class entity : renderers.keySet()) {
            bind(entity, renderers.get(entity));
        }
    }

    public EntityModelBinder() {
        renderers = new LinkedHashMap<>();

        renderers.put(NSPCT4.class, NSPCT4Renderer.FACTORY);
        renderers.put(NSPCT7.class, NSPCT7Renderer.FACTORY);
        renderers.put(NSPCT8.class, NSPCT8Renderer.FACTORY);
        renderers.put(NSPCT8W.class, NSPCT8WRenderer.FACTORY);
        renderers.put(NSPCT8W.Container.class, NSPCT8WRenderer.FACTORY);
        renderers.put(NSPCT9.class, NSPCT9Renderer.FACTORY);
        renderers.put(NSPCT10.class, NSPCT10Renderer.FACTORY);

        renderers.put(NSET2.class, NSET2Renderer.FACTORY);
        renderers.put(NSET3.class, NSET3Renderer.FACTORY);
        renderers.put(NSPCT4M.class, NSPCT4MRenderer.FACTORY);
        renderers.put(NSPCT8C.class, NSPCT8CRenderer.FACTORY);
        renderers.put(NSPCT8C.Container.class, NSPCT8CRenderer.FACTORY);
        renderers.put(NSPCT8J.class, NSPCT8JRenderer.FACTORY);
        renderers.put(NSPCT8M.class, NSPCT8MRenderer.FACTORY);
        renderers.put(NSPCT9M.class, NSPCT9MRenderer.FACTORY);
        renderers.put(NSPCT10J.class, NSPCT10JRenderer.FACTORY);
        renderers.put(NSPCT10M.class, NSPCT10MRenderer.FACTORY);

        renderers.put(NSC1A.class, NSCxRenderer.FACTORY_1A);
        renderers.put(NSC1AM.class, NSCxMRenderer.FACTORY_1A);
        renderers.put(NSC1B.class, NSCxRenderer.FACTORY_1B);
        renderers.put(NSC1BM.class, NSCxMRenderer.FACTORY_1B);

        renderers.put(NSC2A.class, NSCxRenderer.FACTORY_2A);
        renderers.put(NSC2AM.class, NSCxMRenderer.FACTORY_2A);
        renderers.put(NSC2B.class, NSCxRenderer.FACTORY_2B);
        renderers.put(NSC2BM.class, NSCxMRenderer.FACTORY_2B);

        renderers.put(NSC3A.class, NSCxRenderer.FACTORY_3A);
        renderers.put(NSC3AM.class, NSCxMRenderer.FACTORY_3A);
        renderers.put(NSC3B.class, NSCxRenderer.FACTORY_3B);
        renderers.put(NSC3BM.class, NSCxMRenderer.FACTORY_3B);

        renderers.put(NSBT2.class, NSBT2Renderer.FACTORY);
        renderers.put(NSBT2M.class, NSBT2MRenderer.FACTORY);
        renderers.put(NSRM1.class, NSRM1Renderer.FACTORY);
        renderers.put(NSRM1T.class, NSRM1Renderer.FACTORY);

        renderers.put(NSBT3.class, NSBT3Renderer.FACTORY);
        renderers.put(NSBT3M.class, NSBT3MRenderer.FACTORY);
        renderers.put(NSRM2.class, NSRM2Renderer.FACTORY);
        renderers.put(NSRM2T.class, NSRM2Renderer.FACTORY);

        renderers.put(NSBT4A.class, NSBT4Renderer.FACTORY_A);
        renderers.put(NSBT4B.class, NSBT4Renderer.FACTORY_B);
        renderers.put(NSBT4M.class, NSBT4Renderer.FACTORY_M);
        renderers.put(NSRD1Shelf.class, NSRD1ShelfRenderer.FACTORY);
        renderers.put(NSRD1Main.class, NSRD1MainRenderer.FACTORY);

        renderers.put(NSE4.class, NSE4Renderer.FACTORY);

        renderers.put(NSRD2Shelf.class, NSRD2ShelfRenderer.FACTORY);
        renderers.put(NSRD2Main.class, NSRD2MainRenderer.FACTORY);

        renderers.put(NSBT5.class, NSBT5Renderer.FACTORY);
        renderers.put(NSBT5M.class, NSBT5MRenderer.FACTORY);
        renderers.put(NSRM3.class, NSRM3Renderer.FACTORY);
        renderers.put(NSRM3T.class, NSRM3Renderer.FACTORY);

        renderers.put(NSRA1.class, NSRA1Renderer.FACTORY);
        renderers.put(NSRA2.class, NSRA2Renderer.FACTORY);

        renderers.put(NSRM4.class, NSRM4Renderer.FACTORY);
        renderers.put(NSRM4T.class, NSRM4Renderer.FACTORY);

        renderers.put(NSBT6.class, NSBT6Renderer.FACTORY);
        renderers.put(NSBT6M.class, NSBT6MRenderer.FACTORY);
        renderers.put(NSRM5.class, NSRM5Renderer.FACTORY);
        renderers.put(NSRM5T.class, NSRM5Renderer.FACTORY);
        renderers.put(NSRM5L.class, NSRM5LRenderer.FACTORY);
    }

}
