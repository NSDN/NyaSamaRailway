package club.nsdn.nyasamarailway.TileEntities.Rail;

import club.nsdn.nyasamarailway.CreativeTab.CreativeTabLoader;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2016.12.2.
 */
public class RailMonoMagnet extends RailBase {

    public static class TileEntityRail extends TileEntity {

        @Override
        public boolean shouldRenderInPass(int pass) {
            return true;
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

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityRail();
    }

    public RailMonoMagnet() {
        super(false);
        setBlockName("RailMonoMagnet");
        setIconLocation("rail_mono_magnet");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Override
    protected void setBoundsByMeta(int meta) {
        if (meta >= 2 && meta <= 5)
        {
            this.setBlockBounds(0.0F, -0.25F, 0.0F, 1.0F, 0.5F, 1.0F);
        }
        else
        {
            this.setBlockBounds(0.0F, -0.25F, 0.0F, 1.0F, 0.125F, 1.0F);
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return world.getBlock(x, y, z).isReplaceable(world, x, y, z) && world.getBlock(x, y - 1, z) instanceof RailMono;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        if(!world.isRemote) {
            int l = world.getBlockMetadata(x, y, z);
            int i1 = l;
            if(this.field_150053_a) {
                i1 = l & 7;
            }
            this.func_150048_a(world, x, y, z, l, i1, block);
        }
    }

    @Override
    protected void func_150052_a(World world, int x, int y, int z, boolean control)
    {
        if (!world.isRemote)
        {
            (new Rail(world, x, y, z)).func_150655_a(world.isBlockIndirectlyGettingPowered(x, y, z), control);
        }
    }

    public class Rail extends RailBase.Rail {

        @Override
        public boolean checkBlockIsMe(World world, int x, int y, int z) {
            return world.getBlock(x, y, z).getClass() == RailMonoMagnet.class;
        }

        @Override
        protected RailBase.Rail func_150654_a(ChunkPosition chunkPosition)
        {
            return checkBlockIsMe(this.world, chunkPosition.chunkPosX, chunkPosition.chunkPosY, chunkPosition.chunkPosZ) ?
                    new Rail(this.world, chunkPosition.chunkPosX, chunkPosition.chunkPosY, chunkPosition.chunkPosZ)
                    : (
                            checkBlockIsMe(this.world, chunkPosition.chunkPosX, chunkPosition.chunkPosY + 1, chunkPosition.chunkPosZ) ?
                                    new Rail(this.world, chunkPosition.chunkPosX, chunkPosition.chunkPosY + 1, chunkPosition.chunkPosZ)
                                    : (
                                            checkBlockIsMe(this.world, chunkPosition.chunkPosX, chunkPosition.chunkPosY - 1, chunkPosition.chunkPosZ) ?
                                                    new Rail(this.world, chunkPosition.chunkPosX, chunkPosition.chunkPosY - 1, chunkPosition.chunkPosZ)
                                                    : null)
            );
        }

        public Rail(World world, int x, int y, int z) {
            super(world, x, y, z);
        }

    }
}
