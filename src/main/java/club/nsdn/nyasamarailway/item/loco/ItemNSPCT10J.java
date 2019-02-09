package club.nsdn.nyasamarailway.item.loco;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.entity.loco.NSPCT10J;
import club.nsdn.nyasamarailway.item.AbsItemCart;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2018.4.17.
 */
public class ItemNSPCT10J extends AbsItemCart {

    public ItemNSPCT10J() {
        super(NSPCT10J.class, "ItemNSPCT10J", "item_nspc_10j");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Override
    public EntityMinecart getCart(World world, double x, double y, double z) {
        return new NSPCT10J(world, x, y, z);
    }

}
