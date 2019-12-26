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

    private static final int SCREEN_WIDTH = 70;
    private static final int SCREEN_LINES = 6;

    private final WavefrontObject modelMain;
    private final WavefrontObject modelKeys;

    private final ResourceLocation textureBase;
    private final ResourceLocation textureScreen;
    private final ResourceLocation texturePrint;
    private final ResourceLocation textureKeys;
    private final ResourceLocation textureOverlay;

    private static final int LED_R = 0;
    private static final int LED_G = 1;
    private static final int LED_N = 2;
    private final ResourceLocation[] textureLED;

    public YakumoPCRenderer() {
        modelMain = new WavefrontObject(
                new ResourceLocation("nyasamarailway", "models/blocks/yakumo_pc_main.obj")
        );
        modelKeys = new WavefrontObject(
                new ResourceLocation("nyasamarailway", "models/blocks/yakumo_pc_keys.obj")
        );

        textureBase = new ResourceLocation("nyasamarailway", "textures/blocks/pc/yakumo_pc_base.png");
        textureScreen = new ResourceLocation("nyasamarailway", "textures/blocks/pc/yakumo_pc_screen.png");
        texturePrint = new ResourceLocation("nyasamarailway", "textures/blocks/pc/yakumo_pc_print.png");
        textureKeys = new ResourceLocation("nyasamarailway", "textures/blocks/pc/yakumo_pc_keys.png");
        textureOverlay = new ResourceLocation("nyasamarailway", "textures/blocks/pc/yakumo_pc_overlay.png");

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
        String content = "DUMMY"; char hitKey = 0;
        if (te instanceof YakumoPC.TileEntityYakumoPC) {
            content = ((YakumoPC.TileEntityYakumoPC) te).buffer;
            ledLeft = ((YakumoPC.TileEntityYakumoPC) te).ledLeft;
            ledRight = ((YakumoPC.TileEntityYakumoPC) te).ledRight;
            hitKey = ((YakumoPC.TileEntityYakumoPC) te).hitKey;
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

            manager.bindTexture(textureKeys);
            modelKeys.renderPart("keys");
            if (hitKey != 0) {
                manager.bindTexture(textureOverlay);
                modelKeys.renderPart("key_" + hitKey);
            }
        }
        GL11.glPopMatrix();
        GL11.glPopMatrix();

        RendererHelper.beginSpecialLighting();

        final float offset = 0.0625F;
        final double offsetX = 0.35, offsetY = 0.425;
        final int color = 0xFFC107;

        GL11.glPushMatrix();
        GL11.glRotatef(angle,0.0F, -1.0F, 0.0F);
        GL11.glPushMatrix();
        GL11.glTranslated(offsetX, offsetY, 0);
        GL11.glPushMatrix();
        GL11.glRotatef(180.0F,0.0F, 0.0F, 1.0F);
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 0.0F, offset - 0.01F);
        renderString(content, color);
        GL11.glPopMatrix();
        GL11.glPopMatrix();
        GL11.glPopMatrix();
        GL11.glPopMatrix();

        RendererHelper.endSpecialLighting();

        GL11.glPopMatrix();
    }

    private void renderString(String str, int color) {
        FontRenderer renderer = Minecraft.getMinecraft().fontRenderer;
        final double scale = 0.5;
        GL11.glPushMatrix();
        GL11.glScaled(scale, scale, 1.0);
        GL11.glPushMatrix();
        GL11.glScalef(0.02F, 0.02F, 1.0F);
        GL11.glPushMatrix();
        String[] lines = str.split("\n");
        int y = 0;
        for (String s : lines) {
            String buf = "", tmp;
            for (int i = 0; i < s.length(); i++) {
                tmp = String.valueOf(s.charAt(i));
                if (renderer.getStringWidth(buf.concat(tmp)) > SCREEN_WIDTH)
                    break;
                buf = buf.concat(tmp);
            }
            renderer.drawString(buf, 0, y, color);
            y += renderer.FONT_HEIGHT;
        }
        GL11.glPopMatrix();
        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }

}
