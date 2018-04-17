package club.nsdn.nyasamarailway.entity.loco;

import club.nsdn.nyasamarailway.entity.IHighSpeedCart;
import club.nsdn.nyasamarailway.entity.IInspectionCart;
import club.nsdn.nyasamarailway.entity.ILimitVelCart;
import club.nsdn.nyasamarailway.entity.LocoBase;
import club.nsdn.nyasamarailway.item.ItemLoader;
import club.nsdn.nyasamarailway.item.tool.Item1N4148;
import club.nsdn.nyasamarailway.item.tool.ItemNTP32Bit;
import club.nsdn.nyasamarailway.item.tool.ItemNTP8Bit;
import club.nsdn.nyasamarailway.network.TrainPacket;
import club.nsdn.nyasamarailway.util.TrainController;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;

/**
 * Created by drzzm32 on 2018.4.17.
 */
public class NSPCT10J extends LocoBase implements ILimitVelCart, IHighSpeedCart, IInspectionCart {

    private final int INDEX_HIGH = 28;
    public boolean isHighSpeedMode = false;

    private final int INDEX_MV = 29;
    public double maxVelocity = 0;
    private int tmpEngineBrake = -1;

    public NSPCT10J(World world) {
        super(world);
        ignoreFrustumCheck = true;
        setSize(1.75F, 1.0F);
    }

    public NSPCT10J(World world, double x, double y, double z) {
        super(world, x, y, z);
        ignoreFrustumCheck = true;
        setSize(1.75F, 1.0F);
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return boundingBox;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(INDEX_HIGH, Integer.valueOf("0"));
        this.dataWatcher.addObject(INDEX_MV, Float.valueOf("0"));
    }

    public void modifyHighSpeedMode(EntityPlayer player) {
        setHighSpeedMode(!getHighSpeedMode());
        player.addChatComponentMessage(new ChatComponentTranslation(
                "info.nspc10j.mode", String.valueOf(getHighSpeedMode()).toUpperCase()));
    }

    public void setHighSpeedMode(boolean highSpeedMode) {
        this.isHighSpeedMode = highSpeedMode;
        this.dataWatcher.updateObject(INDEX_HIGH, highSpeedMode ? 1 : 0);
    }

    public boolean getHighSpeedMode() {
        return this.dataWatcher.getWatchableObjectInt(INDEX_HIGH) > 0;
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
        setHighSpeedMode(tagCompound.getBoolean("HighSpeed"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setBoolean("HighSpeed", getHighSpeedMode());
    }

    @Override
    protected boolean isHighSpeed() {
        return true;
    }

    @Override
    public float getMaxCartSpeedOnRail() {
        return 8.0F;
    }

    @Override
    public double getMountedYOffset() {
        return getHighSpeedMode() ? 0.1 : 0.5;
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
                        stack.getItem() instanceof ItemNTP8Bit ||
                        stack.getItem() instanceof ItemNTP32Bit) {
                        return true;
                    }
                    if (stack.getItem() instanceof ItemMinecart) return true;
                }
                if (!this.worldObj.isRemote) {
                    player.mountEntity(this);
                    player.addChatComponentMessage(new ChatComponentTranslation("info.nsr.x"));
                }
            }
            return true;
        }
    }

    @Override
    protected void doEngine() {
        tmpPacket = new TrainPacket(getEnginePower(), getEngineBrake(), getEngineDir());
        tmpPacket.highSpeed = isHighSpeed();
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
        if (getHighSpeedMode())
            TrainController.doMotionWithAirHighEx(tmpPacket, this);
        else {
            TrainController.doMotionWithAir(tmpPacket, this);
        }
        setEnginePrevVel(this.Velocity);
        setEngineVel(tmpPacket.Velocity);
    }

    @Override
    public void killMinecart(DamageSource source) {
        this.setDead();
        ItemStack itemstack = new ItemStack(ItemLoader.itemNSPCT8J, 1);
        itemstack.setStackDisplayName(itemstack.getDisplayName());
        if (!source.damageType.equals("nsr")) this.entityDropItem(itemstack, 0.0F);
    }

}
