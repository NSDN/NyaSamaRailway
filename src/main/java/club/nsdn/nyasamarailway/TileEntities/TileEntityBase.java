package club.nsdn.nyasamarailway.TileEntities;

/**
 * Created by drzzm32 on 2016.5.5.
 */

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.block.BlockContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.renderer.texture.IIconRegister;
import club.nsdn.nyasamarailway.CreativeTab.CreativeTabLoader;

public class TileEntityBase extends BlockContainer {

    protected String textureLocation = "";
    protected void setIconLocation(String textureLocation) { this.textureLocation = "nyasamarailway" + ":" + textureLocation; }

    public TileEntityBase(String blockName) {
        super(Material.rock);
        setBlockName(blockName);
        setBlockTextureName("minecraft:quartz_block_side");
        setIconLocation("minecraft:quartz_block_side");
        setHardness(2.0F);
        setLightLevel(0);
        setStepSound(Block.soundTypeStone);
        setResistance(10.0F);
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    public TileEntityBase(Material material, String blockName) {
        super(material);
        setBlockName(blockName);
        setBlockTextureName("minecraft:quartz_block_side");
        setIconLocation("minecraft:quartz_block_side");
        setHardness(2.0F);
        setLightLevel(0);
        setStepSound(Block.soundTypeGlass);
        setResistance(10.0F);
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntity();
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    protected void setBoundsByMeta(int meta) {
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess w, int x, int y, int z) {
        int meta = w.getBlockMetadata(x, y, z);
        setBoundsByMeta(meta);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack) {
        int l = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        if (player.rotationPitch > 22.5F) {
            if (l == 0)
            {
                world.setBlockMetadataWithNotify(x, y, z, 1, 2);
            }

            if (l == 1)
            {
                world.setBlockMetadataWithNotify(x, y, z, 2, 2);
            }

            if (l == 2)
            {
                world.setBlockMetadataWithNotify(x, y, z, 3, 2);
            }

            if (l == 3)
            {
                world.setBlockMetadataWithNotify(x, y, z, 4, 2);
            }
        } else if (player.rotationPitch > -22.5F) {
            if (l == 0)
            {
                world.setBlockMetadataWithNotify(x, y, z, 5, 2);
            }

            if (l == 1)
            {
                world.setBlockMetadataWithNotify(x, y, z, 6, 2);
            }

            if (l == 2)
            {
                world.setBlockMetadataWithNotify(x, y, z, 7, 2);
            }

            if (l == 3)
            {
                world.setBlockMetadataWithNotify(x, y, z, 8, 2);
            }
        } else {
            if (l == 0)
            {
                world.setBlockMetadataWithNotify(x, y, z, 9, 2);
            }

            if (l == 1)
            {
                world.setBlockMetadataWithNotify(x, y, z, 10, 2);
            }

            if (l == 2)
            {
                world.setBlockMetadataWithNotify(x, y, z, 11, 2);
            }

            if (l == 3)
            {
                world.setBlockMetadataWithNotify(x, y, z, 12, 2);
            }
        }

    }

    public boolean renderAsNormalBlock() {
        return false;
    }

    public void registerBlockIcons(IIconRegister icon)
    {
        this.blockIcon = icon.registerIcon(textureLocation);
    }

}
