package club.nsdn.nyasamarailway.renderer.entity;

import club.nsdn.nyasamarailway.api.cart.IExtendedInfoCart;
import club.nsdn.nyasamarailway.api.cart.ILimitVelCart;
import club.nsdn.nyasamarailway.api.cart.IMotorCart;
import club.nsdn.nyasamarailway.api.cart.nsc.IMonoRailCart;
import club.nsdn.nyasamarailway.api.cart.nsc.IRotaCart;
import club.nsdn.nyasamarailway.entity.nsc.*;
import club.nsdn.nyasamatelecom.api.render.RendererHelper;
import club.nsdn.nyasamatelecom.api.tool.ToolBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import cn.ac.nya.forgeobj.WavefrontObject;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class NSCxRenderer extends AbsCartRenerer {

    public static IRenderFactory<EntityMinecart> FACTORY_1A = (RenderManager manager) -> new NSCxRenderer(manager, "nsc_1a", false);
    public static IRenderFactory<EntityMinecart> FACTORY_1B = (RenderManager manager) -> new NSCxRenderer(manager, "nsc_1b", false);
    public static IRenderFactory<EntityMinecart> FACTORY_2A = (RenderManager manager) -> new NSCxRenderer(manager, "nsc_2a", true);
    public static IRenderFactory<EntityMinecart> FACTORY_2B = (RenderManager manager) -> new NSCxRenderer(manager, "nsc_2b", true);
    public static IRenderFactory<EntityMinecart> FACTORY_3A = (RenderManager manager) -> new NSCxRenderer(manager, "nsc_3a", false);
    public static IRenderFactory<EntityMinecart> FACTORY_3B = (RenderManager manager) -> new NSCxRenderer(manager, "nsc_3b", false);

    private WavefrontObject modelBase;
    private WavefrontObject modelPrint;

    private ResourceLocation textureBase;
    private ResourceLocation texturePrint;

    private final WavefrontObject modelJet = new WavefrontObject(
            new ResourceLocation("nyasamarailway", "models/carts/" + "nspc_9" + "_jet.obj")
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

    public NSCxRenderer(RenderManager manager, String name, boolean hasPrint) {
        super(manager);

        modelBase = new WavefrontObject(
                new ResourceLocation("nyasamarailway", "models/carts/" + name + "_base.obj")
        );
        textureBase = new ResourceLocation(
                "nyasamarailway", "textures/carts/" + name + "_base.png"
        );

        if (hasPrint) {
            modelPrint = new WavefrontObject(
                    new ResourceLocation("nyasamarailway", "models/carts/" + name + "_print.obj")
            );
            texturePrint = new ResourceLocation(
                    "nyasamarailway", "textures/carts/" + name + "_print.png"
            );
        } else {
            modelPrint = null; texturePrint = null;
        }

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
        ResourceLocation print = texturePrint, jet = null;
        String str = "";

        if (minecart instanceof IExtendedInfoCart) {
            IExtendedInfoCart cart = (IExtendedInfoCart) minecart;
            String info = cart.getExtendedInfo("side");
            if (!info.isEmpty())
                print = new ResourceLocation(
                        "nsr", "pics/" + info + ".png"
                );
            info = cart.getExtendedInfo("jet");
            if (!info.isEmpty())
                jet = new ResourceLocation(
                        "nsr", "pics/" + info + ".png"
                );
            str = cart.getExtendedInfo("str");
        }

        GL11.glPushMatrix();
        if (minecart instanceof IMonoRailCart)
            GL11.glTranslated(0.0, ((IMonoRailCart) minecart).getShiftY() - 0.3125, 0.0);

        RendererHelper.beginSpecialLighting();
        if (minecart instanceof IRotaCart) {
            IRotaCart rotaCart = (IRotaCart) minecart;
            float angle = rotaCart.getAngle() + 1;
            rotaCart.setAngle(angle);
            if (!minecart.getPassengers().isEmpty()) angle = 90.0F;
            RendererHelper.renderPartWithResourceAndRotation(modelBase, "Top", angle, textureBase);
            RendererHelper.renderOtherPartWithResourceAndRotation(modelBase, "Top", 90.0F, textureBase);
        } else
            RendererHelper.renderWithResourceAndRotation(modelBase, 90.0F, textureBase);
        RendererHelper.endSpecialLighting();

        if (modelPrint != null)
            RendererHelper.renderWithResourceAndRotation(modelPrint, 90.0F, print);

        if (!minecart.getPassengers().isEmpty()) {
            if (minecart.getPassengers().get(0) instanceof EntityPlayer) {

                if (jet != null) RendererHelper.renderWithResourceAndRotation(modelJet, -90.0F, jet);

                boolean shouldRenderHUD = false;
                EntityPlayer player = (EntityPlayer) minecart.getPassengers().get(0);
                if (!player.getHeldItemMainhand().isEmpty()) {
                    if (player.getHeldItemMainhand().getItem() instanceof ToolBase) {
                        shouldRenderHUD = true;
                    }
                }

                GL11.glPushMatrix();
                GL11.glRotatef(180.0F - yaw, 0.0F, -1.0F, 0.0F);
                GL11.glRotated(90.0 - player.rotationYaw, 0, 1, 0);
                GL11.glTranslated(0, 0.125, 0);
                if (shouldRenderHUD) renderHUD(minecart);
                else renderStr(str);
                GL11.glPopMatrix();
            }
        }
        GL11.glPopMatrix();
    }

    private void renderStr(String str) {
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, -0.25F, 0.0F);
        if (!str.isEmpty()) {
            RenderHelper.disableStandardItemLighting();
            doRenderStr(Minecraft.getMinecraft().fontRenderer, str);
            GL11.glPushMatrix();
            GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            doRenderStr(Minecraft.getMinecraft().fontRenderer, str);
            GL11.glPopMatrix();
            RenderHelper.enableStandardItemLighting();
        }
        GL11.glPopMatrix();
    }

    private void renderHUD(EntityMinecart minecart) {
        GL11.glPushMatrix();
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        GL11.glTranslatef(0.0F, -0.5F, 0.0F);

        GL11.glPushMatrix();
        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
        doRenderHUD(minecart);
        GL11.glPopMatrix();

        GL11.glPopMatrix();
    }

    private void doRenderStr(FontRenderer renderer, String str) {
        GL11.glPushMatrix();
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 1.0F, 0.625F);
        GL11.glPushMatrix();
        GL11.glScalef(0.0125F, -0.0125F, 1.0F);
        GL11.glPushMatrix();
        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
        int i = 0;
        for (String s : str.split("\n")) {
            renderer.drawString(s, -renderer.getStringWidth(s) / 2, i, 0x00BCD4);
            i += renderer.FONT_HEIGHT;
        }
        GL11.glPopMatrix();
        GL11.glPopMatrix();
        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }

    private void doRenderHUD(EntityMinecart cart) {
        if (cart instanceof IMotorCart) {
            IMotorCart motorCart = (IMotorCart) cart;
            if (cart instanceof ILimitVelCart) {
                ILimitVelCart velCart = (ILimitVelCart) cart;

                float v = (float) motorCart.getMotorVel();
                float lim = (float) velCart.getMaxVelocity();
                float a = Math.signum(v) / 3;

                float angle;
                int d = motorCart.getMotorDir(), p = motorCart.getMotorPower(), r = motorCart.getMotorBrake();
                boolean isOff = !motorCart.getMotorState();

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
                doRenderText(1, "dir:  " + dir);
                doRenderText(2, "pwr: " + pwr + (isOff ? " IDLE" : "  RUN"));
                doRenderText(3, "brk: " + brk + (r == 1 ? " EME" : ""));
                doRenderText(4, "vel:" + sv + "m/t");
                doRenderText(5, "lim:" + sl + "m/t");

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
            }
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
