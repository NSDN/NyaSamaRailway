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
 * Created by drzzm32 on 2016.12.5.
 */
public class NSPCT5 extends MinecartBase {

    public int cartLength;

    public NSPCT5(World world) { super(world); ignoreFrustumCheck = true; cartLength = 3; }

    public NSPCT5(World world, double x, double y, double z) {
        super(world, x, y, z);
        ignoreFrustumCheck = true;
        cartLength = 3;
    }

    @Override
    public float getMaxCartSpeedOnRail() {
        return 2.0F;
    }

    @Override
    public double getMountedYOffset() {
        return -0.1;
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
    public boolean interactFirst(EntityPlayer player) {
        if (MinecraftForge.EVENT_BUS.post(new MinecartInteractEvent(this, player))) {
            return true;
        } else if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer && this.riddenByEntity != player) {
            return true;
        } else if (this.riddenByEntity != null && this.riddenByEntity != player) {
            return false;
        } else {
            if (player != null) {
                ItemStack stack = player.getCurrentEquippedItem();
                if (stack != null) {
                    if (stack.getItem() instanceof ItemStationSign) {
                        cartLength = cartLength < 5 ? cartLength + 1 : 1;
                        return true;
                    }
                    if (stack.getItem() instanceof Item74HC04 ||
                            stack.getItem() instanceof ItemTrainController8Bit ||
                            stack.getItem() instanceof ItemTrainController32Bit) {
                        return true;
                    }
                }
                if (!this.worldObj.isRemote) {
                    player.mountEntity(this);
                }
            }
            return true;
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);
        this.cartLength = tagCompound.getInteger("cartLength");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setInteger("cartLength", this.cartLength);
    }

}
