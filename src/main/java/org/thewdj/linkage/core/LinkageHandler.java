package org.thewdj.linkage.core;

import org.thewdj.linkage.api.ILinkableCart;
import org.thewdj.linkage.api.ILinkageManager;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraftforge.event.entity.minecart.MinecartUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class LinkageHandler {
    public static final String LINK_A_TIMER = "linkA_timer";
    public static final String LINK_B_TIMER = "linkB_timer";
    public static final double LINK_DRAG = 0.95;
    public static final float MAX_DISTANCE = 8F;
    private static final float STIFFNESS = 0.7F;
    private static final float HS_STIFFNESS = 0.7F;
    private static final float DAMPING = 0.4F;
    private static final float HS_DAMPING = 0.3F;
    private static final float FORCE_LIMITER = 6F;
    private static LinkageHandler instance;

    private LinkageHandler() {
    }

    public static LinkageHandler getInstance() {
        if (instance == null)
            instance = new LinkageHandler();
        return instance;
    }

    /**
     * Returns the optimal distance between two linked carts that the
     * LinkageHandler will attempt to maintain at all times.
     *
     * @param cart1 EntityMinecart
     * @param cart2 EntityMinecart
     * @return The optimal distance
     */
    private float getOptimalDistance(EntityMinecart cart1, EntityMinecart cart2) {
        float dist = 0;
        if (cart1 instanceof ILinkableCart)
            dist += ((ILinkableCart) cart1).getOptimalDistance(cart2);
        else
            dist += ILinkageManager.OPTIMAL_DISTANCE;
        if (cart2 instanceof ILinkableCart)
            dist += ((ILinkableCart) cart2).getOptimalDistance(cart1);
        else
            dist += ILinkageManager.OPTIMAL_DISTANCE;
        return dist;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    private boolean canCartBeAdjustedBy(EntityMinecart cart1, EntityMinecart cart2) {
        if (cart1 == cart2)
            return false;
        if (cart1 instanceof ILinkableCart && !((ILinkableCart) cart1).canBeAdjusted(cart2))
            return false;
        return true;
    }

    /**
     * This is where the physics magic actually gets performed. It uses Spring
     * Forces and Damping Forces to maintain a fixed distance between carts.
     *
     * @param cart1 EntityMinecart
     * @param cart2 EntityMinecart
     */
    protected void adjustVelocity(EntityMinecart cart1, EntityMinecart cart2, LinkageManager.LinkType linkType) {
        String timer = LINK_A_TIMER;
        if (linkType == LinkageManager.LinkType.LINK_B)
            timer = LINK_B_TIMER;
        if (cart1.world.provider.getDimension() != cart2.world.provider.getDimension()) {
            short count = cart1.getEntityData().getShort(timer);
            count++;
            if (count > 200) {
                LinkageManager.INSTANCE.breakLink(cart1, cart2);
                LinkageManager.printDebug("Reason For Broken Link: Carts in different dimensions.");
            }
            cart1.getEntityData().setShort(timer, count);
            return;
        }
        cart1.getEntityData().setShort(timer, (short) 0);

        double dist = cart1.getDistance(cart2);
        if (dist > MAX_DISTANCE) {
            LinkageManager.INSTANCE.breakLink(cart1, cart2);
            LinkageManager.printDebug("Reason For Broken Link: Max distance exceeded.");
            return;
        }

        boolean adj1 = canCartBeAdjustedBy(cart1, cart2);
        boolean adj2 = canCartBeAdjustedBy(cart2, cart1);

        Vec2D cart1Pos = new Vec2D(cart1);
        Vec2D cart2Pos = new Vec2D(cart2);

        Vec2D unit = Vec2D.unit(cart2Pos, cart1Pos);

        // Spring force

        float optDist = getOptimalDistance(cart1, cart2);
        double stretch = dist - optDist;

        boolean highSpeed = CartTools.isTravellingHighSpeed(cart1);

        double stiffness = highSpeed ? HS_STIFFNESS : STIFFNESS;
        double springX = stiffness * stretch * unit.getX();
        double springZ = stiffness * stretch * unit.getY();

        springX = limitForce(springX);
        springZ = limitForce(springZ);

        if (adj1) {
            cart1.motionX += springX;
            cart1.motionZ += springZ;
        }

        if (adj2) {
            cart2.motionX -= springX;
            cart2.motionZ -= springZ;
        }

        // Damping

        Vec2D cart1Vel = new Vec2D(cart1.motionX, cart1.motionZ);
        Vec2D cart2Vel = new Vec2D(cart2.motionX, cart2.motionZ);

        double dot = Vec2D.subtract(cart2Vel, cart1Vel).dotProduct(unit);

        double damping = highSpeed ? HS_DAMPING : DAMPING;
        double dampX = damping * dot * unit.getX();
        double dampZ = damping * dot * unit.getY();

        dampX = limitForce(dampX);
        dampZ = limitForce(dampZ);

        if (adj1) {
            cart1.motionX += dampX;
            cart1.motionZ += dampZ;
        }

        if (adj2) {
            cart2.motionX -= dampX;
            cart2.motionZ -= dampZ;
        }
    }

    private double limitForce(double force) {
        return Math.copySign(Math.min(Math.abs(force), FORCE_LIMITER), force);
    }

    /**
     * This function inspects the links and determines if any physics
     * adjustments need to be made.
     *
     * @param cart EntityMinecart
     */
    private void adjustCart(EntityMinecart cart) {
        if (isLaunched(cart))
            return;

        if (isOnElevator(cart))
            return;

        boolean linkedA = adjustLinkedCart(cart, LinkageManager.LinkType.LINK_A);
        boolean linkedB = adjustLinkedCart(cart, LinkageManager.LinkType.LINK_B);
        boolean linked = linkedA || linkedB;

        // Drag
        if (linked && !CartTools.isTravellingHighSpeed(cart)) {
            cart.motionX *= LINK_DRAG;
            cart.motionZ *= LINK_DRAG;
        }

    }

    private boolean adjustLinkedCart(EntityMinecart cart, LinkageManager.LinkType linkType) {
        boolean linked = false;
        LinkageManager lm = LinkageManager.INSTANCE;
        EntityMinecart link = lm.getLinkedCart(cart, linkType);
        if (link != null) {
            // sanity check to ensure links are consistent
            if (!lm.areLinked(cart, link)) {
                boolean success = lm.repairLink(cart, link);
                //TODO something should happen here
            }
            if (!isLaunched(link) && !isOnElevator(link)) {
                linked = true;
                adjustVelocity(cart, link, linkType);
            }
        }
        return linked;
    }

    /**
     * This is our entry point, its triggered once per tick per cart.
     *
     * @param event MinecartUpdateEvent
     */
    @SubscribeEvent
    public void onMinecartUpdate(MinecartUpdateEvent event) {
        EntityMinecart cart = event.getMinecart();

        // Physics done here
        adjustCart(cart);

    }

    public boolean isLaunched(EntityMinecart cart) {
        int launched = cart.getEntityData().getInteger("Launched");
        return launched > 0;
    }

    public boolean isOnElevator(EntityMinecart cart) {
        int elevator = cart.getEntityData().getByte("elevator");
        return elevator > 0;
    }

}
