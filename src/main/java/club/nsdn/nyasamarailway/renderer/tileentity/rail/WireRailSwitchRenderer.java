package club.nsdn.nyasamarailway.renderer.tileentity.rail;

import club.nsdn.nyasamatelecom.api.render.AbsTileEntitySpecialRenderer;
import club.nsdn.nyasamatelecom.api.render.RendererHelper;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import cn.ac.nya.forgeobj.WavefrontObject;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2020.3.24.
 */
public class WireRailSwitchRenderer extends AbsTileEntitySpecialRenderer {

    private static final int STRAIGHT = 0, TURNED = 1;

    private final WavefrontObject[] model;
    private final ResourceLocation texture;

    public WireRailSwitchRenderer() {
        this.model = new WavefrontObject[] {
                new WavefrontObject(new ResourceLocation("nyasamarailway", "models/rails/rail_wire_straight.obj")),
                new WavefrontObject(new ResourceLocation("nyasamarailway", "models/rails/rail_wire_turned.obj"))
        };
        this.texture = new ResourceLocation("nyasamarailway", "textures/rails/rail_wire_switch.png");
    }

    @Override
    public void render(@Nonnull TileEntityBase te, double x, double y, double z, float partialTicks, int destroyStage, float partial) {
        int meta = te.META;

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y  + 0.5F, (float) z + 0.5F);

        //RendererHelper.beginSpecialLighting();

        switch (te.getBlockMetadata()) {
            case 0: //N=S
                RendererHelper.renderWithResourceAndRotation(this.model[STRAIGHT], 0.0F, texture);
                break;
            case 1: //W=E
                RendererHelper.renderWithResourceAndRotation(this.model[STRAIGHT], 90.0F, texture);
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

        RendererHelper.endSpecialLighting();

        GL11.glPopMatrix();
    }

}
