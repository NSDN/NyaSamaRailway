package club.nsdn.nyasamarailway.Entity;

import club.nsdn.nyasamarailway.Items.Item1N4148;
import club.nsdn.nyasamarailway.Items.ItemLoader;
import club.nsdn.nyasamarailway.Items.ItemTrainController32Bit;
import club.nsdn.nyasamarailway.Items.ItemTrainController8Bit;
import club.nsdn.nyasamarailway.TrainControl.TrainController;
import club.nsdn.nyasamarailway.TrainControl.TrainPacket;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;

/**
 * Created by drzzm32 on 2017.10.6.
 */
public class NSPCT8J extends LocoBase {

    public double prevVelocity;

    public NSPCT8J(World world) {
        super(world);
        ignoreFrustumCheck = true;
    }

    public NSPCT8J(World world, double x, double y, double z) {
        super(world, x, y, z);
        ignoreFrustumCheck = true;
    }

    final int V = 23, PV = 24;

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(V, Float.valueOf("0"));
        this.dataWatcher.addObject(PV, Float.valueOf("0"));
    }

    public double getVelocity() {
        return this.dataWatcher.getWatchableObjectFloat(V);
    }

    public void setVelocity(double value) {
        this.dataWatcher.updateObject(V, (float) value);
    }

    public double getPrevVelocity() {
        return this.dataWatcher.getWatchableObjectFloat(PV);
    }

    public void setPrevVelocity(double value) {
        this.dataWatcher.updateObject(PV, (float) value);
    }

    @Override
    protected boolean isHighSpeed() {
        return true;
    }

    @Override
    public float getMaxCartSpeedOnRail() {
        return 5.0F;
    }

    @Override
    public double getMountedYOffset() {
        return 0.1;
    }

    @Override
    public boolean shouldRiderSit() {
        return true;
    }

    @Override
    public float getLinkageDistance(EntityMinecart cart) {
        return 2.0F;
    }

    @Override
    public float getOptimalDistance(EntityMinecart cart) {
        return 1.6F;
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
                    if (stack.getItem() instanceof Item1N4148 ||
                            stack.getItem() instanceof ItemTrainController8Bit ||
                            stack.getItem() instanceof ItemTrainController32Bit) {
                        return true;
                    }
                    if (stack.getItem() instanceof ItemMinecart) return true;
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
        prevVelocity = tagCompound.getDouble("prevVelocity");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setDouble("prevVelocity", prevVelocity);
    }

    @Override
    protected void doEngine() {
        tmpPacket = new TrainPacket(this.getEntityId(), this.P, this.R, this.Dir);
        tmpPacket.isUnits = isHighSpeed();
        tmpPacket.Velocity = this.Velocity;
        TrainController.doMotionWithAir(tmpPacket, this);
        this.prevVelocity = this.Velocity;
        setPrevVelocity(this.prevVelocity);
        this.Velocity = tmpPacket.Velocity;
        setVelocity(this.Velocity);
    }

    @Override
    public void killMinecart(DamageSource source)
    {
        this.setDead();
        ItemStack itemstack = new ItemStack(ItemLoader.itemNSPCT8J, 1);
        itemstack.setStackDisplayName(itemstack.getDisplayName());
        this.entityDropItem(itemstack, 0.0F);
    }

}
