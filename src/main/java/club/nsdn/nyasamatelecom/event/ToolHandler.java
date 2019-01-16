package club.nsdn.nyasamatelecom.event;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

/**
 * Created by drzzm32 on 2016.10.8.
 */
public class ToolHandler {
    private static ToolHandler instance;

    public static ToolHandler instance() {
        if (instance == null)
            instance = new ToolHandler();
        return instance;
    }

    @SubscribeEvent
    public void onEntityInteract(PlayerInteractEvent event) {

    }

}
