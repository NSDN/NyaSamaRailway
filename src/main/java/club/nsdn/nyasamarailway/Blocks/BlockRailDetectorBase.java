package club.nsdn.nyasamarailway.Blocks;

import club.nsdn.nyasamarailway.CreativeTab.CreativeTabLoader;
import net.minecraft.block.BlockRailDetector;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import org.thewdj.physics.Point3D;

import java.util.*;

/**
 * Created by drzzm32 on 2016.5.5.
 */
public class BlockRailDetectorBase extends BlockRailDetector implements ITileEntityProvider {

    public final int delaySecond;
    public static LinkedHashMap<Point3D, Integer> tmpDelay;

    public TileEntity createNewTileEntity(World world, int i) {
        return new TileEntityRailReceiver();
    }

    public BlockRailDetectorBase(String name) {
        super();
        delaySecond = 0;
        tmpDelay = new LinkedHashMap<Point3D, Integer>();
        setBlockName(name);
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    public BlockRailDetectorBase(String name, int delay) {
        super();
        delaySecond = delay;
        tmpDelay = new LinkedHashMap<Point3D, Integer>();
        setBlockName(name);
        setCreativeTab(null);
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

    protected void setTextureName(String name) {
        setBlockTextureName("nyasamarailway" + ":" + name);
    }

    @Override
    public float getRailMaxSpeed(World world, EntityMinecart cart, int x, int y, int z) {
        return 1.0F;
    }

    @Override
    public int tickRate(World world) {
        return 20;
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        if (!world.isRemote) {
            int meta = world.getBlockMetadata(x, y, z);
            if ((meta & 8) == 0) {
                this.setRailOutput(world, x, y, z, meta);
            }
        }
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        this.setRailOutput(world, x, y, z, world.getBlockMetadata(x, y, z));
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (!world.isRemote) {
            int meta = world.getBlockMetadata(x, y, z);
            if ((meta & 8) != 0) {
                this.setRailOutput(world, x, y, z, meta);
            }
        }
    }

    public int getDelaySecond() {
        return delaySecond;
    }

    public void setRailOutput(World world, int x, int y, int z, int meta) {
        boolean isPowered = (meta & 8) != 0;
        boolean hasCart = false;
        boolean isEnabled = false;
        float bBoxSize = 0.125F;
        List bBox = world.getEntitiesWithinAABB(EntityMinecart.class, AxisAlignedBB.getBoundingBox((double) ((float) x + bBoxSize), (double) y, (double) ((float) z + bBoxSize), (double) ((float) (x + 1) - bBoxSize), (double) ((float) (y + 1) - bBoxSize), (double) ((float) (z + 1) - bBoxSize)));
        if (!bBox.isEmpty()) {
            hasCart = true;
        }

        if (hasCart && !isPowered) {
            if (delaySecond != 0) {
                Point3D p = new Point3D(x, y, z);
                if (!tmpDelay.containsKey(p)) {
                    tmpDelay.put(p, 0);
                }
                if (tmpDelay.get(p) < delaySecond * 20)
                    tmpDelay.put(p, tmpDelay.get(p) + 1);
                else {
                    if (getRailDirection(world, x, y, z) == RailDirection.NS) {
                        if (world.isBlockIndirectlyGettingPowered(x - 1, y, z) || world.isBlockIndirectlyGettingPowered(x + 1, y, z) ||
                                world.isBlockIndirectlyGettingPowered(x - 1, y - 1, z) || world.isBlockIndirectlyGettingPowered(x + 1, y - 1, z)) {
                            isEnabled = true;
                        }
                    } else {
                        if (world.isBlockIndirectlyGettingPowered(x, y, z - 1) || world.isBlockIndirectlyGettingPowered(x, y, z + 1) ||
                            world.isBlockIndirectlyGettingPowered(x, y - 1, z - 1) || world.isBlockIndirectlyGettingPowered(x, y - 1, z + 1)) {
                                isEnabled = true;
                        }
                    }
                    if (world.getTileEntity(x, y, z) instanceof TileEntityRailReceiver) {
                        TileEntityRailReceiver railReceiver = (TileEntityRailReceiver) world.getTileEntity(x, y, z);
                        if (railReceiver.senderRailIsPowered()) isEnabled = true;
                    }

                    if (!isEnabled) {
                        world.setBlockMetadataWithNotify(x, y, z, meta | 8, 3);
                        world.notifyBlocksOfNeighborChange(x, y, z, this);
                        world.notifyBlocksOfNeighborChange(x, y - 1, z, this);
                        world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
                        tmpDelay.put(p, 0);
                    }
                }
            } else {
                world.setBlockMetadataWithNotify(x, y, z, meta | 8, 3);
                world.notifyBlocksOfNeighborChange(x, y, z, this);
                world.notifyBlocksOfNeighborChange(x, y - 1, z, this);
                world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
            }
        } else if (!hasCart && !isPowered && delaySecond != 0) {
            if (tmpDelay.containsKey(new Point3D(x, y, z)))
                tmpDelay.put(new Point3D(x, y, z), 0);
        }

        if (!hasCart && isPowered) {
            world.setBlockMetadataWithNotify(x, y, z, meta & 7, 3);
            world.notifyBlocksOfNeighborChange(x, y, z, this);
            world.notifyBlocksOfNeighborChange(x, y - 1, z, this);
            world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
        }

        if (hasCart) {
            world.scheduleBlockUpdate(x, y, z, this, 20);
        }

        world.func_147453_f(x, y, z, this);
    }
}
