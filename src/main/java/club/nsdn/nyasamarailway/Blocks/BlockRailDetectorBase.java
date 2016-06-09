package club.nsdn.nyasamarailway.Blocks;

/**
 * Created by drzzm32 on 2016.5.5.
 */

import club.nsdn.nyasamarailway.CreativeTab.CreativeTabLoader;
import net.minecraft.block.BlockRailDetector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.*;

public class BlockRailDetectorBase extends BlockRailDetector{

    public final int delaySecond;
    public static LinkedHashMap<Vec3, Integer> tmpDelay;

    public BlockRailDetectorBase(String name)
    {
        super();
        delaySecond = 0;
        tmpDelay = new LinkedHashMap<Vec3, Integer>();
        setBlockName(name);
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    public BlockRailDetectorBase(String name, int delay)
    {
        super();
        delaySecond = delay;
        tmpDelay = new LinkedHashMap<Vec3, Integer>();
        setBlockName(name);
        setCreativeTab(null);
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
        if(!world.isRemote) {
            int meta = world.getBlockMetadata(x, y, z);
            if((meta & 8) == 0) {
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
        if(!world.isRemote) {
            int meta = world.getBlockMetadata(x, y, z);
            if((meta & 8) != 0) {
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
        float bBoxSize = 0.125F;
        List bBox = world.getEntitiesWithinAABB(EntityMinecart.class, AxisAlignedBB.getBoundingBox((double)((float)x + bBoxSize), (double)y, (double)((float)z + bBoxSize), (double)((float)(x + 1) - bBoxSize), (double)((float)(y + 1) - bBoxSize), (double)((float)(z + 1) - bBoxSize)));
        if(!bBox.isEmpty()) {
            hasCart = true;
        }

        if (hasCart && !isPowered) {
            if (!tmpDelay.containsKey(Vec3.createVectorHelper(x, y, z))) {
                tmpDelay.put(Vec3.createVectorHelper(x, y, z), 0);
                System.out.println("New delay added");
            } else {
                tmpDelay.put(Vec3.createVectorHelper(x, y, z), tmpDelay.get(Vec3.createVectorHelper(x, y, z)) + 1);
                System.out.println("Delay count:" + tmpDelay.get(Vec3.createVectorHelper(x, y, z)));
                if (tmpDelay.get(Vec3.createVectorHelper(x, y, z)) > 20 * delaySecond) {
                    System.out.println("Delay end");
                    world.setBlockMetadataWithNotify(x, y, z, meta | 8, 3);
                    world.notifyBlocksOfNeighborChange(x, y, z, this);
                    world.notifyBlocksOfNeighborChange(x, y - 1, z, this);
                    world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
                    tmpDelay.remove(Vec3.createVectorHelper(x, y, z));
                }
            }
        }

        if(!hasCart && isPowered) {
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
