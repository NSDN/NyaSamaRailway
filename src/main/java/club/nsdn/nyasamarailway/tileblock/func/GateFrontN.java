package club.nsdn.nyasamarailway.tileblock.func;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.tileblock.TileBlock;
import club.nsdn.nyasamarailway.tileblock.signal.deco.GateFront;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by drzzm32 on 2017.9.4.
 */
public class GateFrontN extends TileBlock {

    public static class TileEntityGateFrontN extends TileEntityBase {

        public EnumFacing direction;

        public static final int DELAY = 5;
        public int delay;

        public TileEntityGateFrontN() {
            setInfo(4, 0.5, 1.5, 0.5);
        }

        @Override
        protected void updateBounds() {
            setBoundsByXYZ(
                    0.5 - this.SIZE.x / 2, 0, 0.75 - this.SIZE.z / 2,
                    0.5 + this.SIZE.x / 2, this.SIZE.y, 0.75 + this.SIZE.z / 2
            );
        }

        @Nonnull
        @Override
        public AxisAlignedBB getRenderBoundingBox() {
            return super.getRenderBoundingBox().expand(4, 4, 4);
        }

        @Override
        public void fromNBT(NBTTagCompound tagCompound) {
            super.fromNBT(tagCompound);

            direction = EnumFacing.byName(
                    tagCompound.getString("direction")
            );
        }

        @Override
        public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
            if (direction == null) direction = EnumFacing.DOWN;
            tagCompound.setString("direction", direction.getName());

            return super.toNBT(tagCompound);
        }

        int __counter = 0;

        @Override
        public void updateSignal(World world, BlockPos pos) {
            __counter += 1;
            if (__counter >= 16777215) __counter = 0;
            if ((__counter % 2) == 0) return;

            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof TileEntityGateFrontN) {
                TileEntityGateFrontN gateFrontN = (TileEntityGateFrontN) tileEntity;

                doSniff(world, pos, gateFrontN);
            }
        }

        public void doSniff(World world, BlockPos pos, TileEntityGateFrontN gateFrontN) {
            EntityPlayer player;

            if (gateFrontN.direction == null)
                gateFrontN.direction = EnumFacing.DOWN;
            EnumFacing dir = gateFrontN.direction;

            BlockPos vec = new BlockPos(dir.getDirectionVec());
            player = findPlayer(world, pos.add(vec.rotate(Rotation.CLOCKWISE_90)));
            GateBase.TileEntityGateBase gateBase = getGateBase(world, pos.offset(dir));
            if (gateBase != null) {
                boolean delayed = false, playerOK = false;
                if (player != null) {
                    playerOK = gateBase.player.equals(player.getDisplayNameString());
                }

                if (gateBase.getDoorState() == GateDoor.TileEntityGateDoor.STATE_OPEN) {
                    gateFrontN.delay += 1;
                    if (gateFrontN.delay > TileEntityGateFrontN.DELAY * 20) {
                        delayed = true;
                    }
                } else {
                    gateFrontN.delay = 0;
                }

                if (delayed || playerOK) {
                    gateBase.player = "";
                    gateBase.closeDoor();
                    GateFront.TileEntityGateFront gateFront = getGateFront(world, pos.offset(dir, 2));
                    if (gateFront != null) {
                        gateFront.over = -1;
                        gateFront.refresh();
                    }
                }
            }
        }

        public EntityPlayer findPlayer(World world, BlockPos pos) {
            float bBoxSize = 0.125F;
            int x = pos.getX(), y = pos.getY(), z = pos.getZ();
            y += 1; //player's bounding box is higher than cart
            List bBox = world.getEntitiesWithinAABB(
                    EntityPlayer.class,
                    new AxisAlignedBB((double) ((float) x + bBoxSize),
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

        public GateBase.TileEntityGateBase getGateBase(World world, BlockPos pos) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof GateBase.TileEntityGateBase) {
                return (GateBase.TileEntityGateBase) tileEntity;
            }
            return null;
        }

        public GateFront.TileEntityGateFront getGateFront(World world, BlockPos pos) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof GateFront.TileEntityGateFront) {
                return (GateFront.TileEntityGateFront) tileEntity;
            }
            return null;
        }

    }

    public GateFrontN() {
        super("GateFrontN");
        setRegistryName(NyaSamaRailway.MODID, "gate_front_n");
        setLightOpacity(0);
        setLightLevel(0);
    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityGateFrontN();
    }

    @Override
    public boolean isSideSolid(IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, EnumFacing facing) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity == null) return false;
        if (tileEntity instanceof TileEntityBase) {
            int meta = ((TileEntityBase) tileEntity).META;
            EnumFacing dir = getDirFromMeta(meta);
            return dir.getOpposite() == facing;
        }
        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack itemStack) {
        int val = MathHelper.floor((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        setDeviceMeta(world, pos, val);
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityGateFrontN)
            ((TileEntityGateFrontN) tileEntity).direction = getDirFromMeta(val).getOpposite();
    }

    @Override
    @Nonnull
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, @Nonnull World world, @Nonnull BlockPos pos) {
        return super.getSelectedBoundingBox(state, world, pos).contract(0, 0.5, 0);
    }

}
