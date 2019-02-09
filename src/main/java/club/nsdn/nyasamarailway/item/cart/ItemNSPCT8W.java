package club.nsdn.nyasamarailway.item.cart;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.entity.cart.NSPCT8W;
import club.nsdn.nyasamarailway.item.AbsItemCart;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2018.2.24.
 */
public class ItemNSPCT8W extends AbsItemCart {

    public ItemNSPCT8W() {
        super(NSPCT8W.class, "ItemNSPCT8W", "item_nspc_8w");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Override
    public boolean shouldSelfSpawn() {
        return true;
    }

    @Override
    public void selfSpawn(World world, double x, double y, double z, String name, EntityPlayer player) {
        NSPCT8W.doSpawn(world, x, y, z, name);
    }

    @Override
    public EntityMinecart getCart(World world, double x, double y, double z) {
        return null;
    }

}
