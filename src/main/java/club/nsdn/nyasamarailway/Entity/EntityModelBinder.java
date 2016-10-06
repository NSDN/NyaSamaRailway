package club.nsdn.nyasamarailway.Entity;

/**
 * Created by drzzm32 on 2016.5.24.
 */

import club.nsdn.nyasamarailway.Renderers.Entity.*;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
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
    }

}
