package club.nsdn.nyasamarailway.event;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.block.BlockLoader;
import club.nsdn.nyasamarailway.entity.EntityLoader;
import club.nsdn.nyasamarailway.entity.EntityModelBinder;
import club.nsdn.nyasamarailway.item.ItemLoader;
import club.nsdn.nyasamarailway.tileblock.TileEntityLoader;
import club.nsdn.nyasamarailway.tileblock.TileEntityModelBinder;
import club.nsdn.nyasamarailway.util.SoundUtil;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;

/**
 * Created by drzzm32 on 2016.5.13.
 */
public class EventRegister {

    public static void registerCommon() {
        MinecraftForge.EVENT_BUS.register(BlockLoader.instance());
        MinecraftForge.EVENT_BUS.register(ItemLoader.instance());

        MinecraftForge.EVENT_BUS.register(TileEntityLoader.instance());
        MinecraftForge.EVENT_BUS.register(TileEntityModelBinder.instance());

        MinecraftForge.EVENT_BUS.register(EntityLoader.instance());
        MinecraftForge.EVENT_BUS.register(EntityModelBinder.instance());

        MinecraftForge.EVENT_BUS.register(SoundUtil.instance());

        MinecraftForge.EVENT_BUS.register(MinecartInteractHandler.instance());

        ForgeChunkManager.setForcedChunkLoadingCallback(NyaSamaRailway.getInstance(), ChunkLoaderHandler.instance());
        MinecraftForge.EVENT_BUS.register(ChunkLoaderHandler.instance());
    }

    public static void registerServer() {

    }

    public static void registerClient() {
        MinecraftForge.EVENT_BUS.register(NTPCtrlHandler.instance());
    }

}
