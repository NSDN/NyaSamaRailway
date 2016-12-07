package club.nsdn.nyasamarailway.Renderers.TileEntity.Rail;

/**
 * Created by drzzm32 on 2016.11.29.
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

public class RailMonoRenderer extends TileEntitySpecialRenderer {

    private static final int STRAIGHT = 0, SLOPE = 1, TURNED = 2;

    private final ModelBase[] model;
    private final ResourceLocation[] textures;
    private double shiftX, shiftY, shiftZ;

    public RailMonoRenderer(ModelBase[] model, double shiftX, double shiftY, double shiftZ) {
        this.model = model;
        this.textures = new ResourceLocation[] {
                new ResourceLocation("nyasamarailway", "textures/blocks/ConcreteWall.png"),
                new ResourceLocation("nyasamarailway", "textures/blocks/ConcreteWall.png"),
                new ResourceLocation("nyasamarailway", "textures/blocks/ConcreteWall.png")
        };
        this.shiftX = shiftX; this.shiftY = shiftY; this.shiftZ = shiftZ;
    }

    public RailMonoRenderer(ModelBase[] model, String[] texturePath, double shiftX, double shiftY, double shiftZ) {
        this.model = model;
        this.textures = new ResourceLocation[3];
        for (int i = 0; i < 3; i++) {
            textures[i] = new ResourceLocation("nyasamarailway", texturePath[i]);
        }
        this.shiftX = shiftX; this.shiftY = shiftY; this.shiftZ = shiftZ;
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

        Tessellator.instance.setColorOpaque_F(1.0F, 1.0F, 1.0F);

        GL11.glPushMatrix();
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
        GL11.glTranslated(shiftX, -shiftY, shiftZ);

        switch (te.getBlockMetadata()) {
            case 0: //N=S
                GL11.glRotatef(0.0F, 0.0F, 1.0F, 0.0F);
                Minecraft.getMinecraft().renderEngine.bindTexture(textures[STRAIGHT]);
                this.model[STRAIGHT].render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
                break;
            case 1: //W=E
                GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                Minecraft.getMinecraft().renderEngine.bindTexture(textures[STRAIGHT]);
                this.model[STRAIGHT].render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
                break;
            case 2: //E
                GL11.glRotatef(90.0F, 0.0F, -1.0F, 0.0F);
                GL11.glTranslatef(0.0F, -1.0F, 0.0F);
                Minecraft.getMinecraft().renderEngine.bindTexture(textures[SLOPE]);
                this.model[SLOPE].render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
                break;
            case 3: //W
                GL11.glRotatef(-90.0F, 0.0F, -1.0F, 0.0F);
                GL11.glTranslatef(0.0F, -1.0F, 0.0F);
                Minecraft.getMinecraft().renderEngine.bindTexture(textures[SLOPE]);
                this.model[SLOPE].render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
                break;
            case 4: //N
                GL11.glRotatef(180.0F, 0.0F, -1.0F, 0.0F);
                GL11.glTranslatef(0.0F, -1.0F, 0.0F);
                Minecraft.getMinecraft().renderEngine.bindTexture(textures[SLOPE]);
                this.model[SLOPE].render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
                break;
            case 5: //S
                GL11.glRotatef(0.0F, 0.0F, -1.0F, 0.0F);
                GL11.glTranslatef(0.0F, -1.0F, 0.0F);
                Minecraft.getMinecraft().renderEngine.bindTexture(textures[SLOPE]);
                this.model[SLOPE].render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
                break;
            case 6: //S-E
                GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                Minecraft.getMinecraft().renderEngine.bindTexture(textures[TURNED]);
                this.model[TURNED].render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
                break;
            case 7: //S-W
                GL11.glRotatef(270.0F, 0.0F, 1.0F, 0.0F);
                Minecraft.getMinecraft().renderEngine.bindTexture(textures[TURNED]);
                this.model[TURNED].render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
                break;
            case 8: //N-W
                GL11.glRotatef(0.0F, 0.0F, 1.0F, 0.0F);
                Minecraft.getMinecraft().renderEngine.bindTexture(textures[TURNED]);
                this.model[TURNED].render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
                break;
            case 9: //N-E
                GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                Minecraft.getMinecraft().renderEngine.bindTexture(textures[TURNED]);
                this.model[TURNED].render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
                break;
        }

        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }

}
