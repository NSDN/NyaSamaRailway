package club.nsdn.nyasamarailway.tileblock.signal.core;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2017.8.9.
 */
public class BlockTriStateSignalBox extends club.nsdn.nyasamatelecom.api.device.TriStateSignalBox {

    public static class TileEntityTriStateSignalBox extends club.nsdn.nyasamatelecom.api.device.TriStateSignalBox.TileEntityTriStateSignalBox {
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityTriStateSignalBox();
    }

    public BlockTriStateSignalBox() {
        super(NyaSamaRailway.MODID, "TriStateSignalBox", "tri_state_signal_box");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

}
