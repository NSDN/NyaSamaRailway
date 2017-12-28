package club.nsdn.nyasamarailway.tileblock.signal.block;

import club.nsdn.nyasamarailway.tileblock.TileBlock;
import club.nsdn.nyasamarailway.tileblock.signal.TileEntityGlassShieldBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;
import java.util.Random;

/**
 * Created by drzzm32 on 2017.9.6.
 */
public class BlockGlassShield extends TileBlock {

    public static class GlassShield extends TileEntityGlassShieldBase { }

    public BlockGlassShield() {
        super("GlassShield");
        setIconLocation("glass_shield");
        setLightOpacity(1);
        setLightLevel(0);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new GlassShield();
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side)
    {
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack) {
        int meta = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        world.setBlockMetadataWithNotify(x, y, z, meta, 2);
    }

    @Override
    protected void setBoundsByMeta(int meta) {
        float x1 = 0.0F, y1 = 0.0F, z1 = 0.4375F, x2 = 1.0F, y2 = 2.0F, z2 = 0.5625F;

        if ((meta & 0x8) != 0) x1 = 0.875F;
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

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        if ((world.getBlockMetadata(x, y, z) & 0x8) != 0) {
            return null;
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
        return 20;
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (!world.isRemote) {
            if (world.getTileEntity(x, y, z) instanceof GlassShield) {
                GlassShield glassShield = (GlassShield) world.getTileEntity(x, y, z);
                GlassShield nearByShield = getNearbyShield(world, x, y, z);
                boolean control;

                if (glassShield.getSender() == null) {
                    control = hasPlayer(world, x, y, z);
                } else {
                    control = glassShield.senderIsPowered();
                }

                if (control) {
                    if (glassShield.state == GlassShield.STATE_CLOSE) {
                        glassShield.state = GlassShield.STATE_OPENING;
                        if (nearByShield != null) {
                            nearByShield.state = GlassShield.STATE_OPENING;
                        }
                    }
                } else {
                    if (glassShield.state == GlassShield.STATE_OPEN) {
                        if (glassShield.delay < GlassShield.DELAY * 20 &&
                                glassShield.getSender() == null
                                ) glassShield.delay += 1;
                        else {
                            glassShield.state = GlassShield.STATE_CLOSING;
                            if (nearByShield != null) {
                                nearByShield.state = GlassShield.STATE_CLOSING;
                            }
                        }
                    } else {
                        glassShield.delay = 0;
                    }
                }

                switch (glassShield.state) {
                    case GlassShield.STATE_CLOSE:
                        if ((world.getBlockMetadata(x, y, z) & 0x8) != 0) {
                            world.setBlockMetadataWithNotify(x, y, z,
                                    world.getBlockMetadata(x, y, z) & 0x7, 3
                            );
                            world.markBlockForUpdate(x, y, z);
                        }
                        break;
                    case GlassShield.STATE_CLOSING:
                        if (glassShield.progress > 0) glassShield.progress -= 1;
                        else {
                            glassShield.state = GlassShield.STATE_CLOSE;
                            world.setBlockMetadataWithNotify(x, y, z,
                                    world.getBlockMetadata(x, y, z) & 0x7, 3
                            );
                            world.markBlockForUpdate(x, y, z);
                        }
                        break;
                    case GlassShield.STATE_OPEN:
                        if ((world.getBlockMetadata(x, y, z) & 0x8) == 0) {
                            world.setBlockMetadataWithNotify(x, y, z,
                                    world.getBlockMetadata(x, y, z) | 0x8, 3
                            );
                            world.markBlockForUpdate(x, y, z);
                        }
                        break;
                    case GlassShield.STATE_OPENING:
                        if (glassShield.progress < GlassShield.PROGRESS_MAX) glassShield.progress += 1;
                        else {
                            glassShield.state = GlassShield.STATE_OPEN;
                            world.setBlockMetadataWithNotify(x, y, z,
                                    world.getBlockMetadata(x, y, z) | 0x8, 3
                            );
                            world.markBlockForUpdate(x, y, z);
                        }
                        break;
                    default:
                        break;
                }

                if (glassShield.state == GlassShield.STATE_OPENING || glassShield.state == GlassShield.STATE_CLOSING) {
                    world.markBlockForUpdate(x, y, z);
                }
                world.scheduleBlockUpdate(x, y, z, this, 1);
            }
        }
    }

    public boolean hasPlayer(World world, int x, int y, int z) {
        float bBoxExpand = 1.0F;
        y += 1; //player's bounding box is higher than cart
        List bBox = world.getEntitiesWithinAABB(
                EntityPlayer.class,
                AxisAlignedBB.getBoundingBox((double) ((float) x - bBoxExpand),
                        (double) y - 1,
                        (double) ((float) z - bBoxExpand),
                        (double) ((float) x + 1 + bBoxExpand),
                        (double) ((float) y + 1),
                        (double) ((float) z + 1 + bBoxExpand))
        );

        return !bBox.isEmpty();
    }

    public GlassShield getNearbyShield(World world, int x, int y, int z) {
        if (world.getTileEntity(x + 1, y, z) instanceof GlassShield) {
            return (GlassShield) world.getTileEntity(x + 1, y, z);
        }
        if (world.getTileEntity(x - 1, y, z) instanceof GlassShield) {
            return (GlassShield) world.getTileEntity(x - 1, y, z);
        }
        if (world.getTileEntity(x, y, z + 1) instanceof GlassShield) {
            return (GlassShield) world.getTileEntity(x, y, z + 1);
        }
        if (world.getTileEntity(x, y, z - 1) instanceof GlassShield) {
            return (GlassShield) world.getTileEntity(x, y, z - 1);
        }
        return null;
    }

}
