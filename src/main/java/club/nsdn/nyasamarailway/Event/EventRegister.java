package club.nsdn.nyasamarailway.Event;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraftforge.common.MinecraftForge;

/**
 * Created by drzzm32 on 2016.5.13.
 */

public class EventRegister {

    public static void registerCommon() {
        MinecraftForge.EVENT_BUS.register(ToolHandler.instance());
        FMLCommonHandler.instance().bus().register(TrainControlServerHandler.instance());
    }

    public static void registerServer() {

    }

    public static void registerClient() {
        FMLCommonHandler.instance().bus().register(TrainControlClientHandler.instance());
    }

}
