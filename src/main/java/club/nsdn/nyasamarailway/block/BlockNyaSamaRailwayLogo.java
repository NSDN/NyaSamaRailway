package club.nsdn.nyasamarailway.block;

/**
 * Created by drzzm32 on 2016.5.6.
 */

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockNyaSamaRailwayLogo extends Block {

    public BlockNyaSamaRailwayLogo() {
        super(Material.glass);
        setBlockName("NyaSamaRailwayLogo");
        setBlockTextureName("nyasamarailway:nsdn_r_logo");
        setHardness(2.0F);
        setLightLevel(1);
        setStepSound(Block.soundTypeGlass);
        setResistance(10.0F);
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

}
