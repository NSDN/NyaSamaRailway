package club.nsdn.nyasamarailway.tileblock.signal.core;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2017.8.10.
 */
public class BlockSignalBoxSender extends club.nsdn.nyasamatelecom.api.device.SignalBoxSender {

    public static class TileEntitySignalBoxSender extends club.nsdn.nyasamatelecom.api.device.SignalBoxSender.TileEntitySignalBoxSender {
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntitySignalBoxSender();
    }

    public BlockSignalBoxSender() {
        super(NyaSamaRailway.MODID, "SignalBoxSender", "signal_box_sender");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

}
