package club.nsdn.nyasamarailway.Entity;

/**
 * Created by drzzm32 on 2016.5.24.
 */

import club.nsdn.nyasamarailway.Renderers.Entity.*;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraft.client.model.ModelBase;

public class EntityModelBinder {

    public EntityModelBinder(FMLInitializationEvent event) {

        RenderingRegistry.registerEntityRenderingHandler(
                MinecartBase.class, new MinecartRenderer(new MinecartModel(), "textures/carts/nstc_1.png"));

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
                NSPCT5.class, new NSPCT5Renderer(new ModelBase[] { new NSPCT5HeadModel(), new NSPCT5BodyWWModel(), new NSPCT5BodyNWModel() }, "textures/carts/nspc_5.png"));

        RenderingRegistry.registerEntityRenderingHandler(
                NSPCT5L.class, new NSPCT5LRenderer(new ModelBase[] { new NSPCT5HeadModel(), new NSPCT5BodyWWModel(), new NSPCT5BodyNWModel() }, "textures/carts/nspc_5l.png"));
    }

}
