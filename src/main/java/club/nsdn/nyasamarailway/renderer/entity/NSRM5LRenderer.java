package club.nsdn.nyasamarailway.renderer.entity;

import club.nsdn.nyasamarailway.api.cart.AbsTrainBase;
import club.nsdn.nyasamarailway.entity.train.NSRM5L;
import cn.ac.nya.forgeobj.WavefrontObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

/**
 * Created by drzzm32 on 2019.3.21
 */
public class NSRM5LRenderer extends AbsTrainRenderer {

    public static IRenderFactory<AbsTrainBase> FACTORY = NSRM5LRenderer::new;

    private final String _name = "nsr_m5l";

    private final WavefrontObject modelBase = new WavefrontObject(
            new ResourceLocation("nyasamarailway", "models/carts/" + _name + "_main.obj")
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

    public NSRM5LRenderer(RenderManager manager) {
        super(manager);
    }

    @Override
    protected ResourceLocation getEntityTexture(AbsTrainBase train) {
        return textureBase;
    }

    @Override
    public void render(AbsTrainBase train, double x, double y, double z, float yaw) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.0625, 0.0625, 0.0625);

        Minecraft.getMinecraft().getTextureManager().bindTexture(textureBase);
        modelBase.renderAllExcept(
                "door1l", "door1r", "door2l", "door2r", "door3l", "door3r", "door4l", "door4r",
                "w0", "w1", "w2", "w3", "w4", "w5", "w6", "w7", "w8", "w9"
        );

        if (train instanceof NSRM5L) {
            NSRM5L metro = (NSRM5L) train;

            GlStateManager.Profile.TRANSPARENT_MODEL.apply();
            modelBase.renderOnly("w0", "w1", "w2", "w3", "w4", "w5", "w6", "w7", "w8", "w9");

            double prog = metro.doorProgressRight / 100.0 * 15;
            GlStateManager.pushMatrix();
            GlStateManager.translate(-prog, 0, 0);
            modelBase.renderOnly("door1l", "door3l");
            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();
            GlStateManager.translate(prog, 0, 0);
            modelBase.renderOnly("door1r", "door3r");
            GlStateManager.popMatrix();

            prog = metro.doorProgressLeft / 100.0 * 15;
            GlStateManager.pushMatrix();
            GlStateManager.translate(prog, 0, 0);
            modelBase.renderOnly("door2l", "door4l");
            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();
            GlStateManager.translate(-prog, 0, 0);
            modelBase.renderOnly("door2r", "door4r");
            GlStateManager.popMatrix();

            GlStateManager.Profile.TRANSPARENT_MODEL.clean();
        }

        Minecraft.getMinecraft().getTextureManager().bindTexture(texturePrint);
        modelPrint.renderAll();

        GlStateManager.popMatrix();
    }

}
