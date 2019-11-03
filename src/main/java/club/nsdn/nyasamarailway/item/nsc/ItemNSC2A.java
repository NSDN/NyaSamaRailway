package club.nsdn.nyasamarailway.item.nsc;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.entity.nsc.NSC2A;
import club.nsdn.nyasamarailway.item.AbsItemCart;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class ItemNSC2A extends AbsItemCart {

    public ItemNSC2A() {
        super(NSC2A.class, "ItemNSC2A", "item_nsc_2a");
        setCreativeTab(CreativeTabLoader.tabNSTest);
    }

    @Override
    public EntityMinecart getCart(World world, double x, double y, double z) {
        return new NSC2A(world, x, y, z);
    }

}
