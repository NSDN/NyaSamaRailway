package club.nsdn.nyasamarailway.renderer.entity;

import club.nsdn.nyasamarailway.entity.cart.NSPCT8W;
import club.nsdn.nyasamatelecom.api.render.RendererHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.ResourceLocation;
import cn.ac.nya.forgeobj.WavefrontObject;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class NSPCT8WRenderer extends AbsCartRenerer {

    public static IRenderFactory<EntityMinecart> FACTORY = NSPCT8WRenderer::new;

    private final WavefrontObject modelBase = new WavefrontObject(
            new ResourceLocation("nyasamarailway", "models/carts/nspc_8w_base.obj")
    );
    private final WavefrontObject modelPrint = new WavefrontObject(
            new ResourceLocation("nyasamarailway", "models/carts/nspc_8w_print.obj")
    );
    private final WavefrontObject modelHead = new WavefrontObject(
            new ResourceLocation("nyasamarailway", "models/carts/nspc_8w_head.obj")
    );

    private final ResourceLocation textureBase = new ResourceLocation(
            "nyasamarailway", "textures/carts/nspc_8w_base.png"
    );
    private final ResourceLocation texturePrint = new ResourceLocation(
            "nyasamarailway", "textures/carts/nspc_8w_print.png"
    );

    public NSPCT8WRenderer(RenderManager manager) {
        super(manager);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityMinecart cart) {
        return textureBase;
    }

    @Override
    public void render(EntityMinecart minecart, double x, double y, double z, float yaw) {
        if (minecart instanceof NSPCT8W) {
            NSPCT8W cart = (NSPCT8W) minecart;
            GL11.glTranslated(0.0, cart.getShiftYCnt(), 0.0);
            RendererHelper.renderWithResource(modelHead, textureBase);
        } else if (minecart instanceof NSPCT8W.Container) {
            RendererHelper.renderWithResource(modelBase, textureBase);
            RendererHelper.renderWithResource(modelPrint, texturePrint);
        }
    }
}
