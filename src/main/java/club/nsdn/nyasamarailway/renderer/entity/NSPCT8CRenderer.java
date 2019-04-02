package club.nsdn.nyasamarailway.renderer.entity;

import club.nsdn.nyasamarailway.api.cart.AbsContainer;
import club.nsdn.nyasamarailway.entity.loco.NSPCT8C;
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
public class NSPCT8CRenderer extends AbsCartRenerer {

    public static IRenderFactory<EntityMinecart> FACTORY = NSPCT8CRenderer::new;
    public static IRenderFactory<AbsContainer> CONTAINER = Container::new;

    private static final WavefrontObject modelBase = new WavefrontObject(
            new ResourceLocation("nyasamarailway", "models/carts/nspc_8c_base.obj")
    );
    private static final WavefrontObject modelPrint = new WavefrontObject(
            new ResourceLocation("nyasamarailway", "models/carts/nspc_8c_print.obj")
    );
    private static final WavefrontObject modelHead = new WavefrontObject(
            new ResourceLocation("nyasamarailway", "models/carts/nspc_8c_head.obj")
    );

    private static final ResourceLocation textureBase = new ResourceLocation(
            "nyasamarailway", "textures/carts/nspc_8c_base.png"
    );
    private static final ResourceLocation texturePrint = new ResourceLocation(
            "nyasamarailway", "textures/carts/nspc_8c_print.png"
    );

    public static class Container extends AbsContainerRenerer {

        public Container(RenderManager manager) { super(manager); }

        @Override
        public void render(AbsContainer container, double x, double y, double z, float yaw) {
            RendererHelper.renderWithResource(modelBase, textureBase);
            RendererHelper.renderWithResource(modelPrint, texturePrint);
        }

    }

    public NSPCT8CRenderer(RenderManager manager) {
        super(manager);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityMinecart cart) {
        return textureBase;
    }

    @Override
    public void render(EntityMinecart minecart, double x, double y, double z, float yaw) {
        if (minecart instanceof NSPCT8C) {
            NSPCT8C cart = (NSPCT8C) minecart;
            GL11.glTranslated(0.0, cart.getShiftY() - 1.0, 0.0);
            RendererHelper.renderWithResource(modelHead, textureBase);
        }
    }
}
