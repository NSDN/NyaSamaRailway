package club.nsdn.nyasamatelecom.block;

import club.nsdn.nyasamatelecom.NyaSamaTelecom;
import club.nsdn.nyasamatelecom.creativetab.CreativeTabLoader;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

/**
 * Created by drzzm32 on 2018.12.12.
 */
public class BlockNSDNLogo extends BlockGlass {

    public BlockNSDNLogo() {
        super(Material.GLASS, false);
        setUnlocalizedName("NSDNLogo");
        setRegistryName(NyaSamaTelecom.MODID, "nst_nsdn");
        setHardness(2.0F);
        setLightLevel(1);
        setSoundType(SoundType.GLASS);
        setResistance(10.0F);
        setCreativeTab(CreativeTabLoader.tabNyaSamaTelecom);
    }

}
