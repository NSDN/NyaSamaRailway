package club.nsdn.nyasamaoptics.renderer.tileblock;

import club.nsdn.nyasamaoptics.tileblock.light.RGBLight;
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
 * Created by drzzm32 on 2019.1.30.
 */
public class LightRenderer extends AbsTileEntitySpecialRenderer {

    private final ResourceLocation textureShell = new ResourceLocation("nyasamaoptics", "textures/blocks/light_shell.png");
    private final ResourceLocation textureLight = new ResourceLocation("nyasamaoptics", "textures/blocks/light_base.png");

    public LightRenderer() {
    }

    public static void loadModel(RGBLight light) {
        if (light.modelShell == null)
            light.modelShell = new WavefrontObject(
                    new ResourceLocation("nyasamaoptics", "models/blocks/" + light.resource + "_base.obj")
            );
        if (light.modelLight == null)
            light.modelLight = new WavefrontObject(
                    new ResourceLocation("nyasamaoptics", "models/blocks/" + light.resource + "_light.obj")
            );
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

        GL11.glPushMatrix();

        float angle = (meta % 4) * 90.0F;
        GL11.glRotatef(angle, 0.0F, -1.0F, 0.0F);

        if (te.getBlockType() instanceof RGBLight) {
            RGBLight light = (RGBLight) te.getBlockType();

            if (light.modelShell == null)
                light.modelShell = new WavefrontObject(
                    new ResourceLocation("nyasamaoptics", "models/blocks/" + light.resource + "_base.obj")
                );
            if (light.modelLight == null)
                light.modelLight = new WavefrontObject(
                    new ResourceLocation("nyasamaoptics", "models/blocks/" + light.resource + "_light.obj")
                );

            GL11.glPushMatrix();

            switch (meta / 4) {
                case 1:
                    GL11.glRotatef(90.0F, -1.0F, 0.0F, 0.0F);
                    break;
                case 2:
                    GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
                    break;
            }

            GL11.glColor3f(1.0F, 1.0F, 1.0F);
            RendererHelper.renderWithResource(light.modelShell, textureShell);

            if (te instanceof RGBLight.TileEntityRGBLight) {
                RGBLight.TileEntityRGBLight tileEntityRGBLight = (RGBLight.TileEntityRGBLight) te;
                int color = tileEntityRGBLight.color;
                GL11.glColor3f(((color & 0xFF0000) >> 16) / 255.0F, ((color & 0x00FF00) >> 8) / 255.0F, (color & 0x0000FF) / 255.0F);
                if (!tileEntityRGBLight.isEnabled && tileEntityRGBLight.getSender() != null) {
                    GL11.glColor3f(0.33F, 0.33F, 0.33F);
                }
                RendererHelper.renderWithResource(light.modelLight, textureLight);
                GL11.glColor3f(1.0F, 1.0F, 1.0F);
            }

            GL11.glPopMatrix();
        }

        GL11.glPopMatrix();

        RenderHelper.enableStandardItemLighting();

        GL11.glPopMatrix();
    }

}
