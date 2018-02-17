package club.nsdn.nyasamarailway.entity;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.block.rail.IRailSpeedKeep;
import club.nsdn.nyasamarailway.block.rail.special.BlockRailReception;
import club.nsdn.nyasamarailway.block.rail.special.BlockRailReceptionAnti;
import club.nsdn.nyasamarailway.item.tool.Item1N4148;
import club.nsdn.nyasamarailway.network.TrainPacket;
import club.nsdn.nyasamarailway.util.TrainController;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockRailPowered;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.minecraftforge.event.entity.minecart.MinecartUpdateEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by drzzm32 on 2016.6.23.
 */

public class LocoBase extends EntityMinecart implements ILocomotive, mods.railcraft.api.carts.ILinkableCart {

    /** Minecart rotational logic matrix */
    public static final int[][][] matrix = new int[][][]{
            {{0, 0, -1}, {0, 0, 1}}, {{-1, 0, 0}, {1, 0, 0}}, {{-1, -1, 0}, {1, 0, 0}},
            {{-1, 0, 0}, {1, -1, 0}}, {{0, 0, -1}, {0, -1, 1}}, {{0, -1, -1}, {0, 0, 1}},
            {{0, 0, 1}, {1, 0, 0}}, {{0, 0, 1}, {-1, 0, 0}}, {{0, 0, -1}, {-1, 0, 0}},
            {{0, 0, -1}, {1, 0, 0}}
    };
    /** appears to be the progress of the turn */

    public boolean isInReverse = false;

    public int P;
    public int R;
    public int Dir;
    public double Velocity;
    public double prevVelocity;

    private final int INDEX_P = 23, INDEX_R = 24, INDEX_DIR = 25, INDEX_V = 26, INDEX_PV = 27;

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(INDEX_P, Integer.valueOf("0"));
        this.dataWatcher.addObject(INDEX_R, Integer.valueOf("0"));
        this.dataWatcher.addObject(INDEX_DIR, Integer.valueOf("0"));
        this.dataWatcher.addObject(INDEX_V, Float.valueOf("0"));
        this.dataWatcher.addObject(INDEX_PV, Float.valueOf("0"));
    }

    public int getEnginePower() {
        return this.dataWatcher.getWatchableObjectInt(INDEX_P);
    }

    public void setEnginePower(int value) {
        this.P = value;
        this.dataWatcher.updateObject(INDEX_P, value);
    }

    public int getEngineBrake() {
        return this.dataWatcher.getWatchableObjectInt(INDEX_R);
    }

    public void setEngineBrake(int value) {
        this.R = value;
        this.dataWatcher.updateObject(INDEX_R, value);
    }

    public int getEngineDir() {
        return this.dataWatcher.getWatchableObjectInt(INDEX_DIR);
    }

    public void setEngineDir(int value) {
        this.Dir = value;
        this.dataWatcher.updateObject(INDEX_DIR, value);
    }

    public double getEngineVel() {
        return this.dataWatcher.getWatchableObjectFloat(INDEX_V);
    }

    public void setEngineVel(double value) {
        this.Velocity = (float) value;
        this.dataWatcher.updateObject(INDEX_V, (float) value);
    }

    public double getEnginePrevVel() {
        return this.dataWatcher.getWatchableObjectFloat(INDEX_PV);
    }

    public void setEnginePrevVel(double value) {
        this.prevVelocity = (float) value;
        this.dataWatcher.updateObject(INDEX_PV, (float) value);
    }

    public static final byte TICKET_FLAG = 6;
    public static final byte ANCHOR_RADIUS = 2;
    public static final byte MAX_CHUNKS = 25;
    public Set<ChunkCoordIntPair> chunks;
    public ForgeChunkManager.Ticket ticket;

    protected TrainPacket tmpPacket;

    protected boolean isHighSpeed() {
        return false;
    }

    public LocoBase(World world) {
        super(world);
        setCurrentCartSpeedCapOnRail(getMaxCartSpeedOnRail());
    }

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
            setEnginePower(packet.P);
            setEngineBrake(packet.R);
            setEngineDir(packet.Dir);
        }
    }

    @Override
    public float getLinkageDistance(EntityMinecart cart) {
        return 1.5F;
    }

    @Override
    public float getOptimalDistance(EntityMinecart cart) {
        return 0.75F;
    }

    @Override
    public boolean isLinkable() {
        return true;
    }

    @Override
    public boolean canLinkWithCart(EntityMinecart cart) {
        return true;
    }

    @Override
    public boolean hasTwoLinks() {
        return true;
    }

    @Override
    public boolean canBeAdjusted(EntityMinecart cart) {
        return true;
    }

    @Override
    public void onLinkCreated(EntityMinecart cart) {
    }

    @Override
    public void onLinkBroken(EntityMinecart cart) {
    }

    @Override
    protected void func_145821_a(int x, int y, int z, double maxVel, double slopeAdj, Block block, int meta) {
        //applyPush
        if (block instanceof BlockRailReception) {
            if (!((BlockRailReception) block).checkNearbySameRail(worldObj, x, y, z))
                if (!((BlockRailReception) block).timeExceed(worldObj, x, y, z)) {
                    applyDrag();
                    return;
                }
        }
        if (block instanceof BlockRailReceptionAnti) {
            if (!((BlockRailReceptionAnti) block).checkNearbySameRail(worldObj, x, y, z))
                if (!((BlockRailReceptionAnti) block).timeExceed(worldObj, x, y, z)) {
                    applyDrag();
                    return;
                }
        }

        /* ******************************** MAIN FUNC ******************************** */

        this.fallDistance = 0.0F;
        Vec3 vec3 = this.func_70489_a(this.posX, this.posY, this.posZ);
        this.posY = (double)y;
        boolean isRailPowered = false;
        boolean slowDown = false;
        if (block == Blocks.golden_rail) { //Stock Powered Rail, Push-Brake
            isRailPowered = (this.worldObj.getBlockMetadata(x, y, z) & 8) != 0;
            slowDown = !isRailPowered;
        }

        if (((BlockRailBase)block).isPowered()) {
            meta &= 7;
        }

        if (meta >= 2 && meta <= 5) {
            this.posY = (double)(y + 1);
        }

        if (meta == 2) {
            this.motionX -= slopeAdj;
        }

        if (meta == 3) {
            this.motionX += slopeAdj;
        }

        if (meta == 4) {
            this.motionZ += slopeAdj;
        }

        if (meta == 5) {
            this.motionZ -= slopeAdj;
        }

        int[][] aint = matrix[meta];
        double d2 = (double)(aint[1][0] - aint[0][0]);
        double d3 = (double)(aint[1][2] - aint[0][2]);
        double d4 = Math.sqrt(d2 * d2 + d3 * d3);
        double d5 = this.motionX * d2 + this.motionZ * d3;
        if (d5 < 0.0D) {
            d2 = -d2;
            d3 = -d3;
        }

        double vel = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
        if (vel > getMaxCartSpeedOnRail()) {
            vel = getMaxCartSpeedOnRail();
        }

        this.motionX = vel * d2 / d4;
        this.motionZ = vel * d3 / d4;
        double d7;
        double d8;
        double d9;
        double d10;
        if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityLivingBase) {
            d7 = (double)((EntityLivingBase)this.riddenByEntity).moveForward;
            if (d7 > 0.0D) {
                d8 = -Math.sin((double)(this.riddenByEntity.rotationYaw * 3.1415927F / 180.0F));
                d9 = Math.cos((double)(this.riddenByEntity.rotationYaw * 3.1415927F / 180.0F));
                d10 = this.motionX * this.motionX + this.motionZ * this.motionZ;
                if (d10 < 0.01D) {
                    this.motionX += d8 * 0.1D;
                    this.motionZ += d9 * 0.1D;
                    slowDown = false;
                }
            }
        }

        if (slowDown && this.shouldDoRailFunctions()) {
            d7 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            if (d7 < 0.03D) {
                this.motionX *= 0.0D;
                this.motionY *= 0.0D;
                this.motionZ *= 0.0D;
            } else {
                this.motionX *= 0.5D;
                this.motionY *= 0.0D;
                this.motionZ *= 0.5D;
            }
        }

        d7 = 0.0D;
        d8 = (double)x + 0.5D + (double)aint[0][0] * 0.5D;
        d9 = (double)z + 0.5D + (double)aint[0][2] * 0.5D;
        d10 = (double)x + 0.5D + (double)aint[1][0] * 0.5D;
        double d11 = (double)z + 0.5D + (double)aint[1][2] * 0.5D;
        d2 = d10 - d8;
        d3 = d11 - d9;
        if (d2 == 0.0D) {
            this.posX = (double)x + 0.5D;
            d7 = this.posZ - (double)z;
        } else if (d3 == 0.0D) {
            this.posZ = (double)z + 0.5D;
            d7 = this.posX - (double)x;
        } else {
            double d12 = this.posX - d8;
            double d13 = this.posZ - d9;
            d7 = (d12 * d2 + d13 * d3) * 2.0D;
        }

        this.posX = d8 + d2 * d7;
        this.posZ = d9 + d3 * d7;
        this.setPosition(this.posX, this.posY + (double)this.yOffset, this.posZ);
        this.moveMinecartOnRail(x, y, z, maxVel);
        if (aint[0][1] != 0 && MathHelper.floor_double(this.posX) - x == aint[0][0] && MathHelper.floor_double(this.posZ) - z == aint[0][2]) {
            this.setPosition(this.posX, this.posY + (double)aint[0][1], this.posZ);
        } else if (aint[1][1] != 0 && MathHelper.floor_double(this.posX) - x == aint[1][0] && MathHelper.floor_double(this.posZ) - z == aint[1][2]) {
            this.setPosition(this.posX, this.posY + (double)aint[1][1], this.posZ);
        }

        this.applyDrag();
        Vec3 vec31 = this.func_70489_a(this.posX, this.posY, this.posZ);
        if (vec31 != null && vec3 != null) {
            double d14 = (vec3.yCoord - vec31.yCoord) * 0.05D;
            vel = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            if (vel > 0.0D) {
                this.motionX = this.motionX / vel * (vel + d14);
                this.motionZ = this.motionZ / vel * (vel + d14);
            }

            this.setPosition(this.posX, vec31.yCoord, this.posZ);
        }
        /** HOLY SHIT! THE CODE CAUSES BUG!
        int pX = MathHelper.floor_double(this.posX);
        int pZ = MathHelper.floor_double(this.posZ);
        if (pX != x || pZ != z) {
            vel = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.motionX = vel * (double)(pX - x);
            this.motionZ = vel * (double)(pZ - z);
        }
        */
        if (this.shouldDoRailFunctions()) {
            ((BlockRailBase)block).onMinecartPass(this.worldObj, this, x, y, z);
        }

        if (isRailPowered && this.shouldDoRailFunctions()) {
            double d15 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            if (d15 > 0.01D) {
                double d16 = 0.06D;
                this.motionX += this.motionX / d15 * d16;
                this.motionZ += this.motionZ / d15 * d16;
            } else if (meta == 1) {
                if (this.worldObj.getBlock(x - 1, y, z).isNormalCube()) {
                    this.motionX = 0.02D;
                } else if (this.worldObj.getBlock(x + 1, y, z).isNormalCube()) {
                    this.motionX = -0.02D;
                }
            } else if (meta == 0) {
                if (this.worldObj.getBlock(x, y, z - 1).isNormalCube()) {
                    this.motionZ = 0.02D;
                } else if (this.worldObj.getBlock(x, y, z + 1).isNormalCube()) {
                    this.motionZ = -0.02D;
                }
            }
        }
        
    }

    @Override
    public void moveMinecartOnRail(int x, int y, int z, double maxVel) {
        double vX = this.motionX;
        double vZ = this.motionZ;

        if (vX < -maxVel) {
            vX = -maxVel;
        }

        if (vX > maxVel) {
            vX = maxVel;
        }

        if (vZ < -maxVel) {
            vZ = -maxVel;
        }

        if (vZ > maxVel) {
            vZ = maxVel;
        }

        this.moveEntity(vX, 0.0D, vZ);
    }

    protected void doEngine() {
        //Do engine code
        tmpPacket = new TrainPacket(this.getEntityId(), getEnginePower(), getEngineBrake(), getEngineDir());
        tmpPacket.isUnits = isHighSpeed();
        tmpPacket.Velocity = this.Velocity;
        TrainController.doMotion(tmpPacket, this);
        setEnginePrevVel(this.Velocity);
        setEngineVel(tmpPacket.Velocity);
    }

    @Override
    protected void applyDrag() {
        doEngine();

        if (worldObj.getBlock(chunkCoordX, chunkCoordY, chunkCoordZ) instanceof IRailSpeedKeep)
            return;
        super.applyDrag();
    }

    @Override
    public boolean interactFirst(EntityPlayer player) {
        return MinecraftForge.EVENT_BUS.post(new MinecartInteractEvent(this, player));
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
                    if (stack.getItem() instanceof Item1N4148) flag = true;
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
    protected void readEntityFromNBT(NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);
        setEnginePower(tagCompound.getInteger("LocoP"));
        setEngineBrake(tagCompound.getInteger("LocoR"));
        setEngineDir(tagCompound.getInteger("LocoDir"));
        setEngineVel(tagCompound.getDouble("LocoV"));
        setEnginePrevVel(tagCompound.getDouble("LocoPV"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setInteger("LocoP", getEnginePower());
        tagCompound.setInteger("LocoR", getEngineBrake());
        tagCompound.setInteger("LocoDir", getEngineDir());
        tagCompound.setDouble("LocoV", getEngineVel());
        tagCompound.setDouble("LocoPV", getEnginePrevVel());
    }

    @Override
    public void onUpdate() {
        update();

        if (worldObj.isRemote) {
            if (getFlag(TICKET_FLAG))
                if (chunks == null)
                    setupChunks(chunkCoordX, chunkCoordZ);
            return;
        }

        if (ticket == null)
            requestTicket();
    }

    public void update() {
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
                double maxSpeed = Math.min(railMaxSpeed, getMaxCartSpeedOnRail());
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

            if (box == null) {
                box = AxisAlignedBB.getBoundingBox(
                        chunkCoordX, chunkCoordY, chunkCoordZ,
                        chunkCoordX + 1, chunkCoordY + 1, chunkCoordZ + 1
                );
            }
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
    protected void func_94088_b(double v) {
        if(this.motionX < -v) {
            this.motionX = -v;
        }

        if(this.motionX > v) {
            this.motionX = v;
        }

        if(this.motionZ < -v) {
            this.motionZ = -v;
        }

        if(this.motionZ > v) {
            this.motionZ = v;
        }

        double moveY = this.motionY;
        if(this.getMaxSpeedAirVertical() > 0.0F && this.motionY > (double)this.getMaxSpeedAirVertical()) {
            moveY = (double)this.getMaxSpeedAirVertical();
            if(Math.abs(this.motionX) < 0.30000001192092896D && Math.abs(this.motionZ) < 0.30000001192092896D) {
                moveY = 0.15000000596046448D;
                this.motionY = moveY;
            }
        }

        boolean isOnSpeedKeepRail = false;
        if (worldObj.getBlock(chunkCoordX, chunkCoordY, chunkCoordZ) instanceof IRailSpeedKeep)
            isOnSpeedKeepRail = true;

        if(this.onGround && !isOnSpeedKeepRail) {
            this.motionX *= 0.5D;
            this.motionY *= 0.5D;
            this.motionZ *= 0.5D;
        }

        this.moveEntity(this.motionX, moveY, this.motionZ);
        if(!this.onGround) {
            this.motionX *= this.getDragAir();
            this.motionY *= this.getDragAir();
            this.motionZ *= this.getDragAir();
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

    /**
     * Follows code are copy from Railcraft(mods.railcraft.common.carts.EntityCartAnchor)
     */
    protected void releaseTicket() {
        ForgeChunkManager.releaseTicket(ticket);
        ticket = null;
        setFlag(TICKET_FLAG, false);
    }

    private boolean requestTicket() {
        ForgeChunkManager.Ticket chunkTicket = ForgeChunkManager.requestTicket(NyaSamaRailway.getInstance(), worldObj, ForgeChunkManager.Type.ENTITY);
        if (chunkTicket != null) {
            chunkTicket.getModData();
            chunkTicket.setChunkListDepth(MAX_CHUNKS);
            chunkTicket.bindEntity(this);
            setChunkTicket(chunkTicket);
            forceChunkLoading(chunkCoordX, chunkCoordZ);
            return true;
        }
        return false;
    }

    public void setChunkTicket(ForgeChunkManager.Ticket tick) {
        if (this.ticket != tick)
            ForgeChunkManager.releaseTicket(this.ticket);
        this.ticket = tick;
        setFlag(TICKET_FLAG, ticket != null);
    }

    public Set<ChunkCoordIntPair> getChunksAround(int xChunk, int zChunk, int radius) {
        Set<ChunkCoordIntPair> chunkList = new HashSet<ChunkCoordIntPair>();
        for (int xx = xChunk - radius; xx <= xChunk + radius; xx++) {
            for (int zz = zChunk - radius; zz <= zChunk + radius; zz++) {
                chunkList.add(new ChunkCoordIntPair(xx, zz));
            }
        }
        return chunkList;
    }

    public void forceChunkLoading(int xChunk, int zChunk) {
        if (ticket == null)
            return;

        setupChunks(xChunk, zChunk);

        Set<ChunkCoordIntPair> innerChunks = getChunksAround(xChunk, zChunk, 1);

        for (ChunkCoordIntPair chunk : chunks) {
            ForgeChunkManager.forceChunk(ticket, chunk);
            ForgeChunkManager.reorderChunk(ticket, chunk);
        }
        for (ChunkCoordIntPair chunk : innerChunks) {
            ForgeChunkManager.forceChunk(ticket, chunk);
            ForgeChunkManager.reorderChunk(ticket, chunk);
        }

        ChunkCoordIntPair myChunk = new ChunkCoordIntPair(xChunk, zChunk);
        ForgeChunkManager.forceChunk(ticket, myChunk);
        ForgeChunkManager.reorderChunk(ticket, myChunk);
    }

    public void setupChunks(int xChunk, int zChunk) {
        if (getFlag(TICKET_FLAG))
            chunks = getChunksAround(xChunk, zChunk, ANCHOR_RADIUS);
        else
            chunks = null;
    }

    @Override
    public void setDead() {
        releaseTicket();
        super.setDead();
    }

    @Override
    public void travelToDimension(int dim) {
        releaseTicket();
        super.travelToDimension(dim);
    }

}
