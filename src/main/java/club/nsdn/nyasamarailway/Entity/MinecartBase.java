package club.nsdn.nyasamarailway.Entity;

import club.nsdn.nyasamarailway.Blocks.BlockRailReception;
import club.nsdn.nyasamarailway.Blocks.BlockRailReceptionAnti;
import club.nsdn.nyasamarailway.Items.Item74HC04;
import club.nsdn.nyasamarailway.Items.ItemLoader;
import club.nsdn.nyasamarailway.Items.ItemTrainController32Bit;
import club.nsdn.nyasamarailway.Items.ItemTrainController8Bit;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
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

    public MinecartBase(World world) {
        super(world);
    }

    public MinecartBase(World world, double x, double y, double z) {
        super(world, x, y, z);
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
                    if (stack.getItem() instanceof Item74HC04 ||
                            stack.getItem() instanceof ItemTrainController8Bit ||
                            stack.getItem() instanceof ItemTrainController32Bit) {
                        return true;
                    }
                }
                if (!this.worldObj.isRemote) {
                    player.mountEntity(this);
                }
            }
            return true;
        }
    }

    @Override
    public int getMinecartType() {
        return -1;
    }

    @Override
    public double getMountedYOffset() {
        return -0.1;
    }

    @Override
    public float getMaxCartSpeedOnRail() {
        return 1.0F;
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
        if (this.nextLinkTrain > 0 && world.getEntityByID(this.nextLinkTrain) instanceof EntityMinecart) {
            EntityMinecart cart = (EntityMinecart) world.getEntityByID(this.nextLinkTrain);
            double Ks = 500.0;
            double Kd = 500.0;
            double m = 1.0;
            double length = 1.5;
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

        }
    }

    @Override
    protected void func_145821_a(int x, int y, int z, double v1, double v, Block block, int meta) {
        //applyPush
        int metadata = worldObj.getBlockMetadata(x, y, z);
        if (block instanceof BlockRailReception) {
            if (!((BlockRailReception) block).checkNearbySameRail(worldObj, x, y, z))
                if (riddenByEntity == null) return;
        }
        if (block instanceof BlockRailReceptionAnti) {
            if (!((BlockRailReceptionAnti) block).checkNearbySameRail(worldObj, x, y, z))
                if (riddenByEntity == null) return;
        }
        super.func_145821_a(x, y, z, v1, v, block, meta);
    }

    @Override
    protected void applyDrag() {
        //Do engine code
        calcLink(worldObj);

        super.applyDrag();
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float damage) {
        if(!this.worldObj.isRemote && !this.isDead) {
            if(this.isEntityInvulnerable()) {
                return false;
            } else {
                this.setRollingDirection(-this.getRollingDirection());
                this.setRollingAmplitude(10);
                this.setBeenAttacked();
                this.setDamage(this.getDamage() + damage * 10.0F);
                boolean flag = false;
                if (source.getEntity() instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) source.getEntity();
                    ItemStack stack = player.getCurrentEquippedItem();
                    if (stack == null) return false;
                    if (stack.getItem() instanceof Item74HC04) flag = true;
                }
                if(flag || this.getDamage() > 40.0F) {
                    if(this.riddenByEntity != null) {
                        this.riddenByEntity.mountEntity(this);
                    }

                    if(flag && !this.hasCustomInventoryName()) {
                        this.setDead();
                    } else {
                        this.killMinecart(source);
                    }
                }

                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public void killMinecart(DamageSource source)
    {
        this.setDead();
        ItemStack itemstack = new ItemStack(ItemLoader.itemMinecartBase, 1);
        itemstack.setStackDisplayName(itemstack.getDisplayName());
        this.entityDropItem(itemstack, 0.0F);
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
