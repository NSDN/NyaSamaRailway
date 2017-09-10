package club.nsdn.nyasamarailway.Entity;

import club.nsdn.nyasamarailway.Items.ItemLoader;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2016.8.26.
 */
public class NSPCT3 extends MinecartBase {

    public NSPCT3(World world) {
        super(world);
        ignoreFrustumCheck = true;
        canMakePlayerTurn = false;
    }

    public NSPCT3(World world, double x, double y, double z) {
        super(world, x, y, z);
        ignoreFrustumCheck = true;
        canMakePlayerTurn = false;
    }

    @Override
    public float getMaxCartSpeedOnRail() {
        return 5.0F;
    }

    @Override
    public float getLinkageDistance(EntityMinecart cart) {
        return 2.5F;
    }

    @Override
    public float getOptimalDistance(EntityMinecart cart) {
        return 1.5F;
    }

    @Override
    public void killMinecart(DamageSource source)
    {
        this.setDead();
        ItemStack itemstack = new ItemStack(ItemLoader.itemNSPCT3, 1);
        itemstack.setStackDisplayName(itemstack.getDisplayName());
        this.entityDropItem(itemstack, 0.0F);
    }

}
