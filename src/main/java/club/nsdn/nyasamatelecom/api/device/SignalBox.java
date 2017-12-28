package club.nsdn.nyasamatelecom.api.device;

import club.nsdn.nyasamatelecom.api.tileentity.TileEntityActuator;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityReceiver;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

/**
 * Created by drzzm32 on 2017.12.28.
 */
public class SignalBox extends DeviceBase {

    public static class TileEntitySignalBox extends TileEntityActuator {

        public boolean inverterEnabled;
        public boolean prevInverterEnabled;

        @Override
        public void fromNBT(NBTTagCompound tagCompound) {
            inverterEnabled = tagCompound.getBoolean("inverterEnabled");
            super.fromNBT(tagCompound);
        }

        @Override
        public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
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
            TileEntity railTarget = getTarget();
            if (railTarget == null) return false;

            if (railTarget instanceof SignalBoxSender.TileEntitySignalBoxSender) {
                ((SignalBoxSender.TileEntitySignalBoxSender) railTarget).isEnabled = state;
                return true;
            }
            return false;
        }

    }

    public SignalBox(String modid, String name, String icon) {
        super(name);
        setIconLocation(modid, icon);
        setLightOpacity(0);
        setLightLevel(0.1F);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntitySignalBox();
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side)
    {
        return side == ForgeDirection.DOWN;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack) {
        int meta = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        if (player.rotationPitch > 22.5F) {
            world.setBlockMetadataWithNotify(x, y, z, meta, 2);
        } else {
            world.setBlockMetadataWithNotify(x, y, z, meta + 4, 2);
        }

    }

    @Override
    protected void setBoundsByMeta(int meta) {
        float x1 = 0.125F, y1 = 0.0F, z1 = 0.125F, x2 = 0.875F, y2 = 0.5F, z2 = 0.875F;
        switch (meta & 7) {
            case 2:
                setBlockBounds(x1, y1, z1, x2, y2, z2);
                break;
            case 3:
                setBlockBounds(1.0F - z2, y1, x1, 1.0F - z1, y2, x2);
                break;
            case 0:
                setBlockBounds(1.0F - x2, y1, 1.0F - z2, 1.0F - x1, y2, 1.0F - z1);
                break;
            case 1:
                setBlockBounds(z1, y1, 1.0F - x2, z2, y2, 1.0F - x1);
                break;
            case 6:
                setBlockBounds(x1, z1, y1, x2, z2, y2);
                break;
            case 7:
                setBlockBounds(1.0F - y2, z1, x1, 1.0F - y1, z2, x2);
                break;
            case 4:
                setBlockBounds(1.0F - x2, z1, 1.0F - y2, 1.0F - x1, z2, 1.0F - y1);
                break;
            case 5:
                setBlockBounds(y1, z1, 1.0F - x2, y2, z2, 1.0F - x1);
                break;
        }
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        world.scheduleBlockUpdate(x, y, z, this, 1);
    }

    @Override
    public void onBlockPreDestroy(World world, int x, int y, int z, int meta) {
        super.onBlockPreDestroy(world, x, y, z, meta);
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity != null) {
            if (tileEntity instanceof TileEntityReceiver) {
                ((TileEntityReceiver) tileEntity).onDestroy();
            }
        }
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
        return true;
    }

    @Override
    public int tickRate(World world) {
        return 10;
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (!world.isRemote) {
            updateSignal(world, x, y, z);
        }
    }

    public void updateSignal(World world, int x , int y, int z) {
        if (world.getTileEntity(x, y, z) == null) return;
        if (world.getTileEntity(x, y, z) instanceof TileEntitySignalBox) {
            TileEntitySignalBox signalBox = (TileEntitySignalBox) world.getTileEntity(x, y, z);

            int meta = world.getBlockMetadata(x, y, z);
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

            if (!signalBox.tryControlFirst(isEnabled)) {
                if (!signalBox.tryControlSecond(isEnabled)) {
                    if (!signalBox.setTargetSender(isEnabled)) {
                        if (signalBox.getTarget() != null) {
                            signalBox.controlTarget(isEnabled);
                        }
                    }
                }
            }

            if (old != meta || signalBox.prevInverterEnabled != signalBox.inverterEnabled) {
                signalBox.prevInverterEnabled = signalBox.inverterEnabled;
                world.markBlockForUpdate(x, y, z);
                world.setBlockMetadataWithNotify(x, y, z, meta, 3);
            }

            world.scheduleBlockUpdate(x, y, z, this, 1);
        }
    }

}
