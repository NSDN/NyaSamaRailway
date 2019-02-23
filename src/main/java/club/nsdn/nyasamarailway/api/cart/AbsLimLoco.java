package club.nsdn.nyasamarailway.api.cart;

import club.nsdn.nyasamarailway.item.tool.Item1N4148;
import club.nsdn.nyasamarailway.item.tool.Item74HC04;
import club.nsdn.nyasamarailway.item.tool.ItemNTP32Bit;
import club.nsdn.nyasamarailway.item.tool.ItemNTP8Bit;
import club.nsdn.nyasamarailway.network.TrainPacket;
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
public abstract class AbsLimLoco extends AbsLocoBase implements ILimitVelCart {

    public double maxVelocity = 0;
    private static final DataParameter<Float> MAXV = EntityDataManager.createKey(AbsLimLoco.class, DataSerializers.FLOAT);

    private int tmpEngineBrake = -1;

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(MAXV, 0.0F);
    }

    public AbsLimLoco(World world) {
        super(world);
    }

    public AbsLimLoco(World world, double x, double y, double z) {
        super(world, x, y, z);
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

    @Override
    protected void readEntityFromNBT(NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);
        setMaxVelocity(tagCompound.getDouble("LocoMV"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setDouble("LocoMV", getMaxVelocity());
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
                if (
                        stack.getItem() instanceof Item74HC04 || stack.getItem() instanceof Item1N4148 ||
                        stack.getItem() instanceof ItemNTP8Bit || stack.getItem() instanceof ItemNTP32Bit
                ) {
                    return true;
                }
                if (stack.getItem() instanceof ItemMinecart) return true;
            }
            if (!this.world.isRemote) {
                player.startRiding(this);
            }

            return true;
        }
    }

    public int getMaxPassengerSize() {
        return 1;
    }

    @Override
    protected boolean canFitPassenger(Entity entity) {
        return getPassengers().size() < getMaxPassengerSize();
    }

    @Override // Called by rider
    public void updatePassenger(Entity entity) {
        double x = this.posX, z = this.posZ;
        double y = this.posY + this.getMountedYOffset() + entity.getYOffset();
        if (this.isPassenger(entity)) {
            if (getPassengers().size() >= 1) {
                entity.setPosition(x, y, z);
            }
        }
    }

    @Override
    @Nonnull
    public Type getType() {
        return Type.RIDEABLE;
    }

    public abstract void doMotion(TrainPacket packet, EntityMinecart cart);

    @Override
    protected void doEngine() {
        tmpPacket = new TrainPacket(getEnginePower(), getEngineBrake(), getEngineDir());
        tmpPacket.Velocity = this.Velocity;
        if (this.maxVelocity > 0) {
            if (this.Velocity > this.maxVelocity && tmpEngineBrake == -1) {
                tmpEngineBrake = getEngineBrake();
                setEngineBrake(1);
            } else if (this.Velocity > this.maxVelocity && tmpEngineBrake != -1) {
                setEngineBrake(1);
            } else if (this.Velocity <= this.maxVelocity && tmpEngineBrake != -1) {
                setEngineBrake(tmpEngineBrake);
                tmpEngineBrake = -1;
            }
        }
        doMotion(tmpPacket, this);
        setEnginePrevVel(this.Velocity);
        setEngineVel(tmpPacket.Velocity);
    }

}
