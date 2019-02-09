package club.nsdn.nyasamarailway.api.cart;

import club.nsdn.nyasamarailway.network.TrainPacket;
import club.nsdn.nyasamarailway.util.TrainController;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2016.12.1.
 */
public abstract class AbsMotoCart extends AbsCartBase implements IMotorCart, ILimitVelCart {

    public int P;
    public int R;
    public int Dir;
    public double Velocity;
    public boolean motorState;
    public double maxVelocity = 0;

    private static final DataParameter<Integer> POWER = EntityDataManager.createKey(AbsMotoCart.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> BRAKE = EntityDataManager.createKey(AbsMotoCart.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> DIR = EntityDataManager.createKey(AbsMotoCart.class, DataSerializers.VARINT);
    private static final DataParameter<Float> VEL = EntityDataManager.createKey(AbsMotoCart.class, DataSerializers.FLOAT);
    private static final DataParameter<Boolean> STATE = EntityDataManager.createKey(AbsMotoCart.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Float> MAXV = EntityDataManager.createKey(AbsMotoCart.class, DataSerializers.FLOAT);

    private int tmpMotorBrake = -1;

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(POWER, 0);
        dataManager.register(BRAKE, 0);
        dataManager.register(DIR, 0);
        dataManager.register(VEL, 0.0F);
        dataManager.register(STATE, false);
        dataManager.register(MAXV, 0.0F);
    }

    public AbsMotoCart(World world) {
        super(world);
    }

    public AbsMotoCart(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public void setMotorPower(int power) {
        this.P = power;
        dataManager.set(POWER, power);
    }

    @Override
    public void setMotorBrake(int brake) {
        this.R = brake;
        dataManager.set(BRAKE, brake);
    }

    @Override
    public void setMotorState(boolean motorState) {
        this.motorState = motorState;
        dataManager.set(STATE, motorState);
    }

    @Override
    public void setMotorDir(int dir) {
        this.Dir = dir;
        dataManager.set(DIR, dir);
    }

    @Override
    public void setMotorVel(double vel) {
        this.Velocity = (float) vel;
        dataManager.set(VEL, (float) vel);
    }

    @Override
    public int getMotorPower() {
        return dataManager.get(POWER);
    }

    @Override
    public int getMotorBrake() {
        return dataManager.get(BRAKE);
    }

    @Override
    public int getMotorDir() {
        return dataManager.get(DIR);
    }

    @Override
    public double getMotorVel() {
        return dataManager.get(VEL);
    }

    @Override
    public boolean getMotorState() {
        return dataManager.get(STATE);
    }

    @Override
    public double getMaxVelocity() {
        return dataManager.get(MAXV);
    }

    @Override
    public void setMaxVelocity(double value) {
        this.maxVelocity = (float) value;
        dataManager.set(MAXV, (float) value);
    }

    public abstract void doMotion(TrainPacket packet, EntityMinecart cart);

    @Override
    protected void applyDrag() {
        if (this.motorState) {
            TrainPacket tmpPacket = new TrainPacket(getMotorPower(), getMotorBrake(), getMotorDir());
            tmpPacket.Velocity = this.Velocity;
            if (this.maxVelocity > 0) {
                if (this.Velocity > this.maxVelocity && tmpMotorBrake == -1) {
                    tmpMotorBrake = getMotorBrake();
                    setMotorBrake(1);
                } else if (this.Velocity > this.maxVelocity && tmpMotorBrake != -1) {
                    setMotorBrake(1);
                } else if (this.Velocity <= this.maxVelocity && tmpMotorBrake != -1) {
                    setMotorBrake(tmpMotorBrake);
                    tmpMotorBrake = -1;
                }
            }
            doMotion(tmpPacket, this);
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
        setMotorVel(tagCompound.getDouble("MotorV"));
        setMotorState(tagCompound.getBoolean("MotorState"));

        setMaxVelocity(tagCompound.getDouble("MotorMaxV"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setInteger("MotorP", getMotorPower());
        tagCompound.setInteger("MotorR", getMotorBrake());
        tagCompound.setInteger("MotorDir", getMotorDir());
        tagCompound.setDouble("MotorV", getMotorVel());
        tagCompound.setBoolean("MotorState", getMotorState());

        tagCompound.setDouble("MotorMaxV", getMaxVelocity());
    }

}
