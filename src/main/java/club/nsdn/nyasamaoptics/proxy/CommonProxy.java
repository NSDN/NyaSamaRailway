package club.nsdn.nyasamaoptics.proxy;

import club.nsdn.nyasamaoptics.event.EventRegister;
import club.nsdn.nyasamaoptics.network.NetworkWrapper;
import club.nsdn.nyasamaoptics.creativetab.CreativeTabLoader;
import net.minecraftforge.fml.common.event.*;

/**
 * Created by drzzm32 on 2019.1.30.
 */
public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        new CreativeTabLoader(event);
        new NetworkWrapper(event);
        EventRegister.registerCommon();
    }

    public void init(FMLInitializationEvent event) {

    }

    public void postInit(FMLPostInitializationEvent event) {

    }

}
