package club.nsdn.nyasamaoptics.renderer.tileblock;

import club.nsdn.nyasamaoptics.tileblock.screen.TileEntityPlatformPlate;
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
public class PlatformPlateRenderer extends AbsTileEntitySpecialRenderer {

    private final WavefrontObject modelFull = new WavefrontObject(
            new ResourceLocation("nyasamaoptics", "models/blocks/platform_light_full_base.obj")
    );
    private final WavefrontObject modelHalf = new WavefrontObject(
            new ResourceLocation("nyasamaoptics", "models/blocks/platform_light_half_base.obj")
    );

    private final ResourceLocation texture = new ResourceLocation("nyasamaoptics", "textures/blocks/light_shell.png");

    private final boolean isHalf;

    public PlatformPlateRenderer(boolean isHalf) {
        this.isHalf = isHalf;
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
        RendererHelper.renderWithResource(isHalf ? modelHalf : modelFull, texture);

        if (te instanceof TileEntityPlatformPlate) {
            TileEntityPlatformPlate tilePlate = (TileEntityPlatformPlate) te;

            boolean control = true;
            if (tilePlate.getSender() != null) control = tilePlate.isEnabled;
            if (control) {
                final float a = 68.0F, full = 1.8F / 16.0F, half = 0.5F / 16.0F;
                GL11.glPushMatrix();
                if (!isHalf) {
                    GL11.glTranslatef(0.0F, full, -(float) Math.tan(a) * full);
                    final float shift = 0.75F / 16.0F;
                    GL11.glTranslatef(0.0F, (float) Math.tan(a) * shift, shift);
                } else {
                    GL11.glTranslatef(0.0F, half, -(float) Math.tan(a) * half);
                    final float shift = 1.5F / 16.0F;
                    GL11.glTranslatef(0.0F, (float) Math.tan(a) * shift, shift);
                }
                GL11.glPushMatrix();
                GL11.glRotatef(a,1.0F, 0.0F, 0.0F);
                doRenderString(tilePlate);
                GL11.glPopMatrix();
                GL11.glPopMatrix();
            }
        }

        GL11.glPopMatrix();

        GL11.glPopMatrix();

        RenderHelper.enableStandardItemLighting();

        GL11.glPopMatrix();
    }

    private void doRenderString(TileEntityPlatformPlate tilePlate) {
        int fontColor = tilePlate.color, align = tilePlate.align;
        double fontScale = tilePlate.scale; String content = tilePlate.content;
        GL11.glPushMatrix();
        GL11.glRotatef( -90.0F,1.0F, 0.0F, 0.0F);
        GL11.glPushMatrix();
        GL11.glRotatef( 180.0F,0.0F, 1.0F, 0.0F);
        renderString(content, fontScale, align, fontColor);
        GL11.glPopMatrix();
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
                case TileEntityPlatformPlate.ALIGN_CENTER:
                    x = -renderer.getStringWidth(s) / 2; break;
                case TileEntityPlatformPlate.ALIGN_LEFT:
                    x = 0; break;
                case TileEntityPlatformPlate.ALIGN_RIGHT:
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
