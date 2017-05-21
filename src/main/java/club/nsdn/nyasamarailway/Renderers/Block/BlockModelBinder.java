package club.nsdn.nyasamarailway.Renderers.Block;

import club.nsdn.nyasamarailway.Blocks.BlockEdge;
import club.nsdn.nyasamarailway.Blocks.BlockPlatform;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

/**
 * Created by drzzm32 on 2017.5.21.
 */
public class BlockModelBinder {

    public BlockModelBinder(FMLInitializationEvent event) {

        BlockPlatform.renderType = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(BlockPlatform.renderType, new PlatformRenderer());

        BlockEdge.renderType = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(BlockEdge.renderType, new EdgeRenderer());

    }

}
