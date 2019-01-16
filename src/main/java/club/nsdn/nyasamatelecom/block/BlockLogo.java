package club.nsdn.nyasamatelecom.block;

import net.minecraft.block.BlockGlass;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import club.nsdn.nyasamatelecom.NyaSamaTelecom;
import club.nsdn.nyasamatelecom.creativetab.CreativeTabLoader;

/**
 * Created by drzzm32 on 2018.12.12.
 */
public class BlockLogo extends BlockGlass implements IRegisterable {

    @Override
    public String getID() {
        return "nst_logo";
    }

    public BlockLogo() {
        super(Material.GLASS, false);
        setUnlocalizedName("NSTLogo");
        setRegistryName(NyaSamaTelecom.MODID, getID());
        setHardness(2.0F);
        setLightLevel(1);
        setSoundType(SoundType.GLASS);
        setResistance(10.0F);
        setCreativeTab(CreativeTabLoader.tabNyaSamaTelecom);
    }

}
