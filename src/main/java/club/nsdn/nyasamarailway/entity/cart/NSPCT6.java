package club.nsdn.nyasamarailway.entity.cart;

import club.nsdn.nyasamarailway.entity.MinecartBase;
import club.nsdn.nyasamarailway.item.ItemLoader;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2017.9.5.
 */
public class NSPCT6 extends MinecartBase {

    public NSPCT6(World world) {
        super(world);
        ignoreFrustumCheck = true;
    }

    public NSPCT6(World world, double x, double y, double z) {
        super(world, x, y, z);
        ignoreFrustumCheck = true;
    }

    @Override
    public boolean canMakePlayerTurn() {
        return false;
    }

    @Override
    public float getMaxCartSpeedOnRail() {
        return 5.0F;
    }

    @Override
    public double getMountedYOffset() {
        return -0.3;
    }

    @Override
    public float getLinkageDistance(EntityMinecart cart) {
        return 1.5F;
    }

    @Override
    public float getOptimalDistance(EntityMinecart cart) {
        return 1.0F;
    }

    @Override
    public void killMinecart(DamageSource source)
    {
        this.setDead();
        ItemStack itemstack = new ItemStack(ItemLoader.itemNSPCT6, 1);
        itemstack.setStackDisplayName(itemstack.getDisplayName());
        if (!source.damageType.equals("nsr")) this.entityDropItem(itemstack, 0.0F);
    }

}
