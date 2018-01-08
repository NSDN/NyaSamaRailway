package club.nsdn.nyasamarailway.tileblock.rail;

import club.nsdn.nyasamarailway.block.BlockLoader;
import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.tileblock.TileBlock;
import club.nsdn.nyasamarailway.tileblock.rail.mono.RailMonoMagnet;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityTriStateReceiver;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

/**
 * Created by drzzm32 on 2018.1.8.
 */
public class RailMagnetSwitch extends TileBlock {

    public static class MagnetSwitch extends TileEntityTriStateReceiver {

        public ForgeDirection direction;

        @Override
        @SideOnly(Side.CLIENT)
        public AxisAlignedBB getRenderBoundingBox()
        {
            return AxisAlignedBB
                    .getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1)
                    .expand(4, 4, 4);
        }

        public void fromNBT(NBTTagCompound tagCompound) {
            super.fromNBT(tagCompound);
            direction = ForgeDirection.getOrientation(
                    tagCompound.getInteger("direction")
            );
        }

        public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
            if (direction == null) direction = ForgeDirection.UNKNOWN;
            tagCompound.setInteger("direction", direction.ordinal());
            return super.toNBT(tagCompound);
        }

        @Override
        public double getMaxRenderDistanceSquared() {
            return 16384.0D;
        }

    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new MagnetSwitch();
    }

    @Override
    public Material getMaterial() {
        return Material.rock;
    }

    public RailMagnetSwitch() {
        super("RailMagnetSwitch");
        setIconLocation("rail_magnet_switch");
        setLightOpacity(0);
        setLightLevel(0);
    }

    @Override
    protected void setBoundsByMeta(int meta) {
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack) {
        int meta = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        world.setBlockMetadataWithNotify(x, y, z, meta, 2);
        if (world.getTileEntity(x, y, z) instanceof MagnetSwitch) {
            MagnetSwitch monoSwitch = (MagnetSwitch) world.getTileEntity(x, y, z);
            switch (meta) {
                case 0:
                    monoSwitch.direction = ForgeDirection.SOUTH;
                    break;
                case 1:
                    monoSwitch.direction = ForgeDirection.WEST;
                    break;
                case 2:
                    monoSwitch.direction = ForgeDirection.NORTH;
                    break;
                case 3:
                    monoSwitch.direction = ForgeDirection.EAST;
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return side == ForgeDirection.UP;
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        world.scheduleBlockUpdate(x, y, z, this, 1);
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (!world.isRemote) {
            doSwitch(world, x, y, z);
        }
    }

    public void doSwitch(World world, int x ,int y, int z) {
        if (world.getTileEntity(x, y, z) instanceof MagnetSwitch) {
            MagnetSwitch magnetSwitch = (MagnetSwitch) world.getTileEntity(x, y, z);

            if (magnetSwitch.direction == null)
                magnetSwitch.direction = ForgeDirection.UNKNOWN;

            if (world.getBlock(x, y + 1, z) instanceof RailMonoMagnet) {
                int old = world.getBlockMetadata(x, y + 1, z);
                int meta = 0;

                switch (magnetSwitch.state) {
                    case MagnetSwitch.STATE_POS: //left
                        switch (magnetSwitch.direction) {
                            case SOUTH:
                                meta = 9;
                                break;
                            case WEST:
                                meta = 6;
                                break;
                            case NORTH:
                                meta = 7;
                                break;
                            case EAST:
                                meta = 8;
                                break;
                        }
                        break;
                    case MagnetSwitch.STATE_NEG: //right
                        switch (magnetSwitch.direction) {
                            case SOUTH:
                                meta = 8;
                                break;
                            case WEST:
                                meta = 9;
                                break;
                            case NORTH:
                                meta = 6;
                                break;
                            case EAST:
                                meta = 7;
                                break;
                        }
                        break;
                    case MagnetSwitch.STATE_ZERO:
                        switch (magnetSwitch.direction) {
                            case SOUTH:
                                meta = 0;
                                break;
                            case WEST:
                                meta = 1;
                                break;
                            case NORTH:
                                meta = 0;
                                break;
                            case EAST:
                                meta = 1;
                                break;
                        }
                        break;
                    default:
                        break;
                }

                magnetSwitch.state = MagnetSwitch.STATE_ZERO;

                if (old != meta) {
                    world.setBlockMetadataWithNotify(x, y + 1, z, meta, 3);
                    world.notifyBlockChange(x, y + 1, z, BlockLoader.railMonoMagnet);
                    world.markBlockForUpdate(x, y + 1, z);
                }
            }

            world.scheduleBlockUpdate(x, y, z, this, 1);
        }
    }

}
