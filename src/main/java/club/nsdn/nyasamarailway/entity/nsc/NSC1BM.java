package club.nsdn.nyasamarailway.entity.nsc;

import club.nsdn.nyasamarailway.entity.ILimitVelCart;
import club.nsdn.nyasamarailway.entity.LocoBase;
import club.nsdn.nyasamarailway.item.ItemLoader;
import club.nsdn.nyasamarailway.item.tool.Item1N4148;
import club.nsdn.nyasamarailway.item.tool.ItemNTP32Bit;
import club.nsdn.nyasamarailway.item.tool.ItemNTP8Bit;
import club.nsdn.nyasamarailway.network.TrainPacket;
import club.nsdn.nyasamarailway.tileblock.rail.mono.RailMonoMagnetBase;
import club.nsdn.nyasamarailway.util.TrainController;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;

/**
 * Created by drzzm32 on 2019.1.24.
 */
public class NSC1BM extends LocoBase implements ILimitVelCart, IMonoRailCart, IRotaCart {

    public double shiftY = -1.0;

    @Override
    public double getShiftY() {
        return shiftY;
    }

    private final int INDEX_MV = 28;
    public double maxVelocity = 0;
    private int tmpEngineBrake = -1;

    public float angle;

    @Override
    public float getAngle() {
        return angle;
    }

    @Override
    public void setAngle(float v) {
        angle = v % 360.0F;
    }

    public NSC1BM(World world) {
        super(world);
    }

    public NSC1BM(World world, double x, double y, double z) {
        super(world, x, y, z);
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
    public float getMaxCartSpeedOnRail() {
        return 8.0F;
    }

    @Override
    public double getMountedYOffset() {
        return 0.5 + shiftY;
    }

    @Override
    public boolean shouldRiderSit() {
        return true;
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
        ItemStack itemstack = new ItemStack(ItemLoader.itemNSC1BM, 1);
        itemstack.setStackDisplayName(itemstack.getDisplayName());
        if (!source.damageType.equals("nsr")) this.entityDropItem(itemstack, 0.0F);
    }

    @Override
    public void onUpdate() {
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.posY);
        int z = MathHelper.floor_double(this.posZ);
        if (worldObj.getBlock(x, y, z) instanceof RailMonoMagnetBase) {
            RailMonoMagnetBase rail = (RailMonoMagnetBase) worldObj.getBlock(x, y, z);
            int meta = worldObj.getBlockMetadata(x, y, z);
            if (rail.isPowered()) meta &= 0x7;
            if (meta >= 2 && meta <= 5) {
                if (shiftY < -0.5) shiftY += 0.05;
            } else if (shiftY > -1.0) shiftY -= 0.05;
        } else {
            boolean state;
            state = worldObj.getBlock(x + 1, y, z) instanceof RailMonoMagnetBase;
            state |= worldObj.getBlock(x - 1, y, z) instanceof RailMonoMagnetBase;
            state |= worldObj.getBlock(x, y, z + 1) instanceof RailMonoMagnetBase;
            state |= worldObj.getBlock(x, y, z - 1) instanceof RailMonoMagnetBase;

            if (!state && shiftY < 0) shiftY += 0.05;
        }

        super.onUpdate();
    }
}
