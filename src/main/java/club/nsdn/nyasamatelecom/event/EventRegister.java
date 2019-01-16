package club.nsdn.nyasamatelecom.event;

import club.nsdn.nyasamatelecom.block.BlockLoader;
import club.nsdn.nyasamatelecom.item.ItemLoader;
import net.minecraftforge.common.MinecraftForge;

/**
 * Created by drzzm32 on 2018.12.12.
 */
public class EventRegister {

    public static void registerCommon() {
        MinecraftForge.EVENT_BUS.register(BlockLoader.instance());
        MinecraftForge.EVENT_BUS.register(ItemLoader.instance());

        MinecraftForge.EVENT_BUS.register(ToolHandler.instance());
        MinecraftForge.EVENT_BUS.register(TelecomHandler.instance());
    }

    public static void registerServer() {
        // NOTE: Dedicated Server ONLY! Single-player will not work!
        MinecraftForge.EVENT_BUS.register(ServerTickHandler.instance());
    }

    public static void registerClient() {
        MinecraftForge.EVENT_BUS.register(ClientTickHandler.instance());
    }

}
