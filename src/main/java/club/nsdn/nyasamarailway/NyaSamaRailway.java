package club.nsdn.nyasamarailway;

import club.nsdn.nyasamarailway.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

/**
 * Created by drzzm32 on 2019.2.10
 */
@Mod(modid = NyaSamaRailway.MODID, name = NyaSamaRailway.NAME, version = NyaSamaRailway.VERSION)
public class NyaSamaRailway {

    @Mod.Instance
    public static NyaSamaRailway instance;
    public static final String MODID = "nyasamarailway";
    public static final String NAME = "NyaSama Railway";
    public static final String VERSION = "2.0";
    public static Logger logger;

    @SidedProxy(clientSide = "club.nsdn.nyasamarailway.proxy.ClientProxy",
            serverSide = "club.nsdn.nyasamarailway.proxy.ServerProxy")
    public static CommonProxy proxy;

    public static NyaSamaRailway getInstance() {
        return instance;
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

}
