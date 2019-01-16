package club.nsdn.nyasamatelecom.tileblock.core;

import club.nsdn.nyasamatelecom.NyaSamaTelecom;
import club.nsdn.nyasamatelecom.api.device.SignalBoxSender;
import club.nsdn.nyasamatelecom.creativetab.CreativeTabLoader;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2017.12.29.
 */
public class BlockSignalBoxSender extends SignalBoxSender {

    public static class TileEntitySignalBoxSender extends SignalBoxSender.TileEntitySignalBoxSender {
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntitySignalBoxSender();
    }

    public BlockSignalBoxSender() {
        super(NyaSamaTelecom.modid, "BlockSignalBoxSender", "signal_box_sender");
        setCreativeTab(CreativeTabLoader.tabNyaSamaTelecom);
    }

}
