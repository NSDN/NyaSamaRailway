package club.nsdn.nyasamarailway.Proxy;

/**
 * Created by drzzm32 on 2016.5.5.
 */

import club.nsdn.nyasamarailway.Blocks.BlockLoader;
import club.nsdn.nyasamarailway.CreativeTab.CreativeTabLoader;
import club.nsdn.nyasamarailway.Entity.EntityLoader;
import club.nsdn.nyasamarailway.Entity.EntityModelBinder;
import club.nsdn.nyasamarailway.Event.EventRegister;
import club.nsdn.nyasamarailway.ExtMod.ExRollerCoaster;
import club.nsdn.nyasamarailway.ExtMod.Railcraft;
import club.nsdn.nyasamarailway.ExtMod.RailsOfWar;
import club.nsdn.nyasamarailway.ExtMod.Traincraft;
import club.nsdn.nyasamarailway.Items.ItemLoader;
import club.nsdn.nyasamarailway.Network.NetworkWrapper;
import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.TileEntities.TileEntityLoader;
import club.nsdn.nyasamarailway.Util.TrainController;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.*;
import club.nsdn.nyasamarailway.TileEntities.TileEntityModelBinder;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        new NetworkWrapper(event);
        new CreativeTabLoader(event);
        new ItemLoader(event);
        new BlockLoader(event);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        new TileEntityLoader(event);
        new EntityLoader(event);
        EventRegister.registerCommon();
        new TileEntityModelBinder(event);
        new EntityModelBinder(event);
        TrainController.KeyInput.registerKeyBindings();
        EventRegister.registerClient();
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        if (Loader.isModLoaded(ExRollerCoaster.modid)) {
            ExRollerCoaster.setInstance(new ExRollerCoaster());
            NyaSamaRailway.log.info("NyaSamaRailway detected ExRollerCoaster mod");
        }
        if (Loader.isModLoaded(RailsOfWar.modid)) {
            RailsOfWar.setInstance(new RailsOfWar());
            NyaSamaRailway.log.info("NyaSamaRailway detected RailsOfWar mod");
        }
        if (Loader.isModLoaded(Traincraft.modid)) {
            Traincraft.setInstance(new Traincraft());
            NyaSamaRailway.log.info("NyaSamaRailway detected Traincraft mod");
        }
        if (Loader.isModLoaded(Railcraft.modid)) {
            Railcraft.setInstance(new Railcraft());
            NyaSamaRailway.log.info("NyaSamaRailway detected Railcraft mod");
        }
    }


}
