package club.nsdn.nyasamarailway.tileblock.signal.light;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.tileblock.TileBlock;
import club.nsdn.nyasamarailway.api.signal.TileEntitySignalLight;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityReceiver;
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
import java.util.Random;

/**
 * Created by drzzm32 on 2019.2.10
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

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int i) {
        return createNewTileEntity();
    }

    @Override
    public abstract TileEntity createNewTileEntity();

    public AbsSignalLight(String name, String id) {
        super(name);
        setRegistryName(NyaSamaRailway.MODID, id);
        setLightOpacity(0);
        setLightLevel(0.0F);
    }

    @Override
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing facing) {
        return facing == EnumFacing.DOWN || facing == EnumFacing.UP;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack itemStack) {
        int val = MathHelper.floor((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        setDeviceMeta(world, pos, val);
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
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
        world.scheduleUpdate(pos, this, 1);
    }

    @Override
    public int tickRate(World world) {
        return 20;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
        if (!world.isRemote) {
            updateLight(world, pos);
            world.scheduleUpdate(pos, this, 1);
        }
    }

    public int getMetaFromTile(World world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntitySignalLight)
            return ((TileEntitySignalLight) tileEntity).META;
        return -1;
    }

    public boolean isLightOn(World world, BlockPos pos) {
        int meta = getMetaFromTile(world, pos);
        return ((meta >> 2) & 0x3) != 0;
    }

    public EnumFacing getLightDir(World world, BlockPos pos) {
        int meta = getMetaFromTile(world, pos);
        return getDirFromMeta(meta);
    }

    public abstract void updateLight(World world, BlockPos pos);

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

}
