package club.nsdn.nyasamarailway.renderer.tileentity.rail;

import club.nsdn.nyasamarailway.tileblock.rail.ConvWireMono;
import club.nsdn.nyasamatelecom.api.render.AbsTileEntitySpecialRenderer;
import club.nsdn.nyasamatelecom.api.render.RendererHelper;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import cn.ac.nya.forgeobj.WavefrontObject;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class ConvWireMonoRenderer extends AbsTileEntitySpecialRenderer {

    private final WavefrontObject modelMain = new WavefrontObject(
            new ResourceLocation("nyasamarailway", "models/rails/wire_mono_conv.obj")
    );
    private final WavefrontObject modelMainS = new WavefrontObject(
            new ResourceLocation("nyasamarailway", "models/rails/wire_mono_conv_s.obj")
    );
    private final ResourceLocation textureMain = new ResourceLocation("nyasamarailway", "textures/rails/trd_rail_ele.png");

    public ConvWireMonoRenderer() {
    }

    @Override
    public void render(@Nonnull TileEntityBase te, double x, double y, double z, float partialTicks, int destroyStage, float partial) {
        int meta = te.META;
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

        float angle = 0.0F;
        if (te instanceof ConvWireMono.TileEntityConvWireMono) {
            EnumFacing direction = ((ConvWireMono.TileEntityConvWireMono) te).direction;

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

        if (te.getBlockMetadata() > 1)
            RendererHelper.renderWithResourceAndRotation(modelMainS, angle, textureMain);
        else
            RendererHelper.renderWithResourceAndRotation(modelMain, angle, textureMain);

        RenderHelper.enableStandardItemLighting();

        GL11.glPopMatrix();
    }

}
