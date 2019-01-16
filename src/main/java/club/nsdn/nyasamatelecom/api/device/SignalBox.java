package club.nsdn.nyasamatelecom.api.device;

import club.nsdn.nyasamatelecom.api.tileentity.TileEntityActuator;
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
public class SignalBox extends DeviceBase {

    public static class TileEntitySignalBox extends TileEntityActuator {

        public boolean isEnabled;
        public boolean inverterEnabled;
        public boolean prevInverterEnabled;

        public TileEntitySignalBox() {
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
            if (tileEntity instanceof TileEntitySignalBox) {
                TileEntitySignalBox signalBox = (TileEntitySignalBox) tileEntity;

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

                signalBox.isEnabled = isEnabled;

                if (signalBox.inverterEnabled) isEnabled = !isEnabled;

                if (!signalBox.tryControlFirst(isEnabled)) {
                    if (!signalBox.tryControlSecond(isEnabled)) {
                        if (!signalBox.setTargetSender(isEnabled)) {
                            if (!signalBox.setTargetGetter(isEnabled)) {
                                if (signalBox.getTarget() != null) {
                                    tileEntity = signalBox.getTarget();
                                    if (tileEntity instanceof TileEntityReceiver) {
                                        if (tileEntity instanceof IPassive) {
                                            signalBox.controlTarget(isEnabled);
                                        }
                                    } else {
                                        signalBox.controlTarget(isEnabled);
                                    }
                                }
                            }
                        }
                    }
                }

                if (old != meta || signalBox.prevInverterEnabled != signalBox.inverterEnabled) {
                    signalBox.prevInverterEnabled = signalBox.inverterEnabled;
                    signalBox.META = meta;
                    signalBox.refresh();
                }
            }
        }

        @Override
        public void fromNBT(NBTTagCompound tagCompound) {
            super.fromNBT(tagCompound);
            isEnabled = tagCompound.getBoolean("isEnabled");
            inverterEnabled = tagCompound.getBoolean("inverterEnabled");
        }

        @Override
        public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
            tagCompound.setBoolean("isEnabled", isEnabled);
            tagCompound.setBoolean("inverterEnabled", inverterEnabled);
            return super.toNBT(tagCompound);
        }

        public boolean tryControlFirst(boolean state) {
            return false;
        }

        public boolean tryControlSecond(boolean state) {
            return false;
        }

        public boolean setTargetSender(boolean state) {
            TileEntity target = getTarget();
            if (target == null) return false;

            if (target instanceof SignalBoxSender.TileEntitySignalBoxSender) {
                ((SignalBoxSender.TileEntitySignalBoxSender) target).isEnabled = state;
                return true;
            }
            return false;
        }

        public boolean setTargetGetter(boolean state) {
            TileEntity target = getTarget();
            if (target == null) return false;

            if (target instanceof SignalBoxGetter.TileEntitySignalBoxGetter) {
                ((SignalBoxGetter.TileEntitySignalBoxGetter) target).isEnabled = state;
                return true;
            }
            return false;
        }

    }

    public SignalBox(String modid, String name, String id) {
        super(name);
        setRegistryName(modid, id);
        setLightOpacity(0);
        setLightLevel(0.1F);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntitySignalBox();
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
