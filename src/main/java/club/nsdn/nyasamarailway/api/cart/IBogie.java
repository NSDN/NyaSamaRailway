package club.nsdn.nyasamarailway.api.cart;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by drzzm32 on 2019.3.3
 */
public interface IBogie {
    @SideOnly(Side.CLIENT)
    default double getRenderFixOffset() { return 0.1875; }
}
