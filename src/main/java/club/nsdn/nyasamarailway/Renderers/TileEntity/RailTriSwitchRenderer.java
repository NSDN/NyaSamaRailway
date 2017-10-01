package club.nsdn.nyasamarailway.Renderers.TileEntity;

import club.nsdn.nyasamarailway.Renderers.RendererHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.WavefrontObject;
import org.lwjgl.opengl.GL11;

/**
 * Created by drzzm32 on 2017.8.30.
 */
public class RailTriSwitchRenderer extends TileEntitySpecialRenderer {

    private static final int STRAIGHT = 0, TURNED_L = 1, TURNED_R = 2;

    private final WavefrontObject model;
    private final ResourceLocation[] textures;

    public RailTriSwitchRenderer() {
        this.model = new WavefrontObject(new ResourceLocation("nyasamarailway", "models/blocks/rai_tril_switch.obj"));
        this.textures = new ResourceLocation[] {
                new ResourceLocation("nyasamarailway", "textures/blocks/rail_tri_switch_straight.png"),
                new ResourceLocation("nyasamarailway", "textures/blocks/rail_tri_switch_left.png"),
                new ResourceLocation("nyasamarailway", "textures/blocks/rail_tri_switch_right.png")
        };
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

        switch (te.getBlockMetadata()) {
            case 0: //N=S
                RendererHelper.renderWithResourceAndRotation(this.model, 0.0F, textures[STRAIGHT]);
                break;
            case 1: //W=E
                RendererHelper.renderWithResourceAndRotation(this.model, 90.0F, textures[STRAIGHT]);
                break;
            case 6: //S-E
                RendererHelper.renderWithResourceAndRotation(this.model, 180.0F, textures[TURNED_L]);
                break;
            case 7: //S-W
                RendererHelper.renderWithResourceAndRotation(this.model, -90.0F, textures[TURNED_L]);
                break;
            case 8: //N-W
                RendererHelper.renderWithResourceAndRotation(this.model, 0.0F, textures[TURNED_R]);
                break;
            case 9: //N-E
                RendererHelper.renderWithResourceAndRotation(this.model, 90.0F, textures[TURNED_R]);
                break;
        }

        RenderHelper.enableStandardItemLighting();

        GL11.glPopMatrix();
    }

}
