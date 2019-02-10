package club.nsdn.nyasamarailway.renderer.tileentity.rail;

import club.nsdn.nyasamatelecom.api.render.AbsTileEntitySpecialRenderer;
import club.nsdn.nyasamatelecom.api.render.RendererHelper;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import net.minecraft.block.BlockSlab;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import cn.ac.nya.forgeobj.WavefrontObject;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class Rail3rdRenderer extends AbsTileEntitySpecialRenderer {

    private static final int STRAIGHT = 0, SLOPE = 1, TURNED = 2;

    private final WavefrontObject[] model;
    private final WavefrontObject[] eleModel;
    private final WavefrontObject pillarModel;
    private final ResourceLocation texture;
    private final ResourceLocation eleTexture;

    public Rail3rdRenderer() {
        this.model = new WavefrontObject[] {
                new WavefrontObject(new ResourceLocation("nyasamarailway", "models/rails/trd_rail_s.obj")),
                new WavefrontObject(new ResourceLocation("nyasamarailway", "models/rails/trd_rail_l.obj")),
                new WavefrontObject(new ResourceLocation("nyasamarailway", "models/rails/trd_rail_t.obj"))
        };
        this.eleModel = new WavefrontObject[] {
                new WavefrontObject(new ResourceLocation("nyasamarailway", "models/rails/trd_rail_se.obj")),
                new WavefrontObject(new ResourceLocation("nyasamarailway", "models/rails/trd_rail_le.obj")),
                new WavefrontObject(new ResourceLocation("nyasamarailway", "models/rails/trd_rail_te.obj"))
        };
        this.pillarModel = new WavefrontObject(new ResourceLocation("nyasamarailway", "models/rails/trd_rail_p.obj"));

        this.texture = new ResourceLocation("nyasamarailway", "textures/rails/trd_rail_base.png");
        this.eleTexture = new ResourceLocation("nyasamarailway", "textures/rails/trd_rail_ele.png");
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


        switch (te.getBlockMetadata()) {
            case 0: //N=S
                RendererHelper.renderWithResourceAndRotation(this.model[STRAIGHT], 0.0F, texture);
                RendererHelper.renderWithResourceAndRotation(this.eleModel[STRAIGHT], 0.0F, eleTexture);
                if (te.getWorld().getBlockState(te.getPos().down()).getBlock() instanceof BlockSlab)
                    RendererHelper.renderWithResourceAndRotation(this.pillarModel, 0.0F, eleTexture);
                break;
            case 1: //W=E
                RendererHelper.renderWithResourceAndRotation(this.model[STRAIGHT], 90.0F, texture);
                RendererHelper.renderWithResourceAndRotation(this.eleModel[STRAIGHT], 90.0F, eleTexture);
                if (te.getWorld().getBlockState(te.getPos().down()).getBlock() instanceof BlockSlab)
                    RendererHelper.renderWithResourceAndRotation(this.pillarModel, 0.0F, eleTexture);
                break;
            case 2: //E
                RendererHelper.renderWithResourceAndRotation(this.model[SLOPE], -90.0F, texture);
                RendererHelper.renderWithResourceAndRotation(this.eleModel[SLOPE], -90.0F, eleTexture);
                break;
            case 3: //W
                RendererHelper.renderWithResourceAndRotation(this.model[SLOPE], 90.0F, texture);
                RendererHelper.renderWithResourceAndRotation(this.eleModel[SLOPE], 90.0F, eleTexture);
                break;
            case 4: //N
                RendererHelper.renderWithResourceAndRotation(this.model[SLOPE], 180.0F, texture);
                RendererHelper.renderWithResourceAndRotation(this.eleModel[SLOPE], 180.0F, eleTexture);
                break;
            case 5: //S
                RendererHelper.renderWithResourceAndRotation(this.model[SLOPE], 0.0F, texture);
                RendererHelper.renderWithResourceAndRotation(this.eleModel[SLOPE], 0.0F, eleTexture);
                break;
            case 6: //S-E
                RendererHelper.renderWithResourceAndRotation(this.model[TURNED], 180.0F, texture);
                RendererHelper.renderWithResourceAndRotation(this.eleModel[TURNED], 180.0F, eleTexture);
                break;
            case 7: //S-W
                RendererHelper.renderWithResourceAndRotation(this.model[TURNED], -90.0F, texture);
                RendererHelper.renderWithResourceAndRotation(this.eleModel[TURNED], -90.0F, eleTexture);
                break;
            case 8: //N-W
                RendererHelper.renderWithResourceAndRotation(this.model[TURNED], 0.0F, texture);
                RendererHelper.renderWithResourceAndRotation(this.eleModel[TURNED], 0.0F, eleTexture);
                break;
            case 9: //N-E
                RendererHelper.renderWithResourceAndRotation(this.model[TURNED], 90.0F, texture);
                RendererHelper.renderWithResourceAndRotation(this.eleModel[TURNED], 90.0F, eleTexture);
                break;
        }

        RenderHelper.enableStandardItemLighting();

        GL11.glPopMatrix();
    }

}
