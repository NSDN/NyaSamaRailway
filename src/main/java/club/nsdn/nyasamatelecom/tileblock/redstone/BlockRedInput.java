package club.nsdn.nyasamatelecom.tileblock.redstone;

import club.nsdn.nyasamatelecom.NyaSamaTelecom;
import club.nsdn.nyasamatelecom.api.device.SignalBoxSender;
import club.nsdn.nyasamatelecom.creativetab.CreativeTabLoader;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2017.12.29.
 */
public class BlockRedInput extends SignalBoxSender {

    public static class TileEntitySignalBoxSender extends SignalBoxSender.TileEntitySignalBoxSender {
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntitySignalBoxSender();
    }

    public BlockRedInput() {
        super(NyaSamaTelecom.modid, "BlockRedInput", "signal_box_input");
        setCreativeTab(CreativeTabLoader.tabNyaSamaTelecom);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        return false;
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
        return true;
    }

    @Override
    public void updateSignal(World world, int x , int y, int z) {
        if (world.getTileEntity(x, y, z) == null) return;
        if (world.getTileEntity(x, y, z) instanceof TileEntitySignalBoxSender) {
            TileEntitySignalBoxSender sender = (TileEntitySignalBoxSender) world.getTileEntity(x, y, z);

            sender.isEnabled = inputRedstone(world, x, y, z);

            int meta = world.getBlockMetadata(x, y, z);
            int old = meta;
            boolean isEnabled = sender.isEnabled;

            if (sender.getTransceiver() != null) {
                isEnabled = isEnabled && sender.transceiverIsPowered();
            }

            if (isEnabled) meta |= 0x8;
            else meta &= 0x7;

            if (old != meta || sender.prevIsEnabled != sender.isEnabled) {
                sender.prevIsEnabled = sender.isEnabled;
                world.setBlockMetadataWithNotify(x, y, z, meta, 3);
                world.markBlockForUpdate(x, y, z);
            }

            world.scheduleBlockUpdate(x, y, z, this, 1);
        }
    }

    public boolean inputRedstone(World world, int x , int y, int z) {
        return world.isBlockIndirectlyGettingPowered(x, y, z);
    }

}
