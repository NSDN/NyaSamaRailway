package club.nsdn.nyasamarailway.tileblock.signal.deco;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.tileblock.TileBlock;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class GlassShield extends TileBlock {

    public static class TileEntityGlassShield extends club.nsdn.nyasamarailway.api.signal.TileEntityGlassShield {

        public TileEntityGlassShield() {
            setInfo(4, 1, 2, 0.125);
        }

        @Override
        protected void updateBounds() {
            double x1 = 0.5 - this.SIZE.x / 2;
            if ((META & 0x8) != 0) x1 = 0.875F;
            setBoundsByXYZ(
                    x1, 0, 0.5 - this.SIZE.z / 2,
                    0.5 + this.SIZE.x / 2, this.SIZE.y, 0.5 + this.SIZE.z / 2
            );
        }

        @Override
        public void updateSignal(World world, BlockPos pos) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity == null) return;
            if (tileEntity instanceof TileEntityGlassShield) {
                TileEntityGlassShield glassShield = (TileEntityGlassShield) tileEntity;
                TileEntityGlassShield nearByShield = getNearbyShield(world, pos);

                boolean control;

                if (glassShield.getSender() == null) {
                    control = hasPlayer(world, pos);
                } else {
                    control = glassShield.senderIsPowered();
                }

                if (control) {
                    if (glassShield.state == GlassShield.TileEntityGlassShield.STATE_CLOSE) {
                        glassShield.state = GlassShield.TileEntityGlassShield.STATE_OPENING;
                        if (nearByShield != null) {
                            nearByShield.state = GlassShield.TileEntityGlassShield.STATE_OPENING;
                        }
                    }
                } else {
                    if (glassShield.state == GlassShield.TileEntityGlassShield.STATE_OPEN) {
                        if (glassShield.delay < GlassShield.TileEntityGlassShield.DELAY * 20 &&
                                glassShield.getSender() == null
                        ) glassShield.delay += 1;
                        else {
                            glassShield.state = GlassShield.TileEntityGlassShield.STATE_CLOSING;
                            if (nearByShield != null) {
                                nearByShield.state = GlassShield.TileEntityGlassShield.STATE_CLOSING;
                            }
                        }
                    } else {
                        glassShield.delay = 0;
                    }
                }

                switch (glassShield.state) {
                    case GlassShield.TileEntityGlassShield.STATE_CLOSE:
                        if ((glassShield.META & 0x8) != 0) {
                            glassShield.META = glassShield.META & 0x7;
                            glassShield.refresh();
                        }
                        break;
                    case GlassShield.TileEntityGlassShield.STATE_CLOSING:
                        if (glassShield.progress > 0) glassShield.progress -= 1;
                        else {
                            glassShield.state = GlassShield.TileEntityGlassShield.STATE_CLOSE;
                            glassShield.META = glassShield.META & 0x7;
                            glassShield.refresh();
                        }
                        break;
                    case GlassShield.TileEntityGlassShield.STATE_OPEN:
                        if ((glassShield.META & 0x8) == 0) {
                            glassShield.META = glassShield.META | 0x8;
                            glassShield.refresh();
                        }
                        break;
                    case GlassShield.TileEntityGlassShield.STATE_OPENING:
                        if (glassShield.progress < GlassShield.TileEntityGlassShield.PROGRESS_MAX) glassShield.progress += 1;
                        else {
                            glassShield.state = GlassShield.TileEntityGlassShield.STATE_OPEN;
                            glassShield.META = glassShield.META | 0x8;
                            glassShield.refresh();
                        }
                        break;
                    default:
                        break;
                }

                if (glassShield.state == GlassShield.TileEntityGlassShield.STATE_OPENING || glassShield.state == GlassShield.TileEntityGlassShield.STATE_CLOSING) {
                    glassShield.refresh();
                }
            }
        }

        public boolean hasPlayer(World world, BlockPos pos) {
            float bBoxSize = 1.0F;
            int x = pos.getX(), y = pos.getY(), z = pos.getZ();
            AxisAlignedBB aabb = new AxisAlignedBB(
                    (double) x, (double) y, (double) z,
                    (double) (x + 1), (double) (y + 2), (double) (z + 1)
            );
            aabb = aabb.expand(bBoxSize, 0, bBoxSize);
            aabb = aabb.expand(-bBoxSize, 0, -bBoxSize);
            List bBox = world.getEntitiesWithinAABB(
                    EntityPlayer.class, aabb
            );
            return !bBox.isEmpty();
        }

        public TileEntityGlassShield getNearbyShield(World world, BlockPos pos) {
            if (world.getTileEntity(pos.east()) instanceof TileEntityGlassShield)
                return (TileEntityGlassShield) world.getTileEntity(pos.east());
            else if (world.getTileEntity(pos.east(2)) instanceof TileEntityGlassShield)
                return (TileEntityGlassShield) world.getTileEntity(pos.east(2));

            if (world.getTileEntity(pos.west()) instanceof TileEntityGlassShield)
                return (TileEntityGlassShield) world.getTileEntity(pos.west());
            else if (world.getTileEntity(pos.west(2)) instanceof TileEntityGlassShield)
                return (TileEntityGlassShield) world.getTileEntity(pos.west(2));

            if (world.getTileEntity(pos.north()) instanceof TileEntityGlassShield)
                return (TileEntityGlassShield) world.getTileEntity(pos.north());
            else if (world.getTileEntity(pos.north(2)) instanceof TileEntityGlassShield)
                return (TileEntityGlassShield) world.getTileEntity(pos.north(2));

            if (world.getTileEntity(pos.south()) instanceof TileEntityGlassShield)
                return (TileEntityGlassShield) world.getTileEntity(pos.south());
            else if (world.getTileEntity(pos.south(2)) instanceof TileEntityGlassShield)
                return (TileEntityGlassShield) world.getTileEntity(pos.south(2));

            return null;
        }

    }

    public GlassShield() {
        super("GlassShield");
        setRegistryName(NyaSamaRailway.MODID, "glass_shield");
        setLightOpacity(1);
        setLightLevel(0);
    }

    public GlassShield(String name, String id) {
        super(name);
        setRegistryName(NyaSamaRailway.MODID, id);
        setLightOpacity(1);
        setLightLevel(0);
    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityGlassShield();
    }

    @Override
    public boolean isSideSolid(IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, EnumFacing facing) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity == null) return false;
        if (tileEntity instanceof TileEntityBase) {
            int meta = ((TileEntityBase) tileEntity).META;
            EnumFacing dir = getDirFromMeta(meta);
            if (facing.getAxis() == EnumFacing.Axis.Y) return facing == EnumFacing.DOWN;
            return dir == facing.rotateY() || dir == facing.rotateY().getOpposite();
        }
        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack itemStack) {
        int val = MathHelper.floor((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        setDeviceMeta(world, pos, val);
    }

    @Override
    public boolean canBeConnectedTo(IBlockAccess world, BlockPos pos, EnumFacing facing) {
        return isSideSolid(world.getBlockState(pos), world, pos, facing);
    }

}
