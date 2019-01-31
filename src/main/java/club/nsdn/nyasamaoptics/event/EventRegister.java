package club.nsdn.nyasamaoptics.event;

import club.nsdn.nyasamaoptics.block.BlockLoader;
import club.nsdn.nyasamaoptics.item.ItemLoader;
import club.nsdn.nyasamaoptics.tileblock.TileEntityLoader;
import club.nsdn.nyasamaoptics.tileblock.TileEntityModelBinder;
import net.minecraftforge.common.MinecraftForge;

/**
 * Created by drzzm32 on 2019.1.30.
 */
public class EventRegister {

    public static void registerCommon() {
        MinecraftForge.EVENT_BUS.register(BlockLoader.instance());
        MinecraftForge.EVENT_BUS.register(ItemLoader.instance());

        MinecraftForge.EVENT_BUS.register(ToolHandler.instance());

        MinecraftForge.EVENT_BUS.register(TileEntityLoader.instance());
        MinecraftForge.EVENT_BUS.register(TileEntityModelBinder.instance());
    }

    public static void registerServer() {

    }

    public static void registerClient() {

    }

}
