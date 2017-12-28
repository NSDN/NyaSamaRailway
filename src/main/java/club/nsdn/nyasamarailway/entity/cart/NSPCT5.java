package club.nsdn.nyasamarailway.entity.cart;

import club.nsdn.nyasamarailway.entity.MinecartBase;
import club.nsdn.nyasamarailway.item.*;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2016.12.5.
 */
public class NSPCT5 extends MinecartBase {

    public static final int DATA_LENGTH = 29;
    //public int cartLength;

    public NSPCT5(World world) {
        super(world); ignoreFrustumCheck = true;
        this.getDataWatcher().addObject(DATA_LENGTH, 3);
    }

    public NSPCT5(World world, double x, double y, double z) {
        super(world, x, y, z);
        ignoreFrustumCheck = true;
        this.getDataWatcher().addObject(DATA_LENGTH, 3);
    }

    @Override
    public float getMaxCartSpeedOnRail() {
        return 2.0F;
    }

    @Override
    public double getMountedYOffset() {
        return -0.6;
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
    public boolean shouldRiderSit()
    {
        return true;
    }

    @Override
    public void killMinecart(DamageSource source)
    {
        this.setDead();
        ItemStack itemstack = new ItemStack(ItemLoader.itemNSPCT5, 1);
        itemstack.setStackDisplayName(itemstack.getDisplayName());
        this.entityDropItem(itemstack, 0.0F);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);
        this.getDataWatcher().updateObject(DATA_LENGTH, tagCompound.getInteger("cartLength"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setInteger("cartLength", this.getDataWatcher().getWatchableObjectInt(DATA_LENGTH));
    }

}
