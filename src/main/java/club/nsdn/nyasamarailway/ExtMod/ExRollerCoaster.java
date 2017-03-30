package club.nsdn.nyasamarailway.ExtMod;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;

/**
 * Created by drzzm32 on 2016.10.12.
 */
public class ExRollerCoaster implements IExtMod {
    public static ExRollerCoaster instance;
    public static final String modid = "erc";

    public static void setInstance(ExRollerCoaster instance) {
        ExRollerCoaster.instance = instance;
    }

    public static ExRollerCoaster getInstance() {
        return instance;
    }

    @Override
    public String getModID() {
        return modid;
    }

    @Override
    public boolean verifyBlock(Block block) {
        return block.getClass().getSuperclass().getName().contains("blockRailBase") &&
               block.getClass().getName().contains("erc");
    }

    @Override
    public boolean verifyEntity(Entity entity) {
        return entity.getClass().getSuperclass().getName().contains("Wrap_EntityCoaster") ||
               entity.getClass().getSuperclass().getSuperclass().getName().contains("Wrap_EntityCoaster");
    }

}
