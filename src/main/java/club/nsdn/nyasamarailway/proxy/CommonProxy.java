package club.nsdn.nyasamarailway.proxy;

/**
 * Created by drzzm32 on 2016.5.5.
 */

import club.nsdn.nyasamarailway.entity.EntityLoader;
import club.nsdn.nyasamarailway.event.EventRegister;
import club.nsdn.nyasamarailway.extmod.*;
import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.network.NetworkWrapper;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.*;
import club.nsdn.nyasamarailway.item.ItemLoader;
import club.nsdn.nyasamarailway.block.BlockLoader;
import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.tileblock.TileEntityLoader;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event)
    {
        new NetworkWrapper(event);
        new CreativeTabLoader(event);
        new ItemLoader(event);
        new BlockLoader(event);
    }

    public void init(FMLInitializationEvent event)
    {
        new TileEntityLoader(event);
        new EntityLoader(event);
        EventRegister.registerCommon();
    }

    public void postInit(FMLPostInitializationEvent event)
    {
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
