package club.nsdn.nyasamarailway.entity;

/**
 * Created by drzzm32 on 2016.5.24.
 */

import club.nsdn.nyasamarailway.entity.cart.*;
import club.nsdn.nyasamarailway.entity.loco.*;
import club.nsdn.nyasamarailway.renderer.entity.*;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraft.client.model.ModelBase;

public class EntityModelBinder {

    public EntityModelBinder(FMLInitializationEvent event) {

        RenderingRegistry.registerEntityRenderingHandler(
                NSTCT1.class, new MinecartRenderer(new NSTCT1Model(), "textures/carts/nstc_1.png"));

        RenderingRegistry.registerEntityRenderingHandler(
                NSPCT1.class, new MinecartRenderer(new NSPCT1Model(), "textures/carts/nspc_1.png"));

        RenderingRegistry.registerEntityRenderingHandler(
                NSBT1.class, new MinecartRenderer(new NSBT1Model(), "textures/carts/nspc_1.png"));

        RenderingRegistry.registerEntityRenderingHandler(
                NSET1.class, new MinecartRenderer(new NSET1Model(), "textures/carts/nse_1.png"));

        RenderingRegistry.registerEntityRenderingHandler(
                TrainBase.class, new MinecartRenderer(new TrainModel(), "textures/blocks/BrushedAluminum.png"));

        ModelBase models[] = { new NSBT1Model(), new NSPCT2Model() };
        String textures[] = { "textures/carts/nspc_1.png", "textures/blocks/BrushedAluminum.png" };
        RenderingRegistry.registerEntityRenderingHandler(
                NSPCT2.class, new TrainRenderer(models, textures));

        RenderingRegistry.registerEntityRenderingHandler(
                NSPCT3.class, new MinecartRenderer(new NSPCT3Model(), "textures/carts/nspc_3.png"));

        RenderingRegistry.registerEntityRenderingHandler(
                NSPCT4.class, new NSPCT4Renderer(new NSPCT4Model(), "textures/carts/nspc_4.png"));

        RenderingRegistry.registerEntityRenderingHandler(
                NSPCT4M.class, new NSPCT4Renderer(new NSPCT4Model(), "textures/carts/nspc_4m.png"));

        RenderingRegistry.registerEntityRenderingHandler(
                NSPCT5.class, new NSPCT5Renderer(new ModelBase[] { new NSPCT5HeadModel(), new NSPCT5BodyWWModel(), new NSPCT5BodyNWModel() }, "textures/carts/nspc_5.png"));

        RenderingRegistry.registerEntityRenderingHandler(
                NSPCT5L.class, new NSPCT5LRenderer(new ModelBase[] { new NSPCT5HeadModel(), new NSPCT5BodyWWModel(), new NSPCT5BodyNWModel() }, "textures/carts/nspc_5l.png"));

        RenderingRegistry.registerEntityRenderingHandler(
                NSPCT6.class, new NSPCT6Renderer());

        RenderingRegistry.registerEntityRenderingHandler(
                NSPCT6C.class, new NSPCT6CRenderer());

        RenderingRegistry.registerEntityRenderingHandler(
                NSPCT6C.Container.class, new NSPCT6CRenderer());

        RenderingRegistry.registerEntityRenderingHandler(
                NSPCT6W.class, new NSPCT6WRenderer());

        RenderingRegistry.registerEntityRenderingHandler(
                NSPCT6W.Container.class, new NSPCT6WRenderer());

        RenderingRegistry.registerEntityRenderingHandler(
                NSPCT6L.class, new NSPCT6LRenderer());

        RenderingRegistry.registerEntityRenderingHandler(
                NSPCT7.class, new NSPCT7Renderer());

        RenderingRegistry.registerEntityRenderingHandler(
                NSPCT8.class, new NSPCT8Renderer());

        RenderingRegistry.registerEntityRenderingHandler(
                NSPCT8C.class, new NSPCT8CRenderer());

        RenderingRegistry.registerEntityRenderingHandler(
                NSPCT8C.Container.class, new NSPCT8CRenderer());

        RenderingRegistry.registerEntityRenderingHandler(
                NSPCT8W.class, new NSPCT8WRenderer());

        RenderingRegistry.registerEntityRenderingHandler(
                NSPCT8W.Container.class, new NSPCT8WRenderer());

        RenderingRegistry.registerEntityRenderingHandler(
                NSPCT8M.class, new NSPCT8MRenderer());

        RenderingRegistry.registerEntityRenderingHandler(
                NSPCT8J.class, new NSPCT8JRenderer());

        RenderingRegistry.registerEntityRenderingHandler(
                NSET2.class, new NSET2Renderer());

    }

}
