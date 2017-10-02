package club.nsdn.nyasamarailway.Entity;

import club.nsdn.nyasamarailway.Items.ItemLoader;
import club.nsdn.nyasamarailway.TrainControl.TrainController;
import club.nsdn.nyasamarailway.TrainControl.TrainPacket;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2017.10.1.
 */
public class NSPCT8 extends MinecartBase implements IMotorCart {

    public int P;
    public int R;
    public int Dir;
    public double Velocity;
    public boolean motorState;

    public NSPCT8(World world) {
        super(world);
        ignoreFrustumCheck = true;
    }

    public NSPCT8(World world, double x, double y, double z) {
        super(world, x, y, z);
        ignoreFrustumCheck = true;
    }

    @Override
    public boolean canMakePlayerTurn() {
        return false;
    }

    @Override
    public float getMaxCartSpeedOnRail() {
        return 2.0F;
    }

    @Override
    public double getMountedYOffset() {
        return 0.1;
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
    public void setMotorPower(int power) {
        this.P = power;
    }

    @Override
    public void setMotorBrake(int brake) {
        this.R = brake;
    }

    @Override
    public void setMotorState(boolean motorState) {
        this.motorState = motorState;
    }

    @Override
    public void killMinecart(DamageSource source)
    {
        this.setDead();
        ItemStack itemstack = new ItemStack(ItemLoader.itemNSPCT8, 1);
        itemstack.setStackDisplayName(itemstack.getDisplayName());
        this.entityDropItem(itemstack, 0.0F);
    }

    @Override
    protected void applyDrag() {
        if (this.motorState) {
            TrainPacket tmpPacket = new TrainPacket(this.getEntityId(), this.P, this.R, this.Dir);
            tmpPacket.isUnits = true; //High speed
            tmpPacket.Velocity = this.Velocity;
            TrainController.doMotionWithAir(tmpPacket, this);
            this.Velocity = tmpPacket.Velocity;
        } else {
            if (this.motionX != 0) this.Dir = (int) Math.signum(this.motionX / Math.cos(TrainController.calcYaw(this) * Math.PI / 180.0));
            else if (this.motionZ != 0) this.Dir = (int) Math.signum(this.motionZ / -Math.sin(TrainController.calcYaw(this) * Math.PI / 180.0));
            else this.Dir = 0;
        }

        super.applyDrag();
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);
        this.P = tagCompound.getInteger("MotorP");
        this.R = tagCompound.getInteger("MotorR");
        this.Dir = tagCompound.getInteger("MotorDir");
        this.Velocity = tagCompound.getDouble("MotorV");
        this.motorState = tagCompound.getBoolean("MotorState");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setInteger("MotorP", this.P);
        tagCompound.setInteger("MotorR", this.R);
        tagCompound.setInteger("MotorDir", this.Dir);
        tagCompound.setDouble("MotorV", this.Velocity);
        tagCompound.setBoolean("MotorState", this.motorState);
    }


}
