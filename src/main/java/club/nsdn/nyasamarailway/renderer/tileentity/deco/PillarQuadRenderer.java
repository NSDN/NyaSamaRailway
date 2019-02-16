package club.nsdn.nyasamarailway.renderer.tileentity.deco;

import club.nsdn.nyasamarailway.tileblock.deco.PillarBig;
import club.nsdn.nyasamatelecom.api.render.AbsTileEntitySpecialRenderer;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import cn.ac.nya.forgeobj.WavefrontObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class PillarQuadRenderer extends AbsTileEntitySpecialRenderer {

    private final WavefrontObject modelMain = new WavefrontObject(
            new ResourceLocation("nyasamarailway", "models/blocks/pillar_quad.obj")
    );
    private final ResourceLocation textureMain = new ResourceLocation("nyasamarailway", "textures/blocks/pillar_quad_base.png");

    public PillarQuadRenderer() {
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

        Minecraft.getMinecraft().getTextureManager().bindTexture(textureMain);
        GL11.glPushMatrix();
        GL11.glScalef(0.0625F, 0.0625F, 0.0625F);

        // 0 center
        // 5 north
        // 6 south
        // 1 west
        // 2 east
        // 3 down
        // 4 up

        modelMain.renderPart("0");

        int hmeta = meta >> 6;
        if ((hmeta & PillarBig.getValueByFacing(EnumFacing.UP)) != 0)
            modelMain.renderPart("4e");
        else if ((meta & PillarBig.getValueByFacing(EnumFacing.UP)) != 0)
            modelMain.renderPart("4");

        if ((hmeta & PillarBig.getValueByFacing(EnumFacing.DOWN)) != 0)
            modelMain.renderPart("3e");
        else if ((meta & PillarBig.getValueByFacing(EnumFacing.DOWN)) != 0)
            modelMain.renderPart("3");

        if ((hmeta & PillarBig.getValueByFacing(EnumFacing.NORTH)) != 0)
            modelMain.renderPart("5e");
        else if ((meta & PillarBig.getValueByFacing(EnumFacing.NORTH)) != 0)
            modelMain.renderPart("5");

        if ((hmeta & PillarBig.getValueByFacing(EnumFacing.SOUTH)) != 0)
            modelMain.renderPart("6e");
        else if ((meta & PillarBig.getValueByFacing(EnumFacing.SOUTH)) != 0)
            modelMain.renderPart("6");

        if ((hmeta & PillarBig.getValueByFacing(EnumFacing.WEST)) != 0)
            modelMain.renderPart("1e");
        else if ((meta & PillarBig.getValueByFacing(EnumFacing.WEST)) != 0)
            modelMain.renderPart("1");

        if ((hmeta & PillarBig.getValueByFacing(EnumFacing.EAST)) != 0)
            modelMain.renderPart("2e");
        else if ((meta & PillarBig.getValueByFacing(EnumFacing.EAST)) != 0)
            modelMain.renderPart("2");

        GL11.glPopMatrix();

        RenderHelper.enableStandardItemLighting();

        GL11.glPopMatrix();
    }

}