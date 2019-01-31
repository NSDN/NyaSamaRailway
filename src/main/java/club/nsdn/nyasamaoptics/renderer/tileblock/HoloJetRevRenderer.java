package club.nsdn.nyasamaoptics.renderer.tileblock;

import club.nsdn.nyasamaoptics.tileblock.holo.HoloJetRev;
import club.nsdn.nyasamaoptics.util.font.FontLoader;
import club.nsdn.nyasamatelecom.api.render.AbsTileEntitySpecialRenderer;
import club.nsdn.nyasamatelecom.api.render.RendererHelper;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import cn.ac.nya.forgeobj.WavefrontObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.1.30.
 */
public class HoloJetRevRenderer extends AbsTileEntitySpecialRenderer {

    private ResourceLocation baseTexture, jetTexture, text;
    private WavefrontObject baseModel, jetModel;

    public HoloJetRevRenderer() {
        baseTexture = new ResourceLocation("nyasamaoptics", "textures/blocks/light_shell.png");
        jetTexture = new ResourceLocation("nyasamaoptics", "textures/blocks/light_base.png");
        text = new ResourceLocation("nyasamaoptics", "textures/blocks/white.png");
        baseModel = new WavefrontObject(
                new ResourceLocation("nyasamaoptics", "models/blocks/" + "holo_jet_rev" + "_base.obj")
        );
        jetModel = new WavefrontObject(
                new ResourceLocation("nyasamaoptics", "models/blocks/" + "holo_jet_rev" + "_light.obj")
        );
    }

    @Override
    public void render(@Nonnull TileEntityBase te, double x, double y, double z, float partialTicks, int destroyStage, float partial) {
        int meta = te.META;

        if (!(te instanceof HoloJetRev.TileEntityHoloJetRev)) return;
        HoloJetRev.TileEntityHoloJetRev tileEntityHoloJetRev = (HoloJetRev.TileEntityHoloJetRev) te;
        GL11.glPushMatrix();
        {
            GL11.glTranslatef((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);

            tileEntityHoloJetRev.createModel();

            RenderHelper.disableStandardItemLighting();
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_CULL_FACE);

            if (Minecraft.isAmbientOcclusionEnabled())
            {
                GL11.glShadeModel(GL11.GL_SMOOTH);
            }
            else
            {
                GL11.glShadeModel(GL11.GL_FLAT);
            }

            GL11.glColor3f(1.0F, 1.0F, 1.0F);
            float angle = (meta % 4) * 90.0F;
            GL11.glPushMatrix();
            {
                GL11.glRotatef(angle, 0.0F, -1.0F, 0.0F);
                GL11.glPushMatrix();
                {
                    switch (meta / 4) {
                        case 1:
                            GL11.glRotatef(90.0F, -1.0F, 0.0F, 0.0F);
                            break;
                        case 2:
                            GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
                            break;
                    }
                    RendererHelper.renderWithResource(baseModel, baseTexture);
                    RendererHelper.renderWithResource(jetModel, jetTexture);
                }
                GL11.glPopMatrix();
            }
            GL11.glPopMatrix();

            GL11.glPushMatrix();
            {
                GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(angle, 0.0F, 1.0F, 0.0F);

                Minecraft.getMinecraft().renderEngine.bindTexture(text);
                GL11.glPushMatrix();
                {
                    GL11.glTranslatef(0.0F, -1.5F, 0.0F);
                    GL11.glPushMatrix();
                    {
                        if (tileEntityHoloJetRev.align == FontLoader.ALIGN_UP)
                            GL11.glTranslated(0.0, 0.5 - 2 * tileEntityHoloJetRev.scale * (tileEntityHoloJetRev.content.length() - 1), (double) (16 - tileEntityHoloJetRev.thick) / 32.0);
                        else if (tileEntityHoloJetRev.align == FontLoader.ALIGN_DOWN)
                            GL11.glTranslated(0.0, 2.5 - tileEntityHoloJetRev.scale, (double) (16 - tileEntityHoloJetRev.thick) / 32.0);
                        else
                            GL11.glTranslated(0.0, 1.5 - tileEntityHoloJetRev.scale, (double) (16 - tileEntityHoloJetRev.thick) / 32.0);
                        GL11.glPushMatrix();
                        {
                            GL11.glScaled(tileEntityHoloJetRev.scale, tileEntityHoloJetRev.scale, 1.0);
                            boolean control = true;
                            if (tileEntityHoloJetRev.getSender() != null) control = tileEntityHoloJetRev.isEnabled;
                            if (tileEntityHoloJetRev.model != null && control) {
                                int color = tileEntityHoloJetRev.color;
                                GL11.glColor3f(
                                        ((color & 0xFF0000) >> 16) / 255.0F,
                                        ((color & 0x00FF00) >> 8) / 255.0F,
                                        (color & 0x0000FF) / 255.0F
                                );
                                tileEntityHoloJetRev.model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
                            }
                        }
                        GL11.glPopMatrix();
                    }
                    GL11.glPopMatrix();
                }
                GL11.glPopMatrix();
            }
            GL11.glPopMatrix();

            GL11.glColor3f(1.0F, 1.0F, 1.0F);
            RenderHelper.enableStandardItemLighting();
        }
        GL11.glPopMatrix();
    }

}
