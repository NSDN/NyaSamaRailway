package club.nsdn.nyasamarailway.Renderers.TileEntity.Rail;

/**
 * Created by drzzm32 on 2016.7.23.
 */

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class RailRenderer extends TileEntitySpecialRenderer {

    private final ModelBase model;
    private final String texturePath;

    public RailRenderer(ModelBase model) {
        this.model = model;
        this.texturePath = "";
    }

    public RailRenderer(ModelBase model, String texturePath) {
        this.model = model;
        this.texturePath = texturePath;
    }

    private void adjustRotatePivotViaMeta(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        GL11.glPushMatrix();
        GL11.glRotatef(meta * (-90), 0.0F, 0.0F, 1.0F);
        GL11.glPopMatrix();
    }

    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float scale) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);

        ResourceLocation textures;
        if (texturePath == "")
            textures = (new ResourceLocation("nyasamarailway", "textures/blocks/ConcreteWall.png"));
        else
            textures = (new ResourceLocation("nyasamarailway", texturePath));
        Minecraft.getMinecraft().renderEngine.bindTexture(textures);

        Tessellator.instance.setColorOpaque_F(1.0F, 1.0F, 1.0F);

        GL11.glPushMatrix();
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);

        switch (te.getBlockMetadata()) {
            case 0: //N-S
                GL11.glRotatef(0.0F, 0.0F, 1.0F, 0.0F);
                break;
            case 1: //W-E
                GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                break;
            case 2: //E
                GL11.glTranslatef(0.0F, -1.0F, 0.0F);
                GL11.glScalef(1.5F, 1.0F, 1.0F);
                GL11.glRotatef(-55.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
                GL11.glTranslatef(0.0F, -0.5F, 0.5F);
                break;
            case 3: //W
                GL11.glTranslatef(0.0F, -1.0F, 0.0F);
                GL11.glScalef(1.5F, 1.0F, 1.0F);
                GL11.glRotatef(55.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
                GL11.glTranslatef(0.0F, -0.5F, -0.5F);
                break;
            case 4: //N
                GL11.glTranslatef(0.0F, -1.0F, 0.0F);
                GL11.glScalef(1.0F, 1.0F, 1.5F);
                GL11.glRotatef(55.0F, 1.0F, 0.0F, 0.0F);
                GL11.glTranslatef(0.0F, -0.5F, -0.5F);
                break;
            case 5: //S
                GL11.glTranslatef(0.0F, -1.0F, 0.0F);
                GL11.glScalef(1.0F, 1.0F, 1.5F);
                GL11.glRotatef(-55.0F, 1.0F, 0.0F, 0.0F);
                GL11.glTranslatef(0.0F, -0.5F, 0.5F);
                break;
            /*case 6:
                GL11.glTranslatef(0.0F, 1.0F, 0.0F);
                GL11.glRotatef(90.0F, -1.0F, 0.0F, 0.0F);
                GL11.glTranslatef(0.0F, -1.0F, 0.0F);
                GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                break;
            case 7:
                GL11.glTranslatef(0.0F, 1.0F, 0.0F);
                GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                GL11.glTranslatef(0.0F, -1.0F, 0.0F);
                GL11.glRotatef(270.0F, 0.0F, 1.0F, 0.0F);
                break;

            case 8:
                rotation = 0;
                GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
                GL11.glTranslatef(0.0F, -2.0F, 0.0F);
                GL11.glRotatef(rotation, 0.0F, -1.0F, 0.0F);
                break;
            case 9:
                rotation = 90;
                GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
                GL11.glTranslatef(0.0F, -2.0F, 0.0F);
                GL11.glRotatef(rotation, 0.0F, -1.0F, 0.0F);
                break;*/
        }

        this.model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }

}
