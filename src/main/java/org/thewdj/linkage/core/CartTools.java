package org.thewdj.linkage.core;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public final class CartTools {

    public static boolean isTravellingHighSpeed(EntityMinecart cart) {
        return true;
    }

    /**
     * Returns a minecart from a persistent UUID. Only returns carts from the same world.
     *
     * @param id Cart's persistent UUID
     * @return EntityMinecart
     */
    public static @Nullable EntityMinecart getCartFromUUID(@Nullable World world, @Nullable UUID id) {
        if (world == null || id == null)
            return null;
        if (world instanceof WorldServer) {
            Entity entity = ((WorldServer) world).getEntityFromUuid(id);
            if (entity instanceof EntityMinecart && entity.isEntityAlive()) {
                return (EntityMinecart) entity;
            }
        } else {
            // for performance reasons
            //noinspection Convert2streamapi
            for (Entity entity : world.loadedEntityList) {
                if (entity instanceof EntityMinecart && entity.isEntityAlive() && entity.getPersistentID().equals(id))
                    return (EntityMinecart) entity;
            }
        }
        return null;
    }

    public static void removePassengers(EntityMinecart cart, Vec3d resultingPosition) {
        List<Entity> passengers = cart.getPassengers();
        cart.removePassengers();
        for (Entity entity : passengers) {
            if (entity instanceof EntityPlayerMP) {
                EntityPlayerMP player = ((EntityPlayerMP) entity);
                player.setPositionAndUpdate(resultingPosition.x, resultingPosition.y, resultingPosition.z);
            } else
                entity.setLocationAndAngles(resultingPosition.x, resultingPosition.y, resultingPosition.z, entity.rotationYaw, entity.rotationPitch);
        }
    }

}
