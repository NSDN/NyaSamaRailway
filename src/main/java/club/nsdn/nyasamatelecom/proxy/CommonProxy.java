package club.nsdn.nyasamatelecom.proxy;

import club.nsdn.nyasamatelecom.creativetab.CreativeTabLoader;
import club.nsdn.nyasamatelecom.event.EventRegister;
import club.nsdn.nyasamatelecom.network.NetworkWrapper;
import net.minecraftforge.fml.common.event.*;

/**
 * Created by drzzm32 on 2018.12.12.
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
