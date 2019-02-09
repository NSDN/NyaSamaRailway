package club.nsdn.nyasamarailway.item.loco;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.entity.loco.NSPCT8C;
import club.nsdn.nyasamarailway.item.AbsItemCart;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2018.2.24.
 */
public class ItemNSPCT8C extends AbsItemCart {

    public ItemNSPCT8C() {
        super(NSPCT8C.class, "ItemNSPCT8C", "item_nspc_8c");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Override
    public boolean shouldSelfSpawn() {
        return true;
    }

    @Override
    public void selfSpawn(World world, double x, double y, double z, String name, EntityPlayer player) {
        NSPCT8C.doSpawn(world, x, y, z, name);
    }

    @Override
    public EntityMinecart getCart(World world, double x, double y, double z) {
        return null;
    }

}
