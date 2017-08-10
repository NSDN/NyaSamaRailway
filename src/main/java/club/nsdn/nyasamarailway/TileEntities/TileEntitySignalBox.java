package club.nsdn.nyasamarailway.TileEntities;

import club.nsdn.nyasamarailway.ExtMod.Railcraft;
import club.nsdn.nyasamarailway.Items.Item74HC04;
import club.nsdn.nyasamarailway.TileEntities.Rail.RailBase;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

/**
 * Created by drzzm32 on 2017.8.9.
 */
public class TileEntitySignalBox extends TileEntityBase {

    public static class SignalBox extends TileEntityRailActuator {

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

        public boolean setSwitch(boolean state) {
            TileEntity railTarget = getTarget();

            if (railTarget == null) return false;
            if (Railcraft.getInstance() == null) return false;
            if (!Railcraft.getInstance().verifySwitch(railTarget)) return false;

            Railcraft.getInstance().setSwitch(railTarget, state);
            return true;
        }

        public boolean setTargetLight(boolean state) {
            TileEntity railTarget = getTarget();
            if (railTarget == null) return false;

            if (railTarget instanceof TileEntitySignalLight.SignalLight) {
                ((TileEntitySignalLight.SignalLight) railTarget).isPowered = state;
                return true;
            }
            return false;
        }

    }

    public TileEntitySignalBox() {
        super("SignalBox");
        setIconLocation("signal_box");
        setLightOpacity(0);
        setLightLevel(0.1F);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new SignalBox();
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
        SignalBox signalBox = null;
        if (world.getTileEntity(x, y, z) == null) return false;
        if (world.getTileEntity(x, y, z) instanceof SignalBox) {
            signalBox = (SignalBox) world.getTileEntity(x, y, z);

            if (player.isSneaking() && !world.isRemote) {
                signalBox.prevInverterEnabled = signalBox.inverterEnabled;
                if (signalBox.inverterEnabled) {
                    signalBox.inverterEnabled = false;
                    player.addChatComponentMessage(new ChatComponentTranslation("info.signal.box.inverter.off"));
                } else {
                    signalBox.inverterEnabled = true;
                    player.addChatComponentMessage(new ChatComponentTranslation("info.signal.box.inverter.on"));
                }
                return true;
            }

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
        if (world.getTileEntity(x, y, z) instanceof SignalBox) {
            SignalBox signalBox = (SignalBox) world.getTileEntity(x, y, z);

            int meta = world.getBlockMetadata(x, y, z);
            int old = meta;
            boolean isEnabled;

            if (signalBox.getSenderRail() == null) {
                isEnabled = (meta & 0x8) != 0;
            } else {
                isEnabled = signalBox.senderRailIsPowered();

                if (isEnabled) meta |= 0x8;
                else meta &= 0x7;
            }

            if (signalBox.inverterEnabled) isEnabled = !isEnabled;

            if (!signalBox.setSwitch(isEnabled)) {
                if (!signalBox.setTargetLight(isEnabled)) {
                    if (signalBox.getTarget() != null) {
                        signalBox.setTargetMetadata(
                                signalBox.getTarget().getBlockMetadata() & 0x7 | (isEnabled ? 0x8 : 0x0)
                        );
                    }
                }
            }

            if (old != meta || signalBox.prevInverterEnabled != signalBox.inverterEnabled) {
                signalBox.prevInverterEnabled = signalBox.inverterEnabled;
                world.setBlockMetadataWithNotify(x, y, z, meta, 3);
                world.markBlockForUpdate(x, y, z);
            }

            world.scheduleBlockUpdate(x, y, z, this, 1);
        }
    }

}
