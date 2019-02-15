package club.nsdn.nyasamarailway.proxy;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.event.EventRegister;
import club.nsdn.nyasamarailway.util.TrainController;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        EventRegister.registerClient();
        OBJLoader.INSTANCE.addDomain(NyaSamaRailway.MODID);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        TrainController.KeyInput.registerKeyBindings();
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }


}
