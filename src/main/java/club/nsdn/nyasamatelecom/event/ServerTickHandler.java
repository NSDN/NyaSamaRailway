package club.nsdn.nyasamatelecom.event;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Created by drzzm32 on 2018.12.13.
 */
public class ServerTickHandler {

    private static ServerTickHandler instance;

    public static ServerTickHandler instance() {
        if (instance == null)
            instance = new ServerTickHandler();
        return instance;
    }

    @SubscribeEvent
    public void tick(TickEvent.ServerTickEvent event) {
    }

}
