package club.nsdn.nyasamarailway.api.cart;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.item.ItemLoader;
import club.nsdn.nyasamarailway.network.TrainPacket;
import club.nsdn.nyasamarailway.api.signal.TileEntityTrackSideReception;
import club.nsdn.nyasamarailway.util.TrainController;
import org.thewdj.linkage.api.ILinkableCart;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.*;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * Created by drzzm32 on 2019.2.10
 */
public abstract class AbsLocoBase extends AbsCartBase implements ILocomotive, ILinkableCart {

    public int P;
    public int R;
    public int Dir;
    public double Velocity;
    public double prevVelocity;

    private static final DataParameter<Integer> POWER = EntityDataManager.createKey(AbsLocoBase.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> BRAKE = EntityDataManager.createKey(AbsLocoBase.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> DIR = EntityDataManager.createKey(AbsLocoBase.class, DataSerializers.VARINT);
    private static final DataParameter<Float> VEL = EntityDataManager.createKey(AbsLocoBase.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> PVEL = EntityDataManager.createKey(AbsLocoBase.class, DataSerializers.FLOAT);

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(POWER, 0);
        dataManager.register(BRAKE, 0);
        dataManager.register(DIR, 0);
        dataManager.register(VEL, 0.0F);
        dataManager.register(PVEL, 0.0F);

        dataManager.register(TICKET, false);
    }

    public int getEnginePower() {
        return dataManager.get(POWER);
    }

    public void setEnginePower(int value) {
        this.P = value;
        dataManager.set(POWER, value);
    }

    public int getEngineBrake() {
        return dataManager.get(BRAKE);
    }

    public void setEngineBrake(int value) {
        this.R = value;
        dataManager.set(BRAKE, value);
    }

    public int getEngineDir() {
        return dataManager.get(DIR);
    }

    public void setEngineDir(int value) {
        this.Dir = value;
        dataManager.set(DIR, value);
    }

    public double getEngineVel() {
        return dataManager.get(VEL);
    }

    public void setEngineVel(double value) {
        this.Velocity = (float) value;
        dataManager.set(VEL, (float) value);
    }

    public double getEnginePrevVel() {
        return dataManager.get(PVEL);
    }

    public void setEnginePrevVel(double value) {
        this.prevVelocity = (float) value;
        dataManager.set(PVEL, (float) value);
    }

    private static final DataParameter<Boolean> TICKET = EntityDataManager.createKey(AbsLocoBase.class, DataSerializers.BOOLEAN);
    public static final byte ANCHOR_RADIUS = 2;
    public static final byte MAX_CHUNKS = 25;
    public Set<ChunkPos> chunks;
    public ForgeChunkManager.Ticket ticket;

    public void setTicketFlag(boolean flag) {
        dataManager.set(TICKET, flag);
    }

    public boolean hasTicketFlag() {
        return dataManager.get(TICKET);
    }

    protected TrainPacket tmpPacket;

    public AbsLocoBase(World world) {
        super(world);
    }

    public AbsLocoBase(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Nonnull
    public abstract ItemStack getCartItem();

    @Override
    public void killMinecart(DamageSource source) {
        this.setDead();
        ItemStack stack = getCartItem();
        if (stack.isEmpty()) {
            Item item = ItemLoader.itemCarts.get(getClass());
            if (item == null) item = Items.AIR;
            stack = new ItemStack(item, 1);
        }
        stack.setStackDisplayName(stack.getDisplayName());
        if (!source.damageType.equals("nsr")) this.entityDropItem(stack, 0.0F);
    }

    @Override
    public boolean processInitialInteract(@Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
        return MinecraftForge.EVENT_BUS.post(new MinecartInteractEvent(this, player, hand));
    }

    public int getMaxPassengerSize() {
        return 0;
    }

    @Override
    protected boolean canFitPassenger(Entity entity) {
        return getPassengers().size() < getMaxPassengerSize();
    }

    @Override // Called by rider
    public void updatePassenger(Entity entity) { }

    @Override
    @Nonnull
    public Type getType() {
        return Type.FURNACE;
    }

    @Override
    public double getMountedYOffset() {
        return 0.0;
    }

    @Override
    public float getMaxCartSpeedOnRail() {
        return 2.0F;
    }

    public double getPlayerPushVel() {
        return 0.2;
    }

    public void setTrainPacket(TrainPacket packet) {
        setEnginePower(packet.P);
        setEngineBrake(packet.R);
        setEngineDir(packet.Dir);
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
    public boolean canLink(EntityMinecart cart) {
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
    protected void moveAlongTrack(BlockPos pos, IBlockState state) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityTrackSideReception) {
            TileEntityTrackSideReception reception = (TileEntityTrackSideReception) tileEntity;
            if (reception.delay < reception.setDelay * 20) {
                applyDrag();
                return;
            }
        }

        moveAlongTrackCore(pos, state);
    }

    protected void doEngine() {
        //Do engine code
        tmpPacket = new TrainPacket(getEnginePower(), getEngineBrake(), getEngineDir());
        tmpPacket.Velocity = this.Velocity;
        TrainController.doMotion(tmpPacket, this);
        setEnginePrevVel(this.Velocity);
        setEngineVel(tmpPacket.Velocity);
    }

    @Override
    protected void applyDrag() {
        //Do engine code
        doEngine();
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

    /*******************************************************************************************************************/

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (world.isRemote) {
            if (hasTicketFlag())
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
        setTicketFlag(false);
    }

    private boolean requestTicket() {
        ForgeChunkManager.Ticket chunkTicket = ForgeChunkManager.requestTicket(
                NyaSamaRailway.getInstance(), world, ForgeChunkManager.Type.ENTITY
        );
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

    public void setChunkTicket(@Nullable ForgeChunkManager.Ticket tick) {
        if (this.ticket != tick)
            ForgeChunkManager.releaseTicket(this.ticket);
        this.ticket = tick;
        setTicketFlag(ticket != null);
    }

    public Set<ChunkPos> getChunksAround(int xChunk, int zChunk, int radius) {
        Set<ChunkPos> chunkList = new HashSet<ChunkPos>();
        for (int xx = xChunk - radius; xx <= xChunk + radius; xx++) {
            for (int zz = zChunk - radius; zz <= zChunk + radius; zz++) {
                chunkList.add(new ChunkPos(xx, zz));
            }
        }
        return chunkList;
    }

    public void forceChunkLoading(int xChunk, int zChunk) {
        if (ticket == null)
            return;

        setupChunks(xChunk, zChunk);

        Set<ChunkPos> innerChunks = getChunksAround(xChunk, zChunk, 1);

        for (ChunkPos chunk : chunks) {
            ForgeChunkManager.forceChunk(ticket, chunk);
            ForgeChunkManager.reorderChunk(ticket, chunk);
        }
        for (ChunkPos chunk : innerChunks) {
            ForgeChunkManager.forceChunk(ticket, chunk);
            ForgeChunkManager.reorderChunk(ticket, chunk);
        }

        ChunkPos myChunk = new ChunkPos(xChunk, zChunk);
        ForgeChunkManager.forceChunk(ticket, myChunk);
        ForgeChunkManager.reorderChunk(ticket, myChunk);
    }

    public void setupChunks(int xChunk, int zChunk) {
        if (hasTicketFlag())
            chunks = getChunksAround(xChunk, zChunk, ANCHOR_RADIUS);
        else
            chunks = Collections.emptySet();
    }

    @Override
    public void setDead() {
        releaseTicket();
        super.setDead();
    }

    @Nullable
    @Override
    public Entity changeDimension(int dim) {
        releaseTicket();
        return super.changeDimension(dim);
    }

}
