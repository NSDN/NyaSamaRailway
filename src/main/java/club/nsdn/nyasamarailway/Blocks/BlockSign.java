package club.nsdn.nyasamarailway.Blocks;

import club.nsdn.nyasamarailway.CreativeTab.CreativeTabLoader;
import club.nsdn.nyasamarailway.NyaSamaRailway;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

/**
 * Created by drzzm32 on 2017.5.21.
 */
public class BlockSign extends BlockGlass implements IBlockBase {

    public BlockSign() {
        super(Material.GLASS, false);
        setUnlocalizedName("NyaSamaRailwayBlockSign");
        setRegistryName(NyaSamaRailway.modid, getRegisterID());
        setHardness(2.0F);
        setLightLevel(1);
        setSoundType(SoundType.GLASS);
        setResistance(10.0F);
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Override
    public String getRegisterID() {
        return "nsr_sign";
    }
}
