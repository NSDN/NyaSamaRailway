package club.nsdn.nyasamatelecom.util;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.DimensionManager;
import net.minecraft.tileentity.TileEntity;
import org.thewdj.telecom.IWireless;
import net.minecraft.world.World;
import org.thewdj.telecom.IWirelessRx;
import org.thewdj.telecom.IWirelessTx;

import java.util.LinkedHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by drzzm32 on 2018.12.13.
 */
public class TelecomProcessor {

    private static TelecomProcessor instance;

    public static TelecomProcessor instance() {
        if (instance == null) instance = new TelecomProcessor();
        return instance;
    }

    public static class DeviceInfo {

        public String key;
        public int x, y, z;
        public int dimension;

        @PackagePrivate
        TileEntity dev() {
            World world = DimensionManager.getWorld(dimension);
            if (world == null) return null;
            return world.getTileEntity(new BlockPos(x, y, z));
        }

    }

    private LinkedHashMap<String, DeviceInfo> devices;
    private LinkedHashMap<String, DeviceInfo> cache;

    public enum State { SUP, ZERO, ONE }
    private LinkedHashMap<DeviceInfo, State> inputs;
    private LinkedHashMap<DeviceInfo, State> outputs;

    private final ReentrantLock lock = new ReentrantLock();

    private int gcCounter;

    public TelecomProcessor() {
        devices = new LinkedHashMap<>();
        cache = new LinkedHashMap<>();

        inputs = new LinkedHashMap<>();
        outputs = new LinkedHashMap<>();

        gcCounter = 0;
    }

    public void update() {
        if (!lock.tryLock()) return;

        devices.forEach((id, info) -> {
            if (checkDevice(info)) {
                if (info.dev() instanceof IWirelessRx) {
                    if (inputs.containsKey(info)) {
                        if (inputs.get(info) != State.SUP) {
                            State state = inputs.get(info);
                            boolean result = false;
                            if (state == State.ONE) result = true;
                            if (state == State.ZERO) result = false;
                            ((IWirelessRx) info.dev()).setState(result);

                            inputs.replace(info, State.SUP);
                        }
                    }
                } else if (info.dev() instanceof IWirelessTx) {
                    if (outputs.containsKey(info)) {
                        boolean state = ((IWirelessTx) info.dev()).getState();
                        outputs.replace(info, state ? State.ONE : State.ZERO);
                    }
                }
            }
        });

        if (gcCounter < 20) gcCounter += 1;
        else {
            gcCounter = 0;

            gc();
        }

        lock.unlock();
    }

    private void gc() {
        if (devices.isEmpty()) return;

        cache.clear(); cache.putAll(devices);
        cache.forEach((id, info) -> {
            if (!checkDevice(info)) {
                devices.remove(id);
                if (inputs.containsKey(info))
                    inputs.remove(info);
                if (outputs.containsKey(info))
                    outputs.remove(info);
            }
        });
    }

    private boolean checkDevice(DeviceInfo info) {
        return info.dev() != null;
    }

    public DeviceInfo device(String id) {
        try {
            lock.lock();
            if (!devices.containsKey(id)) return null;
            return devices.get(id);
        } finally {
            lock.unlock();
        }
    }

    public boolean isRx(DeviceInfo info) {
        try {
            lock.lock();
            return inputs.containsKey(info);
        } finally {
            lock.unlock();
        }
    }

    public boolean isTx(DeviceInfo info) {
        try {
            lock.lock();
            return outputs.containsKey(info);
        } finally {
            lock.unlock();
        }
    }

    public void set(DeviceInfo info, boolean state) {
        lock.lock();
        if (inputs.containsKey(info)) {
            inputs.replace(info, state ? State.ONE : State.ZERO);
        }
        lock.unlock();
    }

    public boolean get(DeviceInfo info) {
        try {
            lock.lock();
            if (outputs.containsKey(info)) {
                State state = outputs.get(info);
                if (state == State.ONE) return true;
                if (state == State.ZERO) return false;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    private void register(String id, DeviceInfo info) {
        try {
            lock.lock();
            if (devices.containsKey(id)) devices.remove(id);
            devices.put(id, info);
            if (info.dev() instanceof IWirelessRx) {
                if (inputs.containsKey(info)) inputs.remove(info);
                inputs.put(info, State.SUP);
            } else if (info.dev() instanceof IWirelessTx) {
                if (outputs.containsKey(info)) outputs.remove(info);
                outputs.put(info, State.ZERO);
            }
        } finally {
            lock.unlock();
        }
    }

    public void register(IWireless<? extends TileEntity> device) {
        if (device.me() == null) return;
        if (device.id().equals("null") || device.key().equals("null")) return;

        DeviceInfo info = new DeviceInfo();
        info.key = device.key();
        info.dimension = device.me().getWorld().provider.getDimension();
        info.x = device.me().getPos().getX();
        info.y = device.me().getPos().getY();
        info.z = device.me().getPos().getZ();

        register(device.id(), info);
    }

}
