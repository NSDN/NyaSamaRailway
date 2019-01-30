package club.nsdn.nyasamatelecom.block;

import club.nsdn.nyasamatelecom.NyaSamaTelecom;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import club.nsdn.nyasamatelecom.creativetab.CreativeTabLoader;

/**
 * Created by drzzm32 on 2018.12.12.
 */
public class BlockSign extends BlockGlass {

    public BlockSign() {
        super(Material.GLASS, false);
        setUnlocalizedName("NSTSign");
        setRegistryName(NyaSamaTelecom.MODID, "nst_sign");
        setHardness(2.0F);
        setLightLevel(1);
        setSoundType(SoundType.GLASS);
        setResistance(10.0F);
        setCreativeTab(CreativeTabLoader.tabNyaSamaTelecom);
    }

}
