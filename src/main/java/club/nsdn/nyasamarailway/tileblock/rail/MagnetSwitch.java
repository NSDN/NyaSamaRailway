package club.nsdn.nyasamarailway.tileblock.rail;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.api.rail.IMonoRail;
import club.nsdn.nyasamarailway.api.rail.IRailSwitch;
import club.nsdn.nyasamarailway.tileblock.TileBlock;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityTriStateReceiver;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class MagnetSwitch extends TileBlock {

    public static class TileEntityMagnetSwitch extends TileEntityTriStateReceiver implements IRailSwitch {

        public EnumFacing direction;

        public TileEntityMagnetSwitch() {
            setInfo(4, 1, 1, 1);
        }

        @Nonnull
        @Override
        public AxisAlignedBB getRenderBoundingBox() {
            return super.getRenderBoundingBox().expand(4, 4, 4);
        }

        @Override
        public void fromNBT(NBTTagCompound tagCompound) {
            super.fromNBT(tagCompound);

            direction = EnumFacing.byName(
                    tagCompound.getString("direction")
            );
        }

        @Override
        public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
            if (direction == null) direction = EnumFacing.DOWN;
            tagCompound.setString("direction", direction.getName());

            return super.toNBT(tagCompound);
        }

        @Override
        public double getMaxRenderDistanceSquared() {
            return 32768.0D;
        }

        @Override
        public void update() {
            if (!world.isRemote)
                updateSignal(world, pos);
        }

        @Override
        public void updateSignal(World world, BlockPos pos) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity == null) return;
            if (tileEntity instanceof TileEntityMagnetSwitch) {
                TileEntityMagnetSwitch magnetSwitch = (TileEntityMagnetSwitch) tileEntity;

                int meta = 0;
                switch (magnetSwitch.state) {
                    case TileEntityMagnetSwitch.STATE_POS: //left
                        switch (magnetSwitch.direction) {
                            case SOUTH: meta = 9; break;
                            case WEST: meta = 6; break;
                            case NORTH: meta = 7; break;
                            case EAST: meta = 8; break;
                        }
                        break;
                    case TileEntityMagnetSwitch.STATE_NEG: //right
                        switch (magnetSwitch.direction) {
                            case SOUTH:  meta = 8; break;
                            case WEST: meta = 9; break;
                            case NORTH: meta = 6; break;
                            case EAST: meta = 7; break;
                        }
                        break;
                    case TileEntityMagnetSwitch.STATE_ZERO:
                        switch (magnetSwitch.direction) {
                            case SOUTH: meta = 0; break;
                            case WEST: meta = 1; break;
                            case NORTH: meta = 0; break;
                            case EAST: meta = 1; break;
                        }
                        break;
                }
                magnetSwitch.state = TileEntityMagnetSwitch.STATE_ZERO;

                tileEntity = world.getTileEntity(pos.up());
                if (tileEntity instanceof IMonoRail) {
                    if (((IMonoRail) tileEntity).getMeta() != meta) {
                        Block block = world.getBlockState(pos.up()).getBlock();
                        world.setBlockState(pos.up(), block.getStateFromMeta(meta), 3);
                        world.notifyNeighborsOfStateChange(pos.up(), block, false);
                        world.markBlockRangeForRenderUpdate(pos.up(), pos.up());
                    }
                }
            }
        }

    }

    public MagnetSwitch() {
        super("MagnetSwitch");
        setRegistryName(NyaSamaRailway.MODID, "rail_magnet_switch");
        setLightOpacity(0);
        setLightLevel(0);
    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityMagnetSwitch();
    }

    @Override
    public boolean isSideSolid(IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, EnumFacing facing) {
        return facing == EnumFacing.DOWN || facing == EnumFacing.UP;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack itemStack) {
        int val = MathHelper.floor((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        setDeviceMeta(world, pos, val);
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityMagnetSwitch)
            ((TileEntityMagnetSwitch) tileEntity).direction = getDirFromMeta(val).getOpposite();
    }

}
