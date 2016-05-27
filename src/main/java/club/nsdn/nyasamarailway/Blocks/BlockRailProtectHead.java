package club.nsdn.nyasamarailway.Blocks;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2016.5.6.
 */

public class BlockRailProtectHead extends BlockRailBase {

    public BlockRailProtectHead() {
        super("BlockRailProtectHead");
        setTextureName("rail_protect_head");
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
