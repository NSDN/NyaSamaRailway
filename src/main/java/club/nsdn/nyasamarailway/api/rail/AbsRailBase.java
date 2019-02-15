package club.nsdn.nyasamarailway.api.rail;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2019.2.10
 */
public abstract class AbsRailBase extends BlockRailBase implements ITileEntityProvider {

    public static class TileEntityAbsRailBase extends club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase {

        @Nonnull
        @Override
        public AxisAlignedBB getRenderBoundingBox() {
            return super.getRenderBoundingBox().expand(4, 4, 4);
        }

        @Override
        public double getMaxRenderDistanceSquared() {
            return 32768.0D;
        }

    }

    protected final boolean isPowered;

    protected AbsRailBase(String name, String id) {
        super(false);
        this.isPowered = false;
        setSoundType(SoundType.METAL);

        this.hasTileEntity = true;

        setUnlocalizedName(name);
        setRegistryName(NyaSamaRailway.MODID, id);
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    public EnumFacing getDirFromMeta(int meta) {
        switch (meta & 0x3) {
            case 0: return EnumFacing.NORTH;
            case 1: return EnumFacing.EAST;
            case 2: return EnumFacing.SOUTH;
            case 3: return EnumFacing.WEST;
        }
        return EnumFacing.UP;
    }

    @Override
    public Material getMaterial(IBlockState state) {
        return Material.IRON;
    }

    @Override
    public TileEntity createNewTileEntity(@Nullable World world, int meta) {
        return createNewTileEntity();
    }

    public TileEntity createNewTileEntity() {
        return new TileEntityAbsRailBase();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity tileEntity, ItemStack stack) {
        if (tileEntity instanceof IWorldNameable && ((IWorldNameable)tileEntity).hasCustomName()) {
            player.addStat(StatList.getBlockStats(this));
            player.addExhaustion(0.005F);
            if (world.isRemote) {
                return;
            }

            int lvt_7_1_ = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);
            Item lvt_8_1_ = this.getItemDropped(state, world.rand, lvt_7_1_);
            if (lvt_8_1_ == Items.AIR) {
                return;
            }

            ItemStack lvt_9_1_ = new ItemStack(lvt_8_1_, this.quantityDropped(world.rand));
            lvt_9_1_.setStackDisplayName(((IWorldNameable)tileEntity).getName());
            spawnAsEntity(world, pos, lvt_9_1_);
        } else {
            super.harvestBlock(world, player, pos, state, (TileEntity)null, stack);
        }

    }

    @Override
    public boolean eventReceived(IBlockState state, World world, BlockPos pos, int i, int j) {
        super.eventReceived(state, world, pos, i, j);
        TileEntity tileEntity = world.getTileEntity(pos);
        return tileEntity == null ? false : tileEntity.receiveClientEvent(i, j);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        super.breakBlock(world, pos, state);

        world.removeTileEntity(pos);
    }

    @Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return getBoundingBox(state, world, pos);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        EnumRailDirection railDirection = state.getBlock() instanceof AbsRailBase ? this.getRailDirection(world, pos, state, null) : null;
        return railDirection != null && railDirection.isAscending() ? getAscendingAABB() : getFlatAABB();
    }

    public abstract AxisAlignedBB getAscendingAABB();

    public abstract AxisAlignedBB getFlatAABB();

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return world.getBlockState(pos.down()).isSideSolid(world, pos.down(), EnumFacing.UP);
    }

    @Override
    protected IBlockState updateDir(World world, BlockPos pos, IBlockState state, boolean flag) {
        return world.isRemote ? state : getRail(world, pos, state).place(world.isBlockPowered(pos), flag).getBlockState();
    }

    public abstract Rail getRail(World world, BlockPos pos, IBlockState state);

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos neighbor) {
        if (!world.isRemote) {
            this.updateState(state, world, pos, block);
        }
    }

    @Override
    public float getRailMaxSpeed(World world, EntityMinecart cart, BlockPos pos) {
        return 0.4F;
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing facing) {
        IBlockState state = world.getBlockState(pos);
        UnmodifiableIterator iterator = state.getProperties().keySet().iterator();

        IProperty prop;
        do {
            if (!iterator.hasNext()) {
                return false;
            }

            prop = (IProperty)iterator.next();
        } while(!prop.getName().equals("shape"));

        world.setBlockState(pos, state.cycleProperty(prop));
        return true;
    }

    /****************************************************************************************************************************************/

    public class Rail extends BlockRailBase.Rail {

        public Class<? extends AbsRailBase> getOuterClass() {
            return AbsRailBase.class;
        }

        public Rail getRail(World world, BlockPos pos, IBlockState state) { return new Rail(world, pos, state); }

        public void setState(World world, BlockPos pos, IBlockState state) {
            world.setBlockState(pos, state, 3);
        }

        public boolean checkBlockIsMe(IBlockState state) {
            return state.getBlock().getClass() == getOuterClass();
        }

        public boolean checkBlockIsMe(World world, BlockPos pos) {
            return checkBlockIsMe(world.getBlockState(pos));
        }

        protected final World world;
        protected final BlockPos pos;
        protected final AbsRailBase block;
        protected IBlockState state;
        protected final boolean isPowered;
        protected final List<BlockPos> connectedRails = Lists.newArrayList();
        protected final boolean canMakeSlopes;

        public Rail(World world, BlockPos pos, IBlockState state) {
            super(world, pos, state);

            this.world = world;
            this.pos = pos;
            this.state = state;
            this.block = (AbsRailBase)state.getBlock();
            EnumRailDirection railDirection = this.block.getRailDirection(world, pos, state, (EntityMinecart)null);
            this.isPowered = !this.block.isFlexibleRail(world, pos);
            this.canMakeSlopes = this.block.canMakeSlopes(world, pos);
            this.updateConnectedRails(railDirection);
        }

        public List<BlockPos> getConnectedRails() {
            return this.connectedRails;
        }

        protected void updateConnectedRails(EnumRailDirection railDirection) {
            this.connectedRails.clear();
            switch(railDirection) {
                case NORTH_SOUTH:
                    this.connectedRails.add(this.pos.north());
                    this.connectedRails.add(this.pos.south());
                    break;
                case EAST_WEST:
                    this.connectedRails.add(this.pos.west());
                    this.connectedRails.add(this.pos.east());
                    break;
                case ASCENDING_EAST:
                    this.connectedRails.add(this.pos.west());
                    this.connectedRails.add(this.pos.east().up());
                    break;
                case ASCENDING_WEST:
                    this.connectedRails.add(this.pos.west().up());
                    this.connectedRails.add(this.pos.east());
                    break;
                case ASCENDING_NORTH:
                    this.connectedRails.add(this.pos.north().up());
                    this.connectedRails.add(this.pos.south());
                    break;
                case ASCENDING_SOUTH:
                    this.connectedRails.add(this.pos.north());
                    this.connectedRails.add(this.pos.south().up());
                    break;
                case SOUTH_EAST:
                    this.connectedRails.add(this.pos.east());
                    this.connectedRails.add(this.pos.south());
                    break;
                case SOUTH_WEST:
                    this.connectedRails.add(this.pos.west());
                    this.connectedRails.add(this.pos.south());
                    break;
                case NORTH_WEST:
                    this.connectedRails.add(this.pos.west());
                    this.connectedRails.add(this.pos.north());
                    break;
                case NORTH_EAST:
                    this.connectedRails.add(this.pos.east());
                    this.connectedRails.add(this.pos.north());
            }

        }

        protected void removeSoftConnections() {
            for(int i = 0; i < this.connectedRails.size(); ++i) {
                AbsRailBase.Rail blockrailbase$rail = this.findRailAt((BlockPos)this.connectedRails.get(i));
                if (blockrailbase$rail != null && blockrailbase$rail.isConnectedToRail(this)) {
                    this.connectedRails.set(i, blockrailbase$rail.pos);
                } else {
                    this.connectedRails.remove(i--);
                }
            }

        }

        // check neighbor rails
        private boolean hasRailAt(BlockPos pos) {
            return checkBlockIsMe(this.world, pos) || checkBlockIsMe(this.world, pos.up()) || checkBlockIsMe(this.world, pos.down());
        }

        @Nullable
        protected AbsRailBase.Rail findRailAt(BlockPos pos) {
            IBlockState state = this.world.getBlockState(pos);
            if (checkBlockIsMe(state)) {
                return getRail(this.world, pos, state);
            } else {
                BlockPos tmpPos = pos.up();
                state = this.world.getBlockState(tmpPos);
                if (checkBlockIsMe(state)) {
                    return getRail(this.world, tmpPos, state);
                } else {
                    tmpPos = pos.down();
                    state = this.world.getBlockState(tmpPos);
                    AbsRailBase.Rail rail;
                    if (checkBlockIsMe(state)) {
                        rail = getRail(this.world, tmpPos, state);
                    } else {
                        rail = null;
                    }

                    return rail;
                }
            }
        }

        protected boolean isConnectedToRail(AbsRailBase.Rail rail) {
            return this.isConnectedTo(rail.pos);
        }

        protected boolean isConnectedTo(BlockPos pos) {
            for(int i = 0; i < this.connectedRails.size(); ++i) {
                BlockPos blockpos = (BlockPos)this.connectedRails.get(i);
                if (blockpos.getX() == pos.getX() && blockpos.getZ() == pos.getZ()) {
                    return true;
                }
            }

            return false;
        }

        protected int countAdjacentRails() {
            int i = 0;
            Iterator var2 = Plane.HORIZONTAL.iterator();

            while(var2.hasNext()) {
                EnumFacing enumfacing = (EnumFacing)var2.next();
                if (this.hasRailAt(this.pos.offset(enumfacing))) {
                    ++i;
                }
            }

            return i;
        }

        protected boolean canConnectTo(AbsRailBase.Rail rail) {
            return this.isConnectedToRail(rail) || this.connectedRails.size() != 2;
        }

        protected void connectTo(AbsRailBase.Rail rail) {
            this.connectedRails.add(rail.pos);
            BlockPos blockpos = this.pos.north();
            BlockPos blockpos1 = this.pos.south();
            BlockPos blockpos2 = this.pos.west();
            BlockPos blockpos3 = this.pos.east();
            boolean flag = this.isConnectedTo(blockpos);
            boolean flag1 = this.isConnectedTo(blockpos1);
            boolean flag2 = this.isConnectedTo(blockpos2);
            boolean flag3 = this.isConnectedTo(blockpos3);
            EnumRailDirection railDirection = null;
            if (flag || flag1) {
                railDirection = EnumRailDirection.NORTH_SOUTH;
            }

            if (flag2 || flag3) {
                railDirection = EnumRailDirection.EAST_WEST;
            }

            if (!this.isPowered) {
                if (flag1 && flag3 && !flag && !flag2) {
                    railDirection = EnumRailDirection.SOUTH_EAST;
                }

                if (flag1 && flag2 && !flag && !flag3) {
                    railDirection = EnumRailDirection.SOUTH_WEST;
                }

                if (flag && flag2 && !flag1 && !flag3) {
                    railDirection = EnumRailDirection.NORTH_WEST;
                }

                if (flag && flag3 && !flag1 && !flag2) {
                    railDirection = EnumRailDirection.NORTH_EAST;
                }
            }

            if (railDirection == EnumRailDirection.NORTH_SOUTH && this.canMakeSlopes) {
                if (checkBlockIsMe(this.world, blockpos.up())) {
                    railDirection = EnumRailDirection.ASCENDING_NORTH;
                }

                if (checkBlockIsMe(this.world, blockpos1.up())) {
                    railDirection = EnumRailDirection.ASCENDING_SOUTH;
                }
            }

            if (railDirection == EnumRailDirection.EAST_WEST && this.canMakeSlopes) {
                if (checkBlockIsMe(this.world, blockpos3.up())) {
                    railDirection = EnumRailDirection.ASCENDING_EAST;
                }

                if (checkBlockIsMe(this.world, blockpos2.up())) {
                    railDirection = EnumRailDirection.ASCENDING_WEST;
                }
            }

            if (railDirection == null) {
                railDirection = EnumRailDirection.NORTH_SOUTH;
            }

            this.state = this.state.withProperty(this.block.getShapeProperty(), railDirection);
            setState(this.world, this.pos, this.state);
        }

        protected boolean hasNeighborRail(BlockPos pos) {
            AbsRailBase.Rail blockrailbase$rail = this.findRailAt(pos);
            if (blockrailbase$rail == null) {
                return false;
            } else {
                blockrailbase$rail.removeSoftConnections();
                return blockrailbase$rail.canConnectTo(this);
            }
        }

        public AbsRailBase.Rail place(boolean flagA, boolean flagB) {
            BlockPos blockpos = this.pos.north();
            BlockPos blockpos1 = this.pos.south();
            BlockPos blockpos2 = this.pos.west();
            BlockPos blockpos3 = this.pos.east();
            boolean flag = this.hasNeighborRail(blockpos);
            boolean flag1 = this.hasNeighborRail(blockpos1);
            boolean flag2 = this.hasNeighborRail(blockpos2);
            boolean flag3 = this.hasNeighborRail(blockpos3);
            EnumRailDirection railDirection = null;
            if ((flag || flag1) && !flag2 && !flag3) {
                railDirection = EnumRailDirection.NORTH_SOUTH;
            }

            if ((flag2 || flag3) && !flag && !flag1) {
                railDirection = EnumRailDirection.EAST_WEST;
            }

            if (!this.isPowered) {
                if (flag1 && flag3 && !flag && !flag2) {
                    railDirection = EnumRailDirection.SOUTH_EAST;
                }

                if (flag1 && flag2 && !flag && !flag3) {
                    railDirection = EnumRailDirection.SOUTH_WEST;
                }

                if (flag && flag2 && !flag1 && !flag3) {
                    railDirection = EnumRailDirection.NORTH_WEST;
                }

                if (flag && flag3 && !flag1 && !flag2) {
                    railDirection = EnumRailDirection.NORTH_EAST;
                }
            }

            if (railDirection == null) {
                if (flag || flag1) {
                    railDirection = EnumRailDirection.NORTH_SOUTH;
                }

                if (flag2 || flag3) {
                    railDirection = EnumRailDirection.EAST_WEST;
                }

                if (!this.isPowered) {
                    if (flagA) {
                        if (flag1 && flag3) {
                            railDirection = EnumRailDirection.SOUTH_EAST;
                        }

                        if (flag2 && flag1) {
                            railDirection = EnumRailDirection.SOUTH_WEST;
                        }

                        if (flag3 && flag) {
                            railDirection = EnumRailDirection.NORTH_EAST;
                        }

                        if (flag && flag2) {
                            railDirection = EnumRailDirection.NORTH_WEST;
                        }
                    } else {
                        if (flag && flag2) {
                            railDirection = EnumRailDirection.NORTH_WEST;
                        }

                        if (flag3 && flag) {
                            railDirection = EnumRailDirection.NORTH_EAST;
                        }

                        if (flag2 && flag1) {
                            railDirection = EnumRailDirection.SOUTH_WEST;
                        }

                        if (flag1 && flag3) {
                            railDirection = EnumRailDirection.SOUTH_EAST;
                        }
                    }
                }
            }

            if (railDirection == EnumRailDirection.NORTH_SOUTH && this.canMakeSlopes) {
                if (checkBlockIsMe(this.world, blockpos.up())) {
                    railDirection = EnumRailDirection.ASCENDING_NORTH;
                }

                if (checkBlockIsMe(this.world, blockpos1.up())) {
                    railDirection = EnumRailDirection.ASCENDING_SOUTH;
                }
            }

            if (railDirection == EnumRailDirection.EAST_WEST && this.canMakeSlopes) {
                if (checkBlockIsMe(this.world, blockpos3.up())) {
                    railDirection = EnumRailDirection.ASCENDING_EAST;
                }

                if (checkBlockIsMe(this.world, blockpos2.up())) {
                    railDirection = EnumRailDirection.ASCENDING_WEST;
                }
            }

            if (railDirection == null) {
                railDirection = EnumRailDirection.NORTH_SOUTH;
            }

            this.updateConnectedRails(railDirection);
            this.state = this.state.withProperty(this.block.getShapeProperty(), railDirection);
            if (flagB || this.world.getBlockState(this.pos) != this.state) {
                setState(this.world, this.pos, this.state);

                for(int i = 0; i < this.connectedRails.size(); ++i) {
                    AbsRailBase.Rail blockrailbase$rail = this.findRailAt((BlockPos)this.connectedRails.get(i));
                    if (blockrailbase$rail != null) {
                        blockrailbase$rail.removeSoftConnections();
                        if (blockrailbase$rail.canConnectTo(this)) {
                            blockrailbase$rail.connectTo(this);
                        }
                    }
                }
            }

            return this;
        }

        public IBlockState getBlockState() {
            return this.state;
        }
    }
}
