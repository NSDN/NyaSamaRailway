package club.nsdn.nyasamarailway.item.loco;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.entity.loco.NSPCT4M;
import club.nsdn.nyasamarailway.item.AbsItemCart;
import club.nsdn.nyasamarailway.api.rail.IMonoRail;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class ItemNSPCT4M extends AbsItemCart {

    public ItemNSPCT4M() {
        super(NSPCT4M.class, "ItemNSPCT4M", "item_nspc_4m");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Override
    public EntityMinecart getCart(World world, double x, double y, double z) {
        return new NSPCT4M(world, x, y, z);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.getTileEntity(pos) instanceof IMonoRail)
            return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
        return EnumActionResult.FAIL;
    }

}
