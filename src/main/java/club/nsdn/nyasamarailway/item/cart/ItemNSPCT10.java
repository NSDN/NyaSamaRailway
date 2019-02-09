package club.nsdn.nyasamarailway.item.cart;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.entity.cart.NSPCT10;
import club.nsdn.nyasamarailway.item.AbsItemCart;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2018.4.17.
 */
public class ItemNSPCT10 extends AbsItemCart {

    public ItemNSPCT10() {
        super(NSPCT10.class, "ItemNSPCT10", "item_nspc_10");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Override
    public EntityMinecart getCart(World world, double x, double y, double z) {
        return new NSPCT10(world, x, y, z);
    }

}
