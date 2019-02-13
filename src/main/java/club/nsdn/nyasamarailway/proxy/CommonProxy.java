package club.nsdn.nyasamarailway.proxy;

import club.nsdn.nyasamaoptics.tileblock.screen.LEDPlate;
import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.api.signal.TileEntitySignalLight;
import club.nsdn.nyasamarailway.block.BlockLoader;
import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.event.EventRegister;
import club.nsdn.nyasamarailway.network.NetworkWrapper;
import club.nsdn.nyasamarailway.util.NTPCore;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Created by drzzm32 on 2019.2.10
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

        NyaSamaRailway.logger.info("Register Signal Light Controller to NyaSamaTelecom");
        TileEntitySignalLight.registerController();

        NyaSamaRailway.logger.info("Register NTP command to NyaSamaOptics");
        LEDPlate.registerCommand("#!/bin/ntp", new NTPCore());
    }

}
