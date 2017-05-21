package club.nsdn.nyasamarailway.Proxy;

import club.nsdn.nyasamarailway.Event.EventRegister;
import club.nsdn.nyasamarailway.TrainControl.NetworkWrapper;
import net.minecraftforge.fml.common.event.*;
import club.nsdn.nyasamarailway.Items.ItemLoader;
import club.nsdn.nyasamarailway.Blocks.BlockLoader;
import club.nsdn.nyasamarailway.CreativeTab.CreativeTabLoader;

/**
 * Created by drzzm32 on 2017.5.21.
 */
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
        //new TileEntityLoader(event);
        //new EntityLoader(event);
        EventRegister.registerCommon();
    }

    public void postInit(FMLPostInitializationEvent event)
    {
        /*
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
        */
    }

}
