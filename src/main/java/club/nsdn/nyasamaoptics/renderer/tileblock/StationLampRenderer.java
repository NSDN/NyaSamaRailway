package club.nsdn.nyasamaoptics.renderer.tileblock;

import club.nsdn.nyasamaoptics.tileblock.screen.StationLamp;
import club.nsdn.nyasamatelecom.api.render.AbsTileEntitySpecialRenderer;
import club.nsdn.nyasamatelecom.api.render.RendererHelper;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import cn.ac.nya.forgeobj.WavefrontObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.1.30.
 */
public class StationLampRenderer extends AbsTileEntitySpecialRenderer {

    private final WavefrontObject modelBase = new WavefrontObject(
            new ResourceLocation("nyasamaoptics", "models/blocks/" + "station_lamp" + "_base.obj")
    );
    private final WavefrontObject modelLogo = new WavefrontObject(
            new ResourceLocation("nyasamaoptics", "models/blocks/" + "station_lamp" + "_logo.obj")
    );
    private final WavefrontObject modelBack = new WavefrontObject(
            new ResourceLocation("nyasamaoptics", "models/blocks/" + "station_lamp" + "_back.obj")
    );

    private final ResourceLocation textureBase = new ResourceLocation("nyasamaoptics", "textures/blocks/station_lamp_base.png");
    private final ResourceLocation textureLogo = new ResourceLocation("nyasamaoptics", "textures/blocks/station_lamp_logo.png");
    private final ResourceLocation textureBack = new ResourceLocation("nyasamaoptics", "textures/blocks/station_lamp_back.png");

    public StationLampRenderer() {
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

        if (te instanceof StationLamp.TileEntityStationLamp) {
            StationLamp.TileEntityStationLamp tileEntityStationLamp = (StationLamp.TileEntityStationLamp) te;
            GL11.glPushMatrix();

            GL11.glColor3f(1.0F, 1.0F, 1.0F);
            RendererHelper.renderWithResource(modelBase, textureBase);

            int backColor = tileEntityStationLamp.back;
            if (!tileEntityStationLamp.isEnabled && tileEntityStationLamp.getSender() != null) {
                GL11.glColor3f(0.33F, 0.33F, 0.33F);
            }
            if (!tileEntityStationLamp.logo.equals("null")) {
                ResourceLocation logo = new ResourceLocation(tileEntityStationLamp.logo);
                RendererHelper.renderWithResource4(modelLogo, logo);
            } else {
                RendererHelper.renderWithResource4(modelLogo, textureLogo);
            }

            if (!tileEntityStationLamp.isEnabled && tileEntityStationLamp.getSender() != null)
                GL11.glColor3f(0.33F, 0.33F, 0.33F);
            else
                GL11.glColor3f(
                        ((backColor & 0xFF0000) >> 16) / 255.0F,
                        ((backColor & 0x00FF00) >> 8) / 255.0F,
                        (backColor & 0x0000FF) / 255.0F
                );
            RendererHelper.renderWithResource4(modelBack, textureBack);
            GL11.glColor3f(1.0F, 1.0F, 1.0F);

            for (int i = 0; i < 4; i++)
                renderFore(tileEntityStationLamp, 90.0F * i);

            GL11.glPopMatrix();
        }

        GL11.glPopMatrix();

        RenderHelper.enableStandardItemLighting();

        GL11.glPopMatrix();
    }

    private void renderFore(StationLamp.TileEntityStationLamp tileEntityStationLamp, float angle) {
        GL11.glPushMatrix();
        GL11.glRotatef(angle, 0.0F, 1.0F, 0.0F);

        boolean control = true;
        if (tileEntityStationLamp.getSender() != null) control = tileEntityStationLamp.isEnabled;
        if (control) {
            int fontColor = tileEntityStationLamp.color, align = StationLamp.ALIGN_CENTER;
            double fontScale = tileEntityStationLamp.scale; String content = tileEntityStationLamp.content;
            GL11.glPushMatrix();
            GL11.glRotatef(180.0F,0.0F, 0.0F, 1.0F);
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, 0.3125F, -0.5F);
            renderString(content, fontScale, align, fontColor);
            GL11.glPopMatrix();
            GL11.glPopMatrix();
        }

        GL11.glPopMatrix();
    }

    private void renderString(String str, double scale, int align, int color) {
        FontRenderer renderer = Minecraft.getMinecraft().fontRenderer;
        GL11.glPushMatrix();
        GL11.glScaled(scale, scale, 1.0);
        GL11.glPushMatrix();
        GL11.glScalef(0.02F, 0.02F, 1.0F);
        GL11.glPushMatrix();
        String[] lines = str.split("\n");
        GL11.glTranslatef(0.0F, -(float) lines.length / 2.0F * renderer.FONT_HEIGHT, 0.0F);
        int x = 0, y = 0;
        for (String s : lines) {
            switch (align) {
                case StationLamp.ALIGN_CENTER:
                    x = -renderer.getStringWidth(s) / 2; break;
                case StationLamp.ALIGN_LEFT:
                    x = 0; break;
                case StationLamp.ALIGN_RIGHT:
                    x = -renderer.getStringWidth(s); break;
            }
            renderer.drawString(s, x, y, color);
            y += renderer.FONT_HEIGHT;
        }
        GL11.glPopMatrix();
        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }

}
