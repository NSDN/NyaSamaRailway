package club.nsdn.nyasamarailway.api.cart;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by drzzm32 on 2018.4.16.
 */
public interface IHighSpeedCart {
    void modifyHighSpeedMode(EntityPlayer player);
    void setHighSpeedMode(boolean highSpeedMode);
    boolean getHighSpeedMode();
}
