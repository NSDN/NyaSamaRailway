package club.nsdn.nyasamarailway.Renderers;

/**
 * Created by drzzm32 on 2016.5.5.
 */

import org.lwjgl.opengl.GL11;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public class BaseRenderer extends TileEntitySpecialRenderer {

    private final ModelBase model;

    public BaseRenderer(ModelBase model) {
        this.model = model;
    }

    private void adjustRotatePivotViaMeta(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        GL11.glPushMatrix();
        GL11.glRotatef(meta * (-90), 0.0F, 0.0F, 1.0F);
        GL11.glPopMatrix();
    }

    private void adjustLightFixture(World world, int i, int j, int k, Block block) {
        Tessellator tess = Tessellator.instance;
        float brightness = block.getLightOpacity(world, i, j, k);
        int skyLight = world.getLightBrightnessForSkyBlocks(i, j, k, 0);
        int modulousModifier = skyLight % 65536;
        int divModifier = skyLight / 65536;
        tess.setColorOpaque_F(brightness, brightness, brightness);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) modulousModifier, divModifier);
    }

    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float scale) {
        //The PushMatrix tells the renderer to "start" doing something.
        GL11.glPushMatrix();
        //This is setting the initial location.
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
        //This is the texture of your block. It's pathed to be the same place as your other blocks here.
        //Outdated bindTextureByName("/mods/roads/textures/blocks/TrafficLightPoleRed.png");
        //Use in 1.6.2  this
        ResourceLocation textures = (new ResourceLocation("minecraft", "textures/blocks/quartz_block_side.png"));
        //the ':' is very important
        //binding the textures
        Minecraft.getMinecraft().renderEngine.bindTexture(textures);

        //This rotation part is very important! Without it, your model will render upside-down! And for some reason you DO need PushMatrix again!
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

            case 5:
                GL11.glTranslatef(0.0F, 1.0F, 0.0F);
                GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                GL11.glTranslatef(0.0F, -1.0F, 0.0F);
                break;
            case 6:
                GL11.glTranslatef(0.0F, 1.0F, 0.0F);
                GL11.glRotatef(90.0F, 0.0F, 0.0F, -1.0F);
                GL11.glTranslatef(0.0F, -1.0F, 0.0F);
                GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                break;
            case 7:
                GL11.glTranslatef(0.0F, 1.0F, 0.0F);
                GL11.glRotatef(90.0F, -1.0F, 0.0F, 0.0F);
                GL11.glTranslatef(0.0F, -1.0F, 0.0F);
                GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                break;
            case 8:
                GL11.glTranslatef(0.0F, 1.0F, 0.0F);
                GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                GL11.glTranslatef(0.0F, -1.0F, 0.0F);
                GL11.glRotatef(270.0F, 0.0F, 1.0F, 0.0F);
                break;

            case 9:
                rotation = 0;
                GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
                GL11.glTranslatef(0.0F, -2.0F, 0.0F);
                GL11.glRotatef(rotation, 0.0F, -1.0F, 0.0F);
                break;
            case 10:
                rotation = 90;
                GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
                GL11.glTranslatef(0.0F, -2.0F, 0.0F);
                GL11.glRotatef(rotation, 0.0F, -1.0F, 0.0F);
                break;
            case 11:
                rotation = 180;
                GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
                GL11.glTranslatef(0.0F, -2.0F, 0.0F);
                GL11.glRotatef(rotation, 0.0F, -1.0F, 0.0F);
                break;
            case 12:
                rotation = 270;
                GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
                GL11.glTranslatef(0.0F, -2.0F, 0.0F);
                GL11.glRotatef(rotation, 0.0F, -1.0F, 0.0F);
                break;
        }

        //A reference to your Model file. Again, very important.
        this.model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        //Tell it to stop rendering for both the PushMatrix's
        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }

}
