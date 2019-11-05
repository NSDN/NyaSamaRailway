package club.nsdn.nyasamarailway.renderer.tileentity.rail;

import club.nsdn.nyasamarailway.tileblock.rail.RailTriSwitch;
import club.nsdn.nyasamatelecom.api.render.AbsTileEntitySpecialRenderer;
import club.nsdn.nyasamatelecom.api.render.RendererHelper;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import net.minecraft.util.ResourceLocation;
import cn.ac.nya.forgeobj.WavefrontObject;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class RailTriSwitchRenderer extends AbsTileEntitySpecialRenderer {

    private static final int STRAIGHT = 0, TURNED_L = 1, TURNED_R = 2;

    private final WavefrontObject models[];
    private final ResourceLocation texture;

    public RailTriSwitchRenderer() {
        this.texture = new ResourceLocation("nyasamarailway", "textures/rails/rail_tri_switch_base.png");
        this.models = new WavefrontObject[] {
                new WavefrontObject(new ResourceLocation("nyasamarailway", "models/rails/rail_tri_switch_straight.obj")),
                new WavefrontObject(new ResourceLocation("nyasamarailway", "models/rails/rail_tri_switch_left.obj")),
                new WavefrontObject(new ResourceLocation("nyasamarailway", "models/rails/rail_tri_switch_right.obj"))
        };
    }

    @Override
    public void render(@Nonnull TileEntityBase te, double x, double y, double z, float partialTicks, int destroyStage, float partial) {
        int meta = te.META;

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y  + 0.5F, (float) z + 0.5F);

        //RendererHelper.beginSpecialLighting();

        if (te instanceof RailTriSwitch.TileEntityRailTriSwitch) {
            RailTriSwitch.TileEntityRailTriSwitch triSwitch = (RailTriSwitch.TileEntityRailTriSwitch) te;

            float angle = 0.0F;
            if (triSwitch.direction != null) {
                switch (triSwitch.direction) {
                    case SOUTH:
                        angle = 0.0F;
                        break;
                    case WEST:
                        angle = 90.0F;
                        break;
                    case NORTH:
                        angle = 180.0F;
                        break;
                    case EAST:
                        angle = 270.0F;
                        break;
                }
            }

            int direction = STRAIGHT;
            switch (triSwitch.prevState) {
                case RailTriSwitch.TileEntityRailTriSwitch.STATE_POS:
                    direction = TURNED_L;
                    break;
                case RailTriSwitch.TileEntityRailTriSwitch.STATE_NEG:
                    direction = TURNED_R;
                    break;
                case RailTriSwitch.TileEntityRailTriSwitch.STATE_ZERO:
                    direction = STRAIGHT;
                    break;
            }

            RendererHelper.renderWithResourceAndRotation(models[direction], angle, texture);
        }

        RendererHelper.endSpecialLighting();

        GL11.glPopMatrix();
    }

}
