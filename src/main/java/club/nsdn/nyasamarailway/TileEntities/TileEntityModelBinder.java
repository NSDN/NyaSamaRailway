package club.nsdn.nyasamarailway.TileEntities;

/**
 * Created by drzzm32 on 2016.5.5.
 */

import club.nsdn.nyasamarailway.Blocks.BlockRailProtectHead;
import club.nsdn.nyasamarailway.Renderers.*;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;

public class TileEntityModelBinder {

    public TileEntityModelBinder(FMLInitializationEvent event) {

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityTrackPlate.TrackPlate.class,
                new BaseRenderer(new TrackPlateModel()));

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityTrackShelf.TrackShelf.class,
                new BaseRenderer(new TrackShelfModel()));

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityTrackShelfLow.TrackShelfLow.class,
                new BaseRenderer(new TrackShelfLowModel()));


    }

}
