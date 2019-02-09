package club.nsdn.nyasamarailway.tileblock;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamatelecom.api.device.DeviceBase;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;

/**
 * Created by drzzm32 on 2016.5.5.
 */
public class TileBlock extends DeviceBase { // Only for decoration blocks

    public TileBlock(String blockName) {
        super(Material.IRON, blockName);
        setSoundType(SoundType.METAL);
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    public TileEntity createNewTileEntity() {
        return new TileEntityBase();
    }

    /**
     * Get Face's facing
     * */
    public EnumFacing getDirFromMeta(int meta) {
        switch (meta & 0x3) {
            case 0: return EnumFacing.NORTH;
            case 1: return EnumFacing.EAST;
            case 2: return EnumFacing.SOUTH;
            case 3: return EnumFacing.WEST;
        }
        return EnumFacing.DOWN;
    }

    public int getDeviceMeta(World world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityBase)
            return ((TileEntityBase) tileEntity).META;
        return -1;
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
