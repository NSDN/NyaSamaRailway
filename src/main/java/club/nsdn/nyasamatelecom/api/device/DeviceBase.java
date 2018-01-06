package club.nsdn.nyasamatelecom.api.device;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2017.12.28.
 */
public class DeviceBase extends BlockContainer {

    protected String textureLocation = "";
    protected void setIconLocation(String modid, String icon) { this.textureLocation = modid + ":" + icon; }

    public DeviceBase(String blockName) {
        super(Material.rock);
        setBlockName(blockName);
        setBlockTextureName("minecraft:quartz_block_side");
        setHardness(2.0F);
        setLightLevel(0);
        setStepSound(Block.soundTypeStone);
        setResistance(10.0F);
    }

    public DeviceBase(Material material, String blockName) {
        super(material);
        setBlockName(blockName);
        setBlockTextureName("minecraft:quartz_block_side");
        setHardness(2.0F);
        setLightLevel(0);
        setStepSound(Block.soundTypeGlass);
        setResistance(10.0F);
    }

    @Override
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

    protected void setBoundsByXYZ(int meta, float x1, float y1, float z1, float x2, float y2, float z2) {
        switch (meta % 13) {
            case 1:
                setBlockBounds(x1, y1, z1, x2, y2, z2);
                break;
            case 2:
                setBlockBounds(1.0F - z2, y1, x1, 1.0F - z1, y2, x2);
                break;
            case 3:
                setBlockBounds(1.0F - x2, y1, 1.0F - z2, 1.0F - x1, y2, 1.0F - z1);
                break;
            case 4:
                setBlockBounds(z1, y1, 1.0F - x2, z2, y2, 1.0F - x1);
                break;

            case 7:
                setBlockBounds(x1, z1, y1, x2, z2, y2);
                break;
            case 8:
                setBlockBounds(1.0F - y2, z1, x1, 1.0F - y1, z2, x2);
                break;
            case 5:
                setBlockBounds(1.0F - x2, z1, 1.0F - y2, 1.0F - x1, z2, 1.0F - y1);
                break;
            case 6:
                setBlockBounds(y1, z1, 1.0F - x2, y2, z2, 1.0F - x1);
                break;

            case 9:
                setBlockBounds(x1, 1.0F - y2, z1, x2, 1.0F - y1, z2);
                break;
            case 10:
                setBlockBounds(1.0F - z2, 1.0F - y2, x1, 1.0F - z1, 1.0F - y1, x2);
                break;
            case 11:
                setBlockBounds(1.0F - x2, 1.0F - y2, 1.0F - z2, 1.0F - x1, 1.0F - y1, 1.0F - z1);
                break;
            case 12:
                setBlockBounds(z1, 1.0F - y2, 1.0F - x2, z2, 1.0F - y1, 1.0F - x1);
                break;
        }
    }

    protected void setBoundsBySize(int meta, float x, float y, float z) {
        setBoundsByXYZ(meta, 0.5F - x / 2, 0.0F, 0.5F - z / 2, 0.5F + x / 2, y, 0.5F + z / 2);
    }

    protected void setBoundsByMeta(int meta) {
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        return AxisAlignedBB.getBoundingBox(x + this.minX, y + this.minY, z + this.minZ, x + this.maxX, y + this.maxY, z + this.maxZ);
    }

    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        return AxisAlignedBB.getBoundingBox(x + this.minX, y + this.minY, z + this.minZ, x + this.maxX, y + this.maxY, z + this.maxZ);
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

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public void registerBlockIcons(IIconRegister icon)
    {
        this.blockIcon = icon.registerIcon(textureLocation);
    }

}
