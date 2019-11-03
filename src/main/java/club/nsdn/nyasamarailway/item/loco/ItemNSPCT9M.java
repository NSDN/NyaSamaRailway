package club.nsdn.nyasamarailway.item.loco;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.entity.loco.NSPCT9M;
import club.nsdn.nyasamarailway.item.AbsItemCart;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class ItemNSPCT9M extends AbsItemCart {

    public ItemNSPCT9M() {
        super(NSPCT9M.class, "ItemNSPCT9M", "item_nspc_9m");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Override
    public EntityMinecart getCart(World world, double x, double y, double z) {
        return new NSPCT9M(world, x, y, z);
    }

}
