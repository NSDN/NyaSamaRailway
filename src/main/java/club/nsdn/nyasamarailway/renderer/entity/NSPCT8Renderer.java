package club.nsdn.nyasamarailway.renderer.entity;

import club.nsdn.nyasamarailway.entity.cart.NSPCT8;
import club.nsdn.nyasamatelecom.api.render.RendererHelper;
import club.nsdn.nyasamatelecom.api.tool.ToolBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import cn.ac.nya.forgeobj.WavefrontObject;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11;

/**
 * Created by drzzm32 on 2017.10.1.
 */
public class NSPCT8Renderer extends AbsCartRenerer {

    public static IRenderFactory<EntityMinecart> FACTORY = NSPCT8Renderer::new;

    private final String _name = "nspc_8";

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

    private final String _screen = "nspc_8j";

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

    public NSPCT8Renderer(RenderManager manager) {
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

        if (!minecart.getPassengers().isEmpty()) {
            if (minecart.getPassengers().get(0) instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) minecart.getPassengers().get(0);
                if (!player.getHeldItemMainhand().isEmpty()) {
                    if (player.getHeldItemMainhand().getItem() instanceof ToolBase) {
                        RendererHelper.beginSpecialLightingNoDepth();

                        doRenderHUD(minecart);
                        GL11.glPushMatrix();
                        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                        doRenderHUD(minecart);
                        GL11.glPopMatrix();

                        RendererHelper.endSpecialLightingNoDepth();
                    }
                }
            }
        }
    }

    private void doRenderHUD(EntityMinecart cart) {
        if (cart instanceof NSPCT8) {
            NSPCT8 loco = (NSPCT8) cart;

            float v = (float) loco.getMotorVel();
            float lim = (float) loco.getMaxVelocity();
            float a = Math.signum(v) / 3;

            float angle;
            int d = loco.getMotorDir(), p = loco.getMotorPower(), r = loco.getMotorBrake();
            boolean isOff = !loco.getMotorState();
            boolean high = loco.getHighSpeedMode();

            RendererHelper.renderPartWithResource(modelScreen, "base", textureScreen);
            String dir = d == 1 ? "F" : (d == 0 ? "N" : "R");
            String pwr = String.format("%2d", p);
            String brk = String.format("%2d", 10 - r);
            String sv = String.format("%1.2f", v);
            String sl = String.format("%1.2f", lim);

            // HUD1406
            doRenderText(0, "-= NSR--NTP =-");
            doRenderText(1, "dir:  " + dir);
            doRenderText(2, "pwr: " + pwr + (isOff ? " IDLE" : (high ? " HIGH" : "  RUN")));
            doRenderText(3, "brk: " + brk + (r == 1 ? " EME" : ""));
            doRenderText(4, "vel:" + sv + "m/t");
            doRenderText(5, "lim:" + sl + "m/t");

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
