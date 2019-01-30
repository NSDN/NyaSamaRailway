package club.nsdn.nyasamatelecom.tileblock;

import club.nsdn.nyasamatelecom.NyaSamaTelecom;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import club.nsdn.nyasamatelecom.renderer.*;
import club.nsdn.nyasamatelecom.tileblock.core.*;
import club.nsdn.nyasamatelecom.tileblock.redstone.*;
import club.nsdn.nyasamatelecom.tileblock.wireless.*;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.LinkedHashMap;

/**
 * Created by drzzm32 on 2019.1.29.
 */
public class TileEntityModelBinder {

    private static TileEntityModelBinder instance;
    public static TileEntityModelBinder instance() {
        if (instance == null) instance = new TileEntityModelBinder();
        return instance;
    }

    public static LinkedHashMap<Class<? extends TileEntityBase>, AbsTileEntitySpecialRenderer> renderers;

    private static void bind(AbsTileEntitySpecialRenderer renderer, Class<? extends TileEntityBase> tileEntity) {
        ClientRegistry.bindTileEntitySpecialRenderer(tileEntity, renderer);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void registerTileEntitySpecialRenderers(ModelRegistryEvent event) {
        NyaSamaTelecom.logger.info("registering TESRs");
        for (Class<? extends TileEntityBase> tile : renderers.keySet()) {
            bind(renderers.get(tile), tile);
        }
    }

    public TileEntityModelBinder() {
        renderers = new LinkedHashMap<>();

        renderers.put(BlockNSPGA.TileEntityNSPGA.class, new NSPGARenderer());
        renderers.put(BlockNSASMBox.TileEntityNSASMBox.class, new SignalBoxRenderer(false, "nsasm_box_base"));
        renderers.put(BlockSignalBox.TileEntitySignalBox.class, new SignalBoxRenderer(false));
        renderers.put(BlockSignalBoxSender.TileEntitySignalBoxSender.class, new SignalBoxRenderer(true));
        renderers.put(BlockSignalBoxGetter.TileEntitySignalBoxGetter.class, new SignalBoxRenderer(false, "signal_box_gt"));
        renderers.put(BlockTriStateSignalBox.TileEntityTriStateSignalBox.class, new TriStateSignalBoxRenderer());

        renderers.put(BlockRedInput.TileEntityRedInput.class, new SignalBoxRenderer(true, "signal_box_redstone"));
        renderers.put(BlockRedOutput.TileEntityRedOutput.class, new SignalBoxRenderer(false, "signal_box_redstone"));

        renderers.put(BlockWirelessRx.TileEntityWirelessRx.class, new SignalBoxRenderer(true, "signal_box_wireless"));
        renderers.put(BlockWirelessTx.TileEntityWirelessTx.class, new SignalBoxRenderer(false, "signal_box_wireless"));

        renderers.put(BlockRSLatch.TileEntityRSLatch.class, new AdvancedBoxRenderer(true, "rs_latch_base"));
        renderers.put(BlockTimer.TileEntityTimer.class, new AdvancedBoxRenderer(true, "timer_base"));
        renderers.put(BlockDelayer.TileEntityDelayer.class, new SignalBoxRenderer(false, "delayer_base"));


    }

}
