package club.nsdn.nyasamarailway.Entity;

import club.nsdn.nyasamarailway.Items.Item74HC04;
import club.nsdn.nyasamarailway.Items.ItemTrainController32Bit;
import club.nsdn.nyasamarailway.Items.ItemTrainController8Bit;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;

import org.thewdj.physics.Vec3d;

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
            double Ks = 400.0;
            double Kd = 400.0;
            double m = 1.0;
            double length = 2.0;
            double dt = 0.001;

            Vec3d sPos = Vec3d.fromEntityPos(this);
            Vec3d tPos = Vec3d.fromEntityPos(cart);
            Vec3d sV = Vec3d.fromEntityMotion(this);
            Vec3d tV = Vec3d.fromEntityMotion(cart);
            Vec3d SdV = new Vec3d(sPos.subtract(tPos).normalize()).dotProduct(
                    Ks * (calcDist(this, cart) - length) / m * dt
            );
            Vec3d DdV = new Vec3d(sV.subtract(tV)).dotProduct(Kd / m * dt);
            Vec3d dV = SdV.addVector(DdV);

            cart.motionX += -dV.xCoord;
            cart.motionY += -dV.yCoord;
            cart.motionZ += -dV.zCoord;

            this.motionX += dV.xCoord;
            this.motionY += dV.yCoord;
            this.motionZ += dV.zCoord;

            /*
            double dist = calcDist(this, cart);
            double dv = Ks * (dist - length) / m * dt;
            double DdvX = Kd * (this.motionX - cart.motionX) / m * dt;
            double DdvZ = Kd * (this.motionZ - cart.motionZ) / m * dt;

            cart.motionX += dv * (this.posX - cart.posX) / dist + DdvX;
            cart.motionZ += dv * (this.posZ - cart.posZ) / dist + DdvZ;
            this.motionX += -dv * (this.posX - cart.posX) / dist - DdvX;
            this.motionZ += -dv * (this.posZ - cart.posZ) / dist - DdvZ;
            */
        }
    }

    @Override
    protected void applyDrag() {
        //Do engine code
        calcLink(MinecraftServer.getServer().getEntityWorld());

        super.applyDrag();
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);
        this.nextLinkTrain = tagCompound.getInteger("nextLinkTrain");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setInteger("nextLinkTrain", this.nextLinkTrain);
    }
}
