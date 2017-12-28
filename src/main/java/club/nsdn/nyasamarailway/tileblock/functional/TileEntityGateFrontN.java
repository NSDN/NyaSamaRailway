package club.nsdn.nyasamarailway.tileblock.functional;

import club.nsdn.nyasamarailway.tileblock.TileBlock;
import club.nsdn.nyasamarailway.tileblock.signal.block.BlockGateFront;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
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

import java.util.List;
import java.util.Random;

/**
 * Created by drzzm32 on 2017.9.4.
 */
public class TileEntityGateFrontN extends TileBlock {

    public static class GateFrontN extends TileEntity {

        public ForgeDirection direction;

        public static final int DELAY = 5;
        public int delay;

        @Override
        @SideOnly(Side.CLIENT)
        public AxisAlignedBB getRenderBoundingBox()
        {
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

    public TileEntityGateFrontN() {
        super("GateFrontN");
        setIconLocation("gate_front_n");
        setLightOpacity(0);
        setLightLevel(0);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new GateFrontN();
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side)
    {
        return side == ForgeDirection.UP;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack) {
        int meta = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        if (world.getTileEntity(x, y, z) instanceof GateFrontN) {
            GateFrontN gateFrontN = (GateFrontN) world.getTileEntity(x, y, z);
            switch (meta) {
                case 0:
                    gateFrontN.direction = ForgeDirection.SOUTH;
                    break;
                case 1:
                    gateFrontN.direction = ForgeDirection.WEST;
                    break;
                case 2:
                    gateFrontN.direction = ForgeDirection.NORTH;
                    break;
                case 3:
                    gateFrontN.direction = ForgeDirection.EAST;
                    break;
                default:
                    break;
            }
            world.setBlockMetadataWithNotify(x, y, z, meta, 2);
        }
    }

    @Override
    protected void setBoundsByMeta(int meta) {
        float x1 = 0.25F, y1 = 0.0F, z1 = 0.5F, x2 = 0.75F, y2 = 1.5F, z2 = 1.0F;
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

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        float x1 = 0.25F, y1 = 0.0F, z1 = 0.5F, x2 = 0.75F, y2 = 1.0F, z2 = 1.0F;
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
        return 10;
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (!world.isRemote) {
            if (world.getTileEntity(x, y, z) instanceof GateFrontN) {
                GateFrontN gateFrontN = (GateFrontN) world.getTileEntity(x, y, z);

                doSniff(world, x, y, z, gateFrontN);

                world.scheduleBlockUpdate(x, y, z, this, 1);
            }
        }
    }

    public void doSniff(World world, int x, int y, int z, GateFrontN gateFrontN) {
        EntityPlayer player;

        if (gateFrontN.direction == null)
            gateFrontN.direction = ForgeDirection.UNKNOWN;

        switch (gateFrontN.direction) {
            case SOUTH:
                player = findPlayer(world, x - 1, y , z);
                if (getGateBase(world, x, y, z + 1) != null) {
                    boolean delayed = false, playerOK = false;
                    if (player != null) {
                        playerOK = getGateBase(world, x, y, z + 1).player.equals(player.getDisplayName());
                    }

                    if (getGateBase(world, x, y, z + 1).getDoorState() == TileEntityGateDoor.GateDoor.STATE_OPEN) {
                        gateFrontN.delay += 1;
                        if (gateFrontN.delay > GateFrontN.DELAY * 20) {
                            delayed = true;
                        }
                    } else {
                        gateFrontN.delay = 0;
                    }

                    if (delayed || playerOK) {
                        getGateBase(world, x, y, z + 1).player = "";
                        getGateBase(world, x, y, z + 1).closeDoor();
                        if (getGateFront(world, x, y, z + 2) != null) {
                            getGateFront(world, x, y, z + 2).over = -1;
                            world.markBlockForUpdate(x, y, z + 2);
                        }
                    }
                }
                break;
            case WEST:
                player = findPlayer(world, x, y , z - 1);
                if (getGateBase(world, x - 1, y, z) != null) {
                    boolean delayed = false, playerOK = false;
                    if (player != null) {
                        playerOK = getGateBase(world, x - 1, y, z).player.equals(player.getDisplayName());
                    }

                    if (getGateBase(world, x - 1, y, z).getDoorState() == TileEntityGateDoor.GateDoor.STATE_OPEN) {
                        gateFrontN.delay += 1;
                        if (gateFrontN.delay > GateFrontN.DELAY * 20) {
                            delayed = true;
                        }
                    } else {
                        gateFrontN.delay = 0;
                    }

                    if (delayed || playerOK) {
                        getGateBase(world, x - 1, y, z).player = "";
                        getGateBase(world, x - 1, y, z).closeDoor();
                        if (getGateFront(world, x - 2, y, z) != null) {
                            getGateFront(world, x - 2, y, z).over = -1;
                            world.markBlockForUpdate(x - 2, y, z);
                        }
                    }
                }
                break;
            case NORTH:
                player = findPlayer(world, x + 1, y , z);
                if (getGateBase(world, x, y, z - 1) != null) {
                    boolean delayed = false, playerOK = false;
                    if (player != null) {
                        playerOK = getGateBase(world, x, y, z - 1).player.equals(player.getDisplayName());
                    }

                    if (getGateBase(world, x, y, z - 1).getDoorState() == TileEntityGateDoor.GateDoor.STATE_OPEN) {
                        gateFrontN.delay += 1;
                        if (gateFrontN.delay > GateFrontN.DELAY * 20) {
                            delayed = true;
                        }
                    } else {
                        gateFrontN.delay = 0;
                    }

                    if (delayed || playerOK) {
                        getGateBase(world, x, y, z - 1).player = "";
                        getGateBase(world, x, y, z - 1).closeDoor();
                        if (getGateFront(world, x, y, z - 2) != null) {
                            getGateFront(world, x, y, z - 2).over = -1;
                            world.markBlockForUpdate(x, y, z - 2);
                        }
                    }
                }
                break;
            case EAST:
                player = findPlayer(world, x, y , z + 1);
                if (getGateBase(world, x + 1, y, z) != null) {
                    boolean delayed = false, playerOK = false;
                    if (player != null) {
                        playerOK = getGateBase(world, x + 1, y, z).player.equals(player.getDisplayName());
                    }

                    if (getGateBase(world, x + 1, y, z).getDoorState() == TileEntityGateDoor.GateDoor.STATE_OPEN) {
                        gateFrontN.delay += 1;
                        if (gateFrontN.delay > GateFrontN.DELAY * 20) {
                            delayed = true;
                        }
                    } else {
                        gateFrontN.delay = 0;
                    }

                    if (delayed || playerOK) {
                        getGateBase(world, x + 1, y, z).player = "";
                        getGateBase(world, x + 1, y, z).closeDoor();
                        if (getGateFront(world, x + 2, y, z) != null) {
                            getGateFront(world, x + 2, y, z).over = -1;
                            world.markBlockForUpdate(x + 2, y, z);
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    public EntityPlayer findPlayer(World world, int x, int y, int z) {
        float bBoxSize = 0.125F;
        y += 1; //player's bounding box is higher than cart
        List bBox = world.getEntitiesWithinAABB(
                EntityPlayer.class,
                AxisAlignedBB.getBoundingBox((double) ((float) x + bBoxSize),
                        (double) y,
                        (double) ((float) z + bBoxSize),
                        (double) ((float) (x + 1) - bBoxSize),
                        (double) ((float) (y + 1) - bBoxSize),
                        (double) ((float) (z + 1) - bBoxSize))
        );
        if (!bBox.isEmpty()) {
            if (bBox.size() > 1) return null;
            return (EntityPlayer) bBox.get(0);
        }
        return null;
    }

    public TileEntityGateBase.GateBase getGateBase(World world, int x, int y, int z) {
        if (world.getTileEntity(x, y ,z) instanceof TileEntityGateBase.GateBase) {
            return (TileEntityGateBase.GateBase) world.getTileEntity(x, y, z);
        }
        return null;
    }

    public BlockGateFront.GateFront getGateFront(World world, int x, int y, int z) {
        if (world.getTileEntity(x, y ,z) instanceof BlockGateFront.GateFront) {
            return (BlockGateFront.GateFront) world.getTileEntity(x, y, z);
        }
        return null;
    }

}
