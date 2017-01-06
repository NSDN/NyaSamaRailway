package club.nsdn.nyasamarailway.Blocks;

/**
 * Created by drzzm32 on 2016.5.5.
 */

import club.nsdn.nyasamarailway.CreativeTab.CreativeTabLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailPowered;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import org.thewdj.physics.Dynamics.LocoMotions;

import java.util.List;

public class BlockRailPoweredBase extends BlockRailPowered {

    public BlockRailPoweredBase(String name) {
        super();
        setBlockName(name);
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    protected void setTextureName(String name) {
        setBlockTextureName("nyasamarailway" + ":" + name);
    }

    @Override
    public float getRailMaxSpeed(World world, EntityMinecart cart, int x, int y, int z) {
        return 1.0F;
    }

    public int getRailChargeDistance() {
        return 32;
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
        } else if ((meta & 2) > 0 && (meta & 4) == 0) {
            return RailDirection.WE;
        } else if ((meta & 2) == 0 && (meta & 4) > 0) {
            return RailDirection.NS;
        }
        return RailDirection.NONE;
    }

    public boolean isRailPowered(World world, int x, int y, int z) {
        if (world.getBlock(x, y, z) instanceof BlockRailPoweredBase) {
            return (world.getBlockMetadata(x, y, z) & 8) > 0;
        }
        return false;
    }

    @Override
    public void onMinecartPass(World world, EntityMinecart cart, int x, int y, int z) {
        float maxV = getRailMaxSpeed(world, cart, x, y, z);
        if (world.getBlockMetadata(x, y, z) >= 8) {
            if (Math.abs(cart.motionX) < maxV && Math.abs(cart.motionZ) < maxV) {
                cart.motionX = Math.signum(cart.motionX) * LocoMotions.calcVelocityUp(Math.abs(cart.motionX), 0.1, 1.0, 1.0, 0.02);
                cart.motionZ = Math.signum(cart.motionZ) * LocoMotions.calcVelocityUp(Math.abs(cart.motionZ), 0.1, 1.0, 1.0, 0.02);
            }
        } else {
            cart.motionX = Math.signum(cart.motionX) * LocoMotions.calcVelocityDown(Math.abs(cart.motionX), 0.1, 1.0, 1.0, 1.0, 0.2, 0.02);
            cart.motionZ = Math.signum(cart.motionZ) * LocoMotions.calcVelocityDown(Math.abs(cart.motionZ), 0.1, 1.0, 1.0, 1.0, 0.2, 0.02);
        }
    }

    public void onRailPowered(World world, int x, int y, int z, int meta, boolean hasCart) {
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        super.onNeighborBlockChange(world, x, y, z, block);
        int meta = world.getBlockMetadata(x, y, z);
        float bBoxSize = 0.125F;
        boolean hasCart = false;
        List bBox = world.getEntitiesWithinAABB(
                EntityMinecart.class,
                AxisAlignedBB.getBoundingBox((double) ((float) x + bBoxSize),
                        (double) y,
                        (double) ((float) z + bBoxSize),
                        (double) ((float) (x + 1) - bBoxSize),
                        (double) ((float) (y + 1) - bBoxSize),
                        (double) ((float) (z + 1) - bBoxSize))
        );
        if (!bBox.isEmpty()) {
            hasCart = true;
        }
        if (meta >= 8) {
            onRailPowered(world, x, y, z, meta, hasCart);
        }
    }

    @Override
    protected boolean func_150058_a(World world, int x, int y, int z, int meta, boolean bool, int r) {
        if (r >= getRailChargeDistance()) {
            return false;
        } else {
            int baseMeta = meta & 7;
            boolean var9 = true;
            switch (baseMeta) {
                case 0:
                    if (bool) {
                        ++z;
                    } else {
                        --z;
                    }
                    break;
                case 1:
                    if (bool) {
                        --x;
                    } else {
                        ++x;
                    }
                    break;
                case 2:
                    if (bool) {
                        --x;
                    } else {
                        ++x;
                        ++y;
                        var9 = false;
                    }

                    baseMeta = 1;
                    break;
                case 3:
                    if (bool) {
                        --x;
                        ++y;
                        var9 = false;
                    } else {
                        ++x;
                    }

                    baseMeta = 1;
                    break;
                case 4:
                    if (bool) {
                        ++z;
                    } else {
                        --z;
                        ++y;
                        var9 = false;
                    }

                    baseMeta = 0;
                    break;
                case 5:
                    if (bool) {
                        ++z;
                        ++y;
                        var9 = false;
                    } else {
                        --z;
                    }

                    baseMeta = 0;
            }

            return func_150057_a(world, x, y, z, bool, r, baseMeta) ? true : var9 && this.func_150057_a(world, x, y - 1, z, bool, r, baseMeta);
        }
    }

    @Override
    protected boolean func_150057_a(World world, int x, int y, int z, boolean bool, int r, int prevBaseMeta)
    {
        Block block = world.getBlock(x, y, z);

        if ((block == this) || ((block instanceof BlockRailSignalTransfer)))
        {
            int meta = world.getBlockMetadata(x, y, z);
            int baseMeta = meta & 7;

            if (prevBaseMeta == 1 && (baseMeta == 0 || baseMeta == 4 || baseMeta == 5))
            {
                return false;
            }

            if (prevBaseMeta == 0 && (baseMeta == 1 || baseMeta == 2 || baseMeta == 3))
            {
                return false;
            }

            if ((meta & 8) != 0)
            {
                if (world.isBlockIndirectlyGettingPowered(x, y, z))
                {
                    return true;
                }

                return func_150058_a(world, x, y, z, meta, bool, r + 1);
            }
        }

        return false;
    }

}
