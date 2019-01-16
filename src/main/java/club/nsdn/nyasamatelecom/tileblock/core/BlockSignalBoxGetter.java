package club.nsdn.nyasamatelecom.tileblock.core;

import club.nsdn.nyasamatelecom.NyaSamaTelecom;
import club.nsdn.nyasamatelecom.api.device.SignalBoxGetter;
import club.nsdn.nyasamatelecom.creativetab.CreativeTabLoader;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2017.12.29.
 */
public class BlockSignalBoxGetter extends SignalBoxGetter {

    public static class TileEntitySignalBoxGetter extends SignalBoxGetter.TileEntitySignalBoxGetter {
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntitySignalBoxGetter();
    }

    public BlockSignalBoxGetter() {
        super(NyaSamaTelecom.modid, "BlockSignalBoxGetter", "signal_box_getter");
        setCreativeTab(CreativeTabLoader.tabNyaSamaTelecom);
    }

}
