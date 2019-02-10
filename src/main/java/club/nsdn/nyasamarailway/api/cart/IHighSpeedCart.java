package club.nsdn.nyasamarailway.api.cart;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by drzzm32 on 2019.2.10
 */
public interface IHighSpeedCart {
    void modifyHighSpeedMode(EntityPlayer player);
    void setHighSpeedMode(boolean highSpeedMode);
    boolean getHighSpeedMode();
}
