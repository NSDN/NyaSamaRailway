package club.nsdn.nyasamarailway.entity.loco;

import club.nsdn.nyasamarailway.api.cart.AbsLimLoco;
import club.nsdn.nyasamarailway.api.cart.IHighSpeedCart;
import club.nsdn.nyasamarailway.item.tool.Item1N4148;
import club.nsdn.nyasamarailway.item.tool.ItemNTP32Bit;
import club.nsdn.nyasamarailway.item.tool.ItemNTP8Bit;
import club.nsdn.nyasamarailway.network.TrainPacket;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class NSPCT10M extends AbsLimLoco implements IHighSpeedCart {

    public boolean isHighSpeedMode = false;
    private static final DataParameter<Boolean> HIGH = EntityDataManager.createKey(NSPCT10M.class, DataSerializers.BOOLEAN);

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

    public NSPCT10M(World world) {
        super(world);
        ignoreFrustumCheck = true;
        setSize(1.75F, 1.0F);
    }

    public NSPCT10M(World world, double x, double y, double z) {
        super(world, x, y, z);
        ignoreFrustumCheck = true;
        setSize(1.75F, 1.0F);
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
                if (stack.getItem() instanceof Item1N4148 ||
                        stack.getItem() instanceof ItemNTP8Bit ||
                        stack.getItem() instanceof ItemNTP32Bit) {
                    return true;
                }
                if (stack.getItem() instanceof ItemMinecart) return true;
            }
            if (!this.world.isRemote) {
                player.startRiding(this);
                Util.say(player, "info.nsr.x");
            }

            return true;
        }
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
        double x = this.posX, z = this.posZ;
        double y = this.posY + this.getMountedYOffset() + entity.getYOffset();
        if (this.isPassenger(entity)) {
            double index = (double) getPassengers().indexOf(entity);
            double distX = 1.0, distZ = 0.5;
            double vx, vz;
            vx = distX * Math.cos(index * Math.PI / 2 - Math.PI / 4);
            vz = distZ * Math.sin(index * Math.PI / 2 - Math.PI / 4);
            Vec3d vec = (new Vec3d(vx, 0.0D, vz)).rotateYaw(-rotationYaw * 0.017453292F + 3.1415926F);
            entity.setPosition(x + vec.x, y, z + vec.z);
        }
    }

}
