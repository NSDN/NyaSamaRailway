package club.nsdn.nyasamarailway.Entity;

import club.nsdn.nyasamarailway.Items.ItemLoader;
import club.nsdn.nyasamarailway.Util.TrainController;
import club.nsdn.nyasamarailway.Network.TrainPacket;
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
    public float Velocity;
    public boolean motorState;

    private final int INDEX_P = 23, INDEX_R = 24, INDEX_DIR = 25, INDEX_V = 26, INDEX_STE = 27;

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(INDEX_P, Integer.valueOf("0"));
        this.dataWatcher.addObject(INDEX_R, Integer.valueOf("0"));
        this.dataWatcher.addObject(INDEX_DIR, Integer.valueOf("0"));
        this.dataWatcher.addObject(INDEX_V, Float.valueOf("0"));
        this.dataWatcher.addObject(INDEX_STE, Integer.valueOf("0"));
    }

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
        this.dataWatcher.updateObject(INDEX_P, power);
    }

    @Override
    public void setMotorBrake(int brake) {
        this.R = brake;
        this.dataWatcher.updateObject(INDEX_R, brake);
    }

    @Override
    public void setMotorState(boolean motorState) {
        this.motorState = motorState;
        this.dataWatcher.updateObject(INDEX_STE, motorState ? 1 : 0);
    }

    @Override
    public void setMotorDir(int dir) {
        this.Dir = dir;
        this.dataWatcher.updateObject(INDEX_DIR, dir);
    }

    @Override
    public void setMotorVel(float vel) {
        this.Velocity = vel;
        this.dataWatcher.updateObject(INDEX_V, vel);
    }

    @Override
    public int getMotorPower() {
        return this.dataWatcher.getWatchableObjectInt(INDEX_P);
    }

    @Override
    public int getMotorBrake() {
        return this.dataWatcher.getWatchableObjectInt(INDEX_R);
    }

    @Override
    public int getMotorDir() {
        return this.dataWatcher.getWatchableObjectInt(INDEX_DIR);
    }

    @Override
    public float getMotorVel() {
        return this.dataWatcher.getWatchableObjectFloat(INDEX_V);
    }

    @Override
    public boolean getMotorState() {
        return this.dataWatcher.getWatchableObjectInt(INDEX_STE) > 0;
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
            TrainPacket tmpPacket = new TrainPacket(this.getEntityId(), getMotorPower(), getMotorBrake(), getMotorDir());
            tmpPacket.isUnits = true; //High speed
            tmpPacket.Velocity = getMotorVel();
            TrainController.doMotionWithAir(tmpPacket, this);
            setMotorVel((float) tmpPacket.Velocity);
        } else {
            if (this.motionX != 0) setMotorDir((int) Math.signum(this.motionX / Math.cos(TrainController.calcYaw(this) * Math.PI / 180.0)));
            else if (this.motionZ != 0) setMotorDir((int) Math.signum(this.motionZ / -Math.sin(TrainController.calcYaw(this) * Math.PI / 180.0)));
            else setMotorDir(0);
            setMotorVel((float) Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ));
        }

        super.applyDrag();
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);
        setMotorPower(tagCompound.getInteger("MotorP"));
        setMotorBrake(tagCompound.getInteger("MotorR"));
        setMotorDir(tagCompound.getInteger("MotorDir"));
        setMotorVel(tagCompound.getFloat("MotorV"));
        setMotorState(tagCompound.getBoolean("MotorState"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setInteger("MotorP", getMotorPower());
        tagCompound.setInteger("MotorR", getMotorBrake());
        tagCompound.setInteger("MotorDir", getMotorDir());
        tagCompound.setFloat("MotorV", getMotorVel());
        tagCompound.setBoolean("MotorState", getMotorState());
    }


}
