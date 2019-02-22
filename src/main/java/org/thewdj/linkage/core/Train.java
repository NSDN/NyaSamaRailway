package org.thewdj.linkage.core;

import com.google.common.collect.ForwardingList;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.thewdj.linkage.Linkage;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Train implements Iterable<EntityMinecart> {
    public static final String TRAIN_NBT = "nsrTrain";

    private final UUID uuid;
    private final LinkedList<UUID> carts = new LinkedList<>();
    private final List<UUID> safeCarts = Collections.unmodifiableList(carts);
    private final Set<UUID> locks = new HashSet<>();
    private @Nullable
    World world;
    private State state;
    private boolean dirty = true;
    private boolean isDead;

    Train(EntityMinecart cart) {
        this(UUID.randomUUID(),
                State.NORMAL,
                Collections.singleton(cart.getPersistentID()),
                Collections.emptySet());
        this.world = cart.world;
        rebuild(cart);
    }

    Train(UUID id, State state, Collection<UUID> carts, Set<UUID> locks) {
        this.uuid = id;
        this.state = state;
        this.carts.addAll(carts);
        this.locks.addAll(locks);
    }

    public static void printDebug(String msg, Object... args) {

    }

    private static Optional<Manager> getManager(@Nullable World world) {
        return Manager.forWorld(world);
    }

    public static Object getTicker() {
        return new Object() {

            int counter = 0;

            @SubscribeEvent
            public void tick(TickEvent.WorldTickEvent event) {
                if (event.side == Side.SERVER && event.phase == TickEvent.Phase.END) {
                    counter++;
                    if (counter % 32 == 0)
                        getManager(event.world).ifPresent(Manager::tick);
                }
            }
        };
    }

    public static Optional<Train> get(@Nullable EntityMinecart cart) {
        if (cart == null)
            return Optional.empty();
        return getManager(cart.world).map(manager -> {
            MinecraftServer mcServer = FMLCommonHandler.instance().getMinecraftServerInstance();
            if (!mcServer.isCallingFromMinecraftThread())
                throw new IllegalThreadStateException("Must call from server!");
            Train train = manager.get(getTrainUUID(cart));
            if (train == null) {
                train = new Train(cart);
                manager.put(train.uuid, train);
                printDebug("Creating new train object: {0}", train);
            } else {
                train.world = cart.world;
                if (train.isDead || !train.contains(cart) || train.isInvalid()) {
                    train.kill();
                    return null;
                }
            }
            return train;
        });
    }

    private static Optional<Train> getTrainRaw(@Nullable EntityMinecart cart) {
        if (cart == null)
            return Optional.empty();
        Optional<Train> train = getManager(cart.world).map(manager -> manager.get(getTrainUUID(cart)));
        train.ifPresent(t -> t.world = cart.world);
        return train;
    }

    @Override
    public String toString() {
        return String.format("Train{id=%s,n=%d}", uuid, size());
    }

    /**
     * Will stream all carts in the train if on the server, or just the passed in cart if on the client.
     */
    public static Stream<EntityMinecart> streamCarts(EntityMinecart cart) {
        return get(cart).map(Train::stream).orElseGet(() -> Stream.of(cart));
    }

    public static @Nullable UUID readUUID(NBTTagCompound data, String tag) {
        if (data.hasKey(tag)) {
            NBTTagCompound nbtTag = data.getCompoundTag(tag);
            if (nbtTag.hasKey("most")) {
                return new UUID(nbtTag.getLong("most"), nbtTag.getLong("least"));
            } else {
                return NBTUtil.getUUIDFromTag(nbtTag);
            }
        }
        return null;
    }

    public static @Nullable UUID getTrainUUID(EntityMinecart cart) {
        NBTTagCompound nbt = cart.getEntityData();
        return readUUID(nbt, TRAIN_NBT);
    }

    public static boolean isPartOfTrain(EntityMinecart cart) {
        return Train.get(cart).map(t -> t.size() > 1).orElse(false);
    }

    public static boolean areInSameTrain(@Nullable EntityMinecart cart1, @Nullable EntityMinecart cart2) {
        if (cart1 == null || cart2 == null)
            return false;
        if (cart1 == cart2)
            return true;

        UUID train1 = getTrainUUID(cart1);
        UUID train2 = getTrainUUID(cart2);

        return train1 != null && Objects.equals(train1, train2);
    }

    private static Optional<Train> getLongerTrain(EntityMinecart cart1, EntityMinecart cart2) {
        Optional<Train> train1 = getTrainRaw(cart1);
        Optional<Train> train2 = getTrainRaw(cart2);

        if (train1.equals(train2))
            return train1;
        if (!train1.isPresent())
            return train2;
        if (!train2.isPresent())
            return train1;

        if (train1.get().size() >= train2.get().size())
            return train1;
        return train2;
    }

    public static void repairTrain(EntityMinecart cart1, EntityMinecart cart2) {
        getLongerTrain(cart1, cart2).ifPresent(t -> t.rebuild(cart1));
    }

    public static void removeTrainTag(EntityMinecart cart) {
        cart.getEntityData().removeTag(TRAIN_NBT);
    }

    public static void writeUUID(NBTTagCompound data, String tag, @Nullable UUID uuid) {
        if (uuid == null)
            return;
        NBTTagCompound nbtTag = NBTUtil.createUUIDTag(uuid);
        data.setTag(tag, nbtTag);
    }

    public void addTrainTag(EntityMinecart cart) {
        UUID trainId = getUUID();
        writeUUID(cart.getEntityData(), TRAIN_NBT, trainId);
    }

    private @Nullable
    EntityMinecart getCart(UUID cartID) {
        Objects.requireNonNull(world);
        return CartTools.getCartFromUUID(world, cartID);
    }

    private void rebuild(EntityMinecart first) {
        clear();
        rebuild(null, first);
        markDirty();
    }

    private void rebuild(@Nullable EntityMinecart prev, EntityMinecart next) {
        if (prev == null || carts.getFirst() == prev.getPersistentID())
            carts.addFirst(next.getPersistentID());
        else if (carts.getLast() == prev.getPersistentID())
            carts.addLast(next.getPersistentID());
        else
            throw new IllegalStateException("Passed a non-null prev value on an empty train!");

        getTrainRaw(next).filter(t -> t != this).ifPresent(Train::kill);
        addTrainTag(next);

        LinkageManager lm = LinkageManager.INSTANCE;
        EntityMinecart linkA = lm.getLinkedCartA(next);
        EntityMinecart linkB = lm.getLinkedCartB(next);

        if (linkA != null && linkA != prev && !contains(linkA))
            rebuild(next, linkA);

        if (linkB != null && linkB != prev && !contains(linkB))
            rebuild(next, linkB);
    }

    private boolean isInvalid() {
        return isEmpty() || carts.stream().anyMatch(this::isCartInvalid);
    }

    private boolean isCartInvalid(UUID cartID) {
        EntityMinecart cart = getCart(cartID);
        return cart != null && !uuid.equals(getTrainUUID(cart));
    }

    /**
     * Only marks the train for removal, it isn't removed until the next world tick.
     *
     * This is done for thread safety reasons.
     */
    public static void killTrain(EntityMinecart cart) {
//        Game.log(Level.WARN, "Thread: " + Thread.currentThread().getName());
        getTrainRaw(cart).ifPresent(Train::kill);
        removeTrainTag(cart);
    }

    public void kill() {
        isDead = true;
    }

    private void clear() {
        forEach(Train::removeTrainTag);
        carts.clear();
        markDirty();
    }

    public UUID getUUID() {
        return uuid;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean contains(@Nullable EntityMinecart cart) {
        return cart != null && carts.contains(cart.getPersistentID());
    }

    public boolean contains(@Nullable UUID cart) {
        return cart != null && carts.contains(cart);
    }

    public boolean isTrainEnd(@Nullable EntityMinecart cart) {
        return cart != null && getEnds().contains(cart.getPersistentID());
    }

    public Collection<UUID> getEnds() {
        Set<UUID> ends = new HashSet<>();
        if (!carts.isEmpty()) {
            ends.add(carts.getFirst());
            ends.add(carts.getLast());
        }
        return ends;
    }


    public Stream<EntityMinecart> stream() {
        return safeCarts.stream()
                .map(this::getCart)
                .filter(Objects::nonNull);
    }

    @Override
    public Iterator<EntityMinecart> iterator() {
        return stream().iterator();
    }

    public int size() {
        return carts.size();
    }

    public boolean isEmpty() {
        return carts.isEmpty();
    }

    public boolean isTrainLockedDown() {
        return !locks.isEmpty();
    }

    public void addLock(UUID lock) {
        locks.add(lock);
        markDirty();
    }

    public void removeLock(UUID lock) {
        locks.remove(lock);
        markDirty();
    }

    public boolean isIdle() {
        return state == State.IDLE || isTrainLockedDown();
    }

    public boolean isStopped() {
        return state == State.STOPPED;
    }

    public void setTrainState(State state) {
        if (this.state != state) {
            this.state = state;
            markDirty();
        }
    }

    public enum State {

        STOPPED,
        IDLE,
        NORMAL
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Train)) {
            return false;
        }
        Train other = (Train) obj;
        return uuid.equals(other.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    public static <T extends Enum<T>> void writeEnumOrdinal(NBTTagCompound data, String tag, Enum<T> e) {
        assert e.ordinal() < Byte.MAX_VALUE;
        data.setByte(tag, (byte) e.ordinal());
    }

    public static <T extends Enum<T>> T readEnumOrdinal(NBTTagCompound data, String tag, T[] enumConstants, T defaultValue) {
        if (data.hasKey(tag)) {
            byte ordinal = data.getByte(tag);
            return enumConstants[ordinal];
        }
        return defaultValue;
    }

    public enum EnumNBTType {

        END(NBTTagEnd.class),
        BYTE(NBTTagByte.class),
        SHORT(NBTTagShort.class),
        INT(NBTTagInt.class),
        LONG(NBTTagLong.class),
        FLOAT(NBTTagFloat.class),
        DOUBLE(NBTTagDouble.class),
        BYTE_ARRAY(NBTTagByteArray.class),
        STRING(NBTTagString.class),
        LIST(NBTTagList.class),
        COMPOUND(NBTTagCompound.class),
        INT_ARRAY(NBTTagIntArray.class);
        public static final EnumNBTType[] VALUES = values();
        private static final Map<Class<? extends NBTBase>, EnumNBTType> classToType = new HashMap<>();
        public final Class<? extends NBTBase> classObject;

        EnumNBTType(Class<? extends NBTBase> c) {
            this.classObject = c;
        }

        static {
            for (EnumNBTType each : VALUES) {
                classToType.put(each.classObject, each);
            }
        }

        public static EnumNBTType fromClass(Class<? extends NBTBase> c) {
            return classToType.get(c);
        }

    }

    public static <T extends NBTBase> List<T> getNBTList(NBTTagCompound nbt, String tag, Class<T> type) {
        NBTTagList nbtList = nbt.getTagList(tag, EnumNBTType.fromClass(type).ordinal());
        return NBTList.make(nbtList);
    }

    public static <T extends NBTBase> List<T> asList(NBTTagList list) {
        return new NBTList<>(list);
    }

    private static final class NBTList<T extends NBTBase> extends ForwardingList<T> {

        static <T extends NBTBase> NBTList<T> make(NBTTagList nbtList) {
            return new NBTList<>(nbtList);
        }

        private final List<T> backingList;

        @SuppressWarnings("unchecked")
        private NBTList(NBTTagList nbtList) {
            backingList = Lists.newArrayList();
            for (int i = 0; i < nbtList.tagCount(); i++)
                backingList.add((T) nbtList.get(i));
        }

        @Override
        protected List<T> delegate() {
            return backingList;
        }

    }

    static @Nullable
    Train readFromNBT(NBTTagCompound tag) {
        UUID id = readUUID(tag, "id");
        if (id == null)
            return null;
        State state = readEnumOrdinal(tag, "state", State.values(), State.NORMAL);
        List<UUID> carts = getNBTList(tag, "carts", NBTTagCompound.class).stream().map(NBTUtil::getUUIDFromTag).collect(Collectors.toList());
        Set<UUID> locks = getNBTList(tag, "locks", NBTTagCompound.class).stream().map(NBTUtil::getUUIDFromTag).collect(Collectors.toSet());
        return new Train(id, state, carts, locks);
    }

    void writeToNBT(NBTTagCompound tag) {
        writeUUID(tag, "id", uuid);
        writeEnumOrdinal(tag, "state", state);
        NBTTagList listTag = new NBTTagList();
        for (UUID uuid : carts) {
            listTag.appendTag(NBTUtil.createUUIDTag(uuid));
        }
        tag.setTag("carts", listTag);

        NBTTagList locks = new NBTTagList();
        for (UUID uuid : this.locks) {
            locks.appendTag(NBTUtil.createUUIDTag(uuid));
        }
        tag.setTag("locks", locks);
    }

    void markDirty() {
        setDirty(true);
    }

    boolean isDirty() {
        return dirty;
    }

    void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public static final class Manager extends ForwardingMap<UUID, Train> {

        private static final Map<World, Manager> instances = new MapMaker().weakKeys().makeMap();

        final World world;
        final SaveData data;

        private Manager(World world) {
            this.world = world;
            this.data = makeData(world);
        }

        private static Optional<Manager> forWorld(@Nullable World world) {
            if (world == null || world.isRemote)
                return Optional.empty();
            return Optional.of(instances.computeIfAbsent(world, Manager::new));
        }

        private static SaveData makeData(World world) {
            MapStorage storage = world.getPerWorldStorage();
            SaveData data = (SaveData) storage.getOrLoadData(SaveData.class, "nsr.trains");
            if (data == null) {
                data = new SaveData("nsr.trains");
                storage.setData("nsr.trains", data);
            }
            return data;
        }

        public static void clearTrains() {
            instances.values().forEach(ForwardingMap::clear);
        }

        @Override
        protected Map<UUID, Train> delegate() {
            return data.trains;
        }

        public void tick() {
            Iterator<Train> it = values().iterator();
            while (it.hasNext()) {
                Train train = it.next();
                train.world = world;
                if (train.isDead || train.isInvalid()) {
                    train.clear();
                    it.remove();
                    data.markDirty();
                }
            }
        }

    }

    public static final class SaveData extends WorldSavedData {
        final Map<UUID, Train> trains = new ForwardingMap<UUID, Train>() {
            private final Map<UUID, Train> trains = new HashMap<>();

            @Override
            protected Map<UUID, Train> delegate() {
                return trains;
            }

            @Override
            public Train put(UUID key, Train value) {
                markDirty();
                return super.put(key, value);
            }

            @Override
            public void putAll(Map<? extends UUID, ? extends Train> map) {
                standardPutAll(map);
            }

            @Override
            public Train remove(Object key) {
                markDirty();
                return super.remove(key);
            }

            @Override
            public void clear() {
                super.clear();
                markDirty();
            }
        };

        public SaveData(String name) {
            super(name);
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            trains.clear();
            for (NBTTagCompound each : getNBTList(nbt, "trains", NBTTagCompound.class)) {
                Train train = Train.readFromNBT(each);
                if (train != null)
                    trains.put(train.getUUID(), train);
            }
            Linkage.logger.info(String.format("Loaded %d Trains...", trains.size()));
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound compound) {
            Linkage.logger.info(String.format("Saving %d Trains...", trains.size()));
            NBTTagList listTag = new NBTTagList();
            for (Train train : trains.values()) {
                NBTTagCompound tag = new NBTTagCompound();
                train.writeToNBT(tag);
                listTag.appendTag(tag);
                train.setDirty(false);
            }
            compound.setTag("trains", listTag);
            return compound;
        }

        @Override
        public boolean isDirty() {
            return super.isDirty() || trains.values().stream().anyMatch(Train::isDirty);
        }
    }

}
