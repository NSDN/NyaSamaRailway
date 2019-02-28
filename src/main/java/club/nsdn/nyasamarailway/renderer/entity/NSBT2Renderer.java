package club.nsdn.nyasamarailway.renderer.entity;

import club.nsdn.nyasamarailway.entity.cart.NSBT2;
import cn.ac.nya.forgeobj.WavefrontObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

/**
 * Created by drzzm32 on 2019.2.27
 */
public class NSBT2Renderer extends AbsCartRenerer {

    public static IRenderFactory<EntityMinecart> FACTORY = NSBT2Renderer::new;

    private final String _name = "nsb_2";

    private final WavefrontObject modelBase = new WavefrontObject(
            new ResourceLocation("nyasamarailway", "models/carts/" + _name + "_base.obj")
    );
    private final WavefrontObject modelWheel = new WavefrontObject(
            new ResourceLocation("nyasamarailway", "models/carts/" + _name + "_wheel.obj")
    );

    private final ResourceLocation textureBase = new ResourceLocation(
            "nyasamarailway", "textures/carts/" + _name + "_base.png"
    );

    public NSBT2Renderer(RenderManager manager) {
        super(manager);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityMinecart cart) {
        return textureBase;
    }

    @Override
    public void render(EntityMinecart cart, double x, double y, double z, float yaw) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.0625, 0.0625, 0.0625);
        Minecraft.getMinecraft().getTextureManager().bindTexture(textureBase);
        modelBase.renderAllExcept("wheel1", "wheel2");
        GlStateManager.popMatrix();

        float angle = 0.0F;
        if (cart instanceof NSBT2) {
            angle = ((NSBT2) cart).angle;
        }

        GlStateManager.pushMatrix();
        GlStateManager.scale(0.0625, 0.0625, 0.0625);
        GlStateManager.pushMatrix();
        GlStateManager.translate(6, -3, 0);
        GlStateManager.pushMatrix();
        GlStateManager.rotate(angle, 0.0F, 0.0F, 1.0F);
        modelWheel.renderAll();
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.scale(0.0625, 0.0625, 0.0625);
        GlStateManager.pushMatrix();
        GlStateManager.translate(-6, -3, 0);
        GlStateManager.pushMatrix();
        GlStateManager.rotate(angle, 0.0F, 0.0F, 1.0F);
        modelWheel.renderAll();
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
    }

}
