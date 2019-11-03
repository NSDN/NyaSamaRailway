package club.nsdn.nyasamarailway.item.nsc;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.entity.nsc.NSC3A;
import club.nsdn.nyasamarailway.item.AbsItemCart;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class ItemNSC3A extends AbsItemCart {

    public ItemNSC3A() {
        super(NSC3A.class, "ItemNSC3A", "item_nsc_3a");
        setCreativeTab(CreativeTabLoader.tabNSTest);
    }

    @Override
    public EntityMinecart getCart(World world, double x, double y, double z) {
        return new NSC3A(world, x, y, z);
    }

}
