package club.nsdn.nyasamarailway.renderer;

/**
 * Created by drzzm on 2016.11.28.
 */

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.Face;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.client.model.obj.TextureCoordinate;
import net.minecraftforge.client.model.obj.Vertex;
import net.minecraftforge.client.model.obj.WavefrontObject;
import org.lwjgl.opengl.GL11;

public class RendererHelper {
    private static float radius = 0.0F;
    private static float azimuth = 0.0F;
    private static float centre_u = 0.0F;
    private static float centre_v = 0.0F;
    private static final float mapping_factor = 0.98F;

    public static void beginSpecialLightingNoDepth() {
        RenderHelper.disableStandardItemLighting();

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_CULL_FACE);

        if (Minecraft.isAmbientOcclusionEnabled()) {
            GL11.glShadeModel(GL11.GL_SMOOTH);
        } else {
            GL11.glShadeModel(GL11.GL_FLAT);
        }

        Tessellator.instance.setColorOpaque_F(1.0F, 1.0F, 1.0F);

        GL11.glDepthMask(false);
    }

    public static void endSpecialLightingNoDepth() {
        GL11.glDepthMask(true);
        RenderHelper.enableStandardItemLighting();
    }

    public static void beginSpecialLighting() {
        RenderHelper.disableStandardItemLighting();

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_CULL_FACE);

        if (Minecraft.isAmbientOcclusionEnabled()) {
            GL11.glShadeModel(GL11.GL_SMOOTH);
        } else {
            GL11.glShadeModel(GL11.GL_FLAT);
        }

        Tessellator.instance.setColorOpaque_F(1.0F, 1.0F, 1.0F);
    }

    public static void endSpecialLighting() {
        RenderHelper.enableStandardItemLighting();
    }

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

    public static void renderPartWithResourceAndRotation(WavefrontObject model, String part, float angle, ResourceLocation texture) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        GL11.glPushMatrix();
        GL11.glScalef(0.0625F, 0.0625F, 0.0625F);
        GL11.glPushMatrix();
        GL11.glRotatef(angle, 0.0F, -1.0F, 0.0F);
        model.renderPart(part);
        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }

    public static void renderOtherPartWithResourceAndRotation(WavefrontObject model, String part, float angle, ResourceLocation texture) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        GL11.glPushMatrix();
        GL11.glScalef(0.0625F, 0.0625F, 0.0625F);
        GL11.glPushMatrix();
        GL11.glRotatef(angle, 0.0F, -1.0F, 0.0F);
        model.renderAllExcept(part);
        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }

    public static void renderWithIconAndRotation(WavefrontObject model, float angle, IIcon icon, Tessellator tessellator) {
        for (GroupObject group : model.groupObjects) {
            for (Face face : group.faces) {
                Vertex normal = face.faceNormal;
                tessellator.setNormal(normal.x, normal.y, normal.z);
                centre_u = centre_v = 0.0F;
                for (TextureCoordinate coordinate : face.textureCoordinates) {
                    centre_u += coordinate.u;
                    centre_v += coordinate.v;
                }
                centre_u /= face.textureCoordinates.length;
                centre_v /= face.textureCoordinates.length;
                for (int i = 0; i < face.vertices.length; i++) {
                    Vertex vertex = face.vertices[i];
                    radius = (float)Math.sqrt(vertex.x * vertex.x + vertex.z * vertex.z);
                    azimuth = (float)Math.atan2(vertex.z, vertex.x);
                    TextureCoordinate coordinate = face.textureCoordinates[i];
                    tessellator.addVertexWithUV(radius *
                            Math.cos(azimuth + Math.toRadians(angle)), vertex.y, radius * Math.sin(azimuth + Math.toRadians(angle)),
                            icon.getInterpolatedU((centre_u + (coordinate.u - centre_u) * mapping_factor) * 16.0F),
                            icon.getInterpolatedV((centre_v + (coordinate.v - centre_v) * mapping_factor) * 16.0F));
                }
            }
        }
    }
}
