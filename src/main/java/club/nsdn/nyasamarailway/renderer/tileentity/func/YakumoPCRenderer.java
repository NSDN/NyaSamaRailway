package club.nsdn.nyasamarailway.renderer.tileentity.func;

import club.nsdn.nyasamarailway.tileblock.func.YakumoPC;
import club.nsdn.nyasamatelecom.api.render.AbsTileEntitySpecialRenderer;
import club.nsdn.nyasamatelecom.api.render.RendererHelper;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import cn.ac.nya.forgeobj.WavefrontObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.12.25
 */
public class YakumoPCRenderer extends AbsTileEntitySpecialRenderer {

    public static final int ALIGN_CENTER = 0, ALIGN_LEFT = 1, ALIGN_RIGHT = 2;

    private final WavefrontObject modelMain;

    private final ResourceLocation textureBase;
    private final ResourceLocation textureScreen;
    private final ResourceLocation texturePrint;

    private static final int LED_R = 0;
    private static final int LED_G = 1;
    private static final int LED_N = 2;
    private final ResourceLocation[] textureLED;

    public YakumoPCRenderer() {
        modelMain = new WavefrontObject(
                new ResourceLocation("nyasamarailway", "models/blocks/yakumo_pc_main.obj")
        );

        textureBase = new ResourceLocation("nyasamarailway", "textures/blocks/pc/yakumo_pc_base.png");
        textureScreen = new ResourceLocation("nyasamarailway", "textures/blocks/pc/yakumo_pc_screen.png");
        texturePrint = new ResourceLocation("nyasamarailway", "textures/blocks/pc/yakumo_pc_print.png");

        textureLED = new ResourceLocation[] {
                new ResourceLocation("nyasamarailway", "textures/blocks/pc/yakumo_pc_led_r.png"),
                new ResourceLocation("nyasamarailway", "textures/blocks/pc/yakumo_pc_led_g.png"),
                new ResourceLocation("nyasamarailway", "textures/blocks/pc/yakumo_pc_led_none.png")
        };
    }

    @Override
    public void render(@Nonnull TileEntityBase te, double x, double y, double z, float partialTicks, int destroyStage, float partial) {
        int meta = te.META;

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y  + 0.5F, (float) z + 0.5F);

        //RendererHelper.beginSpecialLighting();

        TextureManager manager = Minecraft.getMinecraft().renderEngine;

        int angle = (meta & 0x3) * 90;

        boolean ledLeft = false, ledRight = false;
        String content = "DUMMY";
        if (te instanceof YakumoPC.TileEntityYakumoPC) {
            content = ((YakumoPC.TileEntityYakumoPC) te).buffer;
            ledLeft = ((YakumoPC.TileEntityYakumoPC) te).ledLeft;
            ledRight = ((YakumoPC.TileEntityYakumoPC) te).ledRight;
        }

        GL11.glPushMatrix();
        GL11.glScalef(0.0625F, 0.0625F, 0.0625F);
        GL11.glPushMatrix();
        GL11.glRotatef(angle, 0.0F, -1.0F, 0.0F);
        {
            manager.bindTexture(textureBase);
            modelMain.renderAllExcept("screen", "led_l", "led_r", "logo", "print");
            manager.bindTexture(textureScreen);
            modelMain.renderPart("screen");
            manager.bindTexture(texturePrint);
            modelMain.renderOnly("logo", "print");

            manager.bindTexture(textureLED[ledLeft ? LED_R : LED_N]);
            modelMain.renderPart("led_l");
            manager.bindTexture(textureLED[ledRight ? LED_G : LED_N]);
            modelMain.renderPart("led_r");
        }
        GL11.glPopMatrix();
        GL11.glPopMatrix();

        RendererHelper.beginSpecialLighting();

        final float offset = 0.0625F;
        final double offsetX = 0.35, offsetY = 0.275;
        final int color = 0xFFC107;

        GL11.glPushMatrix();
        GL11.glRotatef(angle,0.0F, -1.0F, 0.0F);
        GL11.glPushMatrix();
        GL11.glTranslated(offsetX, offsetY, 0);
        GL11.glPushMatrix();
        GL11.glRotatef(180.0F,0.0F, 0.0F, 1.0F);
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 0.0F, offset - 0.01F);
        renderString(content, 0.5, ALIGN_LEFT, color);
        GL11.glPopMatrix();
        GL11.glPopMatrix();
        GL11.glPopMatrix();
        GL11.glPopMatrix();

        RendererHelper.endSpecialLighting();

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
                case ALIGN_CENTER:
                    x = -renderer.getStringWidth(s) / 2; break;
                case ALIGN_LEFT:
                    x = 0; break;
                case ALIGN_RIGHT:
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
