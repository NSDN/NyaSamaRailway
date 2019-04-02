package club.nsdn.nyasamarailway.api.cart;

import club.nsdn.nyasamarailway.item.ItemLoader;
import club.nsdn.nyasamarailway.item.tool.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Created by drzzm32 on 2019.2.27
 */
public abstract class AbsTrainBase extends AbsContainer {

    private static final DataParameter<Integer> BOGIE_A = EntityDataManager.createKey(AbsTrainBase.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> BOGIE_B = EntityDataManager.createKey(AbsTrainBase.class, DataSerializers.VARINT);

    public UUID uuidA = UUID.randomUUID(), uuidB = UUID.randomUUID();

    public AbsTrainBase(World world) {
        super(world);
        this.setSize(1.5F, 1.75F);
    }

    public AbsTrainBase(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    protected void entityInit() {
        dataManager.register(BOGIE_A, -1);
        dataManager.register(BOGIE_B, -1);
    }

    @Override
    protected void readEntityFromNBT(@Nonnull NBTTagCompound tagCompound) {
        dataManager.set(BOGIE_A, tagCompound.getInteger("bogieA"));
        dataManager.set(BOGIE_B, tagCompound.getInteger("bogieB"));
        this.uuidA = tagCompound.getUniqueId("uuidA");
        this.uuidB = tagCompound.getUniqueId("uuidB");
    }

    @Override
    protected void writeEntityToNBT(@Nonnull NBTTagCompound tagCompound) {
        tagCompound.setInteger("bogieA", dataManager.get(BOGIE_A));
        tagCompound.setInteger("bogieB", dataManager.get(BOGIE_B));
        tagCompound.setUniqueId("uuidA", this.uuidA);
        tagCompound.setUniqueId("uuidB", this.uuidB);
    }

    public AbsTrainBase setBogieA(Entity bogie) {
        if (bogie != null) {
            if (!world.isRemote)
                this.uuidA = bogie.getUniqueID();
            dataManager.set(BOGIE_A, bogie.getEntityId());
        }
        return this;
    }

    public AbsTrainBase setBogieB(Entity bogie) {
        if (bogie != null) {
            if (!world.isRemote)
                this.uuidB = bogie.getUniqueID();
            dataManager.set(BOGIE_B, bogie.getEntityId());
        }
        return this;
    }

    public Entity getBogieA() {
        Entity entity = world.getEntityByID(dataManager.get(BOGIE_A));
        if (!world.isRemote && world instanceof WorldServer) {
            Entity serverEntity = ((WorldServer) world).getEntityFromUuid(this.uuidA);
            if (serverEntity != null) {
                if (!serverEntity.equals(entity)) {
                    dataManager.set(BOGIE_A, serverEntity.getEntityId());
                }
                return serverEntity;
            } else {
                this.uuidA = UUID.randomUUID();
                dataManager.set(BOGIE_A, -1);
                return null;
            }
        }
        return entity;
    }

    public Entity getBogieB() {
        Entity entity = world.getEntityByID(dataManager.get(BOGIE_B));
        if (!world.isRemote && world instanceof WorldServer) {
            Entity serverEntity = ((WorldServer) world).getEntityFromUuid(this.uuidB);
            if (serverEntity != null) {
                if (!serverEntity.equals(entity)) {
                    dataManager.set(BOGIE_B, serverEntity.getEntityId());
                }
                return serverEntity;
            } else {
                this.uuidB = UUID.randomUUID();
                dataManager.set(BOGIE_B, -1);
                return null;
            }
        }
        return entity;
    }

    @Override
    public void applyEntityCollision(@Nonnull Entity entity) {
        if (entity instanceof AbsTrainBase) {
            if (entity.getEntityBoundingBox().minY < this.getEntityBoundingBox().maxY) {
                super.applyEntityCollision(entity);
            }
        } else if (entity.getEntityBoundingBox().minY <= this.getEntityBoundingBox().minY) {
            super.applyEntityCollision(entity);
        }
    }

    @Override // Called by rider
    public abstract void updatePassenger(@Nonnull Entity entity);

    @Override
    public boolean attackEntityFrom(@Nonnull DamageSource source, float v) {
        if (!this.world.isRemote && !this.isDead) {
            this.markVelocityChanged();
            boolean flag = false;
            if (source.getTrueSource() instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) source.getTrueSource();
                ItemStack stack = player.getHeldItemMainhand();
                if (stack.isEmpty()) return false;
                if (stack.getItem() instanceof Item74HC04) flag = true;
                if (stack.getItem() instanceof Item1N4148) flag = true;
            }
            if (flag) {
                this.removePassengers();

                if (flag && !this.hasCustomName()) {
                    this.setDead();
                    if (getBogieA() != null) getBogieA().setDead();
                    if (getBogieB() != null) getBogieB().setDead();
                } else {
                    this.killTrain(source);
                    if (getBogieA() != null && getBogieA() instanceof EntityMinecart)
                        ((EntityMinecart) getBogieA()).killMinecart(source);
                    if (getBogieB() != null && getBogieB() instanceof EntityMinecart)
                        ((EntityMinecart) getBogieB()).killMinecart(source);
                }
            }

            return true;
        } else {
            return true;
        }
    }

    @Nonnull
    public Item getItem() { return Items.AIR; }

    public void killTrain(DamageSource source) {
        this.setDead();
        ItemStack stack = ItemStack.EMPTY;
        Item item = getItem();
        if (item == Items.AIR) {
            item = ItemLoader.itemTrains.get(getClass());
            if (item == null) item = Items.AIR;
            stack = new ItemStack(item, 1);
        }
        if (!source.damageType.equals("nsr")) this.entityDropItem(stack, 0.0F);
    }

    public double getTrainYOffset() {
        return 1.0;
    }

    @SideOnly(Side.CLIENT)
    public double getRenderYOffset() {
        return -1.0;
    }

    @SideOnly(Side.CLIENT)
    public double getRenderFixOffset() {
        return 0.0;
    }

    @Override
    public void update() {
        if (getBogieA() != null && getBogieB() != null) {
            Entity bogieA = getBogieA();
            Entity bogieB = getBogieB();

            double x, y, z, yaw, pitch;
            x = (bogieA.posX + bogieB.posX) / 2;
            y = (bogieA.posY + bogieB.posY) / 2 + getTrainYOffset();
            z = (bogieA.posZ + bogieB.posZ) / 2;

            Vec3d vec = bogieB.getPositionVector().subtract(bogieA.getPositionVector());
            yaw = Math.atan2(vec.z, vec.x) * 180 / Math.PI;
            double hlen = Math.sqrt(vec.x * vec.x + vec.z * vec.z);
            pitch = Math.atan(vec.y / hlen) * 180 / Math.PI;
            this.setRotation((float) yaw, (float) pitch);

            this.motionX = x - this.posX;
            this.motionY = y - this.posY;
            this.motionZ = z - this.posZ;

            this.move(MoverType.SELF, motionX, motionY, motionZ);
        }
    }

}
