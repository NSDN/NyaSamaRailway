package club.nsdn.nyasamarailway.Blocks;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2016.5.6.
 */

public class BlockRailProtectHead extends BlockRailBase implements IRailDirectional, IRailSpeedKeep {

    public BlockRailProtectHead() {
        super("BlockRailProtectHead");
        setTextureName("rail_protect_head");
    }

    public boolean isForward() {
        return true;
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
        int meta = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        if (meta == 0 || meta == 2) meta = 0;
        else meta = 1;

        world.setBlockMetadataWithNotify(x, y, z, meta, 2);
    }

}
