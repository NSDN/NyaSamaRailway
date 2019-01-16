package club.nsdn.nyasamatelecom.proxy;

import club.nsdn.nyasamatelecom.NyaSamaTelecom;
import club.nsdn.nyasamatelecom.event.EventRegister;
import club.nsdn.nyasamatelecom.network.webservice.ITelecom;
import club.nsdn.nyasamatelecom.network.webservice.TelecomImpl;
import net.minecraftforge.fml.common.event.*;

/**
 * Created by drzzm32 on 2018.12.12.
 */
public class ServerProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        EventRegister.registerServer();

        NyaSamaTelecom.logger.info("Loading WebService...");
        try {
            ITelecom.publish(new TelecomImpl(), "http://0.0.0.0:32/api");
            NyaSamaTelecom.logger.info("WebService loaded.");
        } catch (Exception e) {
            NyaSamaTelecom.logger.info("WebService failed to load: " + e.getMessage());
        }
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event)
    {
        super.postInit(event);
    }


}
