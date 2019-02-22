package org.thewdj.linkage;

import org.apache.logging.log4j.Logger;
import org.thewdj.linkage.api.CartLinkEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import org.thewdj.linkage.core.*;

public class Linkage {

    public static Logger logger = null;

    public Linkage(Logger logger) {
        Linkage.logger = logger;

        MinecraftForge.EVENT_BUS.register(CrowbarHandler.instance());
        MinecraftForge.EVENT_BUS.register(MinecartHooks.INSTANCE);
        MinecraftForge.EVENT_BUS.register(LinkageHandler.getInstance());
        MinecraftForge.EVENT_BUS.register(Train.getTicker());
        MinecraftForge.EVENT_BUS.register(new Object() {
            @SubscribeEvent
            public void logout(PlayerEvent.PlayerLoggedOutEvent event) {
                Entity riding = event.player.getRidingEntity();
                if (riding instanceof EntityMinecart) {
                    EntityMinecart cart = (EntityMinecart) riding;
                    if (Train.isPartOfTrain(cart))
                        CartTools.removePassengers(cart, event.player.getPositionVector().addVector(0, 1, 0));
                }
            }
        });
        MinecraftForge.EVENT_BUS.register(new Object() {
            @SubscribeEvent(priority = EventPriority.HIGHEST)
            public void onLinking(CartLinkEvent.Link event) {
                Train.repairTrain(event.getCartOne(), event.getCartTwo());
            }

            @SubscribeEvent(priority = EventPriority.HIGHEST)
            public void onUnlinking(CartLinkEvent.Unlink event) {
                Train.killTrain(event.getCartOne());
                Train.killTrain(event.getCartTwo());
            }
        });
    }

}
