package club.nsdn.nyasamarailway.tileblock.signal.block;

import club.nsdn.nyasamarailway.tileblock.TileBlock;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityPassiveReceiver;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

/**
 * Created by drzzm32 on 2017.7.4.
 */
public class BlockSignalLight extends TileBlock {

    private static final int LIGHT_OFF = 0;
    private static final int LIGHT_R = 1;
    private static final int LIGHT_Y = 2;
    private static final int LIGHT_G = 3;

    public static class SignalLight extends club.nsdn.nyasamarailway.tileblock.signal.TileEntitySignalLight {
    }

    public BlockSignalLight() {
        super("SignalLight");
        setIconLocation("signal_light");
        setLightOpacity(0);
        setLightLevel(0.75F);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new SignalLight();
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side)
    {
        return side == ForgeDirection.UP;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack) {
        int meta = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        world.setBlockMetadataWithNotify(x, y, z, meta, 2);
    }

    @Override
    protected void setBoundsByMeta(int meta) {
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
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
            if (tileEntity instanceof TileEntityPassiveReceiver) {
                ((TileEntityPassiveReceiver) tileEntity).onDestroy();
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
            updateLight(world, x, y, z);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getMixedBrightnessForBlock(IBlockAccess world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        int light = block.getLightValue(world, x, y, z);

        if (((meta >> 2) & 0x3) == 0) light = 0;

        return world.getLightBrightnessForSkyBlocks(x, y, z, light);
    }

    public void updateLight(World world, int x ,int y, int z) {
        if (world.getTileEntity(x, y, z) == null) return;
        if (world.getTileEntity(x, y, z) instanceof SignalLight) {
            SignalLight signalLight = (SignalLight) world.getTileEntity(x, y, z);
            boolean isEnable;
            if (signalLight.getSender() == null) {
                isEnable = signalLight.isPowered ^ thisBlockIsPowered(world, x, y, z);
            } else {
                isEnable = signalLight.senderIsPowered() ^ thisBlockIsPowered(world, x, y, z);
            }
            int meta = world.getBlockMetadata(x, y, z);
            int old = meta;

            meta = meta & 0x3;
            if (signalLight.isBlinking) {
                if (signalLight.delay > 10) {
                    if (signalLight.delay < 20) {
                        signalLight.delay += 1;
                    } else {
                        signalLight.delay = 0;
                    }
                    meta = setLightState(isEnable, meta, signalLight.lightType);
                } else {
                    signalLight.delay += 1;
                }
            } else {
                meta = setLightState(isEnable, meta, signalLight.lightType);
            }

            if (old != meta || !signalLight.prevLightType.equals(signalLight.lightType)) {
                signalLight.prevLightType = signalLight.lightType;
                world.setBlockMetadataWithNotify(x, y, z, meta, 3);
                world.markBlockForUpdate(x, y, z);
            }

            world.scheduleBlockUpdate(x, y, z, this, 1);
        }
    }

    public int setLightState(boolean isEnable, int meta, String type) {
        String lightType = type.toLowerCase();
        if (lightType.equals("red&off"))
            return isEnable ? meta | (LIGHT_R << 2) : meta;
        else if (lightType.equals("yellow&off"))
            return isEnable ? meta | (LIGHT_Y << 2) : meta;
        else if (lightType.equals("green&off"))
            return isEnable ? meta | (LIGHT_G << 2) : meta;
        else if (lightType.equals("red&yellow"))
            return isEnable ? meta | (LIGHT_R << 2) : meta | (LIGHT_Y << 2);
        else if (lightType.equals("red&green"))
            return isEnable ? meta | (LIGHT_R << 2) : meta | (LIGHT_G << 2);
        else if (lightType.equals("yellow&green"))
            return isEnable ? meta | (LIGHT_Y << 2) : meta | (LIGHT_G << 2);
        else if (lightType.equals("white&blue"))
            return isEnable ? meta | (2 << 2) : meta | (3 << 2);
        else if (lightType.equals("yellow&purple"))
            return isEnable ? meta | (2 << 2) : meta | (3 << 2);
        else
            return meta;
    }

    public boolean nearbyBlockIsPowered(World world, int x, int y, int z) {
        return (
            world.isBlockIndirectlyGettingPowered(x, y, z) ||
            world.isBlockIndirectlyGettingPowered(x + 1, y, z) ||
            world.isBlockIndirectlyGettingPowered(x - 1, y, z) ||
            world.isBlockIndirectlyGettingPowered(x, y, z + 1) ||
            world.isBlockIndirectlyGettingPowered(x, y, z - 1) ||
            world.isBlockIndirectlyGettingPowered(x, y - 1, z)
        );
    }

    public boolean thisBlockIsPowered(World world, int x, int y, int z) {
        return (
            world.isBlockIndirectlyGettingPowered(x, y, z) ||
            world.isBlockIndirectlyGettingPowered(x, y - 1, z)
        );
    }

}
