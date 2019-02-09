package club.nsdn.nyasamarailway.proxy;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.block.BlockLoader;
import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.event.EventRegister;
import club.nsdn.nyasamarailway.network.NetworkWrapper;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

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
        NyaSamaRailway.logger.info("Get lightBeams from NyaSamaOptics");
        BlockLoader.instance().getLightBeams();
    }

}
