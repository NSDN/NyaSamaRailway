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
    public LinkedList<EntityMinecart> bogies;
    public LinkedList<Double> bogiesDist;

    public TrainBase(World world) {
        super(world);
        bogies = new LinkedList<EntityMinecart>();
        bogiesDist = new LinkedList<Double>();
    }

    public TrainBase(World world, double x, double y, double z) {
        super(world, x, y, z);
        bogies = new LinkedList<EntityMinecart>();
        bogiesDist = new LinkedList<Double>();
    }

    public void addBogie(int index, EntityMinecart bogie, double distance) {
        bogies.add(index, bogie);
        bogiesDist.add(index, distance);
    }

    @Override
    public void killMinecart(DamageSource source) {
        this.setDead();
        for (EntityMinecart i : this.bogies) {
            if (i != null)
                i.killMinecart(source);
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

        int i;
        if (!this.worldObj.isRemote && this.worldObj instanceof WorldServer) {
            this.worldObj.theProfiler.startSection("portal");
            MinecraftServer l = ((WorldServer) this.worldObj).func_73046_m();
            i = this.getMaxInPortalTime();
            if (this.inPortal) {
                if (l.getAllowNether()) {
                    if (this.ridingEntity == null && this.portalCounter++ >= i) {
                        this.portalCounter = i;
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

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        int var20 = MathHelper.floor_double(this.posX);
        i = MathHelper.floor_double(this.posY);
        int var21 = MathHelper.floor_double(this.posZ);

        if (!bogies.isEmpty()) {
            EntityMinecart bogieFront = bogies.get(0);
            EntityMinecart bogieBack = bogies.get(1);

            /*some code*/
            /**力学建模需求**/

            if (calcDist(bogieFront, bogieBack) > bogiesDist.get(0) - bogiesDist.get(1)) {
                /**拉特性**/
                if (calcDist(bogieFront.motionX, bogieFront.motionZ) >
                        calcDist(bogieBack.motionX, bogieBack.motionZ)) {
                    /**前主导**/
                    Vec3 vf = Vec3.createVectorHelper(bogieFront.motionX, 0, bogieFront.motionZ);
                    Vec3 vb = Vec3.createVectorHelper(bogieBack.motionX, 0, bogieBack.motionZ);
                    Vec3 dir = Vec3.createVectorHelper(bogieFront.posX - bogieBack.posX, 0, bogieFront.posZ - bogieBack.posZ);
                    vb = calcProjection(calcProjection(vf, dir), vb);
                    bogieBack.motionX = vb.xCoord;
                    bogieBack.motionZ = vb.zCoord;
                } else if (calcDist(bogieFront.motionX, bogieFront.motionZ) <
                        calcDist(bogieBack.motionX, bogieBack.motionZ)){
                    /**后主导**/
                    Vec3 vb = Vec3.createVectorHelper(bogieBack.motionX, 0, bogieBack.motionZ);
                    Vec3 vf = Vec3.createVectorHelper(bogieFront.motionX, 0, bogieFront.motionZ);
                    Vec3 dir = Vec3.createVectorHelper(bogieBack.posX - bogieFront.posX, 0, bogieBack.posZ - bogieFront.posZ);
                    vf = calcProjection(calcProjection(vb, dir), vf);
                    bogieFront.motionX = vf.xCoord;
                    bogieFront.motionZ = vf.zCoord;
                }
            } else if (calcDist(bogieFront, bogieBack) < bogiesDist.get(0) - bogiesDist.get(1)) {
                /**推特性**/
                if (calcDist(bogieFront.motionX, bogieFront.motionZ) >
                        calcDist(bogieBack.motionX, bogieBack.motionZ)) {
                    /**前主导**/
                    Vec3 vf = Vec3.createVectorHelper(bogieFront.motionX, 0, bogieFront.motionZ);
                    Vec3 vb = Vec3.createVectorHelper(bogieBack.motionX, 0, bogieBack.motionZ);
                    Vec3 dir = Vec3.createVectorHelper(bogieBack.posX - bogieFront.posX, 0, bogieBack.posZ - bogieFront.posZ);
                    vb = calcProjection(calcProjection(vf, dir), vb);
                    bogieBack.motionX = vb.xCoord;
                    bogieBack.motionZ = vb.zCoord;
                } else if (calcDist(bogieFront.motionX, bogieFront.motionZ) <
                        calcDist(bogieBack.motionX, bogieBack.motionZ)){
                    /**后主导**/
                    Vec3 vb = Vec3.createVectorHelper(bogieBack.motionX, 0, bogieBack.motionZ);
                    Vec3 vf = Vec3.createVectorHelper(bogieFront.motionX, 0, bogieFront.motionZ);
                    Vec3 dir = Vec3.createVectorHelper(bogieFront.posX - bogieBack.posX, 0, bogieFront.posZ - bogieBack.posZ);
                    vf = calcProjection(calcProjection(vb, dir), vf);
                    bogieFront.motionX = vf.xCoord;
                    bogieFront.motionZ = vf.zCoord;
                }
            }

            bogieFront.onUpdate();
            bogieBack.onUpdate();

            this.posX = (bogieFront.posX + bogieBack.posX) / 2.0;
            this.posY = (bogieFront.posY + bogieBack.posY) / 2.0;
            this.posZ = (bogieFront.posZ + bogieBack.posZ) / 2.0;

            this.rotationYaw = 180.0F - (float) Math.acos((bogieFront.posX - bogieBack.posX) /
                    calcDist(bogieFront.posX - bogieBack.posX,
                            bogieFront.posZ - bogieBack.posZ)
            );

            this.rotationPitch = (float) Math.atan((bogieFront.posY - bogieBack.posY) /
                    calcDist(bogieFront.posX - bogieBack.posX,
                            bogieFront.posZ - bogieBack.posZ)
            );
        }


        if (this.riddenByEntity != null && this.riddenByEntity.isDead) {
            if (this.riddenByEntity.ridingEntity == this) {
                this.riddenByEntity.ridingEntity = null;
            }

            this.riddenByEntity = null;
        }

        MinecraftForge.EVENT_BUS.post(new MinecartUpdateEvent(this, (float) var20, (float) i, (float) var21));

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
