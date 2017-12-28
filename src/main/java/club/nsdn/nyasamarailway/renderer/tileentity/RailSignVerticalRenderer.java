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
 * Created by drzzm32 on 2017.9.10.
 */
public class RailSignVerticalRenderer extends TileEntitySpecialRenderer {

    private final WavefrontObject modelMain = new WavefrontObject(
            new ResourceLocation("nyasamarailway", "models/blocks/rail_sign_vertical.obj")
    );
    private final ResourceLocation textureMain;
    public RailSignVerticalRenderer(String texture) {
        textureMain = new ResourceLocation("nyasamarailway", "textures/blocks/" + texture + ".png");
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

        RendererHelper.renderWithResourceAndRotation(modelMain, angle, textureMain);

        RenderHelper.enableStandardItemLighting();

        GL11.glPopMatrix();
    }

}
