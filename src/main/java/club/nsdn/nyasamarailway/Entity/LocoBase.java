package club.nsdn.nyasamarailway.Entity;

import club.nsdn.nyasamarailway.TrainControl.TrainController;
import club.nsdn.nyasamarailway.TrainControl.TrainPacket;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;

/**
 * Created by drzzm32 on 2016.6.23.
 */

public class LocoBase extends EntityMinecart implements ITrainLinkable {

    public int P;
    public int R;
    public int Dir;
    public double Velocity;

    //public int prevLinkTrain = -1;
    public int nextLinkTrain = -1;

    protected TrainPacket tmpPacket;

    public LocoBase(World world) { super(world); }

    public LocoBase(World world, double x, double y, double z) {
        super(world, x, y, z);
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

    public void setTrainPacket(TrainPacket packet) {
        if (this.getEntityId() == packet.cartID) {
            this.P = packet.P;
            this.R = packet.R;
            this.Dir = packet.Dir;
        }

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
            double length = 1.5;
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
    protected void func_145821_a(int x, int y, int z, double v1, double v, Block block, int meta) {
        //applyPush
        super.func_145821_a(x, y, z, v1, v, block, meta);
    }

    @Override
    protected void applyDrag() {
        //Do engine code
        tmpPacket = new TrainPacket(this.getEntityId(), this.P, this.R, this.Dir);
        tmpPacket.Velocity = this.Velocity;
        TrainController.doMotion(tmpPacket, this);
        this.Velocity = tmpPacket.Velocity;

        calcLink(MinecraftServer.getServer().getEntityWorld());

        super.applyDrag();
    }

    @Override
    public boolean interactFirst(EntityPlayer player) {
        return MinecraftForge.EVENT_BUS.post(new MinecartInteractEvent(this, player));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("LocoP", this.P);
        compound.setInteger("LocoR", this.R);
        compound.setInteger("LocoDir", this.Dir);
        compound.setDouble("LocoV", this.Velocity);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.P = compound.getInteger("LocoP");
        this.R = compound.getInteger("LocoR");
        this.Dir = compound.getInteger("LocoDir");
        this.Velocity = compound.getDouble("LocoV");
    }

}
