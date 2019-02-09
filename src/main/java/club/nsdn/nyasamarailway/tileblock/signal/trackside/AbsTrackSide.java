package club.nsdn.nyasamarailway.tileblock.signal.trackside;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.api.signal.ITrackSide;
import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamatelecom.api.device.SignalBox;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Created by drzzm32 on 2019.1.5.
 */
public abstract class AbsTrackSide extends SignalBox {

    public AbsTrackSide(String name, String id) {
        super(NyaSamaRailway.MODID, name, id);
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int i) {
        return createNewTileEntity();
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack itemStack) {
        int val = MathHelper.floor((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        if (player.rotationPitch > 0.0F) {
            setDeviceMeta(world, pos, val);
        } else {
            setDeviceMeta(world, pos, val + 4);
        }

        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityBase)
            ITrackSide.getDirByMeta((TileEntityBase) tileEntity);
    }

    @Override
    public abstract TileEntity createNewTileEntity();

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)  {
        return onConfigure(world, pos, player);
    }

    public abstract boolean onConfigure(World world, BlockPos pos, EntityPlayer player);

}
