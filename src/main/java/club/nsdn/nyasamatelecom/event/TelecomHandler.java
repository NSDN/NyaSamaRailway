package club.nsdn.nyasamatelecom.event;

import club.nsdn.nyasamatelecom.util.TelecomProcessor;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Created by drzzm32 on 2018.12.13.
 */
public class TelecomHandler {

    private static TelecomHandler instance;

    public static TelecomHandler instance() {
        if (instance == null)
            instance = new TelecomHandler();
        return instance;
    }

    @SubscribeEvent
    public void tick(TickEvent.ServerTickEvent event) {
        TelecomProcessor.instance().update();
    }

}
