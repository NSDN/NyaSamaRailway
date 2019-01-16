package club.nsdn.nyasamatelecom.api.device;

import club.nsdn.nyasamatelecom.api.tileentity.TileEntityReceiver;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityTriStateTransmitter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by drzzm32 on 2018.12.13.
 */
public class TriStateSignalBox extends DeviceBase {

    public static class TileEntityTriStateSignalBox extends TileEntityTriStateTransmitter {

        public TileEntityTriStateSignalBox() {
            setInfo(8, 0.75, 0.5, 0.75);
        }

        @Override
        public void update() {
            super.update();
            if (!getWorld().isRemote)
                updateSignal(world, getPos());
        }

        public void updateSignal(World world, BlockPos pos) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity == null) return;
            if (tileEntity instanceof TileEntityTriStateSignalBox) {
                TileEntityTriStateSignalBox signalBox = (TileEntityTriStateSignalBox) tileEntity;

                int meta = signalBox.META;
                int old = meta;
                boolean isEnabled;

                if (signalBox.getSender() == null) {
                    isEnabled = (meta & 0x8) != 0;
                    meta &= 0x7;
                } else {
                    isEnabled = signalBox.senderIsPowered();

                    if (isEnabled) meta |= 0x8;
                    else meta &= 0x7;
                }

                if (signalBox.inverterEnabled) isEnabled = !isEnabled;

                if (!signalBox.setSwitch(isEnabled)) {
                    int i = 1;
                }

                if (old != meta || signalBox.prevInverterEnabled != signalBox.inverterEnabled
                        || signalBox.prevTriStateIsNeg != signalBox.triStateIsNeg) {
                    signalBox.prevInverterEnabled = signalBox.inverterEnabled;
                    signalBox.prevTriStateIsNeg = signalBox.triStateIsNeg;
                    signalBox.META = meta;
                    signalBox.refresh();
                }
            }
        }

    }

    public TriStateSignalBox(String modid, String name, String id) {
        super(name);
        setRegistryName(modid, id);
        setLightOpacity(0);
        setLightLevel(0.1F);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityTriStateSignalBox();
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack itemStack) {
        int val = MathHelper.floor((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        if (player.rotationPitch > 22.5F) {
            setDeviceMeta(world, pos, val);
        } else {
            setDeviceMeta(world, pos, val + 4);
        }
    }

    @Override
    public void breakBlock(World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity != null) {
            if (tileEntity instanceof TileEntityReceiver) {
                ((TileEntityReceiver) tileEntity).onDestroy();
            }
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EnumFacing facing) {
        return false;
    }

}
