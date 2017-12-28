package club.nsdn.nyasamarailway;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import club.nsdn.nyasamarailway.proxy.CommonProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by drzzm32 on 2016.5.5.
 */
@Mod(modid = NyaSamaRailway.MODID, version = NyaSamaRailway.VERSION)
public class NyaSamaRailway {

    @Mod.Instance("NyaSamaRailway")
    public static NyaSamaRailway instance;
    public static final String MODID = "NyaSamaRailway";
    public static final String VERSION = "0.5";
    public static final boolean isDebug = false;
    public static Logger log = LogManager.getLogger(MODID);

    @SidedProxy(clientSide = "club.nsdn.nyasamarailway.proxy.ClientProxy",
                serverSide = "club.nsdn.nyasamarailway.proxy.ServerProxy")
    public static CommonProxy proxy;

    public static NyaSamaRailway getInstance() { return instance; }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit(event);
    }

}
