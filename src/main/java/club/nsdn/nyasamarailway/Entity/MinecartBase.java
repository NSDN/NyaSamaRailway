package club.nsdn.nyasamarailway.Entity;

import club.nsdn.nyasamarailway.Blocks.BlockRailReception;
import club.nsdn.nyasamarailway.Blocks.BlockRailReceptionAnti;
import club.nsdn.nyasamarailway.Items.*;
import club.nsdn.nyasamarailway.TileEntities.Rail.RailBase;
import club.nsdn.nyasamarailway.TileEntities.Rail.RailMonoMagnet;
import club.nsdn.nyasamarailway.TileEntities.Rail.RailMonoMagnetReception;
import club.nsdn.nyasamarailway.TileEntities.Rail.RailMonoMagnetReceptionAnti;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;

import net.minecraftforge.event.entity.minecart.MinecartUpdateEvent;
import org.thewdj.physics.Vec3d;

import java.util.List;

/**
 * Created by drzzm32 on 2016.5.23.
 */
public class MinecartBase extends EntityMinecartEmpty implements ITrainLinkable {

    /** Minecart rotational logic matrix */
    public static int[][][] matrix = new int[][][] {{{0, 0, -1}, {0, 0, 1}}, {{ -1, 0, 0}, {1, 0, 0}}, {{ -1, -1, 0}, {1, 0, 0}}, {{ -1, 0, 0}, {1, -1, 0}}, {{0, 0, -1}, {0, -1, 1}}, {{0, -1, -1}, {0, 0, 1}}, {{0, 0, 1}, {1, 0, 0}}, {{0, 0, 1}, { -1, 0, 0}}, {{0, 0, -1}, { -1, 0, 0}}, {{0, 0, -1}, {1, 0, 0}}};
    /** appears to be the progress of the turn */
    public boolean isInReverse = false;

    public static final int DATA_LINK = 28;

    //public int prevLinkTrain = -1;
    //public int nextLinkTrain = -1;

    public MinecartBase(World world) {
        super(world);
        getDataWatcher().addObject(DATA_LINK, 0);
    }

    public MinecartBase(World world, double x, double y, double z) {
        super(world, x, y, z);
        getDataWatcher().addObject(DATA_LINK, 0);
    }

    @Override
    public AxisAlignedBB getCollisionBox(Entity entity) {
        double size = 2;
        return AxisAlignedBB.getBoundingBox(1 - size, 1 - size, 1 - size, size, size, size);
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return getCollisionBox(null);
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
        return this.getDataWatcher().getWatchableObjectInt(DATA_LINK);
    }

    public boolean LinkTrain(int ID) {
        /*
        if (this.prevLinkTrain == -1) {
            this.prevLinkTrain = ID;
        } else 
        */
        if (this.getDataWatcher().getWatchableObjectInt(DATA_LINK) == -1) {
            this.getDataWatcher().updateObject(DATA_LINK, ID);
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
        if (this.getDataWatcher().getWatchableObjectInt(DATA_LINK) == ID) {
            this.getDataWatcher().updateObject(DATA_LINK, -1);
        }
    }

    double calcDist(EntityMinecart a, EntityMinecart b) {
        return Math.sqrt(Math.pow(a.posX - b.posX, 2) + Math.pow(a.posY - b.posY, 2) + Math.pow(a.posZ - b.posZ, 2));
    }

    public void calcLink(World world) {
        if (this.getDataWatcher().getWatchableObjectInt(DATA_LINK) > 0 && world.getEntityByID(this.getDataWatcher().getWatchableObjectInt(DATA_LINK)) instanceof EntityMinecart) {
            EntityMinecart cart = (EntityMinecart) world.getEntityByID(this.getDataWatcher().getWatchableObjectInt(DATA_LINK));
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

    @Override  //applyPush()
    protected void func_145821_a(int x, int y, int z, double v1, double v, Block block, int meta) {
        //applyPush
        int metadata = worldObj.getBlockMetadata(x, y, z);
        if (block instanceof BlockRailReception) {
            BlockRailReception.TileEntityRailReception tile = (BlockRailReception.TileEntityRailReception) worldObj.getTileEntity(x, y, z);
            if (!((BlockRailReception) block).checkNearbySameRail(worldObj, x, y, z))
                if (riddenByEntity == null && !tile.cartType.isEmpty()) return;
        }
        if (block instanceof BlockRailReceptionAnti) {
            BlockRailReceptionAnti.TileEntityRailReceptionAnti tile = (BlockRailReceptionAnti.TileEntityRailReceptionAnti) worldObj.getTileEntity(x, y, z);
            if (!((BlockRailReceptionAnti) block).checkNearbySameRail(worldObj, x, y, z))
                if (riddenByEntity == null && !tile.cartType.isEmpty()) return;
        }
        if (block instanceof RailMonoMagnetReception) {
            RailMonoMagnetReception.TileEntityRail tile = (RailMonoMagnetReception.TileEntityRail) worldObj.getTileEntity(x, y, z);
            if (!((RailMonoMagnetReception) block).checkNearbySameRail(worldObj, x, y, z))
                if (riddenByEntity == null && !tile.cartType.isEmpty()) return;
        }
        if (block instanceof RailMonoMagnetReceptionAnti) {
            RailMonoMagnetReceptionAnti.TileEntityRail tile = (RailMonoMagnetReceptionAnti.TileEntityRail) worldObj.getTileEntity(x, y, z);
            if (!((RailMonoMagnetReceptionAnti) block).checkNearbySameRail(worldObj, x, y, z))
                if (riddenByEntity == null && !tile.cartType.isEmpty()) return;
        }
        //applyPush(x, y, z, v1, v, block, meta);
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
        this.getDataWatcher().updateObject(DATA_LINK,tagCompound.getInteger("nextLinkTrain"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setInteger("nextLinkTrain", this.getDataWatcher().getWatchableObjectInt(DATA_LINK));
    }

    public boolean checkBlockIsRail(World world, int x, int y, int z, Class<?> cls) {
        return world.getBlock(x, y, z).getClass() == cls;
    }

    /*******************************************************************************************************************/

    @Override
    public void onUpdate()
    {
        if (this.worldObj.isRemote) {
            super.onUpdate();
            return;
        }

        if (this.getRollingAmplitude() > 0)
        {
            this.setRollingAmplitude(this.getRollingAmplitude() - 1);
        }

        if (this.getDamage() > 0.0F)
        {
            this.setDamage(this.getDamage() - 1.0F);
        }

        if (this.posY < -64.0D)
        {
            this.kill();
        }

        if (!this.worldObj.isRemote && this.worldObj instanceof WorldServer)
        {
            this.worldObj.theProfiler.startSection("portal");
            MinecraftServer minecraftserver = ((WorldServer)this.worldObj).func_73046_m();
            int i = this.getMaxInPortalTime();

            if (this.inPortal)
            {
                if (minecraftserver.getAllowNether())
                {
                    if (this.ridingEntity == null && this.portalCounter++ >= i)
                    {
                        this.portalCounter = i;
                        this.timeUntilPortal = this.getPortalCooldown();
                        byte b0;

                        if (this.worldObj.provider.dimensionId == -1)
                        {
                            b0 = 0;
                        }
                        else
                        {
                            b0 = -1;
                        }

                        this.travelToDimension(b0);
                    }

                    this.inPortal = false;
                }
            }
            else
            {
                if (this.portalCounter > 0)
                {
                    this.portalCounter -= 4;
                }

                if (this.portalCounter < 0)
                {
                    this.portalCounter = 0;
                }
            }

            if (this.timeUntilPortal > 0)
            {
                --this.timeUntilPortal;
            }

            this.worldObj.theProfiler.endSection();

            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
            this.motionY -= 0.03999999910593033D;
            int x = MathHelper.floor_double(this.posX);
            int y = MathHelper.floor_double(this.posY);
            int z = MathHelper.floor_double(this.posZ);

            if (!BlockRailBase.func_150049_b_(this.worldObj, x, y, z) && BlockRailBase.func_150049_b_(this.worldObj, x, y - 1, z))
            {
                --y;
            }

            double d0 = 0.4D;
            double d2 = 0.0078125D;
            Block block = this.worldObj.getBlock(x, y, z);

            if (canUseRail() && BlockRailBase.func_150051_a(block))
            {
                float railMaxSpeed = ((BlockRailBase)block).getRailMaxSpeed(worldObj, this, x, y, z);
                double maxSpeed = Math.min(railMaxSpeed, getCurrentCartSpeedCapOnRail());
                this.func_145821_a(x, y, z, maxSpeed, getSlopeAdjustment(), block, ((BlockRailBase)block).getBasicRailMetadata(worldObj, this, x, y, z));

                if (block == Blocks.activator_rail)
                {
                    this.onActivatorRailPass(x, y, z, (worldObj.getBlockMetadata(x, y, z) & 8) != 0);
                }
            }
            else
            {
                this.func_94088_b(onGround ? d0 : getMaxSpeedAirLateral());
            }

            this.func_145775_I();
            this.rotationPitch = 0.0F;
            double d8 = this.prevPosX - this.posX;
            double d4 = this.prevPosZ - this.posZ;

            if (d8 * d8 + d4 * d4 > 0.001D)
            {
                this.rotationYaw = (float)(Math.atan2(d4, d8) * 180.0D / Math.PI);

                if (this.isInReverse)
                {
                    this.rotationYaw += 180.0F;
                }
            }

            double detlaYaw = (double)MathHelper.wrapAngleTo180_float(this.rotationYaw - this.prevRotationYaw);

            if (detlaYaw < -170.0D || detlaYaw >= 170.0D)
            {
                this.rotationYaw += 180.0F;
                this.isInReverse = !this.isInReverse;
            }

            this.setRotation(this.rotationYaw, this.rotationPitch);

            AxisAlignedBB box = getBoundingBox();

            List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, box);

            if (list != null && !list.isEmpty())
            {
                for (int k = 0; k < list.size(); ++k)
                {
                    Entity entity = (Entity)list.get(k);

                    if (entity != this.riddenByEntity && entity.canBePushed() && entity instanceof EntityMinecart)
                    {
                        entity.applyEntityCollision(this);
                    }
                }
            }

            /* Driver Heading */
            if (this.riddenByEntity != null && !this.riddenByEntity.isDead) {
                if (this.riddenByEntity.ridingEntity == this)
                {
                    if (this.riddenByEntity instanceof EntityPlayer) {
                        ((EntityPlayer) this.riddenByEntity).cameraYaw += detlaYaw;
                    }
                }
            }

            if (this.riddenByEntity != null && this.riddenByEntity.isDead)
            {
                if (this.riddenByEntity.ridingEntity == this)
                {
                    this.riddenByEntity.ridingEntity = null;
                }

                this.riddenByEntity = null;
            }

            MinecraftForge.EVENT_BUS.post(new MinecartUpdateEvent(this, x, y, z));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Vec3 func_70495_a(double doubleX, double doubleY, double doubleZ, double v)
    {
        int x = MathHelper.floor_double(doubleX);
        int y = MathHelper.floor_double(doubleY);
        int z = MathHelper.floor_double(doubleZ);

        if (!BlockRailBase.func_150049_b_(this.worldObj, x, y, z) && BlockRailBase.func_150049_b_(this.worldObj, x, y - 1, z))
        {
            --y;
        }

        Block block = this.worldObj.getBlock(x, y, z);

        if (!BlockRailBase.func_150051_a(block))
        {
            return null;
        }
        else
        {
            int l = ((BlockRailBase)block).getBasicRailMetadata(worldObj, this, x, y, z);

            doubleY = (double)y;

            if (l >= 2 && l <= 5)
            {
                doubleY = (double)(y + 1);
            }

            int[][] aint = matrix[l];
            double d4 = (double)(aint[1][0] - aint[0][0]);
            double d5 = (double)(aint[1][2] - aint[0][2]);
            double d6 = Math.sqrt(d4 * d4 + d5 * d5);
            d4 /= d6;
            d5 /= d6;
            doubleX += d4 * v;
            doubleZ += d5 * v;

            if (aint[0][1] != 0 && MathHelper.floor_double(doubleX) - x == aint[0][0] && MathHelper.floor_double(doubleZ) - z == aint[0][2])
            {
                doubleY += (double)aint[0][1];
            }
            else if (aint[1][1] != 0 && MathHelper.floor_double(doubleX) - x == aint[1][0] && MathHelper.floor_double(doubleZ) - z == aint[1][2])
            {
                doubleY += (double)aint[1][1];
            }

            return this.func_70489_a(doubleX, doubleY, doubleZ);
        }
    }

    @Override
    public Vec3 func_70489_a(double doubleX, double doubleY, double doubleZ)
    {
        int x = MathHelper.floor_double(doubleX);
        int y = MathHelper.floor_double(doubleY);
        int z = MathHelper.floor_double(doubleZ);

        if (!BlockRailBase.func_150049_b_(this.worldObj, x, y, z) && BlockRailBase.func_150049_b_(this.worldObj, x, y - 1, z))
        {
            --y;
        }

        Block block = this.worldObj.getBlock(x, y, z);

        if (BlockRailBase.func_150051_a(block))
        {
            int l = ((BlockRailBase)block).getBasicRailMetadata(worldObj, this, x, y, z);
            doubleY = (double)y;

            if (l >= 2 && l <= 5)
            {
                doubleY = (double)(y + 1);
            }

            int[][] aint = matrix[l];
            double d3 = 0.0D;
            double d4 = (double)x + 0.5D + (double)aint[0][0] * 0.5D;
            double d5 = (double)y + 0.5D + (double)aint[0][1] * 0.5D;
            double d6 = (double)z + 0.5D + (double)aint[0][2] * 0.5D;
            double d7 = (double)x + 0.5D + (double)aint[1][0] * 0.5D;
            double d8 = (double)y + 0.5D + (double)aint[1][1] * 0.5D;
            double d9 = (double)z + 0.5D + (double)aint[1][2] * 0.5D;
            double d10 = d7 - d4;
            double d11 = (d8 - d5) * 2.0D;
            double d12 = d9 - d6;

            if (d10 == 0.0D)
            {
                doubleX = (double)x + 0.5D;
                d3 = doubleZ - (double)z;
            }
            else if (d12 == 0.0D)
            {
                doubleZ = (double)z + 0.5D;
                d3 = doubleX - (double)x;
            }
            else
            {
                double d13 = doubleX - d4;
                double d14 = doubleZ - d6;
                d3 = (d13 * d10 + d14 * d12) * 2.0D;
            }

            doubleX = d4 + d10 * d3;
            doubleY = d5 + d11 * d3;
            doubleZ = d6 + d12 * d3;

            if (d11 < 0.0D)
            {
                ++doubleY;
            }

            if (d11 > 0.0D)
            {
                doubleY += 0.5D;
            }

            return Vec3.createVectorHelper(doubleX, doubleY, doubleZ);
        }
        else
        {
            return null;
        }
    }

}
