package club.nsdn.nyasamarailway.renderer.tileentity;

import club.nsdn.nyasamarailway.renderer.RendererHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.WavefrontObject;
import org.lwjgl.opengl.GL11;

/**
 * Created by drzzm32 on 2017.10.5.
 */
public class TriSignalLightRenderer extends TileEntitySpecialRenderer {

    private final WavefrontObject modelBase = new WavefrontObject(
            new ResourceLocation("nyasamarailway", "models/blocks/tri_signal_light_base.obj")
    );
    private final WavefrontObject modelLight[] = {
            new WavefrontObject(
                    new ResourceLocation("nyasamarailway", "models/blocks/tri_signal_light_r.obj")
            ),
            new WavefrontObject(
                    new ResourceLocation("nyasamarailway", "models/blocks/tri_signal_light_y.obj")
            ),
            new WavefrontObject(
                    new ResourceLocation("nyasamarailway", "models/blocks/tri_signal_light_g.obj")
            )
    };
    private final ResourceLocation textureBase = new ResourceLocation("nyasamarailway", "textures/blocks/signal_light_base.png");
    private final ResourceLocation[] textureLight = {
            new ResourceLocation("nyasamarailway", "textures/blocks/signal_box_none.png"),
            new ResourceLocation("nyasamarailway", "textures/blocks/signal_box_r.png"),
            new ResourceLocation("nyasamarailway", "textures/blocks/signal_box_y.png"),
            new ResourceLocation("nyasamarailway", "textures/blocks/signal_box_g.png")
    };

    public TriSignalLightRenderer() {
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

        int meta = te.getWorldObj().getBlockMetadata(te.xCoord, te.yCoord, te.zCoord);
        int angle = (meta & 0x3) * 90;

        int lightState = (meta >> 2) & 0x3;

        RendererHelper.renderWithResourceAndRotation(modelBase, angle, textureBase);
        RendererHelper.renderWithResourceAndRotation(modelLight[0], angle, textureLight[lightState == 2 ? 1 : 0]);
        RendererHelper.renderWithResourceAndRotation(modelLight[1], angle, textureLight[lightState == 1 ? 2 : 0]);
        RendererHelper.renderWithResourceAndRotation(modelLight[2], angle, textureLight[lightState == 0 ? 3 : 0]);

        RenderHelper.enableStandardItemLighting();

        GL11.glPopMatrix();
    }

}
