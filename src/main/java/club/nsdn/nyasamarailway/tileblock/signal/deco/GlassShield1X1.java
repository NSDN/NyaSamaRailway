package club.nsdn.nyasamarailway.tileblock.signal.deco;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class GlassShield1X1 extends GlassShield {

    public static class TileEntityGlassShield1X1 extends TileEntityGlassShield {

        public TileEntityGlassShield1X1() {
            setInfo(4, 1, 1, 0.125);
        }

        public boolean checkShield(World world, BlockPos pos) {
            Block block = world.getBlockState(pos).getBlock();
            if (block instanceof GlassShield) {
                return block.getClass() != GlassShield1X1.class;
            }
            return false;
        }

        public TileEntityGlassShield getShield(World world, BlockPos pos) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof TileEntityGlassShield)
                return (TileEntityGlassShield) tileEntity;
            return null;
        }

        @Override
        public void updateSignal(World world, BlockPos pos) {
            if (checkShield(world, pos.down()) || checkShield(world, pos.down(2))) {
                TileEntityGlassShield shield = null;
                if (checkShield(world, pos.down()))
                    shield = getShield(world, pos.down());
                else if (checkShield(world, pos.down(2)))
                    shield = getShield(world, pos.down(2));
                if (shield != null) {
                    this.META = shield.META;
                    this.refresh();
                }
            } else super.updateSignal(world, pos);
        }
    }

    public GlassShield1X1() {
        super("GlassShield1X1", "glass_shield_1x1");
    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityGlassShield1X1();
    }

}
