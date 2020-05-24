package club.nsdn.nyasamarailway.renderer.entity;

import club.nsdn.nyasamatelecom.api.render.RendererHelper;
import cn.ac.nya.forgeobj.WavefrontObject;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class NSET2Renderer extends AbsCartRenderer {

    public static IRenderFactory<EntityMinecart> FACTORY = NSET2Renderer::new;

    private final WavefrontObject modelBase = new WavefrontObject(
            new ResourceLocation("nyasamarailway", "models/carts/nse_2_base.obj")
    );
    private final WavefrontObject modelPrint = new WavefrontObject(
            new ResourceLocation("nyasamarailway", "models/carts/nse_2_print.obj")
    );

    private final ResourceLocation textureBase = new ResourceLocation(
            "nyasamarailway", "textures/carts/nse_2_base.png"
    );
    private final ResourceLocation texturePrint = new ResourceLocation(
            "nyasamarailway", "textures/carts/nse_2_print.png"
    );

    public NSET2Renderer(RenderManager manager) {
        super(manager);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityMinecart cart) {
        return textureBase;
    }

    @Override
    public void render(EntityMinecart minecart, double x, double y, double z, float yaw) {
        RendererHelper.renderWithResource(modelBase, textureBase);
        RendererHelper.renderWithResource(modelPrint, texturePrint);
    }

}
