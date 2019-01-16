package club.nsdn.nyasamatelecom.renderer;

import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import club.nsdn.nyasamatelecom.tileblock.core.BlockRSLatch;
import club.nsdn.nyasamatelecom.tileblock.core.BlockTimer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.*;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2018.12.14.
 */
public class AdvancedBoxRenderer extends TileEntitySpecialRenderer<TileEntityBase> {

    private final OBJModel modelBase = OBJLoader.INSTANCE.loadModel(
            new ResourceLocation("nyasamatelecom", "models/block/signal_box_base.obj")
    );

    private final WavefrontObject modelLcd = new WavefrontObject(
            new ResourceLocation("nyasamatelecom", "models/block/signal_box_lcd.obj")
    );

    private final ResourceLocation textureText[];

    private final WavefrontObject modelBtn;
    private final WavefrontObject modelBtnLight;
    private final ResourceLocation textureBase;

    private final WavefrontObject models[] = {
            new WavefrontObject(new ResourceLocation("nyasamatelecom", "models/block/signal_box_sign1.obj")),
            new WavefrontObject(new ResourceLocation("nyasamatelecom", "models/block/signal_box_sign2.obj")),
            new WavefrontObject(new ResourceLocation("nyasamatelecom", "models/block/signal_box_sign3.obj"))
    };
    private final ResourceLocation textures[] = {
            new ResourceLocation("nyasamatelecom", "textures/block/signal_box_r.png"),
            new ResourceLocation("nyasamatelecom", "textures/block/signal_box_y.png"),
            new ResourceLocation("nyasamatelecom", "textures/block/signal_box_g.png"),
            new ResourceLocation("nyasamatelecom", "textures/block/signal_box_none.png"),
            new ResourceLocation("nyasamatelecom", "textures/block/signal_box_w.png")
    };
    private static final int SIGN_R = 0;
    private static final int SIGN_Y = 1;
    private static final int SIGN_G = 2;
    private static final int SIGN_NONE = 3;
    private static final int SIGN_W = 4;

    public AdvancedBoxRenderer(boolean hasButton, String texture) {
        textureText = new ResourceLocation[128];
        for (int i = 0; i < 128; i++)
            textureText[i] =  new ResourceLocation(
                    "nyasamatelecom", "textures/fonts/" + "font_" + i + ".png"
            );

        textureBase = new ResourceLocation("nyasamatelecom", "textures/block/" + texture + ".png");

        if (hasButton) {
            modelBtn = new WavefrontObject(
                    new ResourceLocation("nyasamatelecom", "models/block/signal_box_btn.obj")
            );
            modelBtnLight = new WavefrontObject(
                    new ResourceLocation("nyasamatelecom", "models/block/signal_box_btn_light.obj")
            );
        } else {
            modelBtn = null;
            modelBtnLight = null;
        }
    }

    @Override
    public void render(@Nonnull TileEntityBase te, double x, double y, double z, float partialTicks, int destroyStage, float partial) {
        int meta = te.META;

        boolean stateA = false;
        boolean stateB = false;
        boolean stateC = false;
        boolean isEnabled = false;
        boolean inverted = false;

        if (te instanceof BlockRSLatch.TileEntityRSLatch) {
            BlockRSLatch.TileEntityRSLatch latch = (BlockRSLatch.TileEntityRSLatch) te;
            stateA = latch.isEnabled;
            stateB = latch.state == BlockRSLatch.TileEntityRSLatch.STATE_POS;
            stateC = latch.state == BlockRSLatch.TileEntityRSLatch.STATE_NEG;
            isEnabled = latch.isEnabled;
            inverted = latch.state == BlockRSLatch.TileEntityRSLatch.STATE_ZERO;
        } else if (te instanceof BlockTimer.TileEntityTimer) {
            BlockTimer.TileEntityTimer timer = (BlockTimer.TileEntityTimer) te;
            stateA = timer.autoReload;
            stateB = timer.state == BlockTimer.TileEntityTimer.STATE_POS;
            stateC = timer.state == BlockTimer.TileEntityTimer.STATE_NEG;
            isEnabled = timer.isEnabled;
            inverted = timer.state == BlockTimer.TileEntityTimer.STATE_ZERO;
        }

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

        Tessellator.getInstance().setColorOpaque_F(1.0F, 1.0F, 1.0F);

        GL11.glPushMatrix();

        switch (meta & 0x7) {
            case 0:
                GL11.glRotatef(0, 0.0F, -1.0F, 0.0F);
                break;
            case 1:
                GL11.glRotatef(90, 0.0F, -1.0F, 0.0F);
                break;
            case 2:
                GL11.glRotatef(180, 0.0F, -1.0F, 0.0F);
                break;
            case 3:
                GL11.glRotatef(270, 0.0F, -1.0F, 0.0F);
                break;

            case 4:
                GL11.glTranslatef(0.0F, 1.0F, 0.0F);
                GL11.glRotatef(90.0F, -1.0F, 0.0F, 0.0F);
                GL11.glTranslatef(0.0F, -1.0F, 0.0F);
                GL11.glRotatef(0, 0.0F, -1.0F, 0.0F);
                GL11.glTranslatef(0.0F, 1.0F, -1.0F);
                break;
            case 5:
                GL11.glTranslatef(0.0F, 1.0F, 0.0F);
                GL11.glRotatef(90.0F, 0.0F, 0.0F, -1.0F);
                GL11.glTranslatef(0.0F, -1.0F, 0.0F);
                GL11.glRotatef(90.0F, 0.0F, -1.0F, 0.0F);
                GL11.glTranslatef(0.0F, 1.0F, -1.0F);
                break;
            case 6:
                GL11.glTranslatef(0.0F, 1.0F, 0.0F);
                GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                GL11.glTranslatef(0.0F, -1.0F, 0.0F);
                GL11.glRotatef(180.0F, 0.0F, -1.0F, 0.0F);
                GL11.glTranslatef(0.0F, 1.0F, -1.0F);
                break;
            case 7:
                GL11.glTranslatef(0.0F, 1.0F, 0.0F);
                GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                GL11.glTranslatef(0.0F, -1.0F, 0.0F);
                GL11.glRotatef(270.0F, 0.0F, -1.0F, 0.0F);
                GL11.glTranslatef(0.0F, 1.0F, -1.0F);
                break;
        }

        RendererHelper.renderWithResourceAndRotation(modelBase, 0, textureBase);
        if (modelBtn != null) {
            RendererHelper.renderWithResourceAndRotation(modelBtn, 0, textureBase);
            RendererHelper.renderWithResourceAndRotation(modelBtnLight, 0, textures[isEnabled ? SIGN_W : SIGN_NONE]);
        }
        RendererHelper.renderWithResourceAndRotation(models[SIGN_G], 0, textures[stateC ? SIGN_G : SIGN_NONE]);
        RendererHelper.renderWithResourceAndRotation(models[SIGN_Y], 0, textures[stateB ? SIGN_Y : SIGN_NONE]);
        RendererHelper.renderWithResourceAndRotation(
            models[SIGN_R], 0,
            textures[
                stateA ? (
                    (inverted && modelBtn == null) ? SIGN_W : SIGN_R
                ) : SIGN_NONE
            ]
        );

        if (te instanceof BlockTimer.TileEntityTimer) {
            BlockTimer.TileEntityTimer timer = (BlockTimer.TileEntityTimer) te;
            doRenderText(0, 0, 0xFF1744, 'S');
            doRenderText(0, 1, 0xCDCDCD, 'o');
            doRenderText(0, 2, 0x2979FF, 'C');

            doRenderText(1, 0xFF1744, String.format("%3d", timer.setTime));
            doRenderText(2, 0x2979FF, String.format("%3d", timer.tmpTime));
        }

        GL11.glPopMatrix();

        RenderHelper.enableStandardItemLighting();

        GL11.glPopMatrix();
    }

    private void doRenderText(int r, int color, String text) {
        if (r < 0) r = 0;
        if (r > 2) r = 2;

        GL11.glColor3f(((color & 0xFF0000) >> 16) / 255.0F, ((color & 0x00FF00) >> 8) / 255.0F, (color & 0x0000FF) / 255.0F);
        for (int c = 0; c < (text.length() > 3 ? 3 : text.length()); c++) {
            RendererHelper.renderPartWithResource(modelLcd, "f" + String.valueOf(r + 1) + String.valueOf(c + 1), textureText[text.charAt(c)]);
        }
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
    }

    private void doRenderText(int r, int c, int color, char chr) {
        if (r < 0) r = 0; if (r > 2) r = 2;
        if (c < 0) r = 0; if (c > 2) r = 2;

        GL11.glColor3f(((color & 0xFF0000) >> 16) / 255.0F, ((color & 0x00FF00) >> 8) / 255.0F, (color & 0x0000FF) / 255.0F);
        RendererHelper.renderPartWithResource(modelLcd, "f" + String.valueOf(r + 1) + String.valueOf(c + 1), textureText[chr]);
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
    }

}
