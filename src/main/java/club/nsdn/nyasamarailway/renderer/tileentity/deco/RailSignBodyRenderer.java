package club.nsdn.nyasamarailway.renderer.tileentity.deco;

import club.nsdn.nyasamatelecom.api.render.AbsTileEntitySpecialRenderer;
import club.nsdn.nyasamatelecom.api.render.RendererHelper;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import cn.ac.nya.forgeobj.WavefrontObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2017.9.10.
 */
public class RailSignBodyRenderer extends AbsTileEntitySpecialRenderer {

    private final WavefrontObject model = new WavefrontObject(
            new ResourceLocation("nyasamarailway", "models/blocks/rail_sign_body.obj")
    );

    private final ResourceLocation texture = new ResourceLocation(
            "nyasamarailway", "textures/blocks/" + "rail_sign_body" + ".png"
    );

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

        RendererHelper.renderWithResourceAndRotation(model, angle, texture);

        RenderHelper.enableStandardItemLighting();

        GL11.glPopMatrix();
    }

}
