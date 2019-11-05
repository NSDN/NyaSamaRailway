package club.nsdn.nyasamarailway.renderer.tileentity.rail;

import club.nsdn.nyasamatelecom.api.render.AbsTileEntitySpecialRenderer;
import club.nsdn.nyasamatelecom.api.render.RendererHelper;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import cn.ac.nya.forgeobj.WavefrontObject;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class RailRenderer extends AbsTileEntitySpecialRenderer {

    private static final int STRAIGHT = 0, SLOPE = 1, TURNED = 2;

    private final WavefrontObject[] model;
    private final ResourceLocation texture;

    public RailRenderer(String name) {
        this.model  = new WavefrontObject[] {
                new WavefrontObject(new ResourceLocation("nyasamarailway", "models/rails/" + name + "_straight.obj")),
                new WavefrontObject(new ResourceLocation("nyasamarailway", "models/rails/" + name + "_slope.obj")),
                new WavefrontObject(new ResourceLocation("nyasamarailway", "models/rails/" + name + "_turned.obj"))
        };
        this.texture = new ResourceLocation("nyasamarailway", "textures/rails/" + name + "_base.png");
    }

    @Override
    public void render(@Nonnull TileEntityBase te, double x, double y, double z, float partialTicks, int destroyStage, float partial) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y  + 0.5F, (float) z + 0.5F);

        //RendererHelper.beginSpecialLighting();

        GL11.glPushMatrix();

        switch (te.getBlockMetadata()) {
            case 0: //N=S
                RendererHelper.renderWithResourceAndRotation(this.model[STRAIGHT], 0.0F, texture);
                break;
            case 1: //W=E
                RendererHelper.renderWithResourceAndRotation(this.model[STRAIGHT], 90.0F, texture);
                break;
            case 2: //E
                RendererHelper.renderWithResourceAndRotation(this.model[SLOPE], -90.0F, texture);
                break;
            case 3: //W
                RendererHelper.renderWithResourceAndRotation(this.model[SLOPE], 90.0F, texture);
                break;
            case 4: //N
                RendererHelper.renderWithResourceAndRotation(this.model[SLOPE], 180.0F, texture);
                break;
            case 5: //S
                RendererHelper.renderWithResourceAndRotation(this.model[SLOPE], 0.0F, texture);
                break;
            case 6: //S-E
                RendererHelper.renderWithResourceAndRotation(this.model[TURNED], 180.0F, texture);
                break;
            case 7: //S-W
                RendererHelper.renderWithResourceAndRotation(this.model[TURNED], -90.0F, texture);
                break;
            case 8: //N-W
                RendererHelper.renderWithResourceAndRotation(this.model[TURNED], 0.0F, texture);
                break;
            case 9: //N-E
                RendererHelper.renderWithResourceAndRotation(this.model[TURNED], 90.0F, texture);
                break;
        }

        GL11.glPopMatrix();

        RendererHelper.endSpecialLighting();

        GL11.glPopMatrix();
    }

}
