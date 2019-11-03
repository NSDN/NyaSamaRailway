package club.nsdn.nyasamarailway.item.nsc;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.entity.nsc.NSC1AM;
import club.nsdn.nyasamarailway.item.AbsItemCart;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class ItemNSC1AM extends AbsItemCart {

    public ItemNSC1AM() {
        super(NSC1AM.class, "ItemNSC1AM", "item_nsc_1am");
        setCreativeTab(CreativeTabLoader.tabNSTest);
    }

    @Override
    public EntityMinecart getCart(World world, double x, double y, double z) {
        return new NSC1AM(world, x, y, z);
    }

}
