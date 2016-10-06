package club.nsdn.nyasamarailway.Renderers.TileEntity;

/**
 * Created by drzzm32 on 2016.7.5.
 */

import club.nsdn.nyasamarailway.TileEntities.TileEntityStationSign;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class StationSignRenderer extends TileEntitySpecialRenderer {

    private final ModelBase model;
    private final ResourceLocation texture;

    public StationSignRenderer() {
        this.model = new StationSignModel();
        this.texture = new ResourceLocation("nyasamarailway", "textures/blocks/StationSign.png");
    }

    private void adjustRotatePivotViaMeta(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        GL11.glPushMatrix();
        GL11.glRotatef(meta * (-90), 0.0F, 0.0F, 1.0F);
        GL11.glPopMatrix();
    }

    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float scale) {
        TileEntityStationSign.StationSign sign = (TileEntityStationSign.StationSign) te;
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);

        Minecraft.getMinecraft().renderEngine.bindTexture(texture);
        FontRenderer renderer = this.func_147498_b();
        Tessellator.instance.setColorOpaque_F(1.0F, 1.0F, 1.0F);

        GL11.glPushMatrix();
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);

        int rotation = 0;
        switch (te.getBlockMetadata() % 13) {
            case 1:
                rotation = 0;
                GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
                break;
            case 2:
                rotation = 90;
                GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
                break;
            case 3:
                rotation = 180;
                GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
                break;
            case 4:
                rotation = 270;
                GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
                break;
        }

        this.model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

        GL11.glNormal3f(0.0F, 0.0F, -1.0F);
        GL11.glDepthMask(false);

        GL11.glPushMatrix();
        GL11.glTranslatef(-0.75F + 0.25F, -1.0F + 0.125F, -0.05F);
        GL11.glScalef(0.03F, 0.03F, 1.0F);
        renderer.drawString(sign.StationNameCN, 8, 0, 0);
        GL11.glScalef(0.5F, 0.5F, 1.0F);
        renderer.drawString(sign.StationNameEN, 16, 16, 0);
        GL11.glScalef(1.1F, 1.1F, 1.0F);
        renderer.drawString(sign.LeftStations, 0, 28, 0);
        renderer.drawString("<==>", 24, 28, 0);
        renderer.drawString(sign.RightStations, 44, 28, 0);
        GL11.glPopMatrix();

        GL11.glDepthMask(true);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }

}
