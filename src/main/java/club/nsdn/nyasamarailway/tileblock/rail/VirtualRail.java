package club.nsdn.nyasamarailway.tileblock.rail;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.api.rail.IBaseRail;
import club.nsdn.nyasamarailway.api.rail.IVirtualRail;
import club.nsdn.nyasamarailway.tileblock.TileBlock;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityReceiver;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityTriStateReceiver;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

/**
 * Created by drzzm32 on 2020.2.15.
 */
public class VirtualRail extends TileBlock implements IVirtualRail {

    public static AxisAlignedBB AABB = new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);

    public static class TileEntityVirtualRail extends TileEntityTriStateReceiver {

        public float direction = 0;
        public float actualDir = 0;

        @Nonnull
        @Override
        public AxisAlignedBB getRenderBoundingBox() {
            return super.getRenderBoundingBox().expand(4, 4, 4);
        }

        @Override
        public void fromNBT(NBTTagCompound tagCompound) {
            super.fromNBT(tagCompound);
            direction = tagCompound.getFloat("direction");
            actualDir = tagCompound.getFloat("actualDir");
        }

        @Override
        public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
            tagCompound.setFloat("direction", direction);
            tagCompound.setFloat("actualDir", actualDir);
            return super.toNBT(tagCompound);
        }

    }

    @Override
    public float getTargetDirection(World world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityVirtualRail) {
            TileEntityVirtualRail rail = (TileEntityVirtualRail) tileEntity;

            return rail.actualDir;
        }
        return 0;
    }

    public VirtualRail() {
        super("VirtualRail");
        setRegistryName(NyaSamaRailway.MODID, "virtual_rail");
        setLightOpacity(0);
        setLightLevel(0);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int i) {
        return createNewTileEntity();
    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityVirtualRail();
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return world.getBlockState(pos).getMaterial().isReplaceable();
    }

    @Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return Block.NULL_AABB;
    }

    @Override
    @Nullable
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (world.getTileEntity(pos.down()) instanceof IBaseRail)
            return AABB.expand(0, -0.25, 0);
        return AABB;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack itemStack) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityVirtualRail) {
            TileEntityVirtualRail rail = (TileEntityVirtualRail) tileEntity;

            int val = MathHelper.floor((double)((player.rotationYaw + 360.0F) * 8.0F / 360.0F) + 0.5D) & 7;
            rail.direction = val * 45;
            rail.actualDir = rail.direction;

            rail.refresh();
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
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof TileEntityVirtualRail) {
                TileEntityVirtualRail rail = (TileEntityVirtualRail) tileEntity;
                int old = rail.prevState;

                switch (rail.state) {
                    case TileEntityVirtualRail.STATE_POS: //left
                        rail.actualDir = rail.direction + 45;
                        break;
                    case TileEntityVirtualRail.STATE_NEG: //right
                        rail.actualDir = rail.direction - 45;
                        break;
                    case TileEntityVirtualRail.STATE_ZERO:
                        rail.actualDir = rail.direction;
                        break;
                }
                rail.prevState = rail.state;
                rail.state = RailTriSwitch.TileEntityRailTriSwitch.STATE_ZERO;

                if (old != rail.prevState) {
                    rail.refresh();
                }
                world.scheduleUpdate(pos, this, 1);
            }
        }
    }


}
