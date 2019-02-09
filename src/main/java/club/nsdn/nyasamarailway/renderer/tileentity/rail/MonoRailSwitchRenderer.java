package club.nsdn.nyasamarailway.renderer.tileentity.rail;

import club.nsdn.nyasamarailway.tileblock.rail.MagnetSwitch;
import club.nsdn.nyasamatelecom.api.render.AbsTileEntitySpecialRenderer;
import club.nsdn.nyasamatelecom.api.render.RendererHelper;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import cn.ac.nya.forgeobj.WavefrontObject;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2017.8.30.
 */
public class MonoRailSwitchRenderer extends AbsTileEntitySpecialRenderer {

    private static final int STRAIGHT = 0, TURNED = 1;

    private final WavefrontObject[] model;
    private final ResourceLocation texture;

    public MonoRailSwitchRenderer() {
        this.model = new WavefrontObject[] {
                new WavefrontObject(new ResourceLocation("nyasamarailway", "models/rails/mono_rail_straight.obj")),
                new WavefrontObject(new ResourceLocation("nyasamarailway", "models/rails/mono_rail_turned.obj"))
        };
        this.texture = new ResourceLocation("nyasamarailway", "textures/rails/mono_rail_switch.png");
    }

    public MonoRailSwitchRenderer(boolean is3rdRail) {
        if (is3rdRail) {
            this.model = new WavefrontObject[] {
                    new WavefrontObject(new ResourceLocation("nyasamarailway", "models/rails/trd_rail_s.obj")),
                    new WavefrontObject(new ResourceLocation("nyasamarailway", "models/rails/trd_rail_t.obj"))
            };
            this.texture = new ResourceLocation("nyasamarailway", "textures/rails/trd_rail_ele.png");
        } else {
            this.model = new WavefrontObject[] {
                    new WavefrontObject(new ResourceLocation("nyasamarailway", "models/rails/rail_magnet_switch.obj")),
                    new WavefrontObject(new ResourceLocation("nyasamarailway", "models/rails/rail_magnet_switch.obj"))
            };
            this.texture = new ResourceLocation("nyasamarailway", "textures/rails/trd_rail_ele.png");
        }
    }

    @Override
    public void render(@Nonnull TileEntityBase te, double x, double y, double z, float partialTicks, int destroyStage, float partial) {
        int meta = te.META;

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

        if (te instanceof MagnetSwitch.TileEntityMagnetSwitch) {
            float angle = (meta & 0x3) * 90 + 180.0F; //rotate 180 to fix model
            RendererHelper.renderWithResourceAndRotation(this.model[STRAIGHT], angle, texture);
        } else {
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
        }

        RenderHelper.enableStandardItemLighting();

        GL11.glPopMatrix();
    }

}
