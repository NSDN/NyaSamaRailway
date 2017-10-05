package club.nsdn.nyasamarailway.Renderers.TileEntity;

import club.nsdn.nyasamarailway.TileEntities.TileEntityPillar;
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
 * Created by drzzm32 on 2017.10.5.
 */
public class PillarRenderer extends TileEntitySpecialRenderer {

    private final WavefrontObject modelMain = new WavefrontObject(
            new ResourceLocation("nyasamarailway", "models/blocks/pillar.obj")
    );
    private final ResourceLocation textureMain = new ResourceLocation("nyasamarailway", "textures/blocks/signal_light_base.png");

    public PillarRenderer() {
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

        if (te instanceof TileEntityPillar.Pillar) {
            TileEntityPillar.Pillar pillar = (TileEntityPillar.Pillar) te;
            int meta = pillar.meta;

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

            if ((meta & TileEntityPillar.getValueByForgeDirection(ForgeDirection.UP)) != 0)
                modelMain.renderPart("4");
            if ((meta & TileEntityPillar.getValueByForgeDirection(ForgeDirection.DOWN)) != 0)
                modelMain.renderPart("3");
            if ((meta & TileEntityPillar.getValueByForgeDirection(ForgeDirection.NORTH)) != 0)
                modelMain.renderPart("5");
            if ((meta & TileEntityPillar.getValueByForgeDirection(ForgeDirection.SOUTH)) != 0)
                modelMain.renderPart("6");
            if ((meta & TileEntityPillar.getValueByForgeDirection(ForgeDirection.WEST)) != 0)
                modelMain.renderPart("1");
            if ((meta & TileEntityPillar.getValueByForgeDirection(ForgeDirection.EAST)) != 0)
                modelMain.renderPart("2");

            GL11.glPopMatrix();
        }

        RenderHelper.enableStandardItemLighting();

        GL11.glPopMatrix();
    }

}
