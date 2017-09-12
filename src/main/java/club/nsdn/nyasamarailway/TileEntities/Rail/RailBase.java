package club.nsdn.nyasamarailway.TileEntities.Rail;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.ITileEntityProvider;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by drzzm on 2016.7.23.
 */

public class RailBase extends net.minecraft.block.BlockRailBase implements ITileEntityProvider {

    public static class TileEntityRail extends TileEntity {

        @Override
        @SideOnly(Side.CLIENT)
        public AxisAlignedBB getRenderBoundingBox()
        {
            return AxisAlignedBB
                    .getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1)
                    .expand(4, 4, 4);
        }

        @Override
        public void writeToNBT(NBTTagCompound tagCompound) {
            super.writeToNBT(tagCompound);
        }

        @Override
        public void readFromNBT(NBTTagCompound tagCompound) {
            super.readFromNBT(tagCompound);
        }

    }

    public RailBase(boolean noTurn) {
        super(noTurn);
        this.setBlockBounds(-0.5F, 0.0F, -0.5F, 1.5F, 0.25F, 1.5F);
        this.isBlockContainer = true;
    }

    protected String textureLocation = "";
    protected void setIconLocation(String textureLocation) { this.textureLocation = "nyasamarailway" + ":" + textureLocation; }

    @Override
    public Material getMaterial() {
        return super.getMaterial();
    }

    @Override
    public MapColor getMapColor(int i) {
        return getMaterial().getMaterialMapColor();
    }

    @Override
    public boolean getBlocksMovement(IBlockAccess world, int x, int y, int z) {
        return !getMaterial().blocksMovement();
    }

    @Override
    public int getMobilityFlag() {
        return getMaterial().getMaterialMobility();
    }

    @Override
    public void registerBlockIcons(IIconRegister icon)
    {
        this.blockIcon = icon.registerIcon(textureLocation);
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z)
    {
        super.onBlockAdded(world, x, y, z);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta)
    {
        super.breakBlock(world, x, y, z, block, meta);
        world.removeTileEntity(x, y, z);
    }

    @Override
    public boolean onBlockEventReceived(World world, int x, int y, int z, int i, int j)
    {
        super.onBlockEventReceived(world, x, y, z, i, j);
        TileEntity tileentity = world.getTileEntity(x, y, z);
        if (tileentity != null) {
            return tileentity.receiveClientEvent(i, j);
        } else {
            return false;
        }
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityRail();
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    protected void setBoundsByMeta(int meta) {
        if (meta >= 2 && meta <= 5)
        {
            this.setBlockBounds(-0.5F, 0.0F, -0.5F, 1.5F, 0.5F, 1.5F);
        }
        else
        {
            this.setBlockBounds(-0.5F, 0.0F, -0.5F, 1.5F, 0.25F, 1.5F);
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        return AxisAlignedBB.getBoundingBox(x + this.minX, y + this.minY, z + this.minZ, x + this.maxX, y + this.maxY, z + this.maxZ);
    }

    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }

    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 src, Vec3 dst) {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        return super.collisionRayTrace(world, x, y, z, src, dst);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        setBoundsByMeta(meta);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        super.onNeighborBlockChange(world, x, y, z, block);
    }

    @Override
    protected void func_150052_a(World world, int x, int y, int z, boolean control)
    {
        if (!world.isRemote)
        {
            (new Rail(world, x, y, z)).func_150655_a(world.isBlockIndirectlyGettingPowered(x, y, z), control);
        }
    }
    
    /****************************************************************************************************************************************/
    
    public class Rail extends net.minecraft.block.BlockRailBase.Rail
    {

        public boolean checkBlockIsMe(World world, int x, int y, int z)
        {
            return world.getBlock(x, y, z).getClass() == RailBase.class;
        }
        
        protected World world;
        protected int chunkCoordX;
        protected int chunkCoordY;
        protected int chunkCoordZ;
        private final boolean isNormalRail;
        private List<ChunkPosition> array = new ArrayList<ChunkPosition>();
        private final boolean canMakeSlopes;

        public Rail(World world, int x, int y, int z)
        {
            super(world, x, y, z);
            this.world = world;
            this.chunkCoordX = x;
            this.chunkCoordY = y;
            this.chunkCoordZ = z;
            BlockRailBase block = (BlockRailBase)world.getBlock(x, y, z);
            int l = block.getBasicRailMetadata(world, null, x, y, z);
            this.isNormalRail = !block.isFlexibleRail(world, x, y, z);
            canMakeSlopes = block.canMakeSlopes(world, x, y, z);
            this.func_150648_a(l);
        }

        private void func_150648_a(int p_150648_1_)
        {
            this.array.clear();

            if (p_150648_1_ == 0)
            {
                this.array.add(new ChunkPosition(this.chunkCoordX, this.chunkCoordY, this.chunkCoordZ - 1));
                this.array.add(new ChunkPosition(this.chunkCoordX, this.chunkCoordY, this.chunkCoordZ + 1));
            }
            else if (p_150648_1_ == 1)
            {
                this.array.add(new ChunkPosition(this.chunkCoordX - 1, this.chunkCoordY, this.chunkCoordZ));
                this.array.add(new ChunkPosition(this.chunkCoordX + 1, this.chunkCoordY, this.chunkCoordZ));
            }
            else if (p_150648_1_ == 2)
            {
                this.array.add(new ChunkPosition(this.chunkCoordX - 1, this.chunkCoordY, this.chunkCoordZ));
                this.array.add(new ChunkPosition(this.chunkCoordX + 1, this.chunkCoordY + 1, this.chunkCoordZ));
            }
            else if (p_150648_1_ == 3)
            {
                this.array.add(new ChunkPosition(this.chunkCoordX - 1, this.chunkCoordY + 1, this.chunkCoordZ));
                this.array.add(new ChunkPosition(this.chunkCoordX + 1, this.chunkCoordY, this.chunkCoordZ));
            }
            else if (p_150648_1_ == 4)
            {
                this.array.add(new ChunkPosition(this.chunkCoordX, this.chunkCoordY + 1, this.chunkCoordZ - 1));
                this.array.add(new ChunkPosition(this.chunkCoordX, this.chunkCoordY, this.chunkCoordZ + 1));
            }
            else if (p_150648_1_ == 5)
            {
                this.array.add(new ChunkPosition(this.chunkCoordX, this.chunkCoordY, this.chunkCoordZ - 1));
                this.array.add(new ChunkPosition(this.chunkCoordX, this.chunkCoordY + 1, this.chunkCoordZ + 1));
            }
            else if (p_150648_1_ == 6)
            {
                this.array.add(new ChunkPosition(this.chunkCoordX + 1, this.chunkCoordY, this.chunkCoordZ));
                this.array.add(new ChunkPosition(this.chunkCoordX, this.chunkCoordY, this.chunkCoordZ + 1));
            }
            else if (p_150648_1_ == 7)
            {
                this.array.add(new ChunkPosition(this.chunkCoordX - 1, this.chunkCoordY, this.chunkCoordZ));
                this.array.add(new ChunkPosition(this.chunkCoordX, this.chunkCoordY, this.chunkCoordZ + 1));
            }
            else if (p_150648_1_ == 8)
            {
                this.array.add(new ChunkPosition(this.chunkCoordX - 1, this.chunkCoordY, this.chunkCoordZ));
                this.array.add(new ChunkPosition(this.chunkCoordX, this.chunkCoordY, this.chunkCoordZ - 1));
            }
            else if (p_150648_1_ == 9)
            {
                this.array.add(new ChunkPosition(this.chunkCoordX + 1, this.chunkCoordY, this.chunkCoordZ));
                this.array.add(new ChunkPosition(this.chunkCoordX, this.chunkCoordY, this.chunkCoordZ - 1));
            }
        }

        private void func_150651_b()
        {
            for (int i = 0; i < this.array.size(); ++i)
            {
                RailBase.Rail rail = this.func_150654_a((ChunkPosition)this.array.get(i));

                if (rail != null && rail.func_150653_a(this))
                {
                    this.array.set(i, new ChunkPosition(rail.chunkCoordX, rail.chunkCoordY, rail.chunkCoordZ));
                }
                else
                {
                    this.array.remove(i--);
                }
            }
        }

        //周围轨道方块遍历
        private boolean func_150646_a(int p_150646_1_, int p_150646_2_, int p_150646_3_)
        {
            return checkBlockIsMe(this.world, p_150646_1_, p_150646_2_, p_150646_3_) ? true : (checkBlockIsMe(this.world, p_150646_1_, p_150646_2_ + 1, p_150646_3_) ? true : checkBlockIsMe(this.world, p_150646_1_, p_150646_2_ - 1, p_150646_3_));
        }

        protected RailBase.Rail func_150654_a(ChunkPosition p_150654_1_)
        {
            return checkBlockIsMe(this.world, p_150654_1_.chunkPosX, p_150654_1_.chunkPosY, p_150654_1_.chunkPosZ) ? RailBase.this.new Rail(this.world, p_150654_1_.chunkPosX, p_150654_1_.chunkPosY, p_150654_1_.chunkPosZ) : (checkBlockIsMe(this.world, p_150654_1_.chunkPosX, p_150654_1_.chunkPosY + 1, p_150654_1_.chunkPosZ) ? RailBase.this.new Rail(this.world, p_150654_1_.chunkPosX, p_150654_1_.chunkPosY + 1, p_150654_1_.chunkPosZ) : (checkBlockIsMe(this.world, p_150654_1_.chunkPosX, p_150654_1_.chunkPosY - 1, p_150654_1_.chunkPosZ) ? RailBase.this.new Rail(this.world, p_150654_1_.chunkPosX, p_150654_1_.chunkPosY - 1, p_150654_1_.chunkPosZ) : null));
        }

        //WTF?
        private boolean func_150653_a(RailBase.Rail p_150653_1_)
        {
            for (int i = 0; i < this.array.size(); ++i)
            {
                ChunkPosition chunkposition = (ChunkPosition)this.array.get(i);

                if (chunkposition.chunkPosX == p_150653_1_.chunkCoordX && chunkposition.chunkPosZ == p_150653_1_.chunkCoordZ)
                {
                    return true;
                }
            }

            return false;
        }

        private boolean func_150652_b(int p_150652_1_, int p_150652_2_, int p_150652_3_)
        {
            for (int l = 0; l < this.array.size(); ++l)
            {
                ChunkPosition chunkposition = (ChunkPosition)this.array.get(l);

                if (chunkposition.chunkPosX == p_150652_1_ && chunkposition.chunkPosZ == p_150652_3_)
                {
                    return true;
                }
            }

            return false;
        }

        protected int func_150650_a()
        {
            int i = 0;

            if (this.func_150646_a(this.chunkCoordX, this.chunkCoordY, this.chunkCoordZ - 1))
            {
                ++i;
            }

            if (this.func_150646_a(this.chunkCoordX, this.chunkCoordY, this.chunkCoordZ + 1))
            {
                ++i;
            }

            if (this.func_150646_a(this.chunkCoordX - 1, this.chunkCoordY, this.chunkCoordZ))
            {
                ++i;
            }

            if (this.func_150646_a(this.chunkCoordX + 1, this.chunkCoordY, this.chunkCoordZ))
            {
                ++i;
            }

            return i;
        }

        private boolean func_150649_b(Rail p_150649_1_)
        {
            return this.func_150653_a(p_150649_1_) ? true : (this.array.size() == 2 ? false : true);
        }

        //轨道Metadata操作
        private void func_150645_c(Rail p_150645_1_)
        {
            this.array.add(new ChunkPosition(p_150645_1_.chunkCoordX, p_150645_1_.chunkCoordY, p_150645_1_.chunkCoordZ));
            boolean flag = this.func_150652_b(this.chunkCoordX, this.chunkCoordY, this.chunkCoordZ - 1);
            boolean flag1 = this.func_150652_b(this.chunkCoordX, this.chunkCoordY, this.chunkCoordZ + 1);
            boolean flag2 = this.func_150652_b(this.chunkCoordX - 1, this.chunkCoordY, this.chunkCoordZ);
            boolean flag3 = this.func_150652_b(this.chunkCoordX + 1, this.chunkCoordY, this.chunkCoordZ);
            byte b0 = -1;

            if (flag || flag1)
            {
                b0 = 0;
            }

            if (flag2 || flag3)
            {
                b0 = 1;
            }

            if (!this.isNormalRail)
            {
                if (flag1 && flag3 && !flag && !flag2)
                {
                    b0 = 6;
                }

                if (flag1 && flag2 && !flag && !flag3)
                {
                    b0 = 7;
                }

                if (flag && flag2 && !flag1 && !flag3)
                {
                    b0 = 8;
                }

                if (flag && flag3 && !flag1 && !flag2)
                {
                    b0 = 9;
                }
            }

            if (b0 == 0 && canMakeSlopes)
            {
                if (checkBlockIsMe(this.world, this.chunkCoordX, this.chunkCoordY + 1, this.chunkCoordZ - 1))
                {
                    b0 = 4;
                }

                if (checkBlockIsMe(this.world, this.chunkCoordX, this.chunkCoordY + 1, this.chunkCoordZ + 1))
                {
                    b0 = 5;
                }
            }

            if (b0 == 1 && canMakeSlopes)
            {
                if (checkBlockIsMe(this.world, this.chunkCoordX + 1, this.chunkCoordY + 1, this.chunkCoordZ))
                {
                    b0 = 2;
                }

                if (checkBlockIsMe(this.world, this.chunkCoordX - 1, this.chunkCoordY + 1, this.chunkCoordZ))
                {
                    b0 = 3;
                }
            }

            if (b0 < 0)
            {
                b0 = 0;
            }

            int i = b0;

            if (this.isNormalRail)
            {
                i = this.world.getBlockMetadata(this.chunkCoordX, this.chunkCoordY, this.chunkCoordZ) & 8 | b0;
            }

            this.world.setBlockMetadataWithNotify(this.chunkCoordX, this.chunkCoordY, this.chunkCoordZ, i, 3);
        }

        private boolean func_150647_c(int p_150647_1_, int p_150647_2_, int p_150647_3_)
        {
            RailBase.Rail rail = this.func_150654_a(new ChunkPosition(p_150647_1_, p_150647_2_, p_150647_3_));

            if (rail == null)
            {
                return false;
            }
            else
            {
                rail.func_150651_b();
                return rail.func_150649_b(this);
            }
        }

        //似乎是主函数
        public void func_150655_a(boolean p_150655_1_, boolean p_150655_2_)
        {
            boolean flag2 = this.func_150647_c(this.chunkCoordX, this.chunkCoordY, this.chunkCoordZ - 1);
            boolean flag3 = this.func_150647_c(this.chunkCoordX, this.chunkCoordY, this.chunkCoordZ + 1);
            boolean flag4 = this.func_150647_c(this.chunkCoordX - 1, this.chunkCoordY, this.chunkCoordZ);
            boolean flag5 = this.func_150647_c(this.chunkCoordX + 1, this.chunkCoordY, this.chunkCoordZ);
            byte b0 = -1;

            if ((flag2 || flag3) && !flag4 && !flag5)
            {
                b0 = 0;
            }

            if ((flag4 || flag5) && !flag2 && !flag3)
            {
                b0 = 1;
            }

            if (!this.isNormalRail)
            {
                if (flag3 && flag5 && !flag2 && !flag4)
                {
                    b0 = 6;
                }

                if (flag3 && flag4 && !flag2 && !flag5)
                {
                    b0 = 7;
                }

                if (flag2 && flag4 && !flag3 && !flag5)
                {
                    b0 = 8;
                }

                if (flag2 && flag5 && !flag3 && !flag4)
                {
                    b0 = 9;
                }
            }

            if (b0 == -1)
            {
                if (flag2 || flag3)
                {
                    b0 = 0;
                }

                if (flag4 || flag5)
                {
                    b0 = 1;
                }

                if (!this.isNormalRail)
                {
                    if (p_150655_1_)
                    {
                        if (flag3 && flag5)
                        {
                            b0 = 6;
                        }

                        if (flag4 && flag3)
                        {
                            b0 = 7;
                        }

                        if (flag5 && flag2)
                        {
                            b0 = 9;
                        }

                        if (flag2 && flag4)
                        {
                            b0 = 8;
                        }
                    }
                    else
                    {
                        if (flag2 && flag4)
                        {
                            b0 = 8;
                        }

                        if (flag5 && flag2)
                        {
                            b0 = 9;
                        }

                        if (flag4 && flag3)
                        {
                            b0 = 7;
                        }

                        if (flag3 && flag5)
                        {
                            b0 = 6;
                        }
                    }
                }
            }

            if (b0 == 0 && canMakeSlopes)
            {
                if (checkBlockIsMe(this.world, this.chunkCoordX, this.chunkCoordY + 1, this.chunkCoordZ - 1))
                {
                    b0 = 4;
                }

                if (checkBlockIsMe(this.world, this.chunkCoordX, this.chunkCoordY + 1, this.chunkCoordZ + 1))
                {
                    b0 = 5;
                }
            }

            if (b0 == 1 && canMakeSlopes)
            {
                if (checkBlockIsMe(this.world, this.chunkCoordX + 1, this.chunkCoordY + 1, this.chunkCoordZ))
                {
                    b0 = 2;
                }

                if (checkBlockIsMe(this.world, this.chunkCoordX - 1, this.chunkCoordY + 1, this.chunkCoordZ))
                {
                    b0 = 3;
                }
            }

            if (b0 < 0)
            {
                b0 = 0;
            }

            this.func_150648_a(b0);
            int i = b0;

            if (this.isNormalRail)
            {
                i = this.world.getBlockMetadata(this.chunkCoordX, this.chunkCoordY, this.chunkCoordZ) & 8 | b0;
            }

            if (p_150655_2_ || this.world.getBlockMetadata(this.chunkCoordX, this.chunkCoordY, this.chunkCoordZ) != i)
            {
                this.world.setBlockMetadataWithNotify(this.chunkCoordX, this.chunkCoordY, this.chunkCoordZ, i, 3);

                for (int j = 0; j < this.array.size(); ++j)
                {
                    RailBase.Rail rail = this.func_150654_a((ChunkPosition)this.array.get(j));
                    if (rail != null)
                    {
                        rail.func_150651_b();

                        if (rail.func_150649_b(this))
                        {
                            rail.func_150645_c(this);
                        }
                    }
                }
            }
        }
    }

}
