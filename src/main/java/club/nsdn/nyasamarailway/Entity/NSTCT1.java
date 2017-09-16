package club.nsdn.nyasamarailway.Entity;

import club.nsdn.nyasamarailway.Items.ItemLoader;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2017.9.16.
 */
public class NSTCT1 extends MinecartBase {

    public NSTCT1(World world) { super(world); }

    public NSTCT1(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public float getMaxCartSpeedOnRail() {
        return 1.0F;
    }

    @Override
    public double getMountedYOffset() {
        return 0.5;
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }

    @Override
    public void killMinecart(DamageSource source)
    {
        this.setDead();
        ItemStack itemstack = new ItemStack(ItemLoader.itemNSTCT1, 1);
        itemstack.setStackDisplayName(itemstack.getDisplayName());
        this.entityDropItem(itemstack, 0.0F);
    }

}
