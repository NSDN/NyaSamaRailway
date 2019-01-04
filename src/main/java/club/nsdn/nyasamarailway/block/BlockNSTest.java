package club.nsdn.nyasamarailway.block;

/**
 * Created by drzzm32 on 2019.1.4.
 */

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockNSTest extends Block {

    public BlockNSTest() {
        super(Material.glass);
        setBlockName("BlockNSTest");
        setBlockTextureName("nyasamarailway:nst_test");
        setHardness(2.0F);
        setLightLevel(1);
        setStepSound(Block.soundTypeGlass);
        setResistance(10.0F);
        setCreativeTab(CreativeTabLoader.tabNSTest);
    }

}
