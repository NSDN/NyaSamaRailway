package club.nsdn.nyasamatelecom.tileblock.redstone;

import club.nsdn.nyasamatelecom.NyaSamaTelecom;
import club.nsdn.nyasamatelecom.api.device.SignalBoxGetter;
import club.nsdn.nyasamatelecom.creativetab.CreativeTabLoader;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Created by drzzm32 on 2018.1.29.
 */
public class BlockRedOutput extends SignalBoxGetter {

    public static class TileEntityRedOutput extends SignalBoxGetter.TileEntitySignalBoxGetter {

        @Override
        public void refresh() {
            super.refresh();
            getWorld().notifyNeighborsOfStateChange(pos, getBlockType(), false);
            getWorld().notifyNeighborsOfStateChange(pos.down(), getBlockType(), false);
        }
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityRedOutput();
    }

    public BlockRedOutput() {
        super(NyaSamaTelecom.MODID, "BlockRedOutput", "signal_box_output");
        setCreativeTab(CreativeTabLoader.tabNyaSamaTelecom);
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EnumFacing facing) {
        return true;
    }

    @Override
    public boolean canProvidePower(IBlockState state) {
        return true;
    }

    @Override
    public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing facing) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityRedOutput) {
            return ((TileEntityRedOutput) tileEntity).isEnabled ? 15 : 0;
        }
        return 0;
    }

    @Override
    public int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing facing) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityRedOutput) {
            if (!((TileEntityRedOutput) tileEntity).isEnabled) {
                return 0;
            } else {
                return facing == EnumFacing.UP ? 15 : 0;
            }
        }
        return 0;
    }

}
