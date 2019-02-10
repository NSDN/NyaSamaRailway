package club.nsdn.nyasamarailway.block;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import net.minecraft.block.BlockPane;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class BlockIronBars extends BlockPane {

    public BlockIronBars() {
        super( Material.IRON, false);
        setUnlocalizedName("BlockIronBars");
        setRegistryName(NyaSamaRailway.MODID, "iron_bars");
        setHardness(5.0F);
        setResistance(10.0F);
        setSoundType(SoundType.METAL);
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

}
