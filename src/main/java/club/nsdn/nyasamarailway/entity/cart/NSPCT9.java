package club.nsdn.nyasamarailway.entity.cart;

import club.nsdn.nyasamarailway.entity.IExtendedInfoCart;
import club.nsdn.nyasamarailway.entity.ILimitVelCart;
import club.nsdn.nyasamarailway.entity.IMotorCart;
import club.nsdn.nyasamarailway.entity.MinecartBase;
import club.nsdn.nyasamarailway.item.ItemLoader;
import club.nsdn.nyasamarailway.network.TrainPacket;
import club.nsdn.nyasamarailway.util.HashMap;
import club.nsdn.nyasamarailway.util.TrainController;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2018.3.21.
 */
public class NSPCT9 extends MinecartBase implements IMotorCart, ILimitVelCart, IExtendedInfoCart {

    public int P;
    public int R;
    public int Dir;
    public double Velocity;
    public boolean motorState;

    private final int INDEX_P = 23, INDEX_R = 24, INDEX_DIR = 25, INDEX_V = 26, INDEX_STE = 27;

    private final int INDEX_MV = 28;
    public double maxVelocity = 0;
    private int tmpMotorBrake = -1;

    private final int INDEX_EXT = 29;
    public String extenedInfo = "";

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(INDEX_P, Integer.valueOf("0"));
        this.dataWatcher.addObject(INDEX_R, Integer.valueOf("0"));
        this.dataWatcher.addObject(INDEX_DIR, Integer.valueOf("0"));
        this.dataWatcher.addObject(INDEX_V, Float.valueOf("0"));
        this.dataWatcher.addObject(INDEX_STE, Integer.valueOf("0"));

        this.dataWatcher.addObject(INDEX_MV, Float.valueOf("0"));

        this.dataWatcher.addObject(INDEX_EXT, String.valueOf(""));
    }

    public NSPCT9(World world) {
        super(world);
        ignoreFrustumCheck = true;
    }

    public NSPCT9(World world, double x, double y, double z) {
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
        return -0.3;
    }

    @Override
    public float getLinkageDistance(EntityMinecart cart) {
        return 1.0F;
    }

    @Override
    public float getOptimalDistance(EntityMinecart cart) {
        return 0.7F;
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
    public void setMotorVel(double vel) {
        this.Velocity = (float) vel;
        this.dataWatcher.updateObject(INDEX_V, (float) vel);
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
    public double getMotorVel() {
        return this.dataWatcher.getWatchableObjectFloat(INDEX_V);
    }

    @Override
    public boolean getMotorState() {
        return this.dataWatcher.getWatchableObjectInt(INDEX_STE) > 0;
    }

    @Override
    public double getMaxVelocity() {
        return this.dataWatcher.getWatchableObjectFloat(INDEX_MV);
    }

    @Override
    public void setMaxVelocity(double value) {
        this.maxVelocity = (float) value;
        this.dataWatcher.updateObject(INDEX_MV, (float) value);
    }

    public String getExtendedInfo() {
        return this.dataWatcher.getWatchableObjectString(INDEX_EXT);
    }

    public void setExtendedInfo(String info) {
        this.extenedInfo = info;
        this.dataWatcher.updateObject(INDEX_EXT, info);
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
        if (info.containsKey(key)) info.remove(key);
        info.put(key, data);
        setExtendedInfo(info.toString());
    }

    @Override
    public void killMinecart(DamageSource source)
    {
        this.setDead();
        ItemStack itemstack = new ItemStack(ItemLoader.itemNSPCT9, 1);
        itemstack.setStackDisplayName(itemstack.getDisplayName());
        if (!source.damageType.equals("nsr")) this.entityDropItem(itemstack, 0.0F);
    }

    @Override
    protected void applyDrag() {
        if (this.motorState) {
            TrainPacket tmpPacket = new TrainPacket(getMotorPower(), getMotorBrake(), getMotorDir());
            tmpPacket.highSpeed = true; //High speed
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
        setMotorVel(tagCompound.getDouble("MotorV"));
        setMotorState(tagCompound.getBoolean("MotorState"));

        setMaxVelocity(tagCompound.getDouble("MotorMaxV"));

        setExtendedInfo(tagCompound.getString("ExtendedInfo"));
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

        tagCompound.setString("ExtendedInfo", getExtendedInfo());
    }


}
