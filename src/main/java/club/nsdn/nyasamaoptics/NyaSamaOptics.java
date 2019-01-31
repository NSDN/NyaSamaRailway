package club.nsdn.nyasamaoptics;

import club.nsdn.nyasamaoptics.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

/**
 * Created by drzzm32 on 2019.1.30.
 */
@Mod(modid = NyaSamaOptics.MODID, name = NyaSamaOptics.NAME, version = NyaSamaOptics.VERSION)
public class NyaSamaOptics {

    @Mod.Instance("NyaSamaOptics")
    public static NyaSamaOptics instance;
    public static final String MODID = "nyasamaoptics";
    public static final String NAME = "NyaSama Optics";
    public static final String VERSION = "2.0";
    public static Logger logger;

    @SidedProxy(clientSide = "club.nsdn.nyasamaoptics.proxy.ClientProxy",
                serverSide = "club.nsdn.nyasamaoptics.proxy.ServerProxy")
    public static CommonProxy proxy;

    public static NyaSamaOptics getInstance() {
        if (instance == null) instance = new NyaSamaOptics();
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
