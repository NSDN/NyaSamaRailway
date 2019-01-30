package club.nsdn.nyasamatelecom.api.device;

import club.nsdn.nyasamatelecom.api.tileentity.TileEntityMultiSender;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Created by drzzm32 on 2018.12.13.
 */
public class SignalBoxSender extends DeviceBase {

    public static class TileEntitySignalBoxSender extends TileEntityMultiSender {

        public boolean isEnabled;
        public boolean prevIsEnabled;

        public TileEntitySignalBoxSender() {
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
            if (tileEntity instanceof TileEntitySignalBoxSender) {
                TileEntitySignalBoxSender signalBox = (TileEntitySignalBoxSender) tileEntity;

                int meta = signalBox.META;
                int old = meta;
                boolean isEnabled = signalBox.isEnabled;

                if (signalBox.getTransceiver() != null) {
                    isEnabled = isEnabled && signalBox.transceiverIsPowered();
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

    public SignalBoxSender(String modid, String name, String id) {
        super(name);
        setRegistryName(modid, id);
        setLightOpacity(0);
        setLightLevel(0.1F);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntitySignalBoxSender();
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

    protected void playClickSound(World world, BlockPos pos, boolean state) {
        if (state)
            world.playSound(
                    null, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON,
                    SoundCategory.BLOCKS, 0.3F, 0.6F
            );
        else
            world.playSound(
                    null, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF,
                    SoundCategory.BLOCKS, 0.3F, 0.5F
            );
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)  {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity == null) return false;
        if (tileEntity instanceof TileEntitySignalBoxSender) {
            TileEntitySignalBoxSender sender = (TileEntitySignalBoxSender) tileEntity;
            if (!world.isRemote) {
                sender.prevIsEnabled = sender.isEnabled;
                sender.isEnabled = !sender.isEnabled;
                playClickSound(world, pos, sender.isEnabled);
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EnumFacing facing) {
        return false;
    }

}
