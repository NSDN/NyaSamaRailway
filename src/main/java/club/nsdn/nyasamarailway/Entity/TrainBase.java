package club.nsdn.nyasamarailway.Entity;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartUpdateEvent;

import java.util.LinkedList;

/**
 * Created by drzzm32 on 2016.5.24.
 */

public class TrainBase extends EntityMinecartEmpty {
    public LinkedList<Integer> bogies;
    public LinkedList<Double> bogiesDist;

    public TrainBase(World world) {
        super(world);
        bogies = new LinkedList<Integer>();
        bogiesDist = new LinkedList<Double>();
    }

    public TrainBase(World world, double x, double y, double z) {
        super(world, x, y, z);
        bogies = new LinkedList<Integer>();
        bogiesDist = new LinkedList<Double>();
    }

    public void addBogie(int index, int bogieID, double distance) {
        bogies.add(index, bogieID);
        bogiesDist.add(index, distance);
    }

    @Override
    public void killMinecart(DamageSource source) {
        this.setDead();
        for (int i : this.bogies) {
            if (i > 0)
                ((EntityMinecart) (this.worldObj.getEntityByID(i))).killMinecart(source);
        }
        ItemStack itemstack = new ItemStack(Items.minecart, 1);
        if (this.getCommandSenderName() != null) {
            itemstack.setStackDisplayName(this.getCommandSenderName());
        }

        this.entityDropItem(itemstack, 0.0F);
    }

    @Override
    public int getMinecartType() {
        return -1;
    }

    @Override
    public void onUpdate() {
        if (this.getRollingAmplitude() > 0) {
            this.setRollingAmplitude(this.getRollingAmplitude() - 1);
        }

        if (this.getDamage() > 0.0F) {
            this.setDamage(this.getDamage() - 1.0F);
        }

        if (this.posY < -64.0D) {
            this.kill();
        }

        int Y;
        if (!this.worldObj.isRemote && this.worldObj instanceof WorldServer) {
            this.worldObj.theProfiler.startSection("portal");
            MinecraftServer l = ((WorldServer) this.worldObj).func_73046_m();
            Y = this.getMaxInPortalTime();
            if (this.inPortal) {
                if (l.getAllowNether()) {
                    if (this.ridingEntity == null && this.portalCounter++ >= Y) {
                        this.portalCounter = Y;
                        this.timeUntilPortal = this.getPortalCooldown();
                        byte i1;
                        if (this.worldObj.provider.dimensionId == -1) {
                            i1 = 0;
                        } else {
                            i1 = -1;
                        }

                        this.travelToDimension(i1);
                    }

                    this.inPortal = false;
                }
            } else {
                if (this.portalCounter > 0) {
                    this.portalCounter -= 4;
                }

                if (this.portalCounter < 0) {
                    this.portalCounter = 0;
                }
            }

            if (this.timeUntilPortal > 0) {
                --this.timeUntilPortal;
            }

            this.worldObj.theProfiler.endSection();
        }

        //if(false) {
        if(this.worldObj.isRemote) {
            this.setPosition(this.posX, this.posY, this.posZ);
            this.setRotation(this.rotationYaw, this.rotationPitch);
        } else {
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;

            if (!bogies.isEmpty()) {
                EntityMinecart bogieFront = (EntityMinecart) this.worldObj.getEntityByID(bogies.get(0));
                EntityMinecart bogieBack = (EntityMinecart) this.worldObj.getEntityByID(bogies.get(1));

            /*some code*/

                double Ks = 500.0;
                double Kd = 500.0;
                double m = 1.0;
                double length = 2.0;
                double dt = 0.001;

                double dist = calcDist(bogieFront, bogieBack);
                double dv = Ks * (dist - length) / m * dt;
                double DdvX = Kd * (bogieFront.motionX - bogieBack.motionX) / m * dt;
                double DdvZ = Kd * (bogieFront.motionZ - bogieBack.motionZ) / m * dt;

                bogieBack.motionX += dv * (bogieFront.posX - bogieBack.posX) / dist + DdvX;
                bogieBack.motionZ += dv * (bogieFront.posZ - bogieBack.posZ) / dist + DdvZ;
                bogieFront.motionX += -dv * (bogieFront.posX - bogieBack.posX) / dist - DdvX;
                bogieFront.motionZ += -dv * (bogieFront.posZ - bogieBack.posZ) / dist - DdvZ;

                this.posX = (bogieFront.posX + bogieBack.posX) / 2.0;
                this.posY = (bogieFront.posY + bogieBack.posY) / 2.0;
                this.posZ = (bogieFront.posZ + bogieBack.posZ) / 2.0;

                this.motionX = this.posX - this.prevPosX;
                this.motionY = this.posY - this.prevPosY;
                this.motionZ = this.posZ - this.prevPosZ;

                this.rotationYaw = 180.0F - (float) Math.acos((bogieFront.posX - bogieBack.posX) /
                        calcDist(bogieFront.posX - bogieBack.posX,
                                bogieFront.posZ - bogieBack.posZ)
                );

                this.rotationPitch = (float) Math.atan((bogieFront.posY - bogieBack.posY) /
                        calcDist(bogieFront.posX - bogieBack.posX,
                                bogieFront.posZ - bogieBack.posZ)
                );

                this.moveEntity(this.motionX, this.motionY, this.motionZ);
            }


            if (this.riddenByEntity != null && this.riddenByEntity.isDead) {
                if (this.riddenByEntity.ridingEntity == this) {
                    this.riddenByEntity.ridingEntity = null;
                }

                this.riddenByEntity = null;
            }

            int X = MathHelper.floor_double(this.posX);
            Y = MathHelper.floor_double(this.posY);
            int Z = MathHelper.floor_double(this.posZ);

            MinecraftForge.EVENT_BUS.post(new MinecartUpdateEvent(this, (float) X, (float) Y, (float) Z));
        }

    }

    double calcDist(double x, double y) {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    double calcDist(double x, double y, double z) {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
    }

    double calcDist(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) + Math.pow(z1 - z2, 2));
    }

    double calcDist(EntityMinecart a, EntityMinecart b) {
        return Math.sqrt(Math.pow(a.posX - b.posX, 2) + Math.pow(a.posY - b.posY, 2) + Math.pow(a.posZ - b.posZ, 2));
    }

    Vec3 calcProjection(Vec3 vec, Vec3 dest) {
        double length = vec.dotProduct(dest) / dest.lengthVector();
        Vec3 nor = dest.normalize();
        nor.xCoord *= length;
        nor.yCoord *= length;
        nor.zCoord *= length;
        return nor;
    }

}
