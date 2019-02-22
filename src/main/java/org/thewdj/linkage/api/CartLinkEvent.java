package org.thewdj.linkage.api;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraftforge.fml.common.eventhandler.Event;

public class CartLinkEvent extends Event {

    private final EntityMinecart one;
    private final EntityMinecart two;

    CartLinkEvent(EntityMinecart one, EntityMinecart two) {
        this.one = one;
        this.two = two;
    }

    public EntityMinecart getCartOne() {
        return one;
    }

    public EntityMinecart getCartTwo() {
        return two;
    }

    public static final class Link extends CartLinkEvent {
        public Link(EntityMinecart one, EntityMinecart two) {
            super(one, two);
        }
    }

    public static final class Unlink extends CartLinkEvent {
        public Unlink(EntityMinecart one, EntityMinecart two) {
            super(one, two);
        }
    }
}
