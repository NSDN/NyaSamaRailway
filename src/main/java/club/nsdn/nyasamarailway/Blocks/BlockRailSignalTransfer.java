package club.nsdn.nyasamarailway.Blocks;

/**
 * Created by drzzm32 on 2016.7.26.
 */

import club.nsdn.nyasamarailway.CreativeTab.CreativeTabLoader;

import net.minecraft.block.*;
import net.minecraft.world.World;
import net.minecraft.entity.item.EntityMinecart;

public class BlockRailSignalTransfer extends BlockRailPowered {

    public BlockRailSignalTransfer() {
        super();
        setBlockName("BlockRailSignalTransfer");
        setTextureName("rail_signal_transfer");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
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
    public void onMinecartPass(World world, EntityMinecart cart, int x, int y, int z) {
    }

    public int getRailChargeDistance() {
        return 32;
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

            return this.func_150057_a(world, x, y, z, bool, r, baseMeta) ? true : var9 && this.func_150057_a(world, x, y - 1, z, bool, r, baseMeta);
        }
    }

    @Override
    protected boolean func_150057_a(World world, int x, int y, int z, boolean bool, int r, int prevBaseMeta)
    {
        Block block = world.getBlock(x, y, z);

        if (block instanceof BlockRailPowered)
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

                return this.func_150058_a(world, x, y, z, meta, bool, r + 1);
            }
        }

        return false;
    }

    /*@Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        this.setRailOutput(world, x, y, z, world.getBlockMetadata(x, y, z));
    }

    public void setRailOutput(World world, int x, int y, int z, int meta) {
        boolean isPowered = (meta & 8) != 0;

        if (!isPowered) {
            if (getRailDirection(world, x, y, z) == RailDirection.NS) { //z
                if ((world.getBlock(x, y, z + 1).canProvidePower() && world.getBlockMetadata(x, y, z + 1) >= 8) ||
                        (world.getBlock(x, y, z - 1).canProvidePower() && world.getBlockMetadata(x, y, z - 1) >= 8)) {
                    world.setBlockMetadataWithNotify(x, y, z, meta | 8, 3);
                    world.notifyBlocksOfNeighborChange(x, y, z, this);
                    world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
                }
            } else { //x
                if ((world.getBlock(x + 1, y, z).canProvidePower() && world.getBlockMetadata(x + 1, y, z) >= 8) ||
                        (world.getBlock(x - 1, y, z).canProvidePower() && world.getBlockMetadata(x - 1, y, z) >= 8)) {
                    world.setBlockMetadataWithNotify(x, y, z, meta | 8, 3);
                    world.notifyBlocksOfNeighborChange(x, y, z, this);
                    world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
                }
            }
        }

        if (isPowered) {
            if (getRailDirection(world, x, y, z) == RailDirection.NS) { //z
                if (world.getBlock(x, y, z + 1) == this && world.getBlock(x, y, z - 1) == this) {
                    for (int i = 1; i < 16; i++) {
                        if (world.getBlock(x, y, z + 1 + i).canProvidePower() && world.getBlock(x, y, z - 1 - i).canProvidePower()) {
                            if ((world.getBlockMetadata(x, y, z + 1 + i) < 8) && (world.getBlockMetadata(x, y, z + 1 + i) < 8)) {
                                world.setBlockMetadataWithNotify(x, y, z, meta & 7, 3);
                                world.notifyBlocksOfNeighborChange(x, y, z, this);
                                world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
                                break;
                            }
                        } else if ((!world.getBlock(x, y, z + 1 + i).canProvidePower() || world.getBlockMetadata(x, y, z + 1 + i) < 8) && world.getBlock(x, y, z + 1 + i) != this ||
                                   (!world.getBlock(x, y, z - 1 - i).canProvidePower() || world.getBlockMetadata(x, y, z - 1 - i) < 8) && world.getBlock(x, y, z - 1 - i) != this) {
                            world.setBlockMetadataWithNotify(x, y, z, meta & 7, 3);
                            world.notifyBlocksOfNeighborChange(x, y, z, this);
                            world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
                            break;
                        }
                    }
                } else if (world.getBlock(x, y, z + 1).canProvidePower() && world.getBlock(x, y, z - 1).canProvidePower()) {
                    if ((world.getBlockMetadata(x, y, z + 1) < 8) && (world.getBlockMetadata(x, y, z - 1) < 8)) {
                        world.setBlockMetadataWithNotify(x, y, z, meta & 7, 3);
                        world.notifyBlocksOfNeighborChange(x, y, z, this);
                        world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
                    }
                } else if ((!world.getBlock(x, y, z + 1).canProvidePower() || world.getBlockMetadata(x, y, z + 1) < 8) && world.getBlock(x, y, z + 1) != this ||
                           (!world.getBlock(x, y, z - 1).canProvidePower() || world.getBlockMetadata(x, y, z - 1) < 8) && world.getBlock(x, y, z - 1) != this) {
                    world.setBlockMetadataWithNotify(x, y, z, meta & 7, 3);
                    world.notifyBlocksOfNeighborChange(x, y, z, this);
                    world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
                }
            } else { //x
                if (world.getBlock(x + 1, y, z) == this && world.getBlock(x - 1, y, z) == this) {
                    for (int i = 1; i < 16; i++) {
                        if (world.getBlock(x + 1 + i, y, z).canProvidePower() && world.getBlock(x - 1 - i, y, z).canProvidePower()) {
                            if ((world.getBlockMetadata(x + 1 + i, y, z) < 8) && (world.getBlockMetadata(x + 1 + i, y, z) < 8)) {
                                world.setBlockMetadataWithNotify(x, y, z, meta & 7, 3);
                                world.notifyBlocksOfNeighborChange(x, y, z, this);
                                world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
                                break;
                            }
                        } else if ((!world.getBlock(x + 1 + i, y, z).canProvidePower() || world.getBlockMetadata(x + 1 + i, y, z) < 8) && world.getBlock(x + 1 + i, y, z) != this ||
                                (!world.getBlock(x - 1 - i, y, z).canProvidePower() || world.getBlockMetadata(x - 1 - i, y, z) < 8) && world.getBlock(x - 1 - i, y, z) != this) {
                            world.setBlockMetadataWithNotify(x, y, z, meta & 7, 3);
                            world.notifyBlocksOfNeighborChange(x, y, z, this);
                            world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
                            break;
                        }
                    }
                } else if (world.getBlock(x + 1, y, z).canProvidePower() && world.getBlock(x - 1, y, z).canProvidePower()) {
                    if ((world.getBlockMetadata(x + 1, y, z) < 8) && (world.getBlockMetadata(x - 1, y, z) < 8)) {
                        world.setBlockMetadataWithNotify(x, y, z, meta & 7, 3);
                        world.notifyBlocksOfNeighborChange(x, y, z, this);
                        world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
                    }
                } else if ((!world.getBlock(x + 1, y, z).canProvidePower() || world.getBlockMetadata(x + 1, y, z) < 8) && world.getBlock(x + 1, y, z) != this ||
                        (!world.getBlock(x - 1, y, z).canProvidePower() || world.getBlockMetadata(x - 1, y, z) < 8) && world.getBlock(x - 1, y, z) != this) {
                    world.setBlockMetadataWithNotify(x, y, z, meta & 7, 3);
                    world.notifyBlocksOfNeighborChange(x, y, z, this);
                    world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
                }
            }
        }
        
        world.func_147453_f(x, y, z, this);
    }*/
}
