package club.nsdn.nyasamarailway.renderer.tileentity.rail;

import club.nsdn.nyasamarailway.api.rail.AbsRailBase;
import club.nsdn.nyasamarailway.api.rail.IMonoSwitch;
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
public class MonoRailRenderer extends AbsTileEntitySpecialRenderer {

    private static final int STRAIGHT = 0, SLOPE = 1, TURNED = 2;

    private final WavefrontObject[] model;
    private final ResourceLocation[] textures;

    private final boolean isMagnet;

    public MonoRailRenderer() {
        this.model  = new WavefrontObject[] {
                new WavefrontObject(new ResourceLocation("nyasamarailway", "models/rails/mono_rail_straight.obj")),
                new WavefrontObject(new ResourceLocation("nyasamarailway", "models/rails/mono_rail_slope.obj")),
                new WavefrontObject(new ResourceLocation("nyasamarailway", "models/rails/mono_rail_turned.obj"))
        };
        this.textures = new ResourceLocation[] {
                new ResourceLocation("nyasamarailway", "textures/rails/mono_rail.png"),
                new ResourceLocation("nyasamarailway", "textures/rails/mono_rail.png"),
                new ResourceLocation("nyasamarailway", "textures/rails/mono_rail.png")
        };
        this.isMagnet = false;
    }

    public MonoRailRenderer(String[] texturePath) {
        this.model  = new WavefrontObject[] {
                new WavefrontObject(new ResourceLocation("nyasamarailway", "models/rails/mono_rail_straight_magnet.obj")),
                new WavefrontObject(new ResourceLocation("nyasamarailway", "models/rails/mono_rail_slope_magnet.obj")),
                new WavefrontObject(new ResourceLocation("nyasamarailway", "models/rails/mono_rail_turned_magnet.obj"))
        };
        this.textures = new ResourceLocation[3];
        for (int i = 0; i < 3; i++) {
            textures[i] = new ResourceLocation("nyasamarailway", texturePath[i]);
        }
        this.isMagnet = true;
    }

    @Override
    public void render(@Nonnull TileEntityBase te, double x, double y, double z, float partialTicks, int destroyStage, float partial) {
        int meta = te.META;

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y  + 0.5F, (float) z + 0.5F);

        //RendererHelper.beginSpecialLighting();

        GL11.glPushMatrix();
        if (isMagnet &&
                !(te.getWorld().getBlockState(te.getPos().down()).getBlock() instanceof AbsRailBase) &&
                !(te.getWorld().getTileEntity(te.getPos().down()) instanceof IMonoSwitch)) {
            GL11.glTranslatef(0.0F, 0.25F, 0.0F);
            GL11.glTranslatef(0.0F, 0.00625F, 0.0F);
        }

        switch (te.getBlockMetadata()) {
            case 0: //N=S
                RendererHelper.renderWithResourceAndRotation(this.model[STRAIGHT], 0.0F, textures[STRAIGHT]);
                break;
            case 1: //W=E
                RendererHelper.renderWithResourceAndRotation(this.model[STRAIGHT], 90.0F, textures[STRAIGHT]);
                break;
            case 2: //E
                RendererHelper.renderWithResourceAndRotation(this.model[SLOPE], -90.0F, textures[SLOPE]);
                break;
            case 3: //W
                RendererHelper.renderWithResourceAndRotation(this.model[SLOPE], 90.0F, textures[SLOPE]);
                break;
            case 4: //N
                RendererHelper.renderWithResourceAndRotation(this.model[SLOPE], 180.0F, textures[SLOPE]);
                break;
            case 5: //S
                RendererHelper.renderWithResourceAndRotation(this.model[SLOPE], 0.0F, textures[SLOPE]);
                break;
            case 6: //S-E
                RendererHelper.renderWithResourceAndRotation(this.model[TURNED], 180.0F, textures[TURNED]);
                break;
            case 7: //S-W
                RendererHelper.renderWithResourceAndRotation(this.model[TURNED], -90.0F, textures[TURNED]);
                break;
            case 8: //N-W
                RendererHelper.renderWithResourceAndRotation(this.model[TURNED], 0.0F, textures[TURNED]);
                break;
            case 9: //N-E
                RendererHelper.renderWithResourceAndRotation(this.model[TURNED], 90.0F, textures[TURNED]);
                break;
        }

        GL11.glPopMatrix();

        //RendererHelper.endSpecialLighting();

        GL11.glPopMatrix();
    }

}
