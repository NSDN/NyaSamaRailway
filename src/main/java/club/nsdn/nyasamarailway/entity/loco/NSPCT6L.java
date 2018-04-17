package club.nsdn.nyasamarailway.entity.loco;

import club.nsdn.nyasamarailway.entity.LocoBase;
import club.nsdn.nyasamarailway.item.tool.Item1N4148;
import club.nsdn.nyasamarailway.item.ItemLoader;
import club.nsdn.nyasamarailway.item.tool.ItemNTP32Bit;
import club.nsdn.nyasamarailway.item.tool.ItemNTP8Bit;
import club.nsdn.nyasamarailway.network.TrainPacket;
import club.nsdn.nyasamarailway.util.TrainController;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;

/**
 * Created by drzzm32 on 2017.9.24.
 */
public class NSPCT6L extends LocoBase {

    public NSPCT6L(World world) {
        super(world);
        ignoreFrustumCheck = true;
        setSize(1.75F, 0.5F);
    }

    public NSPCT6L(World world, double x, double y, double z) {
        super(world, x, y, z);
        ignoreFrustumCheck = true;
        setSize(1.75F, 0.5F);
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return boundingBox;
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
        //Do engine code
        tmpPacket = new TrainPacket(getEnginePower(), getEngineBrake(), getEngineDir());
        tmpPacket.highSpeed = isHighSpeed();
        tmpPacket.Velocity = this.Velocity;
        TrainController.doMotionWithAir(tmpPacket, this);
        setEnginePrevVel(this.Velocity);
        setEngineVel(tmpPacket.Velocity);
    }

    @Override
    public void killMinecart(DamageSource source)
    {
        this.setDead();
        ItemStack itemstack = new ItemStack(ItemLoader.itemNSPCT6L, 1);
        itemstack.setStackDisplayName(itemstack.getDisplayName());
        if (!source.damageType.equals("nsr")) this.entityDropItem(itemstack, 0.0F);
    }

}
