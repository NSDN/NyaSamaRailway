package club.nsdn.nyasamarailway.tileblock.signal;

import club.nsdn.nyasamarailway.tileblock.TileBlock;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityPassiveReceiver;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

/**
 * Created by drzzm32 on 2018.1.14.
 */
public abstract class AbsSignalLight extends TileBlock {

    public static final int LIGHT_N = 0;
    public static final int LIGHT_R = 1;
    public static final int LIGHT_Y = 2;
    public static final int LIGHT_G = 3;
    public static final int LIGHT_W = 4;
    public static final int LIGHT_B = 5;
    public static final int LIGHT_P = 6;

    public static final int LIGHT_POWERED = 2;
    public static final int LIGHT_NORMAL = 3;

    @Override
    public abstract TileEntity createNewTileEntity(World world, int meta);

    public AbsSignalLight(String name) {
        super(name);
        setLightOpacity(0);
        setLightLevel(0.0F);
    }

    @Override
    public Material getMaterial() {
        return Material.rock;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return side == ForgeDirection.UP;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack) {
        int meta = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        world.setBlockMetadataWithNotify(x, y, z, meta, 2);
    }

    @Override
    protected abstract void setBoundsByMeta(int meta);

    @Override
    protected void setBoundsByXYZ(int meta, float x1, float y1, float z1, float x2, float y2, float z2) {
        switch (meta & 0x3) {
            case 0:
                setBlockBounds(x1, y1, z1, x2, y2, z2);
                break;
            case 1:
                setBlockBounds(1.0F - z2, y1, x1, 1.0F - z1, y2, x2);
                break;
            case 2:
                setBlockBounds(1.0F - x2, y1, 1.0F - z2, 1.0F - x1, y2, 1.0F - z1);
                break;
            case 3:
                setBlockBounds(z1, y1, 1.0F - x2, z2, y2, 1.0F - x1);
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
            world.scheduleBlockUpdate(x, y, z, this, 1);
        }
    }

    public boolean isLightOn(World world, int x ,int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        return ((meta >> 2) & 0x3) != 0;
    }

    public ForgeDirection getLightDir(World world, int x , int y, int z) {
        switch (world.getBlockMetadata(x, y, z) & 0x3) {
            case 0: return ForgeDirection.NORTH;
            case 1: return ForgeDirection.EAST;
            case 2: return ForgeDirection.SOUTH;
            case 3: return ForgeDirection.WEST;
        }
        return ForgeDirection.UNKNOWN;
    }

    public abstract void updateLight(World world, int x , int y, int z);

    public int setLightState(boolean isEnable, int meta, String type) {
        String lightType = type.toLowerCase();
        if (lightType.equals("red&off"))
            return isEnable ? meta | (LIGHT_R << 2) : meta;
        else if (lightType.equals("yellow&off"))
            return isEnable ? meta | (LIGHT_Y << 2) : meta;
        else if (lightType.equals("green&off"))
            return isEnable ? meta | (LIGHT_G << 2) : meta;
        else if (lightType.equals("white&off"))
            return isEnable ? meta | (LIGHT_POWERED << 2) : meta;
        else if (lightType.equals("blue&off"))
            return isEnable ? meta | (LIGHT_POWERED << 2) : meta;
        else if (lightType.equals("purple&off"))
            return isEnable ? meta | (LIGHT_POWERED << 2) : meta;
        else if (lightType.equals("red&yellow"))
            return isEnable ? meta | (LIGHT_R << 2) : meta | (LIGHT_Y << 2);
        else if (lightType.equals("red&green"))
            return isEnable ? meta | (LIGHT_R << 2) : meta | (LIGHT_G << 2);
        else if (lightType.equals("yellow&green"))
            return isEnable ? meta | (LIGHT_Y << 2) : meta | (LIGHT_G << 2);
        else if (lightType.equals("white&blue"))
            return isEnable ? meta | (LIGHT_POWERED << 2) : meta | (LIGHT_NORMAL << 2);
        else if (lightType.equals("yellow&purple"))
            return isEnable ? meta | (LIGHT_POWERED << 2) : meta | (LIGHT_NORMAL << 2);
        else
            return meta;
    }

    public boolean thisBlockIsPowered(World world, int x, int y, int z) {
        return (
            world.isBlockIndirectlyGettingPowered(x, y, z) ||
            world.isBlockIndirectlyGettingPowered(x, y - 1, z)
        );
    }

}
