package club.nsdn.nyasamarailway.Blocks;

/**
 * Created by drzzm32 on 2016.5.5.
 */

import net.minecraft.world.World;
import net.minecraft.block.BlockRail;
import net.minecraft.entity.item.EntityMinecart;
import club.nsdn.nyasamarailway.CreativeTab.CreativeTabLoader;

public class BlockRailBase extends BlockRail{

    public BlockRailBase(String name)
    {
        super();
        setBlockName(name);
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    protected void setTextureName(String name) {
        setBlockTextureName("nyasamarailway" + ":" + name);
    }

    @Override
    public float getRailMaxSpeed(World world, EntityMinecart cart, int x, int y, int z) {
        return 1.0F;
    }

}
