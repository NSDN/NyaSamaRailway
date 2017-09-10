package club.nsdn.nyasamarailway.TileEntities.Rail;

import club.nsdn.nyasamarailway.Blocks.BlockLoader;
import club.nsdn.nyasamarailway.CreativeTab.CreativeTabLoader;
import club.nsdn.nyasamarailway.TileEntities.TileEntityRailReceiver;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
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
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

/**
 * Created by drzzm32 on 2016.11.29.
 */
public class RailMonoSwitch extends RailBase {

    public static class MonoSwitch extends TileEntityRailReceiver {

        public static final int STATE_POS = 1;
        public static final int STATE_ZERO = 0;
        public static final int STATE_NEG = -1;

        public int switchState;
        public ForgeDirection direction;

        public void setStatePos() {
            switchState = switchState == STATE_NEG ? STATE_ZERO : STATE_POS;
        }

        public void setStateNeg() {
            switchState = switchState == STATE_POS ? STATE_ZERO : STATE_NEG;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public AxisAlignedBB getRenderBoundingBox()
        {
            return AxisAlignedBB
                    .getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1)
                    .expand(4, 4, 4);
        }

        public void fromNBT(NBTTagCompound tagCompound) {
            switchState = tagCompound.getInteger("switchState");
            direction = ForgeDirection.getOrientation(
                    tagCompound.getInteger("direction")
            );
        }

        public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
            tagCompound.setInteger("switchState", switchState);
            tagCompound.setInteger("direction", direction.ordinal());
            return tagCompound;
        }

        @Override
        public double getMaxRenderDistanceSquared() {
            return 16384.0D;
        }

    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new MonoSwitch();
    }

    @Override
    public Material getMaterial() {
        return Material.rock;
    }

    public RailMonoSwitch() {
        super(false);
        setBlockName("RailMonoSwitch");
        setIconLocation("rail_mono_switch");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Override
    protected void setBoundsByMeta(int meta) {
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
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
    protected void func_150052_a(World world, int x, int y, int z, boolean control)
    {
        if (!world.isRemote)
        {
            (new Rail(world, x, y, z)).func_150655_a(world.isBlockIndirectlyGettingPowered(x, y, z), control);
        }
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack) {
        int meta = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        if (world.getTileEntity(x, y, z) instanceof MonoSwitch) {
            MonoSwitch monoSwitch = (MonoSwitch) world.getTileEntity(x, y, z);
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
        if (world.getTileEntity(x, y, z) instanceof MonoSwitch) {
            MonoSwitch monoSwitch = (MonoSwitch) world.getTileEntity(x, y, z);
            int old = world.getBlockMetadata(x, y, z);
            int meta = 0;

            switch (monoSwitch.switchState) {
                case MonoSwitch.STATE_POS: //left
                    switch (monoSwitch.direction) {
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
                case MonoSwitch.STATE_NEG: //right
                    switch (monoSwitch.direction) {
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
                case MonoSwitch.STATE_ZERO:
                    switch (monoSwitch.direction) {
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

            monoSwitch.switchState = MonoSwitch.STATE_ZERO;

            if (world.getBlock(x, y + 1, z) instanceof RailMonoMagnet) {
                if (world.getBlockMetadata(x, y + 1, z) != meta) {
                    world.setBlockMetadataWithNotify(x, y + 1, z, meta, 3);
                    world.notifyBlockChange(x, y + 1, z, BlockLoader.railMonoMagnet);
                    world.markBlockForUpdate(x, y + 1, z);
                }
            }

            if (old != meta) {
                world.setBlockMetadataWithNotify(x, y, z, meta, 3);
                world.notifyBlockChange(x, y, z, BlockLoader.railMono);
                world.markBlockForUpdate(x, y, z);
            }
            world.scheduleBlockUpdate(x, y, z, this, 1);
        }
    }

    public class Rail extends RailBase.Rail {

        @Override
        public boolean checkBlockIsMe(World world, int x, int y, int z) {
            return world.getBlock(x, y, z).getClass() == RailMonoSwitch.class;
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
