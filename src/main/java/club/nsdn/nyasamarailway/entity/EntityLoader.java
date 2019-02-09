package club.nsdn.nyasamarailway.entity;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.entity.cart.*;
import club.nsdn.nyasamarailway.entity.loco.*;
import club.nsdn.nyasamarailway.entity.nsc.*;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import java.util.LinkedList;

/**
 * Created by drzzm32 on 2016.5.24.
 */
public class EntityLoader {

    private static EntityLoader instance;
    public static EntityLoader instance() {
        if (instance == null) instance = new EntityLoader();
        return instance;
    }

    public static LinkedList<Class<? extends Entity>> entities;

    private static void register(int index, Class<? extends Entity> entity) {
        String name = entity.getName().replace(EntityLoader.class.getPackage().getName(), "");
        name = name.replace(".", "_").substring(1).replace("$", "_").toLowerCase();
        EntityRegistry.registerModEntity(
                new ResourceLocation(NyaSamaRailway.MODID, name),
                entity, name, index, NyaSamaRailway.getInstance(),
                256, 3, true
        );
    }

    @SubscribeEvent
    public void registerEntities(RegistryEvent.Register<EntityEntry> event) {
        NyaSamaRailway.logger.info("registering Entities");
        for (int i = 0; i < entities.size(); i++)
            register(i, entities.get(i));
    }

    public EntityLoader() {
        entities = new LinkedList<>();

        entities.add(NSPCT4.class);
        entities.add(NSPCT8.class);
        entities.add(NSPCT8W.class);
        entities.add(NSPCT8W.Container.class);
        entities.add(NSPCT9.class);
        entities.add(NSPCT10.class);

        entities.add(NSET2.class);
        entities.add(NSPCT4M.class);
        entities.add(NSPCT8C.class);
        entities.add(NSPCT8C.Container.class);
        entities.add(NSPCT8J.class);
        entities.add(NSPCT8M.class);
        entities.add(NSPCT9M.class);
        entities.add(NSPCT10J.class);
        entities.add(NSPCT10M.class);

        entities.add(NSC1A.class);
        entities.add(NSC1AM.class);
        entities.add(NSC1B.class);
        entities.add(NSC1BM.class);

        entities.add(NSC2A.class);
        entities.add(NSC2AM.class);
        entities.add(NSC2B.class);
        entities.add(NSC2BM.class);

        entities.add(NSC3A.class);
        entities.add(NSC3AM.class);
        entities.add(NSC3B.class);
        entities.add(NSC3BM.class);
    }

}
