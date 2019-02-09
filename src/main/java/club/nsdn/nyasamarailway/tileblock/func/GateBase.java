package club.nsdn.nyasamarailway.tileblock.func;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.tileblock.TileBlock;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2017.9.4.
 */
public class GateBase extends TileBlock {

    public static class TileEntityGateBase extends TileEntityBase {

        public String player = "";
        public EnumFacing direction;

        public boolean isDoor(int dx, int dz) {
            return world.getTileEntity(
                    getPos().add(dx, 0, dz)
            ) instanceof GateDoor.TileEntityGateDoor;
        }

        public int getDoorState(int dx, int dz) {
            TileEntity tileEntity = world.getTileEntity(getPos().add(dx, 0, dz));
            if (tileEntity == null) return -1;
            if (tileEntity instanceof GateDoor.TileEntityGateDoor)
                return ((GateDoor.TileEntityGateDoor) tileEntity).state;
            return 0;
        }

        public int getDoorState() {
            switch (direction) {
                case SOUTH:
                    if (isDoor(1, 0)) {
                        return getDoorState(1, 0);
                    }
                    break;
                case WEST:
                    if (isDoor(0, 1)) {
                        return getDoorState(0, 1);
                    }
                    break;
                case NORTH:
                    if (isDoor(-1, 0)) {
                        return getDoorState(-1, 0);
                    }
                    break;
                case EAST:
                    if (isDoor(0, -1)) {
                        return getDoorState(0, -1);
                    }
                    break;
                default:
                    break;
            }
            return GateDoor.TileEntityGateDoor.STATE_CLOSE;
        }

        public void setDoorState(int dx, int dz, int state) {
            TileEntity tileEntity = world.getTileEntity(getPos().add(dx, 0, dz));
            if (tileEntity == null) return;
            if (tileEntity instanceof GateDoor.TileEntityGateDoor)
                ((GateDoor.TileEntityGateDoor) tileEntity).state = state;
        }

        public void openDoor() {
            if (direction == null)
                direction = EnumFacing.DOWN;

            switch (direction) {
                case SOUTH:
                    if (isDoor(1, 0)) {
                        if (getDoorState(1, 0) == GateDoor.TileEntityGateDoor.STATE_CLOSE) {
                            setDoorState(1, 0, GateDoor.TileEntityGateDoor.STATE_OPENING);
                        }
                    }
                    break;
                case WEST:
                    if (isDoor(0, 1)) {
                        if (getDoorState(0, 1) == GateDoor.TileEntityGateDoor.STATE_CLOSE) {
                            setDoorState(0, 1, GateDoor.TileEntityGateDoor.STATE_OPENING);
                        }
                    }
                    break;
                case NORTH:
                    if (isDoor(-1, 0)) {
                        if (getDoorState(-1, 0) == GateDoor.TileEntityGateDoor.STATE_CLOSE) {
                            setDoorState(-1, 0, GateDoor.TileEntityGateDoor.STATE_OPENING);
                        }
                    }
                    break;
                case EAST:
                    if (isDoor(0, -1)) {
                        if (getDoorState(0, -1) == GateDoor.TileEntityGateDoor.STATE_CLOSE) {
                            setDoorState(0, -1, GateDoor.TileEntityGateDoor.STATE_OPENING);
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        public void closeDoor() {
            if (direction == null)
                direction = EnumFacing.DOWN;

            switch (direction) {
                case SOUTH:
                    if (isDoor(1, 0)) {
                        if (getDoorState(1, 0) == GateDoor.TileEntityGateDoor.STATE_OPEN) {
                            setDoorState(1, 0, GateDoor.TileEntityGateDoor.STATE_CLOSING);
                        }
                    }
                    break;
                case WEST:
                    if (isDoor(0, 1)) {
                        if (getDoorState(0, 1) == GateDoor.TileEntityGateDoor.STATE_OPEN) {
                            setDoorState(0, 1, GateDoor.TileEntityGateDoor.STATE_CLOSING);
                        }
                    }
                    break;
                case NORTH:
                    if (isDoor(-1, 0)) {
                        if (getDoorState(-1, 0) == GateDoor.TileEntityGateDoor.STATE_OPEN) {
                            setDoorState(-1, 0, GateDoor.TileEntityGateDoor.STATE_CLOSING);
                        }
                    }
                    break;
                case EAST:
                    if (isDoor(0, -1)) {
                        if (getDoorState(0, -1) == GateDoor.TileEntityGateDoor.STATE_OPEN) {
                            setDoorState(0, -1, GateDoor.TileEntityGateDoor.STATE_CLOSING);
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        public TileEntityGateBase() {
            setInfo(4, 0.5, 1.5, 1);
        }

        @Nonnull
        @Override
        public AxisAlignedBB getRenderBoundingBox() {
            return super.getRenderBoundingBox().expand(4, 4, 4);
        }

        @Override
        public void fromNBT(NBTTagCompound tagCompound) {
            super.fromNBT(tagCompound);

            player = tagCompound.getString("player");
            direction = EnumFacing.byName(
                    tagCompound.getString("direction")
            );
        }

        @Override
        public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
            tagCompound.setString("player", player);
            if (direction == null) direction = EnumFacing.DOWN;
            tagCompound.setString("direction", direction.getName());

            return super.toNBT(tagCompound);
        }

    }

    public GateBase() {
        super("GateBase");
        setRegistryName(NyaSamaRailway.MODID, "gate_base");
        setLightOpacity(0);
        setLightLevel(0);
    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityGateBase();
    }

    @Override
    public boolean isSideSolid(IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, EnumFacing facing) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity == null) return false;
        if (tileEntity instanceof TileEntityBase) {
            int meta = ((TileEntityBase) tileEntity).META;
            EnumFacing dir = getDirFromMeta(meta);
            if (facing.getAxis() == EnumFacing.Axis.Y) return facing == EnumFacing.DOWN;
            return dir != facing.rotateY() && dir != facing.rotateY().getOpposite() && facing != EnumFacing.UP;
        }
        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack itemStack) {
        int val = MathHelper.floor((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        setDeviceMeta(world, pos, val);
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityGateBase)
            ((TileEntityGateBase) tileEntity).direction = getDirFromMeta(val).getOpposite();
    }

    @Override
    @Nonnull
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, @Nonnull World world, @Nonnull BlockPos pos) {
        return super.getSelectedBoundingBox(state, world, pos).contract(0, 0.5, 0);
    }

}
