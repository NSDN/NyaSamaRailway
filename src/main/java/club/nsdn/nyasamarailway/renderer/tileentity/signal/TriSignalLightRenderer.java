package club.nsdn.nyasamarailway.renderer.tileentity.signal;

import club.nsdn.nyasamatelecom.api.render.AbsTileEntitySpecialRenderer;
import club.nsdn.nyasamatelecom.api.render.RendererHelper;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import cn.ac.nya.forgeobj.WavefrontObject;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class TriSignalLightRenderer extends AbsTileEntitySpecialRenderer {

    private final WavefrontObject modelBase;
    private final WavefrontObject modelLight[];
    private final ResourceLocation textureBase = new ResourceLocation("nyasamarailway", "textures/blocks/signal_light_base.png");
    private final ResourceLocation[] textureLight = {
            new ResourceLocation("nyasamarailway", "textures/blocks/signal_box_none.png"),
            new ResourceLocation("nyasamarailway", "textures/blocks/signal_box_r.png"),
            new ResourceLocation("nyasamarailway", "textures/blocks/signal_box_y.png"),
            new ResourceLocation("nyasamarailway", "textures/blocks/signal_box_g.png")
    };

    public TriSignalLightRenderer(String baseName) {
        modelBase = new WavefrontObject(
                new ResourceLocation("nyasamarailway", "models/blocks/" + baseName + "_base.obj")
        );
        modelLight = new WavefrontObject[] {
                new WavefrontObject(
                        new ResourceLocation("nyasamarailway", "models/blocks/" + baseName + "_light_1.obj")
                ),
                new WavefrontObject(
                        new ResourceLocation("nyasamarailway", "models/blocks/" + baseName + "_light_2.obj")
                ),
                new WavefrontObject(
                        new ResourceLocation("nyasamarailway", "models/blocks/" + baseName + "_light_3.obj")
                )
        };
    }

    @Override
    public void render(@Nonnull TileEntityBase te, double x, double y, double z, float partialTicks, int destroyStage, float partial) {
        int meta = te.META;

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y  + 0.5F, (float) z + 0.5F);

        RenderHelper.disableStandardItemLighting();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_CULL_FACE);

        if (Minecraft.isAmbientOcclusionEnabled()) {
            GL11.glShadeModel(GL11.GL_SMOOTH);
        } else {
            GL11.glShadeModel(GL11.GL_FLAT);
        }

        int angle = (meta & 0x3) * 90;

        int lightState = (meta >> 2) & 0x3;

        RendererHelper.renderWithResourceAndRotation(modelBase, angle, textureBase);
        RendererHelper.renderWithResourceAndRotation(modelLight[0], angle, textureLight[lightState == 0 ? 3 : 0]);
        RendererHelper.renderWithResourceAndRotation(modelLight[1], angle, textureLight[lightState == 2 ? 1 : 0]);
        RendererHelper.renderWithResourceAndRotation(modelLight[2], angle, textureLight[lightState == 1 ? 2 : 0]);

        RenderHelper.enableStandardItemLighting();

        GL11.glPopMatrix();
    }

}
