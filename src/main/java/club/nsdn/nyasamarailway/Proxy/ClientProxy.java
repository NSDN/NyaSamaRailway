package club.nsdn.nyasamarailway.Proxy;

import club.nsdn.nyasamarailway.Blocks.BlockLoader;
import club.nsdn.nyasamarailway.Event.EventRegister;
import club.nsdn.nyasamarailway.Items.ItemLoader;
import club.nsdn.nyasamarailway.TrainControl.TrainController;
import net.minecraftforge.fml.common.event.*;

/**
 * Created by drzzm32 on 2017.5.21.
 */
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        BlockLoader.preLoadModels();
        ItemLoader.preLoadModels();
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        TrainController.KeyInput.registerKeyBindings();
        EventRegister.registerClient();
        BlockLoader.loadModels();
        ItemLoader.loadModels();
    }

    @Override
    public void postInit(FMLPostInitializationEvent event)
    {
        super.postInit(event);
    }


}
