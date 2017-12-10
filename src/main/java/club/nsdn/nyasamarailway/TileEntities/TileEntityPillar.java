package club.nsdn.nyasamarailway.TileEntities;

import club.nsdn.nyasamarailway.Util.Util;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by drzzm32 on 2017.10.5.
 */
public class TileEntityPillar extends TileEntityBase {

    public static class Pillar extends TileEntity {

        @Override
        @SideOnly(Side.CLIENT)
        public AxisAlignedBB getRenderBoundingBox()
        {
            return AxisAlignedBB
                    .getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1)
                    .expand(4, 4, 4);
        }

        public int meta = 0;

        public void fromNBT(NBTTagCompound tagCompound) {
            meta = tagCompound.getInteger("meta");
        }

        public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
            tagCompound.setInteger("meta", meta);
            return tagCompound;
        }

        @Override
        public void writeToNBT(NBTTagCompound tagCompound) {
            super.writeToNBT(tagCompound);
            toNBT(tagCompound);
        }

        @Override
        public void readFromNBT(NBTTagCompound tagCompound) {
            super.readFromNBT(tagCompound);
            fromNBT(tagCompound);
        }

        @Override
        public Packet getDescriptionPacket() {
            NBTTagCompound tagCompound = new NBTTagCompound();
            toNBT(tagCompound);
            return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, tagCompound);
        }

        @Override
        public void onDataPacket(NetworkManager manager, S35PacketUpdateTileEntity packet) {
            NBTTagCompound tagCompound = packet.func_148857_g();
            fromNBT(tagCompound);
        }

    }

    public TileEntityPillar() {
        super("Pillar");
        setIconLocation("pillar");
        setLightOpacity(0);
        setLightLevel(0);
        if (!Util.loadIf()) setCreativeTab(null);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new Pillar();
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack) {
        doScanBlock(world, x, y, z);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        doScanBlock(world, x, y, z);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        doScanBlock(world, x, y, z);

        if (world.getTileEntity(x, y, z) instanceof Pillar) {
            Pillar pillar = (Pillar) world.getTileEntity(x, y, z);
            int meta = pillar.meta;

            AxisAlignedBB box = AxisAlignedBB.getBoundingBox(
                    0.375, 0.375, 0.375,
                    0.625, 0.625, 0.625
            );

            if ((meta & TileEntityPillar.getValueByForgeDirection(ForgeDirection.UP)) != 0)
                box = box.addCoord(0.0, 0.375, 0.0);
            if ((meta & TileEntityPillar.getValueByForgeDirection(ForgeDirection.DOWN)) != 0)
                box = box.addCoord(0.0, -0.375, 0.0);
            if ((meta & TileEntityPillar.getValueByForgeDirection(ForgeDirection.NORTH)) != 0)
                box = box.addCoord(0.0, 0.0, -0.375);
            if ((meta & TileEntityPillar.getValueByForgeDirection(ForgeDirection.SOUTH)) != 0)
                box = box.addCoord(0.0, 0.0, 0.375);
            if ((meta & TileEntityPillar.getValueByForgeDirection(ForgeDirection.WEST)) != 0)
                box = box.addCoord(-0.375, 0.0, 0.0);
            if ((meta & TileEntityPillar.getValueByForgeDirection(ForgeDirection.EAST)) != 0)
                box = box.addCoord(0.375, 0.0, 0.0);

            setBlockBounds(
                    (float) box.minX, (float) box.minY, (float) box.minZ,
                    (float) box.maxX, (float) box.maxY, (float) box.maxZ
            );
        }
    }

    public boolean checkBlock(IBlockAccess world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        if (block instanceof BlockAir) return false;
        if (block instanceof BlockSlab) return false;
        if (block instanceof BlockPane) return false;
        if (block instanceof BlockWall) return false;

        if (
            block instanceof TileEntitySignalBox ||
            block instanceof TileEntitySignalBoxSender ||
            block instanceof TileEntityTriStateSignalBox ||
            block instanceof TileEntitySignalLight ||
            block instanceof TileEntitySignalLamp ||
            block instanceof TileEntitySignalStick ||
            block instanceof TileEntityBiSignalLight ||
            block instanceof TileEntityTriSignalLight
        ) {
            if (world.getBlock(x, y - 1, z) == this) return true;
        }

        if (world.getTileEntity(x, y, z) != null) {
            if (!(world.getTileEntity(x, y, z) instanceof Pillar)) return false;
        }

        Material material = block.getMaterial();
        if (material == Material.clay || material == Material.ground ||
            material == Material.iron || material == Material.rock ||
            material == Material.glass || material == Material.sand ||
            material == Material.wood
        ) return true;

        return material.isSolid();
    }

    public static int getValueByForgeDirection(ForgeDirection direction) {
        return 1 << (direction.ordinal());
    }

    public void doScanBlock(IBlockAccess world, int x, int y, int z) {
        if (world.getTileEntity(x, y, z) instanceof Pillar) {
            Pillar pillar = (Pillar) world.getTileEntity(x, y, z);
            int meta = 0;

            if (checkBlock(world, x, y + 1, z)) meta |= getValueByForgeDirection(ForgeDirection.UP);
            if (checkBlock(world, x, y - 1, z)) meta |= getValueByForgeDirection(ForgeDirection.DOWN);
            if (checkBlock(world, x + 1, y, z)) meta |= getValueByForgeDirection(ForgeDirection.EAST);
            if (checkBlock(world, x - 1, y, z)) meta |= getValueByForgeDirection(ForgeDirection.WEST);
            if (checkBlock(world, x, y, z + 1)) meta |= getValueByForgeDirection(ForgeDirection.SOUTH);
            if (checkBlock(world, x, y, z - 1)) meta |= getValueByForgeDirection(ForgeDirection.NORTH);

            pillar.meta = meta;
            if (world instanceof World)
                ((World) world).markBlockForUpdate(x, y, z);
        }
    }

}
