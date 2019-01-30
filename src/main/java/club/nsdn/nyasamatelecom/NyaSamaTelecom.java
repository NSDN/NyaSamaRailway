package club.nsdn.nyasamatelecom;

import club.nsdn.nyasamatelecom.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

/**
 * Created by drzzm32 on 2018.12.12.
 */
@Mod(modid = NyaSamaTelecom.MODID, name = NyaSamaTelecom.NAME, version = NyaSamaTelecom.VERSION)
public class NyaSamaTelecom {

    @Mod.Instance("NyaSamaTelecom")
    public static NyaSamaTelecom instance;
    public static final String MODID = "nyasamatelecom";
    public static final String NAME = "NyaSama Telecom";
    public static final String VERSION = "2.0";
    public static Logger logger;

    @SidedProxy(
        clientSide = "club.nsdn.nyasamatelecom.proxy.ClientProxy",
        serverSide = "club.nsdn.nyasamatelecom.proxy.ServerProxy")
    public static CommonProxy proxy;

    public static NyaSamaTelecom getInstance() {
        if (instance == null) instance = new NyaSamaTelecom();
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
