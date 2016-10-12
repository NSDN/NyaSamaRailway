package club.nsdn.nyasamarailway.ExtMod;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;

/**
 * Created by drzzm32 on 2016.10.12.
 */
public class RailsOfWar implements IExtMod {
    public static RailsOfWar instance;
    public static final String modid = "row";

    public static void setInstance(RailsOfWar instance) {
        RailsOfWar.instance = instance;
    }

    public static RailsOfWar getInstance() {
        return instance;
    }

    @Override
    public String getModID() {
        return modid;
    }

    @Override
    public boolean verifyBlock(Block block) {
        return block.getClass().getName().contains("BlockPointer") &&
               block.getClass().getName().contains("net.row"); //Verify pointer only, not rail
    }

    @Override
    public boolean verifyEntity(Entity entity) {
        return entity.getClass().getSuperclass().getSuperclass().getName().contains("RoWRollingStock");
    }
}
