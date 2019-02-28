package club.nsdn.nyasamarailway.item.loco;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.entity.loco.NSBT2M;
import club.nsdn.nyasamarailway.item.AbsItemCart;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2019.2.27
 */
public class ItemNSBT2M extends AbsItemCart {

    public ItemNSBT2M() {
        super(NSBT2M.class, "ItemNSBT2M", "item_nsb_2m");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Override
    public EntityMinecart getCart(World world, double x, double y, double z) {
        return new NSBT2M(world, x, y, z);
    }

}
