package club.nsdn.nyasamarailway.renderer.tileentity.deco;

import club.nsdn.nyasamarailway.tileblock.deco.Pillar;
import club.nsdn.nyasamatelecom.api.render.AbsTileEntitySpecialRenderer;
import club.nsdn.nyasamatelecom.api.render.RendererHelper;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import cn.ac.nya.forgeobj.WavefrontObject;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class PillarRenderer extends AbsTileEntitySpecialRenderer {

    private final WavefrontObject modelMain = new WavefrontObject(
            new ResourceLocation("nyasamarailway", "models/blocks/pillar.obj")
    );
    private final ResourceLocation textureMain = new ResourceLocation("nyasamarailway", "textures/blocks/signal_light_base.png");

    public PillarRenderer() {
    }

    @Override
    public void render(@Nonnull TileEntityBase te, double x, double y, double z, float partialTicks, int destroyStage, float partial) {
        int meta = te.META;

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y  + 0.5F, (float) z + 0.5F);

        //RendererHelper.beginSpecialLighting();

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

        if ((meta & Pillar.getValueByFacing(EnumFacing.UP)) != 0)
            modelMain.renderPart("4");
        if ((meta & Pillar.getValueByFacing(EnumFacing.DOWN)) != 0)
            modelMain.renderPart("3");
        if ((meta & Pillar.getValueByFacing(EnumFacing.NORTH)) != 0)
            modelMain.renderPart("5");
        if ((meta & Pillar.getValueByFacing(EnumFacing.SOUTH)) != 0)
            modelMain.renderPart("6");
        if ((meta & Pillar.getValueByFacing(EnumFacing.WEST)) != 0)
            modelMain.renderPart("1");
        if ((meta & Pillar.getValueByFacing(EnumFacing.EAST)) != 0)
            modelMain.renderPart("2");

        GL11.glPopMatrix();

        RendererHelper.endSpecialLighting();

        GL11.glPopMatrix();
    }

}
