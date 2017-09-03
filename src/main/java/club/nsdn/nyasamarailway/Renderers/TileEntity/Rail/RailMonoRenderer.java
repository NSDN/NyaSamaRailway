package club.nsdn.nyasamarailway.Renderers.TileEntity.Rail;

/**
 * Created by drzzm32 on 2016.11.29.
 */

import club.nsdn.nyasamarailway.Renderers.RendererHelper;
import club.nsdn.nyasamarailway.TileEntities.Rail.RailMonoMagnetPowerable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.obj.WavefrontObject;
import org.lwjgl.opengl.GL11;

public class RailMonoRenderer extends TileEntitySpecialRenderer {

    private static final int STRAIGHT = 0, SLOPE = 1, TURNED = 2;
    private static final int STRAIGHT_P = 3, SLOPE_P = 4, TURNED_P = 5;

    private final WavefrontObject[] model;
    private final ResourceLocation[] textures;

    private final boolean isMagnet;

    public RailMonoRenderer() {
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

    public RailMonoRenderer(String[] texturePath) {
        this.model  = new WavefrontObject[] {
                new WavefrontObject(new ResourceLocation("nyasamarailway", "models/rails/mono_rail_straight_magnet.obj")),
                new WavefrontObject(new ResourceLocation("nyasamarailway", "models/rails/mono_rail_slope_magnet.obj")),
                new WavefrontObject(new ResourceLocation("nyasamarailway", "models/rails/mono_rail_turned_magnet.obj"))
        };
        this.textures = new ResourceLocation[6];
        for (int i = 0; i < 3; i++) {
            textures[i] = new ResourceLocation("nyasamarailway", texturePath[i]);
        }
        for (int i = 3; i < 6; i++) {
            textures[i] = new ResourceLocation("nyasamarailway", texturePath[i - 3].replace(".png", "_powered.png"));
        }
        this.isMagnet = true;
    }

    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float scale) {
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

        Tessellator.instance.setColorOpaque_F(1.0F, 1.0F, 1.0F);

        if (te instanceof RailMonoMagnetPowerable) {
            int meta = te.getBlockMetadata();
            if (meta >= 8) {
                switch (meta & 7) {
                    case 0: //N=S
                        RendererHelper.renderWithResourceAndRotation(this.model[STRAIGHT], 0.0F, textures[STRAIGHT_P]);
                        break;
                    case 1: //W=E
                        RendererHelper.renderWithResourceAndRotation(this.model[STRAIGHT], 90.0F, textures[STRAIGHT_P]);
                        break;
                    case 2: //E
                        RendererHelper.renderWithResourceAndRotation(this.model[SLOPE], -90.0F, textures[SLOPE_P]);
                        break;
                    case 3: //W
                        RendererHelper.renderWithResourceAndRotation(this.model[SLOPE], 90.0F, textures[SLOPE_P]);
                        break;
                    case 4: //N
                        RendererHelper.renderWithResourceAndRotation(this.model[SLOPE], 180.0F, textures[SLOPE_P]);
                        break;
                    case 5: //S
                        RendererHelper.renderWithResourceAndRotation(this.model[SLOPE], 0.0F, textures[SLOPE_P]);
                        break;
                }
            } else {
                switch (te.getBlockMetadata() & 7) {
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
                }
            }
        } else {
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
        }

        RenderHelper.enableStandardItemLighting();

        GL11.glPopMatrix();
    }

}
