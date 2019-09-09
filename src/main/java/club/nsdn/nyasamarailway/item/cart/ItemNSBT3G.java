package club.nsdn.nyasamarailway.item.cart;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.entity.cart.NSBT3G;
import club.nsdn.nyasamarailway.item.AbsItemCart;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2019.9.9.
 */
public class ItemNSBT3G extends AbsItemCart {

    public ItemNSBT3G() {
        super(NSBT3G.class, "ItemNSBT3G", "item_nsb_3g");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Override
    public EntityMinecart getCart(World world, double x, double y, double z) {
        return new NSBT3G(world, x, y, z);
    }

}
