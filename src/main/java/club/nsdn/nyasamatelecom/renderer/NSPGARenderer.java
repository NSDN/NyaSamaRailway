package club.nsdn.nyasamatelecom.renderer;

import club.nsdn.nyasamatelecom.NyaSamaTelecom;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import club.nsdn.nyasamatelecom.tileblock.core.BlockNSPGA;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import cn.ac.nya.forgeobj.WavefrontObject;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.1.29.
 */
public class NSPGARenderer extends AbsTileEntitySpecialRenderer {

    private final WavefrontObject modelBase = new WavefrontObject(
            new ResourceLocation(NyaSamaTelecom.MODID, "models/blocks/nspga_base.obj")
    );
    private final ResourceLocation textureBase = new ResourceLocation(NyaSamaTelecom.MODID, "textures/blocks/nspga_base.png");

    private final WavefrontObject modelPrint = new WavefrontObject(
            new ResourceLocation(NyaSamaTelecom.MODID, "models/blocks/nspga_print.obj")
    );

    public NSPGARenderer() {
    }

    @Override
    public void render(@Nonnull TileEntityBase te, double x, double y, double z, float partialTicks, int destroyStage, float partial) {
        int meta = te.META;
        if (!(te.getBlockType() instanceof BlockNSPGA)) return;
        BlockNSPGA block = (BlockNSPGA) te.getBlockType();
        if (block.texturePrint == null)
            block.texturePrint = new ResourceLocation(NyaSamaTelecom.MODID, "textures/blocks/" + block.name + "_print" + ".png");

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

        GL11.glPushMatrix();

        switch (meta & 0x7) {
            case 0:
                GL11.glRotatef(0, 0.0F, -1.0F, 0.0F);
                break;
            case 1:
                GL11.glRotatef(90, 0.0F, -1.0F, 0.0F);
                break;
            case 2:
                GL11.glRotatef(180, 0.0F, -1.0F, 0.0F);
                break;
            case 3:
                GL11.glRotatef(270, 0.0F, -1.0F, 0.0F);
                break;

            case 4:
                GL11.glTranslatef(0.0F, 1.0F, 0.0F);
                GL11.glRotatef(90.0F, -1.0F, 0.0F, 0.0F);
                GL11.glTranslatef(0.0F, -1.0F, 0.0F);
                GL11.glRotatef(0, 0.0F, -1.0F, 0.0F);
                GL11.glTranslatef(0.0F, 1.0F, -1.0F);
                break;
            case 5:
                GL11.glTranslatef(0.0F, 1.0F, 0.0F);
                GL11.glRotatef(90.0F, 0.0F, 0.0F, -1.0F);
                GL11.glTranslatef(0.0F, -1.0F, 0.0F);
                GL11.glRotatef(90.0F, 0.0F, -1.0F, 0.0F);
                GL11.glTranslatef(0.0F, 1.0F, -1.0F);
                break;
            case 6:
                GL11.glTranslatef(0.0F, 1.0F, 0.0F);
                GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                GL11.glTranslatef(0.0F, -1.0F, 0.0F);
                GL11.glRotatef(180.0F, 0.0F, -1.0F, 0.0F);
                GL11.glTranslatef(0.0F, 1.0F, -1.0F);
                break;
            case 7:
                GL11.glTranslatef(0.0F, 1.0F, 0.0F);
                GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                GL11.glTranslatef(0.0F, -1.0F, 0.0F);
                GL11.glRotatef(270.0F, 0.0F, -1.0F, 0.0F);
                GL11.glTranslatef(0.0F, 1.0F, -1.0F);
                break;
        }

        RendererHelper.renderWithResourceAndRotation(modelBase, 0, textureBase);
        RendererHelper.renderWithResourceAndRotation(modelPrint, 0, block.texturePrint);

        GL11.glPopMatrix();

        RenderHelper.enableStandardItemLighting();

        GL11.glPopMatrix();
    }

}
