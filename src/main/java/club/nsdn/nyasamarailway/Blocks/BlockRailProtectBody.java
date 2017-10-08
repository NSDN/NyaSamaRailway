package club.nsdn.nyasamarailway.Blocks;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2016.5.6.
 */

public class BlockRailProtectBody extends BlockRailBase implements IRailSpeedKeep {

    public BlockRailProtectBody() {
        super("BlockRailProtectBody");
        setTextureName("rail_protect");
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return world.getBlock(x, y, z).isReplaceable(world, x, y, z);
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        //this rail do not need this!
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        //this rail do not need this!
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack) {
        int meta = MathHelper.floor_double(((double)(player.rotationYaw * 4.0F / 360.0F) * 2) + 0.5D) & 7;

        if (meta == 0 || meta == 4) meta = 0;
        else if (meta == 2 || meta == 6) meta = 1;
        else if (meta == 1) meta = 9;
        else if (meta == 3) meta = 6;
        else if (meta == 5) meta = 7;
        else meta = 8;

        world.setBlockMetadataWithNotify(x, y, z, meta, 2);
    }

}
