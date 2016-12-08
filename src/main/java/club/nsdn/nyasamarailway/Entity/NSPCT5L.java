package club.nsdn.nyasamarailway.Entity;

import club.nsdn.nyasamarailway.Items.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;

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

    @Override
    public float getMaxCartSpeedOnRail() {
        return 2.0F;
    }

    @Override
    public double getMountedYOffset() {
        return -0.4;
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
