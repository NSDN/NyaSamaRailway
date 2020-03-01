package club.nsdn.nyasamarailway.entity.cart;

import club.nsdn.nyasamarailway.api.cart.AbsMotoCart;
import club.nsdn.nyasamarailway.api.cart.CartUtil;
import club.nsdn.nyasamarailway.api.cart.IExtendedInfoCart;
import club.nsdn.nyasamarailway.api.cart.IHighSpeedCart;
import club.nsdn.nyasamarailway.api.item.IController;
import club.nsdn.nyasamarailway.network.TrainPacket;
import club.nsdn.nyasamarailway.util.HashMap;
import club.nsdn.nyasamarailway.util.TrainController;
import club.nsdn.nyasamatelecom.api.util.Util;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class NSPCT10 extends AbsMotoCart implements IHighSpeedCart, IExtendedInfoCart {

    public boolean isHighSpeedMode = false;
    private static final DataParameter<Boolean> HIGH = EntityDataManager.createKey(NSPCT10.class, DataSerializers.BOOLEAN);

    public String extenedInfo = "";
    private static final DataParameter<String> EXT = EntityDataManager.createKey(NSPCT10.class, DataSerializers.STRING);

    @Override
    protected void entityInit() {
        super.entityInit();

        dataManager.register(HIGH, false);
        dataManager.register(EXT, "");
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

    public String getExtendedInfo() {
        return dataManager.get(EXT);
    }

    public void setExtendedInfo(String info) {
        this.extenedInfo = info;
        dataManager.set(EXT, info);
    }

    @Override
    public String getExtendedInfo(String key) {
        HashMap info = new HashMap();
        info.fromString(getExtendedInfo());
        if (info.containsKey(key))
            return info.get(key);
        return "";
    }

    @Override
    public void setExtendedInfo(String key, String data) {
        HashMap info = new HashMap();
        info.fromString(getExtendedInfo());
        info.remove(key);
        info.put(key, data);
        setExtendedInfo(info.toString());
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);

        setHighSpeedMode(tagCompound.getBoolean("HighSpeed"));
        setExtendedInfo(tagCompound.getString("ExtendedInfo"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);

        tagCompound.setBoolean("HighSpeed", getHighSpeedMode());
        tagCompound.setString("ExtendedInfo", getExtendedInfo());
    }

    public NSPCT10(World world) {
        super(world);
        ignoreFrustumCheck = true;
        setSize(1.75F, 1.0F);
    }

    public NSPCT10(World world, double x, double y, double z) {
        super(world, x, y, z);
        ignoreFrustumCheck = true;
        setSize(1.75F, 1.0F);
    }

    @Override
    public boolean processInitialInteract(@Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
        if (super.processInitialInteract(player, hand)) {
            return true;
        } else if (player.isSneaking()) {
            return false;
        } else if (!canFitPassenger(player)) {
            return true;
        } else {
            ItemStack stack = player.getHeldItemMainhand();
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof IController)
                    return true;
                if (stack.getItem() instanceof ItemMinecart)
                    return true;
            }
            if (!this.world.isRemote) {
                player.startRiding(this);
                Util.say(player, "info.nsr.x");
            }

            return true;
        }
    }

    @Override
    public float getMaxCartSpeedOnRail() {
        return 6.0F;
    }

    @Override
    public double getMountedYOffset() {
        return getHighSpeedMode() ? 0.4 : 0.8;
    }

    @Override
    public boolean shouldRiderSit() {
        return getHighSpeedMode();
    }

    @Override
    public float getLinkageDistance(EntityMinecart cart) {
        return 5.0F;
    }

    @Override
    public float getOptimalDistance(EntityMinecart cart) {
        return 3.0F;
    }

    @Nonnull
    @Override
    public ItemStack getCartItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public void doMotion(TrainPacket packet, EntityMinecart cart) {
        if (getHighSpeedMode())
            TrainController.doMotionWithAirHigh(packet, cart);
        else {
            TrainController.doMotionWithAir(packet, cart);
        }
    }

    @Override
    public int getMaxPassengerSize() {
        return 4;
    }

    @Override // Called by rider
    public void updatePassenger(Entity entity) {
        CartUtil.updatePassenger4(this, entity);
    }

    @Override
    public boolean hasWatchDog() {
        return true;
    }

    @Override
    public boolean feedWatchDog() {
        if (getMotorState()) {
            return hasPassenger();
        } else if (isMoving()) {
            if (getMotorBrake() > 1)
                return hasPassenger();
        }
        return true;
    }

}
