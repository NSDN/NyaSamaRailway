package club.nsdn.nyasamarailway.Entity;

import club.nsdn.nyasamarailway.Blocks.BlockRailReception;
import club.nsdn.nyasamarailway.Blocks.BlockRailReceptionAnti;
import club.nsdn.nyasamarailway.Blocks.IRailSpeedKeep;
import club.nsdn.nyasamarailway.Items.Item1N4148;
import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.TrainControl.TrainController;
import club.nsdn.nyasamarailway.TrainControl.TrainPacket;
import net.minecraft.block.Block;
import club.nsdn.nyasamarailway.Blocks.BlockRailBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by drzzm32 on 2016.6.23.
 */

public class LocoBase extends EntityMinecart implements mods.railcraft.api.carts.ILinkableCart {

    public int P;
    public int R;
    public int Dir;
    public double Velocity;

    public static final byte TICKET_FLAG = 6;
    public static final byte ANCHOR_RADIUS = 2;
    public static final byte MAX_CHUNKS = 25;
    public Set<ChunkCoordIntPair> chunks;
    public ForgeChunkManager.Ticket ticket;

    protected TrainPacket tmpPacket;

    protected boolean isHighSpeed() {
        return false;
    }

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
    protected void func_145821_a(int x, int y, int z, double v1, double v, Block block, int meta) {
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
        super.func_145821_a(x, y, z, v1, v, block, meta);
    }

    protected void doEngine() {
        //Do engine code
        tmpPacket = new TrainPacket(this.getEntityId(), this.P, this.R, this.Dir);
        tmpPacket.isUnits = isHighSpeed();
        tmpPacket.Velocity = this.Velocity;
        TrainController.doMotion(tmpPacket, this);
        this.Velocity = tmpPacket.Velocity;
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
        this.P = tagCompound.getInteger("LocoP");
        this.R = tagCompound.getInteger("LocoR");
        this.Dir = tagCompound.getInteger("LocoDir");
        this.Velocity = tagCompound.getDouble("LocoV");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setInteger("LocoP", this.P);
        tagCompound.setInteger("LocoR", this.R);
        tagCompound.setInteger("LocoDir", this.Dir);
        tagCompound.setDouble("LocoV", this.Velocity);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (worldObj.isRemote) {
            if (getFlag(TICKET_FLAG))
                if (chunks == null)
                    setupChunks(chunkCoordX, chunkCoordZ);
            return;
        }

        if (ticket == null)
            requestTicket();
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
