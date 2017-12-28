package club.nsdn.nyasamatelecom.api.device;

import club.nsdn.nyasamatelecom.api.tileentity.TileEntityMultiSender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
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
public class SignalBoxSender extends DeviceBase {

    public static class TileEntitySignalBoxSender extends TileEntityMultiSender {

        public boolean isEnabled;
        public boolean prevIsEnabled;

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

    public SignalBoxSender(String modid, String name, String icon) {
        super(name);
        setIconLocation(modid, icon);
        setLightOpacity(0);
        setLightLevel(0.1F);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntitySignalBoxSender();
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
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        TileEntitySignalBoxSender sender;
        if (world.getTileEntity(x, y, z) == null) return false;
        if (world.getTileEntity(x, y, z) instanceof TileEntitySignalBoxSender) {
            sender = (TileEntitySignalBoxSender) world.getTileEntity(x, y, z);
            if (!world.isRemote) {
                sender.prevIsEnabled = sender.isEnabled;
                if (sender.isEnabled) {
                    sender.isEnabled = false;
                } else {
                    sender.isEnabled = true;
                }
                world.playSoundEffect(
                        (double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D,
                        "random.click", 0.3F, 0.5F
                );
            }
            return true;
        }

        return false;
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        world.scheduleBlockUpdate(x, y, z, this, 1);
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
        return false;
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
        if (world.getTileEntity(x, y, z) instanceof TileEntitySignalBoxSender) {
            TileEntitySignalBoxSender sender = (TileEntitySignalBoxSender) world.getTileEntity(x, y, z);

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

}
