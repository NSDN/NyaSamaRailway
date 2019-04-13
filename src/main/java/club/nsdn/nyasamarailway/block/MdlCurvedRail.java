package club.nsdn.nyasamarailway.block;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.4.13.
 */
public class MdlCurvedRail extends Block {

    public static enum EnumType implements IStringSerializable {
        MONO("mono"),
        NS("ns"),
        SS("ss");

        private final String name;

        EnumType(String name) { this.name = name; }

        public String toString() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }

        public static EnumType from(int i) {
            int len = values().length;
            if (i >= len) i = 0;
            return values()[i];
        }
    }

    public static final PropertyEnum<EnumType> TYPE = PropertyEnum.create("type", EnumType.class);

    public MdlCurvedRail() {
        super(Material.IRON);
        setUnlocalizedName("MdlCurvedRail");
        setRegistryName(NyaSamaRailway.MODID, "curved_rail");
        setHardness(5.0F);
        setLightOpacity(1);
        setResistance(10.0F);
        setSoundType(SoundType.METAL);
        setDefaultState(blockState.getBaseState());
    }

    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(TYPE, EnumType.from(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE).ordinal();
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

}
