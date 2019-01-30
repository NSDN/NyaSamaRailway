package club.nsdn.nyasamatelecom.renderer;

import cn.ac.nya.forgeobj.WavefrontObject;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Created by drzzm32 on 2018.12.14.
 */
public class RendererHelper {

    public static void renderWithResourceAndRotation(WavefrontObject model, float angle, ResourceLocation texture) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        GL11.glPushMatrix();
        GL11.glScalef(0.0625F, 0.0625F, 0.0625F);
        GL11.glPushMatrix();
        GL11.glRotatef(angle, 0.0F, -1.0F, 0.0F);
        model.renderAll();
        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }

    public static void renderWithResource(WavefrontObject model, ResourceLocation texture) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        GL11.glPushMatrix();
        GL11.glScalef(0.0625F, 0.0625F, 0.0625F);
        model.renderAll();
        GL11.glPopMatrix();
    }

    public static void renderPartWithResource(WavefrontObject model, String part, ResourceLocation texture) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        GL11.glPushMatrix();
        GL11.glScalef(0.0625F, 0.0625F, 0.0625F);
        model.renderPart(part);
        GL11.glPopMatrix();
    }
}
