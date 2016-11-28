package club.nsdn.nyasamarailway.Entity;

import club.nsdn.nyasamarailway.Blocks.BlockRailReception;
import club.nsdn.nyasamarailway.Blocks.BlockRailReceptionAnti;
import club.nsdn.nyasamarailway.Items.Item74HC04;
import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.TrainControl.TrainController;
import club.nsdn.nyasamarailway.TrainControl.TrainPacket;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import org.thewdj.physics.Vec3d;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by drzzm32 on 2016.6.23.
 */

public class LocoBase extends EntityMinecart implements ITrainLinkable {

    public int P;
    public int R;
    public int Dir;
    public double Velocity;

    public static final byte TICKET_FLAG = 6;
    public static final byte ANCHOR_RADIUS = 2;
    public static final byte MAX_CHUNKS = 25;
    public Set<ChunkCoordIntPair> chunks;
    public ForgeChunkManager.Ticket ticket;

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
                if (metadata < 8) return;
        }
        if (block instanceof BlockRailReceptionAnti) {
            if (!((BlockRailReceptionAnti) block).checkNearbySameRail(worldObj, x, y, z))
                if (metadata < 8) return;
        }
        super.func_145821_a(x, y, z, v1, v, block, meta);
    }

    @Override
    protected void applyDrag() {
        //Do engine code
        tmpPacket = new TrainPacket(this.getEntityId(), this.P, this.R, this.Dir);
        tmpPacket.Velocity = this.Velocity;
        TrainController.doMotion(tmpPacket, this);
        this.Velocity = tmpPacket.Velocity;

        calcLink(worldObj);

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
    protected void readEntityFromNBT(NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);
        this.P = tagCompound.getInteger("LocoP");
        this.R = tagCompound.getInteger("LocoR");
        this.Dir = tagCompound.getInteger("LocoDir");
        this.Velocity = tagCompound.getDouble("LocoV");
        this.nextLinkTrain = tagCompound.getInteger("nextLinkTrain");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setInteger("LocoP", this.P);
        tagCompound.setInteger("LocoR", this.R);
        tagCompound.setInteger("LocoDir", this.Dir);
        tagCompound.setDouble("LocoV", this.Velocity);
        tagCompound.setInteger("nextLinkTrain", this.nextLinkTrain);
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
