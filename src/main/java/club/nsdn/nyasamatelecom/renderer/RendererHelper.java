package club.nsdn.nyasamatelecom.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.obj.OBJModel;
import org.lwjgl.opengl.GL11;

/**
 * Created by drzzm32 on 2018.12.14.
 */
public class RendererHelper {

    private static final float mapping_factor = 0.98F;

    private static void render(IBakedModel model) {
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(GL11.GL_QUADS, Attributes.DEFAULT_BAKED_FORMAT);
        for (BakedQuad q : model.getQuads(null, null, 0))
            bufferBuilder.addVertexData(q.getVertexData());
        bufferBuilder.endVertex();
        Tessellator.getInstance().draw();
    }

    public static void renderWithResourceAndRotation(IBakedModel model, float angle, ResourceLocation texture) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        GL11.glPushMatrix();
        GL11.glScalef(0.0625F, 0.0625F, 0.0625F);
        GL11.glPushMatrix();
        GL11.glRotatef(angle, 0.0F, -1.0F, 0.0F);
        render(model);
        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }

    public static void renderWithResource(IBakedModel model, ResourceLocation texture) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        GL11.glPushMatrix();
        GL11.glScalef(0.0625F, 0.0625F, 0.0625F);
        render(model);
        GL11.glPopMatrix();
    }

    public static void renderPartWithResource(OBJModel model, String part, ResourceLocation texture) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        GL11.glPushMatrix();
        GL11.glScalef(0.0625F, 0.0625F, 0.0625F);
        model.groups.
        GL11.glPopMatrix();
    }

    public static void renderWithIconAndRotation(OBJModel model, float angle, IIcon icon, Tessellator tessellator) {
        float radius, azimuth, centre_u, centre_v;

        for (GroupObject group : model.groupObjects) {
            for (OBJModel.Face face : group.faces) {
                OBJModel.Vertex normal = face.faceNormal;
                tessellator.setNormal(normal.x, normal.y, normal.z);
                centre_u = centre_v = 0.0F;
                for (OBJModel.TextureCoordinate coordinate : face.textureCoordinates) {
                    centre_u += coordinate.u;
                    centre_v += coordinate.v;
                }
                centre_u /= face.textureCoordinates.length;
                centre_v /= face.textureCoordinates.length;
                for (int i = 0; i < face.vertices.length; i++) {
                    OBJModel.Vertex vertex = face.vertices[i];
                    radius = (float)Math.sqrt(vertex.x * vertex.x + vertex.z * vertex.z);
                    azimuth = (float)Math.atan2(vertex.z, vertex.x);
                    OBJModel.TextureCoordinate coordinate = face.textureCoordinates[i];
                    tessellator.addVertexWithUV(radius *
                            Math.cos(azimuth + Math.toRadians(angle)), vertex.y, radius * Math.sin(azimuth + Math.toRadians(angle)),
                            icon.getInterpolatedU((centre_u + (coordinate.u - centre_u) * mapping_factor) * 16.0F),
                            icon.getInterpolatedV((centre_v + (coordinate.v - centre_v) * mapping_factor) * 16.0F));
                }
            }
        }
    }
}
