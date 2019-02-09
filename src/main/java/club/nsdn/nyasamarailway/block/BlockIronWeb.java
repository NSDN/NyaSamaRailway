package club.nsdn.nyasamarailway.block;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import net.minecraft.block.BlockPane;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

/**
 * Created by drzzm32 on 2016.5.9.
 */
public class BlockIronWeb extends BlockPane {

    public BlockIronWeb() {
        super( Material.IRON, false);
        setUnlocalizedName("BlockIronWeb");
        setRegistryName(NyaSamaRailway.MODID, "iron_web");
        setHardness(5.0F);
        setResistance(10.0F);
        setSoundType(SoundType.METAL);
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

}
