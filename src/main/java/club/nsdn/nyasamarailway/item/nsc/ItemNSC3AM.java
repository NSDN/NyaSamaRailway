package club.nsdn.nyasamarailway.item.nsc;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.entity.nsc.NSC3AM;
import club.nsdn.nyasamarailway.item.AbsItemCart;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class ItemNSC3AM extends AbsItemCart {

    public ItemNSC3AM() {
        super(NSC3AM.class, "ItemNSC3AM", "item_nsc_3am");
        setCreativeTab(CreativeTabLoader.tabNSTest);
    }

    @Override
    public EntityMinecart getCart(World world, double x, double y, double z) {
        return new NSC3AM(world, x, y, z);
    }

}
