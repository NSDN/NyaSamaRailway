package club.nsdn.nyasamarailway.TileEntities.Rail;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.ITileEntityProvider;


/**
 * Created by drzzm on 2016.7.23.
 */

public class RailBase extends net.minecraft.block.BlockRailBase implements ITileEntityProvider {

    public static class Rail extends TileEntity {

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

    public RailBase(boolean canTurn) {
        super(canTurn);
        this.setBlockBounds(-0.5F, 0.0F, -0.5F, 1.5F, 0.25F, 1.5F);
        this.isBlockContainer = true;
    }

    protected String textureLocation = "";
    protected void setIconLocation(String textureLocation) { this.textureLocation = "nyasamarailway" + ":" + textureLocation; }

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
        return new Rail();
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
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        return super.getSelectedBoundingBoxFromPool(world, x, y, z);
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

}
