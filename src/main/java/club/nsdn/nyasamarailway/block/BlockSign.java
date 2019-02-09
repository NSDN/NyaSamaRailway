package club.nsdn.nyasamarailway.block;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

/**
 * Created by drzzm32 on 2019.1.30.
 */
public class BlockSign extends BlockGlass {

    public BlockSign() {
        super(Material.GLASS, false);
        setUnlocalizedName("NSRSign");
        setRegistryName(NyaSamaRailway.MODID, "nsr_sign");
        setHardness(2.0F);
        setLightLevel(1);
        setSoundType(SoundType.GLASS);
        setResistance(10.0F);
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

}
