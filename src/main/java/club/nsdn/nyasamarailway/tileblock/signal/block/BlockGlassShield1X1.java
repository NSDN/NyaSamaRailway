package club.nsdn.nyasamarailway.tileblock.signal.block;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by drzzm32 on 2017.9.12.
 */
public class BlockGlassShield1X1 extends BlockGlassShield {

    public static class GlassShield extends BlockGlassShield.GlassShield { }

    public BlockGlassShield1X1() {
        super("GlassShield1X1", "glass_shield_1x1");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new GlassShield();
    }

    @Override
    protected void setBoundsByMeta(int meta) {
        float x1 = 0.0F, y1 = 0.0F, z1 = 0.4375F, x2 = 1.0F, y2 = 1.0F, z2 = 0.5625F;

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

    public boolean checkShield(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        return (block instanceof BlockGlassShield) && (block != this);
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (!world.isRemote) {
            if (checkShield(world, x, y - 1, z) || checkShield(world, x, y - 2, z) ) {
                if (checkShield(world, x, y - 1, z)) {
                    world.setBlockMetadataWithNotify(x, y, z,
                            world.getBlockMetadata(x, y - 1, z), 3
                    );
                    world.markBlockForUpdate(x, y, z);
                } else if (checkShield(world, x, y - 2, z)) {
                    world.setBlockMetadataWithNotify(x, y, z,
                            world.getBlockMetadata(x, y - 2, z), 3
                    );
                }
                world.scheduleBlockUpdate(x, y, z, this, 1);
            } else super.updateTick(world, x, y, z, random);
        }
    }

    @Override
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
