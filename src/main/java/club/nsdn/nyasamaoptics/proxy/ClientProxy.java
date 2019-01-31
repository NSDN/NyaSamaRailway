package club.nsdn.nyasamaoptics.proxy;

import club.nsdn.nyasamaoptics.block.BlockLoader;
import club.nsdn.nyasamaoptics.event.EventRegister;
import club.nsdn.nyasamaoptics.renderer.tileblock.LightRenderer;
import club.nsdn.nyasamaoptics.tileblock.light.RGBLight;
import club.nsdn.nyasamaoptics.util.font.FontLoader;
import net.minecraft.block.Block;
import net.minecraftforge.fml.common.event.*;
import net.minecraft.client.Minecraft;

/**
 * Created by drzzm32 on 2019.1.30.
 */
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        EventRegister.registerClient();
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        new FontLoader(Minecraft.getMinecraft().getResourceManager());

        for (Block b : BlockLoader.blocks) { //Load RGB Lights models
            if (b instanceof RGBLight) {
                RGBLight light = (RGBLight) b;
                LightRenderer.loadModel(light);
            }
        }

    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }


}
