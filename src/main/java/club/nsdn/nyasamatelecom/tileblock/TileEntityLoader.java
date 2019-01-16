package club.nsdn.nyasamatelecom.tileblock;

import club.nsdn.nyasamatelecom.tileblock.core.*;
import club.nsdn.nyasamatelecom.tileblock.redstone.*;
import club.nsdn.nyasamatelecom.tileblock.wireless.*;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraft.tileentity.TileEntity;

import java.util.LinkedHashMap;

/**
 * Created by drzzm32 on 2016.10.8.
 */
public class TileEntityLoader {

    public static LinkedHashMap<String, Class<? extends TileEntity>> tileEntities;

    private static void register(Class<? extends TileEntity> tileEntity, String name) {
        GameRegistry.registerTileEntity(tileEntity, name);
    }

    public TileEntityLoader(FMLInitializationEvent event) {
        tileEntities = new LinkedHashMap<String, Class<? extends TileEntity>>();

        tileEntities.put("tileNSPGA", BlockNSPGA.TileEntityNSPGA.class);
        tileEntities.put("tileNSASMBox", BlockNSASMBox.TileEntityNSASMBox.class);
        tileEntities.put("tileSignalBox", BlockSignalBox.TileEntitySignalBox.class);
        tileEntities.put("tileSignalBoxSender", BlockSignalBoxSender.TileEntitySignalBoxSender.class);
        tileEntities.put("tileSignalBoxGetter", BlockSignalBoxGetter.TileEntitySignalBoxGetter.class);
        tileEntities.put("tileTriStateSignalBox", BlockTriStateSignalBox.TileEntityTriStateSignalBox.class);

        tileEntities.put("tileRedInput", BlockRedInput.TileEntitySignalBoxSender.class);
        tileEntities.put("tileRedOutput", BlockRedOutput.TileEntitySignalBoxGetter.class);

        tileEntities.put("tileWirelessRx", BlockWirelessRx.TileEntityWirelessRx.class);
        tileEntities.put("tileWirelessTx", BlockWirelessTx.TileEntityWirelessTx.class);

        tileEntities.put("tileRSLatch", BlockRSLatch.TileEntityRSLatch.class);
        tileEntities.put("tileTimer", BlockTimer.TileEntityTimer.class);
        tileEntities.put("tileDelayer", BlockDelayer.TileEntityDelayer.class);

        for (String name : tileEntities.keySet()) {
            register(tileEntities.get(name), name);
        }
    }

}
