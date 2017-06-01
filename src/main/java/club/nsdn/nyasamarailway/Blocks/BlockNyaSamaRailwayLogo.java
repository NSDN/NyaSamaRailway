package club.nsdn.nyasamarailway.Blocks;

import club.nsdn.nyasamarailway.CreativeTab.CreativeTabLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

/**
 * Created by drzzm32 on 2017.5.21.
 */
public class BlockNyaSamaRailwayLogo extends BlockGlass implements IBlockBase {

    public BlockNyaSamaRailwayLogo() {
        super(Material.GLASS, false);
        setUnlocalizedName("NyaSamaRailwayLogo");
        setRegistryName("nyasamarailway", getRegisterID());
        setHardness(2.0F);
        setLightLevel(1);
        setSoundType(SoundType.GLASS);
        setResistance(10.0F);
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Override
    public String getRegisterID() {
        return "nsr_logo";
    }
}
