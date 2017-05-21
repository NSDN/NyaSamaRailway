package club.nsdn.nyasamarailway.Blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import club.nsdn.nyasamarailway.CreativeTab.CreativeTabLoader;

/**
 * Created by drzzm32 on 2017.5.21.
 */
public class BlockSign extends Block {

    public BlockSign() {
        super(Material.glass);
        setBlockName("NyaSamaRailwayBlockSign");
        setBlockTextureName("nyasamarailway:logo");
        setHardness(2.0F);
        setLightLevel(1);
        setStepSound(Block.soundTypeGlass);
        setResistance(10.0F);
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

}
