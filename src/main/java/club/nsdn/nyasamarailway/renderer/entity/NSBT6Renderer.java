package club.nsdn.nyasamarailway.renderer.entity;

import club.nsdn.nyasamarailway.api.cart.nsc.IMonoRailCart;
import club.nsdn.nyasamatelecom.api.render.RendererHelper;
import cn.ac.nya.forgeobj.WavefrontObject;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11;

/**
 * Created by drzzm32 on 2019.3.21
 */
public class NSBT6Renderer extends AbsCartRenerer {

    public static IRenderFactory<EntityMinecart> FACTORY = NSBT6Renderer::new;

    private final String _name = "nsb_6";

    private final WavefrontObject modelBase = new WavefrontObject(
            new ResourceLocation("nyasamarailway", "models/carts/" + _name + "_base.obj")
    );

    private final ResourceLocation textureBase = new ResourceLocation(
            "nyasamarailway", "textures/carts/" + _name + "_base.png"
    );

    public NSBT6Renderer(RenderManager manager) {
        super(manager);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityMinecart cart) {
        return textureBase;
    }

    @Override
    public void render(EntityMinecart cart, double x, double y, double z, float yaw) {
        GL11.glPushMatrix();
        if (cart instanceof IMonoRailCart)
            GL11.glTranslated(0.0, ((IMonoRailCart) cart).getShiftY() - 0.3125, 0.0);
        RendererHelper.renderWithResource(modelBase, textureBase);
        GL11.glPopMatrix();
    }

}
