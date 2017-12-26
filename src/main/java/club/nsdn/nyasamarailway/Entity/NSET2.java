package club.nsdn.nyasamarailway.Entity;

import club.nsdn.nyasamarailway.Items.ItemLoader;
import club.nsdn.nyasamarailway.Network.TrainPacket;
import club.nsdn.nyasamarailway.Util.TrainController;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2017.9.16.
 */
public class NSET2 extends LocoBase {

    public NSET2(World world) {
        super(world);
    }

    public NSET2(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    protected boolean isHighSpeed() {
        return true;
    }

    @Override
    public float getLinkageDistance(EntityMinecart cart) {
        return 2.0F;
    }

    @Override
    public float getOptimalDistance(EntityMinecart cart) {
        return 1.5F;
    }

    @Override
    protected void doEngine() {
        //Do engine code
        tmpPacket = new TrainPacket(this.getEntityId(), getEnginePower(), getEngineBrake(), getEngineDir());
        tmpPacket.isUnits = isHighSpeed();
        tmpPacket.Velocity = this.Velocity;
        TrainController.doMotionWithAir(tmpPacket, this);
        this.Velocity = tmpPacket.Velocity;
    }

    @Override
    public void killMinecart(DamageSource source)
    {
        this.setDead();
        ItemStack itemstack = new ItemStack(ItemLoader.itemNSET2, 1);
        itemstack.setStackDisplayName(itemstack.getDisplayName());
        this.entityDropItem(itemstack, 0.0F);
    }

}
