package club.nsdn.nyasamarailway.renderer.tileentity.rail;

import club.nsdn.nyasamarailway.api.rail.TileEntityRailEndpoint;
import club.nsdn.nyasamarailway.util.Vertex;
import club.nsdn.nyasamatelecom.api.render.AbsTileEntitySpecialRenderer;
import club.nsdn.nyasamatelecom.api.render.RendererHelper;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import cn.ac.nya.forgeobj.WavefrontObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.3.17.
 */
public class CurvedRailRenderer extends AbsTileEntitySpecialRenderer {

    private final WavefrontObject model = new WavefrontObject(new ResourceLocation("nyasamarailway", "models/rails/mono_rail_curved_simple.obj"));
    private final ResourceLocation texture = new ResourceLocation("nyasamarailway", "textures/rails/mono_rail_curved.png");

    @Override
    public boolean isGlobalRenderer(TileEntity te) {
        return true;
    }

    @Override
    public void render(@Nonnull TileEntityBase te, double x, double y, double z, float partialTicks, int destroyStage, float partial) {
        if (te instanceof TileEntityRailEndpoint) {
            TileEntityRailEndpoint endpoint = (TileEntityRailEndpoint) te;
            BlockPos blockPos = endpoint.getPos();

            GL11.glPushMatrix();
            GL11.glTranslated(x - blockPos.getX(), y - blockPos.getY() + 1.0, z - blockPos.getZ());

            Minecraft.getMinecraft().getTextureManager().bindTexture(texture);

            //RendererHelper.beginSpecialLighting();

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();

            buffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_NORMAL);

            if (endpoint.getVertices().isEmpty())
                endpoint.cookVertices(model);

            for (Vertex vertex : endpoint.getVertices())
                vertex.push(buffer);

            tessellator.draw();

            //RendererHelper.endSpecialLighting();

            GL11.glPopMatrix();
        }
    }

}
