package club.nsdn.nyasamarailway.entity.loco;

import club.nsdn.nyasamarailway.entity.LocoBase;
import club.nsdn.nyasamarailway.item.ItemLoader;
import club.nsdn.nyasamarailway.network.TrainPacket;
import club.nsdn.nyasamarailway.util.TrainController;
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
        return 3.0F;
    }

    @Override
    public float getOptimalDistance(EntityMinecart cart) {
        return 1.5F;
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
        ItemStack itemstack = new ItemStack(ItemLoader.itemNSET2, 1);
        itemstack.setStackDisplayName(itemstack.getDisplayName());
        if (!source.damageType.equals("nsr")) this.entityDropItem(itemstack, 0.0F);
    }

}
