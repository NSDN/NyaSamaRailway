package club.nsdn.nyasamarailway.renderer.entity;

import club.nsdn.nyasamarailway.entity.cart.NSBT4A;
import club.nsdn.nyasamarailway.entity.cart.NSBT4B;
import club.nsdn.nyasamarailway.entity.loco.NSBT4M;
import cn.ac.nya.forgeobj.WavefrontObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

/**
 * Created by drzzm32 on 2019.3.1
 */
public class NSBT4Renderer extends AbsCartRenderer {

    public static IRenderFactory<EntityMinecart> FACTORY_A = (RenderManager manager) -> new NSBT4Renderer(manager, "nsb_4a");
    public static IRenderFactory<EntityMinecart> FACTORY_B = (RenderManager manager) -> new NSBT4Renderer(manager, "nsb_4b");
    public static IRenderFactory<EntityMinecart> FACTORY_M = (RenderManager manager) -> new NSBT4Renderer(manager, "nsb_4m");

    private final WavefrontObject modelBase;
    private final WavefrontObject modelWheel;
    private final ResourceLocation textureBase;

    public NSBT4Renderer(RenderManager manager, String name) {
        super(manager);

        this.modelBase = new WavefrontObject(
                new ResourceLocation("nyasamarailway", "models/carts/" + name + "_base.obj")
        );
        this.modelWheel = new WavefrontObject(
                new ResourceLocation("nyasamarailway", "models/carts/" + name + "_wheel.obj")
        );
        this.textureBase = new ResourceLocation(
                "nyasamarailway", "textures/carts/" + name + "_base.png"
        );
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
        if (cart instanceof NSBT4A) {
            angle = ((NSBT4A) cart).angle;
        } else if (cart instanceof NSBT4B) {
            angle = ((NSBT4B) cart).angle;
        } else if (cart instanceof NSBT4M) {
            angle = ((NSBT4M) cart).angle;
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
