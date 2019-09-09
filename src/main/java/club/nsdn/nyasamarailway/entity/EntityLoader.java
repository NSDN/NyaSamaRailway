package club.nsdn.nyasamarailway.entity;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.entity.cart.*;
import club.nsdn.nyasamarailway.entity.loco.*;
import club.nsdn.nyasamarailway.entity.nsc.*;
import club.nsdn.nyasamarailway.entity.train.*;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import java.util.LinkedList;

/**
 * Created by drzzm32 on 2019.2.10
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
        entities.add(NSPCT7.class);
        entities.add(NSPCT8.class);
        entities.add(NSPCT8W.class);
        entities.add(NSPCT8W.Container.class);
        entities.add(NSPCT9.class);
        entities.add(NSPCT10.class);

        entities.add(NSET2.class);
        entities.add(NSET3.class);
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

        entities.add(NSBT2.class);
        entities.add(NSBT2M.class);
        entities.add(NSRM1.class);
        entities.add(NSRM1T.class);

        entities.add(NSBT3.class);
        entities.add(NSBT3M.class);
        entities.add(NSRM2.class);
        entities.add(NSRM2T.class);

        entities.add(NSBT3G.class);
        entities.add(NSBT3GM.class);
        entities.add(NSRM2G.class);
        entities.add(NSRM2GT.class);

        entities.add(NSBT4A.class);
        entities.add(NSBT4B.class);
        entities.add(NSBT4M.class);
        entities.add(NSRD1Shelf.class);
        entities.add(NSRD1Main.class);

        entities.add(NSRD2Shelf.class);
        entities.add(NSRD2Main.class);

        entities.add(NSE4.class);

        entities.add(NSBT5.class);
        entities.add(NSBT5M.class);
        entities.add(NSRM3.class);
        entities.add(NSRM3T.class);

        entities.add(NSRA1.class);
        entities.add(NSRA2.class);

        entities.add(NSRM4.class);
        entities.add(NSRM4T.class);

        entities.add(NSBT6.class);
        entities.add(NSBT6M.class);
        entities.add(NSRM5.class);
        entities.add(NSRM5T.class);
        entities.add(NSRM5L.class);
    }

}
