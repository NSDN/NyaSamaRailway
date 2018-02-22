package club.nsdn.nyasamarailway.renderer.tileentity.rail;

import club.nsdn.nyasamarailway.renderer.RendererHelper;
import club.nsdn.nyasamarailway.tileblock.rail.ConvWireMono;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.WavefrontObject;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

/**
 * Created by drzzm32 on 2018.2.21.
 */
public class ConvWireMonoRenderer extends TileEntitySpecialRenderer {

    private final WavefrontObject modelMain = new WavefrontObject(
            new ResourceLocation("nyasamarailway", "models/rails/wire_mono_conv.obj")
    );
    private final ResourceLocation textureMain = new ResourceLocation("nyasamarailway", "textures/rails/trd_rail_ele.png");

    public ConvWireMonoRenderer() {
    }

    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float scale) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y  + 0.5F, (float) z + 0.5F);

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

        float angle = 0.0F;
        if (te instanceof ConvWireMono.Conv) {
            ForgeDirection direction = ((ConvWireMono.Conv) te).direction;

            if (direction != null) {
                switch (direction) {
                    case SOUTH:
                        angle = 0.0F;
                        break;
                    case WEST:
                        angle = 90.0F;
                        break;
                    case NORTH:
                        angle = 180.0F;
                        break;
                    case EAST:
                        angle = 270.0F;
                        break;
                }
            }
        }

        RendererHelper.renderWithResourceAndRotation(modelMain, angle, textureMain);

        RenderHelper.enableStandardItemLighting();

        GL11.glPopMatrix();
    }

}
