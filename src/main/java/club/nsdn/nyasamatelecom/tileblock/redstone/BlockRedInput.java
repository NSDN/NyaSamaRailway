package club.nsdn.nyasamatelecom.tileblock.redstone;

import club.nsdn.nyasamatelecom.NyaSamaTelecom;
import club.nsdn.nyasamatelecom.api.device.SignalBoxSender;
import club.nsdn.nyasamatelecom.creativetab.CreativeTabLoader;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Created by drzzm32 on 2017.12.29.
 */
public class BlockRedInput extends SignalBoxSender {

    public static class TileEntityRedInput extends SignalBoxSender.TileEntitySignalBoxSender {

        @Override
        public void updateSignal(World world, BlockPos pos) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof TileEntityRedInput) {
                TileEntityRedInput redInput = (TileEntityRedInput) tileEntity;

                redInput.isEnabled = inputRedstone(world, pos);

                int meta = redInput.META;
                int old = meta;
                boolean isEnabled = redInput.isEnabled;

                if (redInput.getTransceiver() != null) {
                    isEnabled = isEnabled && redInput.transceiverIsPowered();
                }

                if (isEnabled) meta |= 0x8;
                else meta &= 0x7;

                if (old != meta || redInput.prevIsEnabled != redInput.isEnabled) {
                    redInput.prevIsEnabled = redInput.isEnabled;
                    redInput.META = meta;
                    redInput.refresh();
                }
            }
        }

        public boolean inputRedstone(World world, BlockPos pos) {
            return world.isBlockIndirectlyGettingPowered(pos) > 0;
        }

    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityRedInput();
    }

    public BlockRedInput() {
        super(NyaSamaTelecom.MODID, "BlockRedInput", "signal_box_input");
        setCreativeTab(CreativeTabLoader.tabNyaSamaTelecom);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)  {
        return false;
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EnumFacing facing) {
        return true;
    }

}
