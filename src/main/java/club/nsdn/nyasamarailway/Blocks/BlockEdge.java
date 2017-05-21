package club.nsdn.nyasamarailway.Blocks;

import club.nsdn.nyasamarailway.CreativeTab.CreativeTabLoader;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by drzzm on 2016.11.28.
 */
public class BlockEdge extends Block {

    public static int renderType;
    private float xBoundMin;
    private float yBoundMin;
    private float zBoundMin;
    private float xBoundMax;
    private float yBoundMax;
    private float zBoundMax;
    private IIcon blockIcon;

    public BlockEdge()
    {
        super(Material.rock);
        setHardness(1.0F);
        setResistance(2.0F);
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
        setBlockName("EdgeBlock");
        setBlockTextureName("nyasamarailway:edge_block");
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitx, float hity, float hitz)
    {
        if (player.inventory.getCurrentItem() != null)
        {
            int blockMetadata = world.getBlockMetadata(x, y, z);
            if ((player.inventory.getCurrentItem().isItemEqual(new ItemStack(this))) && (hity == 0.5F) && ((blockMetadata & 0x4) == 4))
            {
                world.setBlockMetadataWithNotify(x, y, z, blockMetadata - 4, 2);
                if (!player.capabilities.isCreativeMode) {
                    player.inventory.getCurrentItem().stackSize -= 1;
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
    {
        int blockMetadata = world.getBlockMetadata(x, y, z);
        if ((blockMetadata & 0x3) == 0) {
            this.xBoundMin = 0.375F;
        } else {
            this.xBoundMin = 0.0F;
        }
        if ((blockMetadata & 0x8) == 8) {
            this.yBoundMin = 0.5F;
        } else {
            this.yBoundMin = 0.0F;
        }
        if ((blockMetadata & 0x3) == 1) {
            this.zBoundMin = 0.375F;
        } else {
            this.zBoundMin = 0.0F;
        }
        if ((blockMetadata & 0x3) == 2) {
            this.xBoundMax = 0.625F;
        } else {
            this.xBoundMax = 1.0F;
        }
        if ((blockMetadata & 0x4) == 4) {
            this.yBoundMax = 0.5F;
        } else {
            this.yBoundMax = 1.0F;
        }
        if ((blockMetadata & 0x3) == 3) {
            this.zBoundMax = 0.625F;
        } else {
            this.zBoundMax = 1.0F;
        }
        setBlockBounds(this.xBoundMin, this.yBoundMin, this.zBoundMin, this.xBoundMax, this.yBoundMax, this.zBoundMax);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
    {
        setBlockBoundsBasedOnState(world, x, y, z);
        return AxisAlignedBB.getBoundingBox(x + this.minX, y + this.minY, z + this.minZ, x + this.maxX, y + this.maxY, z + this.maxZ);
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float hitx, float hity, float hitz, int metadata)
    {
        if (side == 0) {
            return 8;
        }
        if (side == 1)
        {
            if (world.getBlock(x, y - 1, z).equals(this)) {
                return world.getBlockMetadata(x, y - 1, z) | 0x4;
            }
            return 4;
        }
        if (hity <= 0.5F)
        {
            if (side == 2) {
                return 5;
            }
            if (side == 3) {
                return 7;
            }
            if (side == 4) {
                return 4;
            }
            return 6;
        }
        if (side == 2) {
            return 9;
        }
        if (side == 3) {
            return 11;
        }
        if (side == 4) {
            return 8;
        }
        return 10;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public int getRenderType()
    {
        return renderType;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side)
    {
        return side == ForgeDirection.UP;
    }

}
