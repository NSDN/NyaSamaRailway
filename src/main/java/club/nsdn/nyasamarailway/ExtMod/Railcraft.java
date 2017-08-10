package club.nsdn.nyasamarailway.ExtMod;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by drzzm32 on 2016.10.12.
 */
public class Railcraft implements IExtMod {
    public static Railcraft instance;
    public static final String modid = "Railcraft";

    public static void setInstance(Railcraft instance) {
        Railcraft.instance = instance;
    }

    public static Railcraft getInstance() {
        return instance;
    }

    @Override
    public String getModID() {
        return modid;
    }

    @Override
    public boolean verifyBlock(Block block) {
        return block.getClass().getName().contains("mods.railcraft.common.blocks");
    }

    @Override
    public boolean verifyEntity(Entity entity) {
        return entity.getClass().getName().contains("mods.railcraft.common.carts");
    }

    public boolean verifySwitch(TileEntity tileEntity) {
        return tileEntity.getClass().getSuperclass().getName().contains(
                "mods.railcraft.common.blocks.signals.TileSwitchBase"
        ) || tileEntity.getClass().getSuperclass().getSuperclass().getName().contains(
                "mods.railcraft.common.blocks.signals.TileSwitchBase"
        );
    }

    public boolean setSwitch(TileEntity tileEntity, boolean state) {
        if (verifySwitch(tileEntity)) {
            Util.modifyNBT(tileEntity, "Powered", state);
            return true;
        } else {
            return false;
        }
    }
}
