package club.nsdn.nyasamarailway.entity.cart;

import club.nsdn.nyasamarailway.entity.MinecartBase;
import club.nsdn.nyasamarailway.item.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2016.12.7.
 */
public class NSPCT5L extends MinecartBase {

    public static final int DATA_LENGTH = 29;
    //public int cartLength;

    public NSPCT5L(World world) {
        super(world); ignoreFrustumCheck = true;
        this.getDataWatcher().addObject(DATA_LENGTH, 3);
    }

    public NSPCT5L(World world, double x, double y, double z) {
        super(world, x, y, z);
        ignoreFrustumCheck = true;
        this.getDataWatcher().addObject(DATA_LENGTH, 3);
    }

    public void modifyLength() {
        int len = dataWatcher.getWatchableObjectInt(DATA_LENGTH);
        dataWatcher.updateObject(DATA_LENGTH, len < 5 ? len + 1 : 1);
    }

    @Override
    public AxisAlignedBB getCollisionBox(Entity entity) {
        double size = 4;
        return AxisAlignedBB.getBoundingBox(1 - size, 1 - size, 1 - size, size, size, size);
    }

    @Override
    public float getMaxCartSpeedOnRail() {
        return 2.0F;
    }

    @Override
    public double getMountedYOffset() {
        return 0.0;
    }

    @Override
    public float getLinkageDistance(EntityMinecart cart) {
        return 3.5F;
    }

    @Override
    public float getOptimalDistance(EntityMinecart cart) {
        return 3.0F;
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
        ItemStack itemstack = new ItemStack(ItemLoader.itemNSPCT5L, 1);
        itemstack.setStackDisplayName(itemstack.getDisplayName());
        if (!source.damageType.equals("nsr")) this.entityDropItem(itemstack, 0.0F);
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
