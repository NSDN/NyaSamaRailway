package club.nsdn.nyasamatelecom.tileblock;

import club.nsdn.nyasamatelecom.NyaSamaTelecom;
import club.nsdn.nyasamatelecom.tileblock.core.*;
import club.nsdn.nyasamatelecom.tileblock.redstone.*;
import club.nsdn.nyasamatelecom.tileblock.wireless.*;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraft.tileentity.TileEntity;

import java.util.LinkedList;

/**
 * Created by drzzm32 on 2019.1.29.
 */
public class TileEntityLoader {

    private static TileEntityLoader instance;
    public static TileEntityLoader instance() {
        if (instance == null) instance = new TileEntityLoader();
        return instance;
    }

    public static LinkedList<Class<? extends TileEntity>> tileEntities;

    private static void register(Class<? extends TileEntity> tileEntity) {
        String name = tileEntity.getSimpleName().replace("TileEntity", "tile");
        GameRegistry.registerTileEntity(tileEntity, new ResourceLocation(NyaSamaTelecom.MODID, name));
    }

    @SubscribeEvent
    public void registerTileEntities(RegistryEvent.Register<Block> event) {
        NyaSamaTelecom.logger.info("registering TileEntities");
        for (Class<? extends TileEntity> t : tileEntities)
            register(t);
    }

    public TileEntityLoader() {
        tileEntities = new LinkedList<>();

        tileEntities.add(BlockNSPGA.TileEntityNSPGA.class);
        tileEntities.add(BlockNSASMBox.TileEntityNSASMBox.class);
        tileEntities.add(BlockSignalBox.TileEntitySignalBox.class);
        tileEntities.add(BlockSignalBoxSender.TileEntitySignalBoxSender.class);
        tileEntities.add(BlockSignalBoxGetter.TileEntitySignalBoxGetter.class);
        tileEntities.add(BlockTriStateSignalBox.TileEntityTriStateSignalBox.class);

        tileEntities.add(BlockRedInput.TileEntityRedInput.class);
        tileEntities.add(BlockRedOutput.TileEntityRedOutput.class);

        tileEntities.add(BlockWirelessRx.TileEntityWirelessRx.class);
        tileEntities.add(BlockWirelessTx.TileEntityWirelessTx.class);

        tileEntities.add(BlockRSLatch.TileEntityRSLatch.class);
        tileEntities.add(BlockTimer.TileEntityTimer.class);
        tileEntities.add(BlockDelayer.TileEntityDelayer.class);
    }

}
