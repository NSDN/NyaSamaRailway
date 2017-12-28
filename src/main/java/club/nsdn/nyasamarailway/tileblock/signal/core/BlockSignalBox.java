package club.nsdn.nyasamarailway.tileblock.signal.core;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.extmod.Railcraft;
import club.nsdn.nyasamarailway.tileblock.signal.TileEntitySignalLight;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2017.8.9.
 */
public class BlockSignalBox extends club.nsdn.nyasamatelecom.api.device.SignalBox {

    public static class TileEntitySignalBox extends club.nsdn.nyasamatelecom.api.device.SignalBox.TileEntitySignalBox {

        @Override
        public boolean tryControlFirst(boolean state) {
            TileEntity railTarget = getTarget();

            if (railTarget == null) return false;
            if (Railcraft.getInstance() == null) return false;
            if (!Railcraft.getInstance().verifySwitch(railTarget)) return false;

            Railcraft.getInstance().setSwitch(railTarget, state);
            return true;
        }

        @Override
        public boolean tryControlSecond(boolean state) {
            TileEntity railTarget = getTarget();
            if (railTarget == null) return false;

            if (railTarget instanceof TileEntitySignalLight) {
                ((TileEntitySignalLight) railTarget).isPowered = state;
                return true;
            }
            return false;
        }

    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntitySignalBox();
    }

    public BlockSignalBox() {
        super(NyaSamaRailway.MODID, "SignalBox", "signal_box");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

}
