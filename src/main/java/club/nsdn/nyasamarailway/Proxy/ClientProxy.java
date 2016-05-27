package club.nsdn.nyasamarailway.Proxy;

/**
 * Created by drzzm32 on 2016.5.5.
 */

import club.nsdn.nyasamarailway.Entity.EntityModelBinder;
import club.nsdn.nyasamarailway.Entity.MinecartBase;
import club.nsdn.nyasamarailway.Event.EventRegister;
import club.nsdn.nyasamarailway.Renderers.Entity.MinecartRenderer;
import club.nsdn.nyasamarailway.TrainControl.TrainController;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.*;
import club.nsdn.nyasamarailway.TileEntities.TileEntityModelBinder;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        org.lwjgl.opengl.Display.setTitle("moded Minecraft by NSDN-MC ver1.0");
        super.preInit(event);
    }

    @Override
    public void init(FMLInitializationEvent event)
    {
        super.init(event);
        new TileEntityModelBinder(event);
        new EntityModelBinder(event);
        TrainController.KeyInput.registerKeyBindings();
        EventRegister.registerClient();
    }

    @Override
    public void postInit(FMLPostInitializationEvent event)
    {
        super.postInit(event);
    }


}
