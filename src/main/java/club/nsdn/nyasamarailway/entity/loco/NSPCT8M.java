package club.nsdn.nyasamarailway.entity.loco;

import club.nsdn.nyasamarailway.entity.ILimitVelCart;
import club.nsdn.nyasamarailway.entity.LocoBase;
import club.nsdn.nyasamarailway.item.tool.Item1N4148;
import club.nsdn.nyasamarailway.item.ItemLoader;
import club.nsdn.nyasamarailway.item.tool.ItemTrainController32Bit;
import club.nsdn.nyasamarailway.item.tool.ItemTrainController8Bit;
import club.nsdn.nyasamarailway.util.TrainController;
import club.nsdn.nyasamarailway.network.TrainPacket;
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
 * Created by drzzm32 on 2017.10.5.
 */
public class NSPCT8M extends LocoBase implements ILimitVelCart {

    private final int INDEX_MV = 28;
    public double maxVelocity = 0;
    private int tmpEngineBrake = 0;

    public NSPCT8M(World world) {
        super(world);
        ignoreFrustumCheck = true;
    }

    public NSPCT8M(World world, double x, double y, double z) {
        super(world, x, y, z);
        ignoreFrustumCheck = true;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(INDEX_MV, Float.valueOf("0"));
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
    protected boolean isHighSpeed() {
        return true;
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
    protected void doEngine() {
        tmpPacket = new TrainPacket(this.getEntityId(), getEnginePower(), getEngineBrake(), getEngineDir());
        tmpPacket.isUnits = isHighSpeed();
        tmpPacket.Velocity = this.Velocity;
        if (this.maxVelocity > 0) {
            if (this.Velocity > this.maxVelocity && tmpEngineBrake == 0) {
                tmpEngineBrake = getEngineBrake();
                setEngineBrake(1);
            } else if (this.Velocity <= this.maxVelocity && tmpEngineBrake != 0) {
                setEngineBrake(tmpEngineBrake);
                tmpEngineBrake = 0;
            }
        }
        TrainController.doMotionWithAir(tmpPacket, this);
        this.Velocity = tmpPacket.Velocity;
    }

    @Override
    public void killMinecart(DamageSource source)
    {
        this.setDead();
        ItemStack itemstack = new ItemStack(ItemLoader.itemNSPCT8M, 1);
        itemstack.setStackDisplayName(itemstack.getDisplayName());
        this.entityDropItem(itemstack, 0.0F);
    }

}
