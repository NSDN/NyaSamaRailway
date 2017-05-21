package club.nsdn.nyasamarailway.Proxy;

import club.nsdn.nyasamarailway.Event.EventRegister;
import club.nsdn.nyasamarailway.Renderers.Block.BlockModelBinder;
import club.nsdn.nyasamarailway.TrainControl.TrainController;
import net.minecraftforge.fml.common.event.*;

/**
 * Created by drzzm32 on 2017.5.21.
 */
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);
    }

    @Override
    public void init(FMLInitializationEvent event)
    {
        super.init(event);
        new BlockModelBinder(event);
        //new TileEntityModelBinder(event);
        //new EntityModelBinder(event);
        TrainController.KeyInput.registerKeyBindings();
        EventRegister.registerClient();
    }

    @Override
    public void postInit(FMLPostInitializationEvent event)
    {
        super.postInit(event);
    }


}
