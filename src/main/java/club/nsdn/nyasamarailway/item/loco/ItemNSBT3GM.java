package club.nsdn.nyasamarailway.item.loco;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.entity.loco.NSBT3GM;
import club.nsdn.nyasamarailway.item.AbsItemCart;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2019.9.9.
 */
public class ItemNSBT3GM extends AbsItemCart {

    public ItemNSBT3GM() {
        super(NSBT3GM.class, "ItemNSBT3GM", "item_nsb_3gm");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Override
    public EntityMinecart getCart(World world, double x, double y, double z) {
        return new NSBT3GM(world, x, y, z);
    }

}
