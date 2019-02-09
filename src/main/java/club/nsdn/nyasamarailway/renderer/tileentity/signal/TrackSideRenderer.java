package club.nsdn.nyasamarailway.renderer.tileentity.signal;

import club.nsdn.nyasamarailway.api.signal.ITrackSide;
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
 * Created by drzzm32 on 2019.1.5.
 */
public class TrackSideRenderer extends AbsTileEntitySpecialRenderer {

    private final WavefrontObject modelBase = new WavefrontObject(
            new ResourceLocation("nyasamarailway", "models/blocks/track_side_base.obj")
    );
    private final ResourceLocation textureBase = new ResourceLocation(
            "nyasamarailway", "textures/blocks/track_side_base.png"
    );

    private final WavefrontObject modelSign = new WavefrontObject(
            new ResourceLocation("nyasamarailway", "models/blocks/track_side_sign.obj")
    );
    private final ResourceLocation textureSign;

    private final WavefrontObject ledMdl[] = {
            new WavefrontObject(new ResourceLocation("nyasamarailway", "models/blocks/track_side_led_1.obj")),
            new WavefrontObject(new ResourceLocation("nyasamarailway", "models/blocks/track_side_led_2.obj")),
            new WavefrontObject(new ResourceLocation("nyasamarailway", "models/blocks/track_side_led_3.obj")),
            new WavefrontObject(new ResourceLocation("nyasamarailway", "models/blocks/track_side_led_f.obj")),
            new WavefrontObject(new ResourceLocation("nyasamarailway", "models/blocks/track_side_led_b.obj"))
    };
    private final ResourceLocation ledTex[] = {
            new ResourceLocation("nyasamarailway", "textures/blocks/signal_box_r.png"),
            new ResourceLocation("nyasamarailway", "textures/blocks/signal_box_y.png"),
            new ResourceLocation("nyasamarailway", "textures/blocks/signal_box_g.png"),
            new ResourceLocation("nyasamarailway", "textures/blocks/signal_box_w.png"),
            new ResourceLocation("nyasamarailway", "textures/blocks/signal_box_none.png")
    };
    private static final int SIGN_R = 0;
    private static final int SIGN_Y = 1;
    private static final int SIGN_G = 2;
    private static final int SIGN_W = 3;
    private static final int SIGN_NONE = 4;

    private static final int LED_SGN = 0;
    private static final int LED_TXD = 1;
    private static final int LED_RXD = 2;
    private static final int LED_FRT = 3;
    private static final int LED_BCK = 4;

    public TrackSideRenderer(String texture) {
        textureSign = new ResourceLocation("nyasamarailway", "textures/blocks/" + texture + ".png");
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

        float angle = (meta & 0x3) * 90.0F;
        GL11.glRotatef(angle, 0.0F, -1.0F, 0.0F);

        if (te instanceof ITrackSide) {
            ITrackSide trackSide = (ITrackSide) te;

            GL11.glPushMatrix();

            if ((meta & 0x4) != 0) GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);

            RendererHelper.renderWithResource(modelBase, textureBase);
            RendererHelper.renderWithResource(modelSign, textureSign);

            RendererHelper.renderWithResource(ledMdl[LED_SGN], ledTex[trackSide.getSGNState() ? SIGN_R : SIGN_NONE]);
            RendererHelper.renderWithResource(ledMdl[LED_TXD], ledTex[trackSide.getTXDState() ? SIGN_Y : SIGN_NONE]);
            RendererHelper.renderWithResource(ledMdl[LED_RXD], ledTex[trackSide.getRXDState() ? SIGN_G : SIGN_NONE]);

            RendererHelper.renderWithResource(ledMdl[LED_FRT], ledTex[trackSide.hasInvert() ? (trackSide.isInvert() ? SIGN_NONE : SIGN_W) : SIGN_NONE]);
            RendererHelper.renderWithResource(ledMdl[LED_BCK], ledTex[trackSide.hasInvert() ? (trackSide.isInvert() ? SIGN_W : SIGN_NONE) : SIGN_NONE]);

            GL11.glPopMatrix();
        }

        GL11.glPopMatrix();

        RenderHelper.enableStandardItemLighting();

        GL11.glPopMatrix();
    }

}
