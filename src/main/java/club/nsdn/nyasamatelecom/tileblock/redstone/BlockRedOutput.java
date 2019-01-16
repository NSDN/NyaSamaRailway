package club.nsdn.nyasamatelecom.tileblock.redstone;

import club.nsdn.nyasamatelecom.NyaSamaTelecom;
import club.nsdn.nyasamatelecom.api.device.SignalBoxGetter;
import club.nsdn.nyasamatelecom.creativetab.CreativeTabLoader;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2017.12.29.
 */
public class BlockRedOutput extends SignalBoxGetter {

    public static class TileEntitySignalBoxGetter extends SignalBoxGetter.TileEntitySignalBoxGetter {
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntitySignalBoxGetter();
    }

    public BlockRedOutput() {
        super(NyaSamaTelecom.modid, "BlockRedOutput", "signal_box_output");
        setCreativeTab(CreativeTabLoader.tabNyaSamaTelecom);
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
        return true;
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int direction) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TileEntitySignalBoxGetter) {
            return ((TileEntitySignalBoxGetter) tileEntity).isEnabled ? 15 : 0;
        }
        return 0;
    }

    @Override
    public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int direction) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TileEntitySignalBoxGetter) {
            if (!((TileEntitySignalBoxGetter) tileEntity).isEnabled) {
                return 0;
            } else {
                return direction == 1 ? 15 : 0;
            }
        }
        return 0;
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

}
