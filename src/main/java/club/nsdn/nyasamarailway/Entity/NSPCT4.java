package club.nsdn.nyasamarailway.Entity;

import club.nsdn.nyasamarailway.Items.ItemLoader;
import club.nsdn.nyasamarailway.Network.TrainPacket;
import club.nsdn.nyasamarailway.TileEntities.Rail.RailMonoMagnetBase;
import club.nsdn.nyasamarailway.Util.TrainController;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2016.12.1.
 */
public class NSPCT4 extends MinecartBase implements IMotorCart, ILimitVelCart {

    public double shiftY = -1.0;

    public int P;
    public int R;
    public int Dir;
    public double Velocity;
    public boolean motorState;

    private final int INDEX_P = 23, INDEX_R = 24, INDEX_DIR = 25, INDEX_V = 26, INDEX_STE = 27;

    private final int INDEX_MV = 28;
    public double maxVelocity = 0;
    private int tmpMotorBrake = 0;

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(INDEX_P, Integer.valueOf("0"));
        this.dataWatcher.addObject(INDEX_R, Integer.valueOf("0"));
        this.dataWatcher.addObject(INDEX_DIR, Integer.valueOf("0"));
        this.dataWatcher.addObject(INDEX_V, Float.valueOf("0"));
        this.dataWatcher.addObject(INDEX_STE, Integer.valueOf("0"));

        this.dataWatcher.addObject(INDEX_MV, Float.valueOf("0"));
    }

    public NSPCT4(World world) {
        super(world);
    }

    public NSPCT4(World world, double x, double y, double z) {
        super(world, x, y, z);
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
        return 0.3 + shiftY;
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

    @Override
    public void killMinecart(DamageSource source)
    {
        this.setDead();
        ItemStack itemstack = new ItemStack(ItemLoader.itemNSPCT4, 1);
        itemstack.setStackDisplayName(itemstack.getDisplayName());
        this.entityDropItem(itemstack, 0.0F);
    }

    @Override
    protected void applyDrag() {
        if (this.motorState) {
            TrainPacket tmpPacket = new TrainPacket(this.getEntityId(), getMotorPower(), getMotorBrake(), getMotorDir());
            tmpPacket.isUnits = true; //High speed
            tmpPacket.Velocity = getMotorVel();
            if (this.maxVelocity > 0) {
                if (this.Velocity > this.maxVelocity && tmpMotorBrake == 0) {
                    tmpMotorBrake = getMotorBrake();
                    setMotorBrake(1);
                } else if (this.Velocity <= this.maxVelocity && tmpMotorBrake != 0) {
                    setMotorBrake(tmpMotorBrake);
                    tmpMotorBrake = 0;
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

    @Override
    public void onUpdate() {
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.posY);
        int z = MathHelper.floor_double(this.posZ);
        if (worldObj.getBlock(x, y, z) instanceof RailMonoMagnetBase) {
            if (shiftY > -1.0) shiftY -= 0.05;
        } else {
            if (worldObj.getBlock(x + 1, y, z) instanceof RailMonoMagnetBase) return;
            if (worldObj.getBlock(x - 1, y, z) instanceof RailMonoMagnetBase) return;
            if (worldObj.getBlock(x, y, z + 1) instanceof RailMonoMagnetBase) return;
            if (worldObj.getBlock(x, y, z - 1) instanceof RailMonoMagnetBase) return;

            if (shiftY < 0) shiftY += 0.05;
        }

        super.onUpdate();
    }
}
