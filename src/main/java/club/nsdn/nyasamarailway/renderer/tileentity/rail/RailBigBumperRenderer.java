package club.nsdn.nyasamarailway.renderer.tileentity.rail;

import club.nsdn.nyasamatelecom.api.render.AbsTileEntitySpecialRenderer;
import club.nsdn.nyasamatelecom.api.render.RendererHelper;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import cn.ac.nya.forgeobj.WavefrontObject;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.9.9.
 */
public class RailBigBumperRenderer extends AbsTileEntitySpecialRenderer {

    private final WavefrontObject modelMain = new WavefrontObject(
            new ResourceLocation("nyasamarailway", "models/blocks/big_bumper_main.obj")
    );
    private final ResourceLocation textureMain = new ResourceLocation("nyasamarailway", "textures/blocks/big_bumper_main.png");

    public RailBigBumperRenderer() {
    }

    @Override
    public void render(@Nonnull TileEntityBase te, double x, double y, double z, float partialTicks, int destroyStage, float partial) {
        int meta = te.META;

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y  + 0.5F, (float) z + 0.5F);

        RendererHelper.beginSpecialLighting();

        int angle = (meta & 0x3) * 90;

        GL11.glPushMatrix();
        RendererHelper.renderWithResourceAndRotation(modelMain, angle, textureMain);
        GL11.glPopMatrix();

        RendererHelper.endSpecialLighting();

        GL11.glPopMatrix();
    }

}
