package club.nsdn.nyasamarailway.Blocks;

import club.nsdn.nyasamarailway.CreativeTab.CreativeTabLoader;
import net.minecraft.block.BlockPane;
import net.minecraft.block.material.Material;

/**
 * Created by drzzm32 on 2017.5.21.
 */
public class BlockIronWeb extends BlockPane {

    public BlockIronWeb() {
        super("nyasamarailway:iron_web", "nyasamarailway:iron_web", Material.iron, true);
        setBlockName("BlockIronWeb");
        setHardness(2.0F);
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

}
