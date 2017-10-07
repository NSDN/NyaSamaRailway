package club.nsdn.nyasamarailway.TileEntities;

import club.nsdn.nyasamarailway.Blocks.BlockRailBase;
import club.nsdn.nyasamarailway.CreativeTab.CreativeTabLoader;
import club.nsdn.nyasamarailway.TileEntities.Signals.TileEntityRailTriStateReceiver;
import club.nsdn.nyasamarailway.Util.Util;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

/**
 * Created by drzzm32 on 2017.10.1.
 */
public class TileEntityRailTriSwitch extends BlockRailBase implements ITileEntityProvider {

    public static class TriSwitch extends TileEntityRailTriStateReceiver {

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
        return new TriSwitch();
    }

    @Override
    public Material getMaterial() {
        return Material.rock;
    }

    public TileEntityRailTriSwitch() {
        super("RailTriSwitch");
        setTextureName("rail_tri_switch_straight");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
        if (!Util.loadIf()) setCreativeTab(null);
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
        if (world.getTileEntity(x, y, z) instanceof TriSwitch) {
            TriSwitch triSwitch = (TriSwitch) world.getTileEntity(x, y, z);
            switch (meta) {
                case 0:
                    triSwitch.direction = ForgeDirection.SOUTH;
                    break;
                case 1:
                    triSwitch.direction = ForgeDirection.WEST;
                    break;
                case 2:
                    triSwitch.direction = ForgeDirection.NORTH;
                    break;
                case 3:
                    triSwitch.direction = ForgeDirection.EAST;
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
        if (world.getTileEntity(x, y, z) instanceof TriSwitch) {
            TriSwitch triSwitch = (TriSwitch) world.getTileEntity(x, y, z);
            int old = world.getBlockMetadata(x, y, z);
            int meta = 0;

            switch (triSwitch.state) {
                case TriSwitch.STATE_POS: //left
                    switch (triSwitch.direction) {
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
                case TriSwitch.STATE_NEG: //right
                    switch (triSwitch.direction) {
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
                case TriSwitch.STATE_ZERO:
                    switch (triSwitch.direction) {
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

            triSwitch.prevState = triSwitch.state;
            triSwitch.state = TriSwitch.STATE_ZERO;

            if (old != meta) {
                world.setBlockMetadataWithNotify(x, y, z, meta, 3);
                world.notifyBlockChange(x, y, z, this);
                world.markBlockForUpdate(x, y, z);
            }
            world.scheduleBlockUpdate(x, y, z, this, 1);
        }
    }
}
