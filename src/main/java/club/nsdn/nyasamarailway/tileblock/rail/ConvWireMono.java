package club.nsdn.nyasamarailway.tileblock.rail;

import club.nsdn.nyasamarailway.block.rail.BlockRailBase;
import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
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
 * Created by drzzm32 on 2018.2.21.
 */
public class ConvWireMono extends BlockRailBase implements ITileEntityProvider {

    public static class Conv extends TileEntity {

        public ForgeDirection direction;

        @Override
        @SideOnly(Side.CLIENT)
        public AxisAlignedBB getRenderBoundingBox() {
            return AxisAlignedBB
                    .getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1)
                    .expand(4, 4, 4);
        }

        public void fromNBT(NBTTagCompound tagCompound) {
            direction = ForgeDirection.getOrientation(
                    tagCompound.getInteger("direction")
            );
        }

        public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
            if (direction == null) direction = ForgeDirection.UNKNOWN;
            tagCompound.setInteger("direction", direction.ordinal());
            return tagCompound;
        }

        @Override
        public double getMaxRenderDistanceSquared() {
            return 16384.0D;
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
            tagCompound = toNBT(tagCompound);
            return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, tagCompound);
        }

        @Override
        public void onDataPacket(NetworkManager manager, S35PacketUpdateTileEntity packet) {
            NBTTagCompound tagCompound = packet.func_148857_g();
            fromNBT(tagCompound);
        }

    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new Conv();
    }

    @Override
    public Material getMaterial() {
        return Material.rock;
    }

    public ConvWireMono() {
        super("ConvWireMono");
        setTextureName("conv_wire_mono");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Override
    public float getRailMaxSpeed(World world, EntityMinecart cart, int x, int y, int z) {
        return 2.0F;
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return world.getBlock(x, y, z).isReplaceable(world, x, y, z);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        //Switch do not need this!
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack) {
        int meta = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        if (world.getTileEntity(x, y, z) instanceof Conv) {
            Conv conv = (Conv) world.getTileEntity(x, y, z);
            switch (meta) {
                case 0:
                    conv.direction = ForgeDirection.SOUTH;
                    break;
                case 1:
                    conv.direction = ForgeDirection.WEST;
                    break;
                case 2:
                    conv.direction = ForgeDirection.NORTH;
                    break;
                case 3:
                    conv.direction = ForgeDirection.EAST;
                    break;
                default:
                    break;
            }
            if ((meta + 2) % 2 == 0) meta = 0;
            else meta = 1;
            world.setBlockMetadataWithNotify(x, y, z, meta, 2);
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
    }

    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

}
