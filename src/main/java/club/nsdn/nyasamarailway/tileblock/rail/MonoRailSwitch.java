package club.nsdn.nyasamarailway.tileblock.rail;

import club.nsdn.nyasamarailway.api.rail.*;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityTriStateReceiver;
import club.nsdn.nyasamatelecom.api.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class MonoRailSwitch extends AbsRail {

    public static class TileEntityMonoRailSwitch extends TileEntityTriStateReceiver implements IBaseRail, IMonoSwitch {

        public EnumFacing direction;

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
        public void update() {
            if (!world.isRemote)
                updateSignal(world, pos);
        }

        @Override
        public void updateSignal(World world, BlockPos pos) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity == null) return;
            if (tileEntity instanceof TileEntityMonoRailSwitch) {
                TileEntityMonoRailSwitch monoSwitch = (TileEntityMonoRailSwitch) tileEntity;
                EnumFacing dir = monoSwitch.direction;
                int old = getBlockMetadata();

                int meta = 0;
                switch (monoSwitch.state) {
                    case TileEntityMonoRailSwitch.STATE_POS: //left
                        switch (monoSwitch.direction) {
                            case SOUTH: meta = 9; break;
                            case WEST: meta = 6; break;
                            case NORTH: meta = 7; break;
                            case EAST: meta = 8; break;
                        }
                        break;
                    case TileEntityMonoRailSwitch.STATE_NEG: //right
                        switch (monoSwitch.direction) {
                            case SOUTH:  meta = 8; break;
                            case WEST: meta = 9; break;
                            case NORTH: meta = 6; break;
                            case EAST: meta = 7; break;
                        }
                        break;
                    case TileEntityMonoRailSwitch.STATE_ZERO:
                        switch (monoSwitch.direction) {
                            case SOUTH: meta = 0; break;
                            case WEST: meta = 1; break;
                            case NORTH: meta = 0; break;
                            case EAST: meta = 1; break;
                        }
                        break;
                }
                monoSwitch.state = TileEntityMonoRailSwitch.STATE_ZERO;

                tileEntity = world.getTileEntity(pos.up());
                if (tileEntity instanceof IMonoRail) {
                    if (((IMonoRail) tileEntity).getMeta() != meta) {
                        Block block = world.getBlockState(pos.up()).getBlock();
                        Util.setStateWithTile(world, pos.up(), block.getStateFromMeta(meta));
                        world.notifyNeighborsOfStateChange(pos.up(), block, false);
                        world.markBlockRangeForRenderUpdate(pos.up(), pos.up());
                    }
                }

                if (old != meta) {
                    Util.setStateWithTile(world, pos, getBlockType().getStateFromMeta(meta));
                    world.notifyNeighborsOfStateChange(pos, getBlockType(), false);
                }
            }
        }

    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityMonoRailSwitch();
    }

    public MonoRailSwitch(String name, String id) {
        super(name, id);
    }

    public MonoRailSwitch() {
        super("MonoRailSwitch", "rail_mono_switch");
    }

    @Override
    public float getRailMaxSpeed(World world, EntityMinecart cart, BlockPos pos) {
        return 5.0F;
    }

    @Override
    public AxisAlignedBB getAscendingAABB() {
        return new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
    }

    @Override
    public AxisAlignedBB getFlatAABB() {
        return new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return world.getBlockState(pos).getMaterial().isReplaceable();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {

    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos neighbor) {

    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing facing) {
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack itemStack) {
        int val = MathHelper.floor((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityMonoRailSwitch) {
            TileEntityMonoRailSwitch monoSwitch = (TileEntityMonoRailSwitch) tileEntity;

            monoSwitch.direction = getDirFromMeta(val).getOpposite();
            if ((val + 2) % 2 == 0) val = 0;
            else val = 1;

            Util.setStateWithTile(world, pos, getStateFromMeta(val));
        }
    }

    @Override
    public AbsRail.Rail getRail(World world, BlockPos pos, IBlockState state) {
        return new Rail(world, pos, state);
    }

    public class Rail extends AbsRail.Rail {

        @Override
        public Class<? extends AbsRailBase> getOuterClass() {
            return MonoRailSwitch.class;
        }

        @Override
        public AbsRailBase.Rail getRail(World world, BlockPos pos, IBlockState state) {
            return new Rail(world, pos, state);
        }

        @Override
        public void setState(World world, BlockPos pos, IBlockState state) {

        }

        public Rail(World world, BlockPos pos, IBlockState state) {
            super(world, pos, state);
        }

    }

}
