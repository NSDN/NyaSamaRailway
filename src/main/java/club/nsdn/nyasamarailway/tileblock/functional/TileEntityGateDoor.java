package club.nsdn.nyasamarailway.tileblock.functional;

import club.nsdn.nyasamarailway.tileblock.TileBlock;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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

import java.util.Random;

/**
 * Created by drzzm32 on 2017.9.4.
 */
public class TileEntityGateDoor extends TileBlock {

    public static class GateDoor extends TileEntity {

        public int progress = 0;
        public float prevDist;

        public static final int PROGRESS_MAX = 10;

        public static final int STATE_CLOSE = 0;
        public static final int STATE_CLOSING = 1;
        public static final int STATE_OPEN = 2;
        public static final int STATE_OPENING = 3;
        public int state = STATE_CLOSE;

        @Override
        @SideOnly(Side.CLIENT)
        public AxisAlignedBB getRenderBoundingBox()
        {
            return AxisAlignedBB
                    .getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1)
                    .expand(4, 4, 4);
        }

        public void fromNBT(NBTTagCompound tagCompound) {
            progress = tagCompound.getInteger("progress");
            state = tagCompound.getInteger("state");
        }

        public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
            tagCompound.setInteger("progress", progress);
            tagCompound.setInteger("state", state);
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

    public TileEntityGateDoor() {
        super("GateDoor");
        setIconLocation("gate_door");
        setLightOpacity(0);
        setLightLevel(0);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new GateDoor();
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side)
    {
        return side == ForgeDirection.UP;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack) {
        int meta = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        world.setBlockMetadataWithNotify(x, y, z, meta, 2);
    }

    @Override
    protected void setBoundsByMeta(int meta) {
        float x1 = -0.25F, y1 = 0.0F, z1 = 0.4375F, x2 = 1.25F, y2 = 1.5F, z2 = 0.5625F;

        switch (meta & 3) {
            case 0:
                setBlockBounds(x1, y1, z1, x2, y2, z2);
                break;
            case 1:
                setBlockBounds(1.0F - z2, y1, x1, 1.0F - z1, y2, x2);
                break;
            case 2:
                setBlockBounds(1.0F - x2, y1, 1.0F - z2, 1.0F - x1, y2, 1.0F - z1);
                break;
            case 3:
                setBlockBounds(z1, y1, 1.0F - x2, z2, y2, 1.0F - x1);
                break;
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        if ((world.getBlockMetadata(x, y, z) & 0x8) != 0) {
            return null;
        }
        return AxisAlignedBB.getBoundingBox(x + this.minX, y + this.minY, z + this.minZ, x + this.maxX, y + this.maxY, z + this.maxZ);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        float x1 = -0.25F, y1 = 0.125F, z1 = 0.4375F, x2 = 1.25F, y2 = 0.875F, z2 = 0.5625F;
        switch (meta & 3) {
            case 0:
                setBlockBounds(x1, y1, z1, x2, y2, z2);
                break;
            case 1:
                setBlockBounds(1.0F - z2, y1, x1, 1.0F - z1, y2, x2);
                break;
            case 2:
                setBlockBounds(1.0F - x2, y1, 1.0F - z2, 1.0F - x1, y2, 1.0F - z1);
                break;
            case 3:
                setBlockBounds(z1, y1, 1.0F - x2, z2, y2, 1.0F - x1);
                break;
        }
        return AxisAlignedBB.getBoundingBox(x + this.minX, y + this.minY, z + this.minZ, x + this.maxX, y + this.maxY, z + this.maxZ);
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        world.scheduleBlockUpdate(x, y, z, this, 1);
    }

    @Override
    public int tickRate(World world) {
        return 20;
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (!world.isRemote) {
            if (world.getTileEntity(x, y, z) instanceof GateDoor) {
                GateDoor gateDoor = (GateDoor) world.getTileEntity(x, y, z);

                switch (gateDoor.state) {
                    case GateDoor.STATE_CLOSE:
                        if ((world.getBlockMetadata(x, y, z) & 0x8) != 0) {
                            world.setBlockMetadataWithNotify(x, y, z,
                                    world.getBlockMetadata(x, y, z) & 0x7, 3
                            );
                            world.markBlockForUpdate(x, y, z);
                        }
                        break;
                    case GateDoor.STATE_CLOSING:
                        if (gateDoor.progress > 0) gateDoor.progress -= 1;
                        else {
                            gateDoor.state = GateDoor.STATE_CLOSE;
                            world.setBlockMetadataWithNotify(x, y, z,
                                    world.getBlockMetadata(x, y, z) & 0x7, 3
                            );
                            world.markBlockForUpdate(x, y, z);
                        }
                        break;
                    case GateDoor.STATE_OPEN:
                        if ((world.getBlockMetadata(x, y, z) & 0x8) == 0) {
                            world.setBlockMetadataWithNotify(x, y, z,
                                    world.getBlockMetadata(x, y, z) | 0x8, 3
                            );
                            world.markBlockForUpdate(x, y, z);
                        }
                        break;
                    case GateDoor.STATE_OPENING:
                        if (gateDoor.progress < GateDoor.PROGRESS_MAX) gateDoor.progress += 1;
                        else {
                            gateDoor.state = GateDoor.STATE_OPEN;
                            world.setBlockMetadataWithNotify(x, y, z,
                                    world.getBlockMetadata(x, y, z) | 0x8, 3
                            );
                            world.markBlockForUpdate(x, y, z);
                        }
                        break;
                    default:
                        break;
                }

                if (gateDoor.state == GateDoor.STATE_OPENING || gateDoor.state == GateDoor.STATE_CLOSING) {
                    world.markBlockForUpdate(x, y, z);
                }
                world.scheduleBlockUpdate(x, y, z, this, 1);
            }
        }
    }

}
