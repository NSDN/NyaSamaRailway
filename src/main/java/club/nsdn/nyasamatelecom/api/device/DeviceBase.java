package club.nsdn.nyasamatelecom.api.device;

import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.*;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by drzzm32 on 2018.12.13.
 */
public class DeviceBase extends BlockContainer {

    public DeviceBase(String blockName) {
        super(Material.ROCK);
        setUnlocalizedName(blockName);
        setHardness(2.0F);
        setLightLevel(0);
        setLightOpacity(0);
        setSoundType(SoundType.STONE);
        setResistance(10.0F);
    }

    public DeviceBase(Material material, String blockName) {
        super(material);
        setUnlocalizedName(blockName);
        setHardness(2.0F);
        setLightLevel(0);
        setLightOpacity(0);
        setSoundType(SoundType.GLASS);
        setResistance(10.0F);
    }

    @Override
    public TileEntity createNewTileEntity(@Nullable World world, int meta) {
        return new TileEntityBase();
    }

    protected void setDeviceMeta(IBlockAccess world, BlockPos pos, int meta) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity != null)
            if (tileEntity instanceof TileEntityBase)
                ((TileEntityBase) tileEntity).META = meta;
    }

    @Override
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity != null)
            if (tileEntity instanceof TileEntityBase)
                return ((TileEntityBase) tileEntity).AABB();
        return Block.FULL_BLOCK_AABB;
    }

    @Override
    public boolean isFullCube(IBlockState p_isFullCube_1_) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack itemStack) {
        int val = MathHelper.floor((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        if (player.rotationPitch > 22.5F) {
            setDeviceMeta(world, pos, val);
        } else if (player.rotationPitch > -22.5F) {
            setDeviceMeta(world, pos, val + 4);
        } else {
            setDeviceMeta(world, pos, val + 8);
        }
    }

}
