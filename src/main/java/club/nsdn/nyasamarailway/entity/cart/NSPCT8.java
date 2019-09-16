package club.nsdn.nyasamarailway.entity.cart;

import club.nsdn.nyasamarailway.api.cart.AbsMotoCart;
import club.nsdn.nyasamarailway.api.cart.CartUtil;
import club.nsdn.nyasamarailway.api.cart.IHighSpeedCart;
import club.nsdn.nyasamarailway.util.TrainController;
import club.nsdn.nyasamarailway.network.TrainPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class NSPCT8 extends AbsMotoCart implements IHighSpeedCart {

    public boolean isHighSpeedMode = false;
    private static final DataParameter<Boolean> HIGH = EntityDataManager.createKey(NSPCT8.class, DataSerializers.BOOLEAN);

    @Override
    protected void entityInit() {
        super.entityInit();

        dataManager.register(HIGH, false);
    }

    public void modifyHighSpeedMode(EntityPlayer player) {
    }

    public void setHighSpeedMode(boolean highSpeedMode) {
        this.isHighSpeedMode = highSpeedMode;
        dataManager.set(HIGH, highSpeedMode);
    }

    public boolean getHighSpeedMode() {
        return dataManager.get(HIGH);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);
        setHighSpeedMode(tagCompound.getBoolean("HighSpeed"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setBoolean("HighSpeed", getHighSpeedMode());
    }

    public NSPCT8(World world) {
        super(world);
        ignoreFrustumCheck = true;
        setSize(1.0F, 1.0F);
    }

    public NSPCT8(World world, double x, double y, double z) {
        super(world, x, y, z);
        ignoreFrustumCheck = true;
        setSize(1.0F, 1.0F);
    }

    @Override
    public float getMaxCartSpeedOnRail() {
        return 3.5F;
    }

    @Override
    public double getMountedYOffset() {
        return 0.0;
    }

    @Override
    public float getLinkageDistance(EntityMinecart cart) {
        return 2.0F;
    }

    @Override
    public float getOptimalDistance(EntityMinecart cart) {
        return 1.6F;
    }

    @Nonnull
    @Override
    public ItemStack getCartItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public void doMotion(TrainPacket packet, EntityMinecart cart) {
        if (getHighSpeedMode())
            TrainController.doMotionWithAirEx(packet, cart);
        else {
            TrainController.doMotionWithAir(packet, cart);
        }
    }

    @Override
    public int getMaxPassengerSize() {
        return 2;
    }

    @Override // Called by rider
    public void updatePassenger(Entity entity) {
        CartUtil.updatePassenger2(this, entity);
    }

}
