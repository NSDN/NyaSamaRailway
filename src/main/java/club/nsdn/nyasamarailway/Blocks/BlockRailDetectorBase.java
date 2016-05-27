package club.nsdn.nyasamarailway.Blocks;

/**
 * Created by drzzm32 on 2016.5.5.
 */

import club.nsdn.nyasamarailway.CreativeTab.CreativeTabLoader;
import net.minecraft.block.BlockRailDetector;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.world.World;

public class BlockRailDetectorBase extends BlockRailDetector{

    public BlockRailDetectorBase(String name)
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
