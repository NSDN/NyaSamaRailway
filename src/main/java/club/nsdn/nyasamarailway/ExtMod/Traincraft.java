package club.nsdn.nyasamarailway.ExtMod;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;

import java.lang.reflect.Field;

/**
 * Created by drzzm32 on 2016.11.27.
 */
public class Traincraft implements IExtMod {
    public static Traincraft instance;
    public static final String modid = "tc";

    public static void setInstance(Traincraft instance) {
        Traincraft.instance = instance;
    }

    public static Traincraft getInstance() {
        return instance;
    }

    @Override
    public String getModID() {
        return modid;
    }

    @Override
    public boolean verifyBlock(Block block) {
        return false;
    }

    @Override
    public boolean verifyEntity(Entity entity) {
        return ClassVerifier.verifyClass(entity.getClass(), "AbstractTrains", EntityMinecart.class);
    }

    public boolean isLocomotive(Entity entity) {
        return ClassVerifier.verifyClass(entity.getClass(), "train.common.api.Locomotive", EntityMinecart.class);
    }

    public double Locomotive_getAccel(Entity entity) {
        if (isLocomotive(entity)) {
            Field[] field = entity.getClass().getDeclaredFields();
            for (Field f : field) {
                if (f.getName().contains("accelerate")) {
                    double tmp = 0;
                    try {
                        tmp = f.getDouble(entity);
                    } catch (Exception e) {
                        continue;
                    }
                    return tmp;
                }
            }
        }
        return 0;
    }

    public boolean Locomotive_setAccel(Entity entity, double value) {
        if (isLocomotive(entity)) {
            Field[] field = entity.getClass().getDeclaredFields();
            for (Field f : field) {
                if (f.getName().contains("accelerate")) {
                    try {
                        f.setDouble(entity, value);
                    } catch (Exception e) {
                        continue;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public double Locomotive_getBrake(Entity entity, double value) {
        if (isLocomotive(entity)) {
            Field[] field = entity.getClass().getDeclaredFields();
            for (Field f : field) {
                if (f.getName().contains("brake")) {
                    double tmp = 0;
                    try {
                        tmp = f.getDouble(entity);
                    } catch (Exception e) {
                        continue;
                    }
                    return tmp;
                }
            }
        }
        return 0;
    }

    public boolean Locomotive_setBrake(Entity entity, double value) {
        if (isLocomotive(entity)) {
            Field[] field = entity.getClass().getDeclaredFields();
            for (Field f : field) {
                if (f.getName().contains("brake")) {
                    try {
                        f.setDouble(entity, value);
                    } catch (Exception e) {
                        continue;
                    }
                    return true;
                }
            }
        }
        return false;
    }

}
