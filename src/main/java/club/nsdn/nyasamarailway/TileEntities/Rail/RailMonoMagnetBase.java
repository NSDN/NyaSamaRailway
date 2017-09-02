package club.nsdn.nyasamarailway.TileEntities.Rail;

import club.nsdn.nyasamarailway.CreativeTab.CreativeTabLoader;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2017.1.13.
 */
public class RailMonoMagnetBase extends RailBase {

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

        @Override
        public Packet getDescriptionPacket() {
            NBTTagCompound tagCompound = new NBTTagCompound();
            return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, tagCompound);
        }

        @Override
        public void onDataPacket(NetworkManager manager, S35PacketUpdateTileEntity packet) {
            NBTTagCompound tagCompound = packet.func_148857_g();
        }

    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityRail();
    }

    @Override
    public Material getMaterial() {
        return Material.iron;
    }

    public RailMonoMagnetBase(boolean noTurn, String name, String icon) {
        super(noTurn);
        setBlockName(name);
        setIconLocation(icon);
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    public enum RailDirection {
        NONE,
        WE, //West-East
        NS //North-South
    }

    public RailDirection getRailDirection(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        if ((meta & 2) == 0 && (meta & 4) == 0) {
            return ((meta & 1) == 0) ? RailDirection.NS : RailDirection.WE;
        } else if ((meta & 2) > 0) {
            return RailDirection.WE;
        } else if ((meta & 4) > 0) {
            return RailDirection.NS;
        }
        return RailDirection.NONE;
    }

    @Override
    public float getRailMaxSpeed(World world, EntityMinecart cart, int x, int y, int z) {
        return 1.0F;
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
            return world.getBlock(x, y, z) instanceof RailMonoMagnetBase;
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
