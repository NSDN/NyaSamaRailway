package club.nsdn.nyasamarailway.item.cart;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.entity.cart.NSPCT9;
import club.nsdn.nyasamarailway.item.AbsItemCart;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class ItemNSPCT9 extends AbsItemCart {

    public ItemNSPCT9() {
        super(NSPCT9.class, "ItemNSPCT9", "item_nspc_9");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Override
    public EntityMinecart getCart(World world, double x, double y, double z) {
        return new NSPCT9(world, x, y, z);
    }

}
