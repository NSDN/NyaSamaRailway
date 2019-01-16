package club.nsdn.nyasamatelecom.api.device;

import club.nsdn.nyasamatelecom.api.tileentity.TileEntityReceiver;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.thewdj.telecom.IPassive;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

/**
 * Created by drzzm32 on 2018.12.13.
 */
public class SignalBoxGetter extends DeviceBase {

    public static class TileEntitySignalBoxGetter extends TileEntityReceiver {

        public boolean isEnabled;
        boolean prevIsEnabled;

        public TileEntitySignalBoxGetter() {
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
            if (tileEntity instanceof TileEntitySignalBoxGetter) {
                TileEntitySignalBoxGetter signalBox = (TileEntitySignalBoxGetter) tileEntity;

                int meta = signalBox.META;
                int old = meta;
                boolean isEnabled = signalBox.isEnabled;

                if (signalBox.getSender() != null) {
                    isEnabled = signalBox.senderIsPowered();
                    signalBox.isEnabled = isEnabled;
                }

                if (isEnabled) meta |= 0x8;
                else meta &= 0x7;

                if (old != meta || signalBox.isEnabled != signalBox.prevIsEnabled) {
                    signalBox.prevIsEnabled = signalBox.isEnabled;
                    signalBox.META = meta;
                    signalBox.refresh();
                }
            }
        }

        @Override
        public void fromNBT(NBTTagCompound tagCompound) {
            isEnabled = tagCompound.getBoolean("isEnabled");
            super.fromNBT(tagCompound);
        }

        @Override
        public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
            tagCompound.setBoolean("isEnabled", isEnabled);
            return super.toNBT(tagCompound);
        }

    }

    public SignalBoxGetter(String modid, String name, String id) {
        super(name);
        setRegistryName(modid, id);
        setLightOpacity(0);
        setLightLevel(0.1F);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntitySignalBoxGetter();
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
