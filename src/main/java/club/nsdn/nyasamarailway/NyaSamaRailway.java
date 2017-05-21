package club.nsdn.nyasamarailway;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import club.nsdn.nyasamarailway.Proxy.CommonProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;

/**
 * Created by drzzm32 on 2017.5.21.
 */
@Mod(modid = NyaSamaRailway.MODID, version = NyaSamaRailway.VERSION)
public class NyaSamaRailway {

    @Mod.Instance("NyaSamaRailway")
    public static NyaSamaRailway instance;
    public static final String MODID = "NyaSamaRailway";
    public static final String VERSION = "2.0";
    public static final boolean isDebug = false;
    public static Logger log = LogManager.getLogger(MODID);

    @SidedProxy(clientSide = "club.nsdn.nyasamarailway.Proxy.ClientProxy",
                serverSide = "club.nsdn.nyasamarailway.Proxy.ServerProxy")
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
