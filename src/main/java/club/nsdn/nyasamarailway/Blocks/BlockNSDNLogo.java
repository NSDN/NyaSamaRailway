package club.nsdn.nyasamarailway.Blocks;

/**
 * Created by drzzm32 on 2016.5.6.
 */

import club.nsdn.nyasamarailway.CreativeTab.CreativeTabLoader;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockNSDNLogo extends Block {

    public BlockNSDNLogo() {
        super(Material.glass);
        setBlockName("NyaSamaRailwayNSDNLogo");
        setBlockTextureName("nyasamarailway:nsdn_logo");
        setHardness(2.0F);
        setLightLevel(1);
        setStepSound(Block.soundTypeGlass);
        setResistance(10.0F);
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

}
