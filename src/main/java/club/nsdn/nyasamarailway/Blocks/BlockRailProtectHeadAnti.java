package club.nsdn.nyasamarailway.Blocks;

import net.minecraft.block.Block;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2017.5.21.
 */
public class BlockRailProtectHeadAnti extends BlockRailBase implements IRailDirectional {

    public BlockRailProtectHeadAnti() {
        super("BlockRailProtectHeadAnti");
        setTextureName("rail_protect_head_anti");
    }

    public boolean isForward() {
        return false;
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return world.getBlock(x, y, z).isReplaceable(world, x, y, z);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        if(!world.isRemote) {
            int l = world.getBlockMetadata(x, y, z);
            int i1 = l;
            if(this.field_150053_a) {
                i1 = l & 7;
            }
            this.func_150048_a(world, x, y, z, l, i1, block);
        }
    }
}
