package club.nsdn.nyasamarailway.util;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.LinkedList;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class SoundUtil {

    private static SoundUtil instance;
    public static SoundUtil instance() {
        if (instance == null) instance = new SoundUtil();
        return instance;
    }

    public LinkedList<SoundEvent> sounds;

    public final SoundEvent RECEPTION_PAUSE;
    public final SoundEvent RECEPTION_JETTY;
    public final SoundEvent RECEPTION_READY;
    public final SoundEvent RECEPTION_DELAY;
    public final SoundEvent RECEPTION_CLOSE;
    public final SoundEvent GATE_BEEP;

    @SubscribeEvent
    public void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        NyaSamaRailway.logger.info("registering SoundEvents");
        event.getRegistry().registerAll(sounds.toArray(new SoundEvent[0]));
    }

    private SoundEvent addSound(String id) {
        ResourceLocation location = new ResourceLocation(NyaSamaRailway.MODID, id);
        SoundEvent soundEvent = new SoundEvent(location);
        soundEvent.setRegistryName(location);
        sounds.add(soundEvent);
        return soundEvent;
    }

    public SoundUtil() {
        sounds = new LinkedList<>();

        RECEPTION_PAUSE = addSound("info.reception.pause");
        RECEPTION_JETTY = addSound("info.reception.jetty");
        RECEPTION_READY = addSound("info.reception.ready");
        RECEPTION_DELAY = addSound("info.reception.delay");
        RECEPTION_CLOSE = addSound("info.reception.close");
        GATE_BEEP = addSound("info.gate.beep");
    }

    public void playSound(Entity entity, SoundEvent event) {
        playSound(entity.world, entity.posX, entity.posY, entity.posZ, event);
    }

    public void playSoundTop(World world, BlockPos pos, SoundEvent event) {
        playSound(world, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, event);
    }

    public void playSound(World world, double x, double y, double z, SoundEvent event) {
        world.playSound(null, x, y, z, event, SoundCategory.RECORDS, 0.5F, 1.0F);
    }

}
