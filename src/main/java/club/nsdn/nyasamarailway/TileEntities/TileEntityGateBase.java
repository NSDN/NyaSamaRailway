package club.nsdn.nyasamarailway.TileEntities;

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

/**
 * Created by drzzm32 on 2017.9.4.
 */
public class TileEntityGateBase extends TileEntityBase {

    public static class GateBase extends TileEntity {

        public String player = "";
        public ForgeDirection direction;

        public boolean isDoor(int dx, int dz) {
            return worldObj.getTileEntity(
                    xCoord + dx, yCoord, zCoord + dz
            ) instanceof TileEntityGateDoor.GateDoor;
        }

        public int getDoorState(int dx, int dz) {
            return ((TileEntityGateDoor.GateDoor) (worldObj.getTileEntity(
                    xCoord + dx, yCoord, zCoord + dz
            ))).state;
        }

        public void setDoorState(int dx, int dz, int state) {
            ((TileEntityGateDoor.GateDoor) (worldObj.getTileEntity(
            xCoord + dx, yCoord, zCoord + dz
            ))).state = state;
        }

        public void openDoor() {
            switch (direction) {
                case SOUTH:
                    if (isDoor(1, 0)) {
                        if (getDoorState(1, 0) == TileEntityGateDoor.GateDoor.STATE_CLOSE) {
                            setDoorState(1, 0, TileEntityGateDoor.GateDoor.STATE_OPENING);
                        }
                    }
                    break;
                case WEST:
                    if (isDoor(0, 1)) {
                        if (getDoorState(0, 1) == TileEntityGateDoor.GateDoor.STATE_CLOSE) {
                            setDoorState(0, 1, TileEntityGateDoor.GateDoor.STATE_OPENING);
                        }
                    }
                    break;
                case NORTH:
                    if (isDoor(-1, 0)) {
                        if (getDoorState(-1, 0) == TileEntityGateDoor.GateDoor.STATE_CLOSE) {
                            setDoorState(-1, 0, TileEntityGateDoor.GateDoor.STATE_OPENING);
                        }
                    }
                    break;
                case EAST:
                    if (isDoor(0, -1)) {
                        if (getDoorState(0, -1) == TileEntityGateDoor.GateDoor.STATE_CLOSE) {
                            setDoorState(0, -1, TileEntityGateDoor.GateDoor.STATE_OPENING);
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        public void closeDoor() {
            switch (direction) {
                case SOUTH:
                    if (isDoor(1, 0)) {
                        if (getDoorState(1, 0) == TileEntityGateDoor.GateDoor.STATE_OPEN) {
                            setDoorState(1, 0, TileEntityGateDoor.GateDoor.STATE_CLOSING);
                        }
                    }
                    break;
                case WEST:
                    if (isDoor(0, 1)) {
                        if (getDoorState(0, 1) == TileEntityGateDoor.GateDoor.STATE_OPEN) {
                            setDoorState(0, 1, TileEntityGateDoor.GateDoor.STATE_CLOSING);
                        }
                    }
                    break;
                case NORTH:
                    if (isDoor(-1, 0)) {
                        if (getDoorState(-1, 0) == TileEntityGateDoor.GateDoor.STATE_OPEN) {
                            setDoorState(-1, 0, TileEntityGateDoor.GateDoor.STATE_CLOSING);
                        }
                    }
                    break;
                case EAST:
                    if (isDoor(0, -1)) {
                        if (getDoorState(0, -1) == TileEntityGateDoor.GateDoor.STATE_OPEN) {
                            setDoorState(0, -1, TileEntityGateDoor.GateDoor.STATE_CLOSING);
                        }
                    }
                    break;
                default:
                    break;
            }
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
            player = tagCompound.getString("player");
            direction = ForgeDirection.getOrientation(
                    tagCompound.getInteger("direction")
            );
        }

        public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
            tagCompound.setString("player", player);
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

    public TileEntityGateBase() {
        super("GateBase");
        setIconLocation("gate_base");
        setLightOpacity(0);
        setLightLevel(0);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new GateBase();
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side)
    {
        return side == ForgeDirection.UP;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack) {
        int meta = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        if (world.getTileEntity(x, y, z) instanceof GateBase) {
            GateBase gateBase = (GateBase) world.getTileEntity(x, y, z);
            switch (meta) {
                case 0:
                    gateBase.direction = ForgeDirection.SOUTH;
                    break;
                case 1:
                    gateBase.direction = ForgeDirection.WEST;
                    break;
                case 2:
                    gateBase.direction = ForgeDirection.NORTH;
                    break;
                case 3:
                    gateBase.direction = ForgeDirection.EAST;
                    break;
                default:
                    break;
            }
            world.setBlockMetadataWithNotify(x, y, z, meta, 2);

        }
    }

    @Override
    protected void setBoundsByMeta(int meta) {
        float x1 = 0.25F, y1 = 0.0F, z1 = 0.0F, x2 = 0.75F, y2 = 1.5F, z2 = 1.0F;
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
        float x1 = 0.25F, y1 = 0.0F, z1 = 0.0F, x2 = 0.75F, y2 = 1.0F, z2 = 1.0F;
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

}
