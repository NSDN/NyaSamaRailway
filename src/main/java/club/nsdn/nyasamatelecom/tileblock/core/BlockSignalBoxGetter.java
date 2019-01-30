package club.nsdn.nyasamatelecom.tileblock.core;

import club.nsdn.nyasamatelecom.NyaSamaTelecom;
import club.nsdn.nyasamatelecom.api.device.SignalBoxGetter;
import club.nsdn.nyasamatelecom.creativetab.CreativeTabLoader;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2019.1.29.
 */
public class BlockSignalBoxGetter extends SignalBoxGetter {

    public static class TileEntitySignalBoxGetter extends SignalBoxGetter.TileEntitySignalBoxGetter {
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntitySignalBoxGetter();
    }

    public BlockSignalBoxGetter() {
        super(NyaSamaTelecom.MODID, "BlockSignalBoxGetter", "signal_box_getter");
        setCreativeTab(CreativeTabLoader.tabNyaSamaTelecom);
    }

}
