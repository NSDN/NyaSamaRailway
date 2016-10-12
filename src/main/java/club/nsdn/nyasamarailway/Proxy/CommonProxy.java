package club.nsdn.nyasamarailway.Proxy;

/**
 * Created by drzzm32 on 2016.5.5.
 */

import club.nsdn.nyasamarailway.Entity.EntityLoader;
import club.nsdn.nyasamarailway.Event.EventRegister;
import club.nsdn.nyasamarailway.ExtMod.*;
import club.nsdn.nyasamarailway.TrainControl.NetworkWrapper;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.*;
import club.nsdn.nyasamarailway.Items.ItemLoader;
import club.nsdn.nyasamarailway.Blocks.BlockLoader;
import club.nsdn.nyasamarailway.CreativeTab.CreativeTabLoader;
import club.nsdn.nyasamarailway.TileEntities.TileEntityLoader;

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
        if (Loader.isModLoaded("erc")) {
            ExRollerCoaster.setInstance(new ExRollerCoaster());
            System.out.println("[NSDN] NyaSamaRailway detected ExRollerCoaster mod");
        }
        if (Loader.isModLoaded("row")) {
            RailsOfWar.setInstance(new RailsOfWar());
            System.out.println("[NSDN] NyaSamaRailway detected RailsOfWar mod");
        }
    }

}
