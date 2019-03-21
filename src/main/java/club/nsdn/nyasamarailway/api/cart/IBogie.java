package club.nsdn.nyasamarailway.api.cart;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

/**
 * Created by drzzm32 on 2019.3.3
 */
public interface IBogie {
    @SideOnly(Side.CLIENT)
    default double getRenderFixOffset() { return 0.1875; }
    default UUID getBaseCartID() { return UUID.randomUUID(); }
    default Entity getBaseCart() { return null; }
    default void setBaseCart(Entity baseCart) {  }
}
