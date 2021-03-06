package club.nsdn.nyasamarailway.entity;

import club.nsdn.nyasamarailway.block.rail.special.BlockRailReception;
import club.nsdn.nyasamarailway.block.rail.special.BlockRailReceptionAnti;
import club.nsdn.nyasamarailway.block.rail.IRailSpeedKeep;
import club.nsdn.nyasamarailway.item.tool.Item1N4148;
import club.nsdn.nyasamarailway.item.tool.ItemNTP32Bit;
import club.nsdn.nyasamarailway.item.tool.ItemNTP8Bit;
import club.nsdn.nyasamarailway.tileblock.rail.mono.RailMonoMagnetReception;
import club.nsdn.nyasamarailway.tileblock.rail.mono.RailMonoMagnetReceptionAnti;
import club.nsdn.nyasamarailway.tileblock.signal.TileEntityTrackSideReception;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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

import java.util.List;

/**
 * Created by drzzm32 on 2016.5.23.
 */
public class MinecartBase extends EntityMinecartEmpty implements mods.railcraft.api.carts.ILinkableCart {

    /** Minecart rotational logic matrix */
    public static final int[][][] matrix = new int[][][]{
            {{0, 0, -1}, {0, 0, 1}}, {{-1, 0, 0}, {1, 0, 0}}, {{-1, -1, 0}, {1, 0, 0}},
            {{-1, 0, 0}, {1, -1, 0}}, {{0, 0, -1}, {0, -1, 1}}, {{0, -1, -1}, {0, 0, 1}},
            {{0, 0, 1}, {1, 0, 0}}, {{0, 0, 1}, {-1, 0, 0}}, {{0, 0, -1}, {-1, 0, 0}},
            {{0, 0, -1}, {1, 0, 0}}
    };    /** appears to be the progress of the turn */
    public boolean isInReverse = false;

    public boolean canMakePlayerTurn() {
        return true;
    }

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
                    if (stack.getItem() instanceof Item1N4148 ||
                            stack.getItem() instanceof ItemNTP8Bit ||
                            stack.getItem() instanceof ItemNTP32Bit) {
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

    @Override
    public boolean isRiding() {
        return this.ridingEntity != null;
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

    @Override  //applyPush()
    protected void func_145821_a(int x, int y, int z, double maxVel, double slopeAdj, Block block, int meta) {
        //applyPush
        if (worldObj.getTileEntity(x, y, z) instanceof TileEntityTrackSideReception) {
            TileEntityTrackSideReception reception = (TileEntityTrackSideReception) worldObj.getTileEntity(x, y, z);
            if (riddenByEntity == null && !reception.cartType.isEmpty()) {
                if (!reception.cartType.equals("loco"))
                    return;
            }
        }

        if (block instanceof BlockRailReception) {
            BlockRailReception.TileEntityRailReception tile = (BlockRailReception.TileEntityRailReception) worldObj.getTileEntity(x, y, z);
            if (!((BlockRailReception) block).checkNearbySameRail(worldObj, x, y, z))
                if (riddenByEntity == null && !tile.cartType.isEmpty()) {
                    if (!tile.cartType.equals("loco"))
                        return;
                }
        }
        if (block instanceof BlockRailReceptionAnti) {
            BlockRailReceptionAnti.TileEntityRailReceptionAnti tile = (BlockRailReceptionAnti.TileEntityRailReceptionAnti) worldObj.getTileEntity(x, y, z);
            if (!((BlockRailReceptionAnti) block).checkNearbySameRail(worldObj, x, y, z))
                if (riddenByEntity == null && !tile.cartType.isEmpty()) {
                    if (!tile.cartType.equals("loco"))
                        return;
                }
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

    @Override
    protected void applyDrag() {
        //Do engine code
        if (worldObj.getBlock(chunkCoordX, chunkCoordY, chunkCoordZ) instanceof IRailSpeedKeep)
            return;
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
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
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

            double detlaYaw = (double)MathHelper.wrapAngleTo180_float(this.rotationYaw - this.prevRotationYaw);
            /* Driver Heading */
            if (this.riddenByEntity != null && !this.riddenByEntity.isDead) {
                if (this.riddenByEntity.ridingEntity == this)
                {
                    if (this.riddenByEntity instanceof EntityPlayer) {
                        if (canMakePlayerTurn())
                            ((EntityPlayer) this.riddenByEntity).rotationYaw += detlaYaw;
                    }
                }
            }

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

            /* Driver Heading */
            if (this.riddenByEntity != null && !this.riddenByEntity.isDead) {
                if (this.riddenByEntity.ridingEntity == this)
                {
                    if (this.riddenByEntity instanceof EntityPlayer) {
                        if (canMakePlayerTurn())
                            ((EntityPlayer) this.riddenByEntity).rotationYaw += detlaYaw;
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

}
