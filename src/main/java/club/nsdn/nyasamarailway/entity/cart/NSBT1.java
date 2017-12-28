package club.nsdn.nyasamarailway.entity.cart;

import club.nsdn.nyasamarailway.entity.MinecartBase;
import club.nsdn.nyasamarailway.item.ItemLoader;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2016.5.26.
 */
public class NSBT1 extends MinecartBase {

    public NSBT1(World world) { super(world); }

    public NSBT1(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public float getMaxCartSpeedOnRail() {
        return 2.0F;
    }

    @Override
    public void killMinecart(DamageSource source)
    {
        this.setDead();
        ItemStack itemstack = new ItemStack(ItemLoader.itemNSBT1, 1);
        itemstack.setStackDisplayName(itemstack.getDisplayName());
        this.entityDropItem(itemstack, 0.0F);
    }

}
