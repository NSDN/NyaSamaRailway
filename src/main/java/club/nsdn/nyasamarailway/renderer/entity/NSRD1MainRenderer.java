package club.nsdn.nyasamarailway.renderer.entity;

import club.nsdn.nyasamarailway.api.cart.AbsTrainBase;
import club.nsdn.nyasamatelecom.api.render.RendererHelper;
import cn.ac.nya.forgeobj.WavefrontObject;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

/**
 * Created by drzzm32 on 2019.3.1
 */
public class NSRD1MainRenderer extends AbsTrainRenderer {

    public static IRenderFactory<AbsTrainBase> FACTORY = NSRD1MainRenderer::new;

    private final String _name = "nsr_d1_main";

    private final WavefrontObject modelBase = new WavefrontObject(
            new ResourceLocation("nyasamarailway", "models/carts/" + _name + "_base.obj")
    );
    private final WavefrontObject modelPrint = new WavefrontObject(
            new ResourceLocation("nyasamarailway", "models/carts/" + _name + "_print.obj")
    );

    private final ResourceLocation textureBase = new ResourceLocation(
            "nyasamarailway", "textures/carts/" + _name + "_base.png"
    );
    private final ResourceLocation texturePrint = new ResourceLocation(
            "nyasamarailway", "textures/carts/" + _name + "_print.png"
    );

    public NSRD1MainRenderer(RenderManager manager) {
        super(manager);
    }

    @Override
    protected ResourceLocation getEntityTexture(AbsTrainBase train) {
        return textureBase;
    }

    @Override
    public void render(AbsTrainBase train, double x, double y, double z, float yaw) {
        RendererHelper.renderWithResource(modelBase, textureBase);
        RendererHelper.renderWithResource(modelPrint, texturePrint);
    }

}
