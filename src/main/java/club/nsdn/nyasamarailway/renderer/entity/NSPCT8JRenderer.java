package club.nsdn.nyasamarailway.renderer.entity;

import club.nsdn.nyasamarailway.entity.loco.NSPCT8J;
import club.nsdn.nyasamarailway.renderer.RendererHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderMinecart;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.model.obj.WavefrontObject;
import org.lwjgl.opengl.GL11;

/**
 * Created by drzzm32 on 2017.10.6.
 */
public class NSPCT8JRenderer extends RenderMinecart {

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

    public NSPCT8JRenderer() {
        super();

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
    public void doRender(EntityMinecart minecart, double x, double y, double z, float Yaw, float p_doRender_9_) {
        GL11.glPushMatrix();
        this.bindEntityTexture(minecart);
        long var10 = (long)minecart.getEntityId() * 493286711L;
        var10 = var10 * var10 * 4392167121L + var10 * 98761L;
        float var12 = (((float)(var10 >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        float var13 = (((float)(var10 >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        float var14 = (((float)(var10 >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        GL11.glTranslatef(var12, var13, var14);
        double var15 = minecart.lastTickPosX + (minecart.posX - minecart.lastTickPosX) * (double)p_doRender_9_;
        double var17 = minecart.lastTickPosY + (minecart.posY - minecart.lastTickPosY) * (double)p_doRender_9_;
        double var19 = minecart.lastTickPosZ + (minecart.posZ - minecart.lastTickPosZ) * (double)p_doRender_9_;
        double var21 = 0.30000001192092896D;
        Vec3 var23 = minecart.func_70489_a(var15, var17, var19);
        float var24 = minecart.prevRotationPitch + (minecart.rotationPitch - minecart.prevRotationPitch) * p_doRender_9_;
        if(var23 != null) {
            Vec3 var25 = minecart.func_70495_a(var15, var17, var19, var21);
            Vec3 var26 = minecart.func_70495_a(var15, var17, var19, -var21);
            if(var25 == null) {
                var25 = var23;
            }

            if(var26 == null) {
                var26 = var23;
            }

            x += var23.xCoord - var15;
            y += (var25.yCoord + var26.yCoord) / 2.0D - var17;
            z += var23.zCoord - var19;
            Vec3 var27 = var26.addVector(-var25.xCoord, -var25.yCoord, -var25.zCoord);
            if(var27.lengthVector() != 0.0D) {
                var27 = var27.normalize();
                Yaw = (float)(Math.atan2(var27.zCoord, var27.xCoord) * 180.0D / 3.141592653589793D);
                var24 = (float)(Math.atan(var27.yCoord) * 73.0D);
            }
        }

        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glRotatef(180.0F - Yaw, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-var24, 0.0F, 0.0F, 1.0F);
        float var31 = (float)minecart.getRollingAmplitude() - p_doRender_9_;
        float var32 = minecart.getDamage() - p_doRender_9_;
        if(var32 < 0.0F) {
            var32 = 0.0F;
        }

        if(var31 > 0.0F) {
            GL11.glRotatef(MathHelper.sin(var31) * var31 * var32 / 10.0F * (float)minecart.getRollingDirection(), 1.0F, 0.0F, 0.0F);
        }

        int var33 = minecart.getDisplayTileOffset();
        Block var28 = minecart.func_145820_n();
        int var29 = minecart.getDisplayTileData();
        if(var28.getRenderType() != -1) {
            GL11.glPushMatrix();
            this.bindTexture(TextureMap.locationBlocksTexture);
            float var30 = 0.75F;
            GL11.glScalef(var30, var30, var30);
            GL11.glTranslatef(0.0F, (float)var33 / 16.0F, 0.0F);
            this.func_147910_a(minecart, p_doRender_9_, var28, var29);
            GL11.glPopMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.bindEntityTexture(minecart);
        }

        RendererHelper.renderWithResource(modelBase, textureBase);
        RendererHelper.renderWithResource(modelPrint, texturePrint);

        RenderHelper.disableStandardItemLighting();
        GL11.glDepthMask(false);

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_CULL_FACE);

        if (Minecraft.isAmbientOcclusionEnabled()) {
            GL11.glShadeModel(GL11.GL_SMOOTH);
        } else {
            GL11.glShadeModel(GL11.GL_FLAT);
        }

        Tessellator.instance.setColorOpaque_F(1.0F, 1.0F, 1.0F);

        doRenderHUD(minecart);
        GL11.glPushMatrix();
        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
        doRenderHUD(minecart);
        GL11.glPopMatrix();

        GL11.glDepthMask(true);
        RenderHelper.enableStandardItemLighting();

        GL11.glPopMatrix();
    }

    private void doRenderHUD(EntityMinecart cart) {
        if (cart instanceof NSPCT8J) {
            NSPCT8J loco = (NSPCT8J) cart;

            float v = (float) loco.getEngineVel();
            float a = v - (float) loco.getEnginePrevVel();

            float angle;
            int d = loco.getEngineDir(), p = loco.getEnginePower(), r = loco.getEngineBrake();
            boolean high = loco.getHighSpeedMode();

            RendererHelper.renderPartWithResource(modelScreen, "base", textureScreen);
            String dir = d == 1 ? "F" : (d == 0 ? "N" : "R");
            String pwr = String.format("%2d", p);
            String brk = String.format("%2d", 10 - r);
            String sv = String.format("%1.2f", v);
            String sa = String.format("%1.2f", a * 100);

            // HUD1406
            doRenderText(0, "-= NSR--NTP =-");
            doRenderText(1, "dir:  " + dir);
            doRenderText(2, "pwr: " + pwr + (r <= 1 ? " STOP" : (high ? " HIGH" : "  RUN")));
            doRenderText(3, "brk: " + brk + (r == 1 ? " EME" : ""));
            doRenderText(4, "vel:" + sv + "m/t");
            doRenderText(5, "acc:" + sa + "cm/t2");

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
