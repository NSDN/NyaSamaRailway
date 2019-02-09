package club.nsdn.nyasamarailway.item.nsc;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.entity.nsc.NSC2B;
import club.nsdn.nyasamarailway.item.AbsItemCart;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2019.1.24.
 */
public class ItemNSC2B extends AbsItemCart {

    public ItemNSC2B() {
        super(NSC2B.class, "ItemNSC2B", "item_nsc_2b");
        setCreativeTab(CreativeTabLoader.tabNSTest);
    }

    @Override
    public EntityMinecart getCart(World world, double x, double y, double z) {
        return new NSC2B(world, x, y, z);
    }
    
}
