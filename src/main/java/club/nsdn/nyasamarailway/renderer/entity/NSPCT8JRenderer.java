package club.nsdn.nyasamarailway.renderer.entity;

import club.nsdn.nyasamarailway.api.cart.IMobileBlocking;
import club.nsdn.nyasamarailway.entity.loco.NSPCT8J;
import club.nsdn.nyasamatelecom.api.render.RendererHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.ResourceLocation;
import cn.ac.nya.forgeobj.WavefrontObject;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class NSPCT8JRenderer extends AbsCartRenerer {

    public static IRenderFactory<EntityMinecart> FACTORY = NSPCT8JRenderer::new;

    private final String _name = "nspc_8j";

    private final WavefrontObject modelBase = new WavefrontObject(
            new ResourceLocation("nyasamarailway", "models/carts/" + _name + "_base.obj")
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

    private final WavefrontObject modelScreen = new WavefrontObject( // base, r0-r5
            new ResourceLocation("nyasamarailway", "models/carts/" + _name + "_screen.obj")
    );
    private final ResourceLocation textureScreen = new ResourceLocation(
            "nyasamarailway", "textures/carts/" + _name + "_screen.png"
    );

    private final WavefrontObject modelMeterV = new WavefrontObject(
            new ResourceLocation("nyasamarailway", "models/carts/" + _name + "_meter_v.obj")
    );
    private final ResourceLocation textureMeterV = new ResourceLocation(
            "nyasamarailway", "textures/carts/" + _name + "_meter_v.png"
    );

    private final WavefrontObject modelMeterA = new WavefrontObject(
            new ResourceLocation("nyasamarailway", "models/carts/" + _name + "_meter_a.obj")
    );
    private final ResourceLocation textureMeterA = new ResourceLocation(
            "nyasamarailway", "textures/carts/" + _name + "_meter_a.png"
    );

    private final WavefrontObject modelMeterPointer = new WavefrontObject(
            new ResourceLocation("nyasamarailway", "models/carts/" + _name + "_meter_pointer.obj")
    );
    private final ResourceLocation textureMeterPointer = new ResourceLocation(
            "nyasamarailway", "textures/carts/" + _name + "_meter_pointer.png"
    );

    private static final float ANGLE_HALF = 143;

    private final ResourceLocation textureText[];

    public NSPCT8JRenderer(RenderManager manager) {
        super(manager);

        textureText = new ResourceLocation[128];
        for (int i = 0; i < 128; i++)
            textureText[i] =  new ResourceLocation(
                "nyasamarailway", "textures/fonts/" + "font_" + i + ".png"
            );
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityMinecart cart) {
        return textureBase;
    }

    @Override
    public void render(EntityMinecart minecart, double x, double y, double z, float yaw) {
        RendererHelper.renderWithResource(modelBase, textureBase);
        RendererHelper.renderWithResource(modelPrint, texturePrint);

        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 0.125F, 0.0F);
        doRenderHUD(minecart);
        GL11.glPopMatrix();
    }

    private void doRenderHUD(EntityMinecart cart) {
        if (cart instanceof NSPCT8J) {
            NSPCT8J loco = (NSPCT8J) cart;

            float v = (float) loco.getEngineVel();
            float lim = (float) loco.getMaxVelocity();
            float a = v - (float) loco.getEnginePrevVel();

            int d = loco.getEngineDir(), p = loco.getEnginePower(), r = loco.getEngineBrake();
            boolean high = loco.getHighSpeedMode();

            boolean MBlkState = loco.getBlockingState();

            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            RendererHelper.renderPartWithResource(modelScreen, "base", textureScreen);
            String dir = d == 1 ? "F" : (d == 0 ? "N" : "R");
            String pwr = String.format("%2d", p);
            String brk = String.format("%2d", 10 - r);
            String sv = String.format("%1.2f", v);
            String sl = String.format("%1.2f", lim);

            // HUD1406
            doRenderText(0, "-= NSR--NTP =-");
            doRenderText(1, "dir:  " + dir + "  " + (MBlkState ? "B" : ""));
            doRenderText(2, "pwr: " + pwr + (r <= 1 ? " STOP" : (high ? " HIGH" : "  RUN")));
            doRenderText(3, "brk: " + brk + (r == 1 ? " EME" : ""));
            doRenderText(4, "vel:" + sv + "m/t");
            doRenderText(5, "lim:" + sl + "m/t");

            GL11.glPushMatrix();
            GL11.glRotated(180, 0, 1, 0);
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            RendererHelper.renderPartWithResource(modelScreen, "base", textureScreen);
            // HUD1406
            doRenderText(0, "-= NTP--EXT =-");
            doRenderText(1, "vel:" + String.format("%1.2f", v * 72) + "km/h");
            doRenderText(2, "acc:" + String.format("%1.2f", a * 400) + "m/s2");
            doRenderText(3, "cur:" + (loco.getCurved() ? "TRUE" : "FALSE"));
            doRenderText(4, "dim:" + String.format("%d", loco.dimension));
            doRenderText(5, "yaw:" + String.format("%1.2f", loco.rotationYaw));
            GL11.glPopMatrix();

            doRenderMeter(loco);
            GL11.glPushMatrix();
            GL11.glRotated(180, 0, 1, 0);
            doRenderMeter(loco);
            GL11.glPopMatrix();
        }
    }

    private void doRenderMeter(NSPCT8J loco) {
        float v = (float) loco.getEngineVel();
        float a = v - (float) loco.getEnginePrevVel();

        float angle;

        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        RendererHelper.renderWithResource(modelMeterV, textureMeterV);
        angle = v / 6.0F * ANGLE_HALF * 2 - ANGLE_HALF;
        if (angle > ANGLE_HALF) angle = ANGLE_HALF;
        GL11.glPushMatrix();
        GL11.glTranslatef(0.625F, 0.9375F, -0.625F);
        GL11.glTranslatef(-0.00625F, 0.0F, 0.00625F);
        GL11.glPushMatrix();
        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
        GL11.glPushMatrix();
        GL11.glRotatef(angle, 1.0F, 0.0F, 0.0F);
        RendererHelper.renderWithResource(modelMeterPointer, textureMeterPointer);
        GL11.glPopMatrix();
        GL11.glPopMatrix();
        GL11.glPopMatrix();

        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        RendererHelper.renderWithResource(modelMeterA, textureMeterA);
        angle = a / 0.03F * ANGLE_HALF;
        if (Math.abs(angle) > ANGLE_HALF) angle = Math.signum(angle) * ANGLE_HALF;
        GL11.glPushMatrix();
        GL11.glTranslatef(0.625F, 0.9375F, 0.625F);
        GL11.glTranslatef(-0.00625F, 0.0F, -0.00625F);
        GL11.glPushMatrix();
        GL11.glRotatef(-45.0F, 0.0F, 1.0F, 0.0F);
        GL11.glPushMatrix();
        GL11.glRotatef(angle, 1.0F, 0.0F, 0.0F);
        RendererHelper.renderWithResource(modelMeterPointer, textureMeterPointer);
        GL11.glPopMatrix();
        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }

    private void doRenderText(int r, String text) {
        if (r < 0) r = 0;
        if (r > 5) r = 5;

        GL11.glColor3f(1.0F, 0.435F, 0.0F); // 0xff6f00
        for (int c = 0; c < (text.length() > 14 ? 14 : text.length()); c++) {
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, 0.0F, 0.0625F * c);
            RendererHelper.renderPartWithResource(modelScreen, "r" + r, textureText[text.charAt(c)]);
            GL11.glPopMatrix();
        }
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
    }

}
