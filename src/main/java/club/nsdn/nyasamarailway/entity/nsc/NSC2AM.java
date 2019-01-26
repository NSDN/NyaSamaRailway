package club.nsdn.nyasamarailway.entity.nsc;

import club.nsdn.nyasamarailway.entity.IExtendedInfoCart;
import club.nsdn.nyasamarailway.entity.ILimitVelCart;
import club.nsdn.nyasamarailway.entity.LocoBase;
import club.nsdn.nyasamarailway.item.ItemLoader;
import club.nsdn.nyasamarailway.item.tool.Item1N4148;
import club.nsdn.nyasamarailway.item.tool.ItemNTP32Bit;
import club.nsdn.nyasamarailway.item.tool.ItemNTP8Bit;
import club.nsdn.nyasamarailway.network.TrainPacket;
import club.nsdn.nyasamarailway.util.HashMap;
import club.nsdn.nyasamarailway.util.TrainController;
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
 * Created by drzzm32 on 2019.1.24.
 */
public class NSC2AM extends LocoBase implements ILimitVelCart, IExtendedInfoCart {

    private final int INDEX_MV = 28;
    public double maxVelocity = 0;
    private int tmpEngineBrake = -1;

    private final int INDEX_EXT = 29;
    public String extenedInfo = "";

    public NSC2AM(World world) {
        super(world);
        ignoreFrustumCheck = true;
    }

    public NSC2AM(World world, double x, double y, double z) {
        super(world, x, y, z);
        ignoreFrustumCheck = true;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(INDEX_MV, Float.valueOf("0"));
        this.dataWatcher.addObject(INDEX_EXT, String.valueOf(""));
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
    protected void readEntityFromNBT(NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);
        setMaxVelocity(tagCompound.getDouble("LocoMV"));
        setExtendedInfo(tagCompound.getString("ExtendedInfo"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setDouble("LocoMV", getMaxVelocity());
        tagCompound.setString("ExtendedInfo", getExtendedInfo());
    }

    @Override
    protected boolean isHighSpeed() {
        return true;
    }

    @Override
    public float getMaxCartSpeedOnRail() {
        return 5.0F;
    }

    @Override
    public double getMountedYOffset() {
        return -0.3;
    }

    @Override
    public boolean shouldRiderSit() {
        return true;
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
        TrainController.doMotionWithEuler(tmpPacket, this, getMaxCartSpeedOnRail());
        setEnginePrevVel(this.Velocity);
        setEngineVel(tmpPacket.Velocity);
    }

    @Override
    public void killMinecart(DamageSource source)
    {
        this.setDead();
        ItemStack itemstack = new ItemStack(ItemLoader.itemNSC2AM, 1);
        itemstack.setStackDisplayName(itemstack.getDisplayName());
        if (!source.damageType.equals("nsr")) this.entityDropItem(itemstack, 0.0F);
    }

}
