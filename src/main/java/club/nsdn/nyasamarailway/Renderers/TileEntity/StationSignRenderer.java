package club.nsdn.nyasamarailway.Renderers.TileEntity;

/**
 * Created by drzzm32 on 2016.7.5.
 */

import club.nsdn.nyasamarailway.TileEntities.TileEntityStationSign;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class StationSignRenderer extends TileEntitySpecialRenderer {

    private final ModelBase model;
    private final ResourceLocation texture;

    public StationSignRenderer() {
        this.model = new StationSignModel();
        this.texture = new ResourceLocation("nyasamarailway", "textures/blocks/StationSign.png");
    }

    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float scale) {
        TileEntityStationSign.StationSign sign = (TileEntityStationSign.StationSign) te;
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);

        Minecraft.getMinecraft().renderEngine.bindTexture(texture);

        RenderHelper.disableStandardItemLighting();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_CULL_FACE);

        if (Minecraft.isAmbientOcclusionEnabled())
        {
            GL11.glShadeModel(GL11.GL_SMOOTH);
        }
        else
        {
            GL11.glShadeModel(GL11.GL_FLAT);
        }

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

        GL11.glTranslatef(0.0F, -0.85F, -0.05F);

        drawCenteredString(sign.StationNameCN, 0, 2.0F);
        drawCenteredString(sign.StationNameEN, 20, 0.8F);

        final String SPLIT = "/";
        final String CENTER = " <==> ";
        final int SPACE = len(" ");

        String left = sign.LeftStations.split(SPLIT)[0], right = sign.RightStations.split(SPLIT)[0];
        String leftSpace, rightSpace;
        boolean hasMultiStations = false;
        
        leftSpace = getSpaces(left, right).left;
        rightSpace = getSpaces(left, right).right;
        
        String stations = leftSpace + left + leftSpace + CENTER + rightSpace + right + rightSpace;

        if (sign.LeftStations.split(SPLIT).length > 1) hasMultiStations = true;
        if (sign.RightStations.split(SPLIT).length > 1) hasMultiStations = true;

        if (!hasMultiStations) {
            if (!stations.equals(CENTER))
                drawCenteredString(stations, 24, 1.0F);
        } else {
            String leftA, leftB = "", rightA, rightB = "";
            leftA = sign.LeftStations.split(SPLIT)[0];
            rightA = sign.RightStations.split(SPLIT)[0];
            if (sign.LeftStations.split(SPLIT).length > 1) leftB = sign.LeftStations.split(SPLIT)[1];
            if (sign.RightStations.split(SPLIT).length > 1) rightB = sign.RightStations.split(SPLIT)[1];
            
            String leftLarger = leftA.length() > leftB.length() ? leftA : leftB;
            String rightLarger = rightA.length() > rightB.length() ? rightA : rightB;
            
            leftSpace = getSpaces(leftLarger, rightLarger).left;
            rightSpace = getSpaces(leftLarger, rightLarger).right;

            String space = spaces(Math.abs(len(leftA) - len(leftB)) / 2 / SPACE);
            if (leftLarger.equals(leftA)) {
                leftB = space + leftB + space;
            } else {
                leftA = space + leftA + space;
            }

            space = spaces(Math.abs(len(rightA) - len(rightB)) / 2 / SPACE);
            if (rightLarger.equals(rightA)) {
                rightB = space + rightB + space;
            } else {
                rightA = space + rightA + space;
            }

            stations = leftSpace + leftA + leftSpace + CENTER + rightSpace + rightA + rightSpace;
            drawCenteredString(stations, 24, 1.0F);
            stations = leftSpace + leftB + leftSpace + spaces(len(CENTER) / SPACE) + rightSpace + rightB + rightSpace;
            drawCenteredString(stations, 32, 1.0F);
        }

        GL11.glPopMatrix();

        GL11.glDepthMask(true);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        GL11.glPopMatrix();

        RenderHelper.enableStandardItemLighting();

        GL11.glPopMatrix();
    }
    
    public class Spaces {
        public String left; public String right;
        public Spaces() { left = ""; right = ""; }
    }
    
    public Spaces getSpaces(String left, String right) {
        Spaces spaces = new Spaces();
        if (len(left) < len(right)) {
            spaces.left = spaces((len(right) - len(left)) / 2 / len(" "));
        } else if (len(left) > len(right)) {
            spaces.right = spaces((len(left) - len(right)) / 2 / len(" "));
        }
        return spaces;
    }

    public String spaces(int count) {
        String s = "";
        for (int i = 0; i < count; i++)
            s = s.concat(" ");
        return s;
    }

    public int len(String string) {
        return this.func_147498_b().getStringWidth(string);
    }

    public void drawCenteredString(String string, int y, float scale) {
        FontRenderer renderer = this.func_147498_b();
        GL11.glPushMatrix();
        GL11.glScalef(1.0F / 60.0F, 1.0F / 60.0F, 1.0F);
        GL11.glScalef(scale, scale, 1.0F);
        if ((float) renderer.getStringWidth(string) * scale > 70.0F) {
            float fix = 70.0F / ((float) renderer.getStringWidth(string) * scale);
            GL11.glScalef(fix, fix, 1.0F);
            fix = (1.0F / fix - 1.0F) * 25.0F;
            if (scale > 1.0F) GL11.glTranslatef(0.0F, -fix * 0.05F, 0.0F);
            else GL11.glTranslatef(0.0F, fix, 0.0F);
        }
        renderer.drawString(string, -renderer.getStringWidth(string) / 2, y, 0);
        GL11.glPopMatrix();
    }

}
