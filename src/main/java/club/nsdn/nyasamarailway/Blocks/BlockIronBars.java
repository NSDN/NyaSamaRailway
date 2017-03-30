package club.nsdn.nyasamarailway.Blocks;

import club.nsdn.nyasamarailway.CreativeTab.CreativeTabLoader;
import net.minecraft.block.BlockPane;
import net.minecraft.block.material.Material;

/**
 * Created by drzzm32 on 2016.5.9.
 */

public class BlockIronBars extends BlockPane {

    public BlockIronBars() {
        super("nyasamarailway:iron_bars", "nyasamarailway:iron_bars", Material.iron, true);
        setBlockName("BlockIronBars");
        setHardness(2.0F);
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

}
