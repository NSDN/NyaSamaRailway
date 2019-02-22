package org.thewdj.linkage.core;

import club.nsdn.nyasamatelecom.api.util.Util;
import com.google.common.collect.MapMaker;
import org.thewdj.linkage.api.ILinkableCart;
import org.thewdj.linkage.api.IToolCrowbar;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Map;

public class CrowbarHandler {
    public static final float SMACK_VELOCITY = 0.07f;
    private static final Map<EntityPlayer, EntityMinecart> linkMap = new MapMaker().weakKeys().weakValues().makeMap();
    private static CrowbarHandler instance;

    public static CrowbarHandler instance() {
        if (instance == null)
            instance = new CrowbarHandler();
        return instance;
    }

    @SubscribeEvent
    public void onEntityInteract(MinecartInteractEvent event) {
        EntityPlayer thePlayer = event.getPlayer();
        Entity entity = event.getEntity();
        EnumHand hand = event.getHand();

        if (event.getItem().getItem() instanceof IToolCrowbar)
            event.setCanceled(true);

        ItemStack stack = event.getItem();
        if (!stack.isEmpty() && stack.getItem() instanceof IToolCrowbar) {
            thePlayer.swingArm(event.getHand());
            event.setCanceled(true);
        } else
            return;

        if (thePlayer.world.isRemote)
            return;

        IToolCrowbar crowbar = (IToolCrowbar) stack.getItem();
        if (entity instanceof EntityMinecart) {
            EntityMinecart cart = (EntityMinecart) entity;
            if (crowbar.canLink(thePlayer, hand, stack, cart))
                linkCart(thePlayer, hand, stack, cart, crowbar);
        }
    }

    private void linkCart(EntityPlayer player, EnumHand hand, ItemStack stack, EntityMinecart cart, IToolCrowbar crowbar) {
        boolean used = false;
        boolean linkable = cart instanceof ILinkableCart;
        if (!linkable || ((ILinkableCart) cart).isLinkable()) {
            EntityMinecart last = linkMap.remove(player);
            if (last != null && last.isEntityAlive()) {
                LinkageManager lm = LinkageManager.INSTANCE;
                if (lm.areLinked(cart, last, false)) {
                    lm.breakLink(cart, last);
                    used = true;
                    Util.say(player, "linkage.broken");
                    LinkageManager.printDebug("Reason For Broken Link: User removed link.");
                } else {
                    used = lm.createLink(last, cart);
                    if (used)
                        Util.say(player, "linkage.created");
                }
                if (!used)
                    Util.say(player, "linkage.failed");
            } else {
                linkMap.put(player, cart);
                Util.say(player, "linkage.started");
            }
        }
        if (used)
            crowbar.onLink(player, hand, stack, cart);
    }

}
