package club.nsdn.nyasamarailway.renderer.tileentity.rail;

import club.nsdn.nyasamarailway.api.rail.TileEntityRailEndpoint;
import club.nsdn.nyasamarailway.util.Vertex;
import club.nsdn.nyasamatelecom.api.render.AbsTileEntitySpecialRenderer;
import club.nsdn.nyasamatelecom.api.render.RendererHelper;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import cn.ac.nya.forgeobj.Face;
import cn.ac.nya.forgeobj.GroupObject;
import cn.ac.nya.forgeobj.WavefrontObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.3.17.
 */
public class CurvedRailRenderer extends AbsTileEntitySpecialRenderer {

    private final WavefrontObject model = new WavefrontObject(new ResourceLocation("nyasamarailway", "models/rails/mono_rail_curved.obj"));
    private final ResourceLocation texture = new ResourceLocation("nyasamarailway", "textures/rails/mono_rail_curved.png");

    @Override
    public boolean isGlobalRenderer(TileEntity te) {
        return true;
    }

    public static Vec3d rotatePitchFix(Vec3d vec, float pitch) {
        float c = MathHelper.cos(pitch);
        float s = MathHelper.sin(pitch);
        double x = vec.x * (double)c + vec.y * (double)s;
        double y = vec.y * (double)c - vec.x * (double)s;
        double z = vec.z;
        return new Vec3d(x, y, z);
    }

    @Override
    public void render(@Nonnull TileEntityBase te, double x, double y, double z, float partialTicks, int destroyStage, float partial) {
        if (te instanceof TileEntityRailEndpoint) {
            TileEntityRailEndpoint endpoint = (TileEntityRailEndpoint) te;
            BlockPos blockPos = endpoint.getPos();

            GL11.glPushMatrix();
            GL11.glTranslated(x - blockPos.getX(), y - blockPos.getY() + 1.0, z - blockPos.getZ());

            Minecraft.getMinecraft().getTextureManager().bindTexture(texture);

            RendererHelper.beginSpecialLighting();

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();

            buffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_NORMAL);

            Vec3d vec, nex; double step = 0.5;
            if (endpoint.cookedVertices.isEmpty()) {
                for (double d = 0; d <= endpoint.len(); d += step) {
                    if (d == endpoint.len()) {
                        vec = endpoint.get(d - step / 100.0);
                        nex = endpoint.get(d);
                        nex = nex.subtract(vec);
                        vec = endpoint.get(d);
                    } else {
                        vec = endpoint.get(d);
                        nex = endpoint.get(d + step);
                        nex = nex.subtract(vec);
                    }

                    double yaw = Math.atan2(nex.z, nex.x);
                    double hlen = Math.sqrt(nex.x * nex.x + nex.z * nex.z);
                    double pitch = Math.atan(nex.y / hlen);

                    for (GroupObject group : model.groupObjects)
                        for (Face face : group.faces) {
                            for(int i = 0; i < face.vertices.length; ++i) {
                                if (face.textureCoordinates != null && face.textureCoordinates.length > 0) {
                                    Vertex vertex = new Vertex();

                                    Vec3d pos = new Vec3d((double) face.vertices[i].x, (double) face.vertices[i].y, (double) face.vertices[i].z);
                                    pos = rotatePitchFix(pos, (float) -pitch).rotateYaw((float) -yaw).add(vec);

                                    vertex.pos(pos.x, pos.y, pos.z)
                                    .tex((double) face.textureCoordinates[i].u, (double) face.textureCoordinates[i].v)
                                    .nor(face.faceNormal.x, face.faceNormal.y, face.faceNormal.z);

                                    endpoint.cookedVertices.add(vertex);
                                }
                            }
                        }
                }
            }

            for (Vertex vertex : endpoint.cookedVertices)
                vertex.push(buffer);

            tessellator.draw();

            RendererHelper.endSpecialLighting();

            GL11.glPopMatrix();
        }
    }

}
