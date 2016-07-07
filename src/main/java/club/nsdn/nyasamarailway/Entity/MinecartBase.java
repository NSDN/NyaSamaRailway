package club.nsdn.nyasamarailway.Entity;

import club.nsdn.nyasamarailway.Items.Item74HC04;
import club.nsdn.nyasamarailway.Items.ItemTrainController32Bit;
import club.nsdn.nyasamarailway.Items.ItemTrainController8Bit;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;

/**
 * Created by drzzm32 on 2016.5.23.
 */
public class MinecartBase extends EntityMinecartEmpty implements ITrainLinkable {

    //public int prevLinkTrain = -1;
    public int nextLinkTrain = -1;

    public MinecartBase(World world) { super(world); }

    public MinecartBase(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public boolean interactFirst(EntityPlayer player) {
        if(MinecraftForge.EVENT_BUS.post(new MinecartInteractEvent(this, player))) {
            return true;
        } else if(this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer && this.riddenByEntity != player) {
            return true;
        } else if(this.riddenByEntity != null && this.riddenByEntity != player) {
            return false;
        } else if (player.getCurrentEquippedItem().getItem() instanceof Item74HC04 ||
                player.getCurrentEquippedItem().getItem() instanceof ItemTrainController8Bit ||
                player.getCurrentEquippedItem().getItem() instanceof ItemTrainController32Bit) {
            return true;
        } else {
            if(!this.worldObj.isRemote) {
                player.mountEntity(this);
            }

            return true;
        }
    }

    @Override
    public int getMinecartType() { return -1; }

    @Override
    public double getMountedYOffset() {
        return -0.1;
    }

    @Override
    public float getMaxCartSpeedOnRail() {
        return 2.0F;
    }

    public int getPrevTrainID() {
        return -1;
    }

    public int getNextTrainID() {
        return this.nextLinkTrain;
    }

    public boolean LinkTrain(int ID) {
        /*
        if (this.prevLinkTrain == -1) {
            this.prevLinkTrain = ID;
        } else 
        */
        if (this.nextLinkTrain == -1) {
            this.nextLinkTrain = ID;
        } else {
            return false;
        }
        return true;
    }

    public void deLinkTrain(int ID) {
        /*
        if (this.prevLinkTrain == ID) {
            this.prevLinkTrain = -1;
        } else 
        */
        if (this.nextLinkTrain == ID) {
            this.nextLinkTrain = -1;
        }
    }

    double calcDist(EntityMinecart a, EntityMinecart b) {
        return Math.sqrt(Math.pow(a.posX - b.posX, 2) + Math.pow(a.posY - b.posY, 2) + Math.pow(a.posZ - b.posZ, 2));
    }
    
    public void calcLink(World world) {
        if (this.nextLinkTrain > 0 && world.getEntityByID(this.nextLinkTrain) != null) {
            EntityMinecart cart = (EntityMinecart) world.getEntityByID(this.nextLinkTrain);
            double Ks = 500.0;
            double Kd = 500.0;
            double m = 1.0;
            double length = 2.0;
            double dt = 0.001;

            double dist = calcDist(this, cart);
            double dv = Ks * (dist - length) / m * dt;
            double DdvX = Kd * (this.motionX - cart.motionX) / m * dt;
            double DdvZ = Kd * (this.motionZ - cart.motionZ) / m * dt;

            cart.motionX += dv * (this.posX - cart.posX) / dist + DdvX;
            cart.motionZ += dv * (this.posZ - cart.posZ) / dist + DdvZ;
            this.motionX += -dv * (this.posX - cart.posX) / dist - DdvX;
            this.motionZ += -dv * (this.posZ - cart.posZ) / dist - DdvZ;
        }
    }

    @Override
    protected void applyDrag() {
        //Do engine code
        calcLink(MinecraftServer.getServer().getEntityWorld());

        super.applyDrag();
    }
}
