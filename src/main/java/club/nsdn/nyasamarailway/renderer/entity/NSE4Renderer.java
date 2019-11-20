package club.nsdn.nyasamarailway.renderer.entity;

import club.nsdn.nyasamarailway.api.cart.AbsTrainBase;
import club.nsdn.nyasamarailway.api.cart.ILimitVelCart;
import club.nsdn.nyasamarailway.api.cart.ILocomotive;
import club.nsdn.nyasamarailway.entity.loco.NSBT4M;
import club.nsdn.nyasamarailway.entity.train.NSRM1;
import club.nsdn.nyasamatelecom.api.render.RendererHelper;
import cn.ac.nya.forgeobj.WavefrontObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11;

/**
 * Created by drzzm32 on 2019.3.3
 */
public class NSE4Renderer extends AbsTrainRenerer {

    public static IRenderFactory<AbsTrainBase> FACTORY = NSE4Renderer::new;

    private final String _name = "nse_4";

    private final WavefrontObject modelBase = new WavefrontObject(
            new ResourceLocation("nyasamarailway", "models/carts/" + _name + "_main.obj")
    );

    private final ResourceLocation textureBase = new ResourceLocation(
            "nyasamarailway", "textures/carts/" + _name + "_base.png"
    );

    private final WavefrontObject modelPrint = new WavefrontObject(
            new ResourceLocation("nyasamarailway", "models/carts/" + _name + "_print.obj")
    );

    private final ResourceLocation texturePrint = new ResourceLocation(
            "nyasamarailway", "textures/carts/" + _name + "_print.png"
    );

    private final String _screen = "nsc_x";

    private final WavefrontObject modelScreen = new WavefrontObject( // base, r0-r5
            new ResourceLocation("nyasamarailway", "models/carts/" + _screen + "_screen.obj")
    );
    private final ResourceLocation textureScreen = new ResourceLocation(
            "nyasamarailway", "textures/carts/" + _screen + "_screen.png"
    );

    private final WavefrontObject modelMeterV = new WavefrontObject(
            new ResourceLocation("nyasamarailway", "models/carts/" + _screen + "_meter_v.obj")
    );
    private final ResourceLocation textureMeterV = new ResourceLocation(
            "nyasamarailway", "textures/carts/" + _screen + "_meter_v.png"
    );

    private final WavefrontObject modelMeterA = new WavefrontObject(
            new ResourceLocation("nyasamarailway", "models/carts/" + _screen + "_meter_a.obj")
    );
    private final ResourceLocation textureMeterA = new ResourceLocation(
            "nyasamarailway", "textures/carts/" + _screen + "_meter_a.png"
    );

    private final WavefrontObject modelMeterPointer = new WavefrontObject(
            new ResourceLocation("nyasamarailway", "models/carts/" + _screen + "_meter_pointer.obj")
    );
    private final ResourceLocation textureMeterPointer = new ResourceLocation(
            "nyasamarailway", "textures/carts/" + _screen + "_meter_pointer.png"
    );

    private static final float ANGLE_HALF = 143;

    private final ResourceLocation textureText[];

    public NSE4Renderer(RenderManager manager) {
        super(manager);

        textureText = new ResourceLocation[128];
        for (int i = 0; i < 128; i++)
            textureText[i] =  new ResourceLocation(
                    "nyasamarailway", "textures/fonts/" + "font_" + i + ".png"
            );
    }

    @Override
    protected ResourceLocation getEntityTexture(AbsTrainBase train) {
        return textureBase;
    }

    @Override
    public void render(AbsTrainBase train, double x, double y, double z, float yaw) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.0625, 0.0625, 0.0625);

        Minecraft.getMinecraft().getTextureManager().bindTexture(textureBase);
        modelBase.renderAllExcept( "w0", "w1", "w2", "w3");

        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        modelBase.renderOnly("w0", "w1", "w2", "w3");

        GlStateManager.popMatrix();

        RendererHelper.renderWithResource(modelPrint, texturePrint);

        if (train.getBogieA() instanceof NSBT4M) {
            NSBT4M motor = (NSBT4M) train.getBogieA();
            renderHUD(motor);
        }

    }

    private void renderHUD(NSBT4M motor) {
        GL11.glPushMatrix();
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        GL11.glTranslatef(0.0F, 0.5F, 0.0F);

        GL11.glPushMatrix();
        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
        doRenderHUD(motor);
        GL11.glPopMatrix();

        GL11.glPopMatrix();
    }

    private void doRenderHUD(NSBT4M motor) {
        if (motor != null) {
            float v = (float) motor.getEngineVel();
            float lim = (float) motor.getMaxVelocity();
            float a = v - (float) motor.getEnginePrevVel();

            float angle;
            int d = motor.getEngineDir(), p = motor.getEnginePower(), r = motor.getEngineBrake();

            boolean MBlkState = motor.getBlockingState();

            String dir = d == 1 ? "F" : (d == 0 ? "N" : "R");
            String pwr = String.format("%2d", p);
            String brk = String.format("%2d", 10 - r);
            String sv = String.format("%1.2f", v);
            String sl = String.format("%1.2f", lim);

            GL11.glPushMatrix();
            GL11.glTranslated(-0.25, 0, 0);
            GL11.glRotated(-15, 0, 0, 1);
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            RendererHelper.renderPartWithResource(modelScreen, "base", textureScreen);
            // HUD1406
            doRenderText(0, "-= NSR--NTP =-");
            doRenderText(1, "dir:  " + dir + "  " + (MBlkState ? "B" : ""));
            doRenderText(2, "pwr: " + pwr + (r <= 1 ? " STOP" : "  RUN"));
            doRenderText(3, "brk: " + brk + (r == 1 ? " EME" : ""));
            doRenderText(4, "vel:" + sv + "m/t");
            doRenderText(5, "lim:" + sl + "m/t");
            GL11.glPopMatrix();

            GL11.glPushMatrix();
            GL11.glRotated(-180, 0, 1, 0);
            GL11.glTranslated(-0.25, 0, 0);
            GL11.glRotated(-15, 0, 0, 1);
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            RendererHelper.renderPartWithResource(modelScreen, "base", textureScreen);
            // HUD1406
            doRenderText(0, "-= NTP--EXT =-");
            doRenderText(1, "vel:" + String.format("%1.2f", v * 72) + "km/h");
            doRenderText(2, "acc:" + String.format("%1.2f", a * 400) + "m/s2");
            doRenderText(3, "name: " + Long.toHexString(motor.getEntityId()) + "h");
            doRenderText(4, "dim:" + String.format("%x", motor.dimension));
            doRenderText(5, "yaw:" + String.format("%1.2f", motor.rotationYaw));
            GL11.glPopMatrix();

            GL11.glPushMatrix();
            GL11.glRotated(30, 0, 0, 1);
            GL11.glTranslated(0.5, 0, 0);
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            RendererHelper.renderWithResource(modelMeterV, textureMeterV);
            angle = v / 9.0F * ANGLE_HALF * 2 - ANGLE_HALF;
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
            GL11.glPopMatrix();
        }
    }

    private void doRenderText(int r, String text) {
        if (r < 0) r = 0;
        if (r > 5) r = 5;

        GL11.glColor3ub((byte) 0x00, (byte) 0xBC, (byte) 0xD4); // 0x00bcd4
        for (int c = 0; c < (text.length() > 14 ? 14 : text.length()); c++) {
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, 0.0F, 0.0625F * c);
            RendererHelper.renderPartWithResource(modelScreen, "r" + r, textureText[text.charAt(c)]);
            GL11.glPopMatrix();
        }
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
    }

}
