package club.nsdn.nyasamaoptics.renderer.tileblock;

import club.nsdn.nyasamaoptics.tileblock.holo.PillarHead;
import club.nsdn.nyasamatelecom.api.render.AbsTileEntitySpecialRenderer;
import club.nsdn.nyasamatelecom.api.render.RendererHelper;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import cn.ac.nya.forgeobj.WavefrontObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.1.30.
 */
public class PillarHeadRenderer extends AbsTileEntitySpecialRenderer {

    private ResourceLocation baseTexture, text;
    private WavefrontObject baseModel;

    public PillarHeadRenderer() {
        baseTexture = new ResourceLocation("nyasamaoptics", "textures/blocks/light_shell.png");
        text = new ResourceLocation("nyasamaoptics", "textures/blocks/white.png");
        baseModel = new WavefrontObject(
                new ResourceLocation("nyasamaoptics", "models/blocks/" + "pillar_head" + "_base.obj")
        );
    }

    @Override
    public void render(@Nonnull TileEntityBase te, double x, double y, double z, float partialTicks, int destroyStage, float partial) {
        int meta = te.META;

        if (!(te instanceof PillarHead.TileEntityPillarHead)) return;
        PillarHead.TileEntityPillarHead tileEntityPillarHead = (PillarHead.TileEntityPillarHead) te;
        GL11.glPushMatrix();
        {
            GL11.glTranslatef((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);

            tileEntityPillarHead.createModel();

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


            float angle = (meta % 4) * 90.0F;
            GL11.glColor3f(1.0F, 1.0F, 1.0F);
            RendererHelper.renderWithResourceAndRotation(baseModel, angle, baseTexture);

            GL11.glPushMatrix();
            {
                GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(angle, 0.0F, 1.0F, 0.0F);

                Minecraft.getMinecraft().renderEngine.bindTexture(text);
                GL11.glPushMatrix();
                {
                    GL11.glTranslatef(0.0F, 0.5F, 0.0F);
                    GL11.glPushMatrix();
                    {
                        GL11.glScaled(tileEntityPillarHead.scale, tileEntityPillarHead.scale, 1.0);
                        GL11.glPushMatrix();
                        {
                            doRenderText(tileEntityPillarHead);
                            GL11.glRotated(180.0, 0.0, 1.0, 0.0);
                            doRenderText(tileEntityPillarHead);
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

    private void doRenderText(PillarHead.TileEntityPillarHead tileEntityPillarHead) {
        boolean control = true;
        if (tileEntityPillarHead.getSender() != null) control = tileEntityPillarHead.isEnabled;
        if (tileEntityPillarHead.model != null && control) {
            int color = tileEntityPillarHead.color;
            GL11.glColor3f(
                    ((color & 0xFF0000) >> 16) / 255.0F,
                    ((color & 0x00FF00) >> 8) / 255.0F,
                    (color & 0x0000FF) / 255.0F
            );
            GL11.glPushMatrix();
            GL11.glTranslated(0.0, 0.0, 0.0625 * (3.9 - tileEntityPillarHead.thick));
            tileEntityPillarHead.model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
            GL11.glPopMatrix();
        }
    }

}
