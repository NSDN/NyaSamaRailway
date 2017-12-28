package club.nsdn.nyasamarailway.extmod;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;

/**
 * Created by drzzm32 on 2016.10.12.
 */

public interface IExtMod {
    String getModID();
    boolean verifyBlock(Block block);
    boolean verifyEntity(Entity entity);
}
