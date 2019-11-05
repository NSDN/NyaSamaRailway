package club.nsdn.nyasamarailway.renderer.tileentity.func;

import club.nsdn.nyasamarailway.api.signal.TileEntityGlassShield;
import club.nsdn.nyasamarailway.tileblock.signal.deco.GlassShield1X1;
import club.nsdn.nyasamatelecom.api.render.AbsTileEntitySpecialRenderer;
import club.nsdn.nyasamatelecom.api.render.RendererHelper;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import cn.ac.nya.forgeobj.WavefrontObject;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class GlassShieldRenderer extends AbsTileEntitySpecialRenderer {

    public static final int SHIELD = 0;
    public static final int SHIELD_HALF = 1;
    public static final int SHIELD_1X1 = 2;
    public static final int SHIELD_3X1 = 3;
    public static final int SHIELD_3X1D5 = 4;
    public static final int SHIELD_1D5X1D5 = 5;
    
    private final WavefrontObject[] modelMain;
    private final ResourceLocation textureMain;
    private final int renderType;

    public static final float ANIMATION_STEP = 4;
    public float MOVE_DIST = 1.0F - (1.0F / 16.0F);

    public GlassShieldRenderer(int renderType) {
        modelMain = new WavefrontObject[] {
                new WavefrontObject(
                        new ResourceLocation("nyasamarailway", "models/blocks/glass_shield.obj")
                ),
                new WavefrontObject(
                        new ResourceLocation("nyasamarailway", "models/blocks/glass_shield_half.obj")
                ),
                new WavefrontObject(
                        new ResourceLocation("nyasamarailway", "models/blocks/glass_shield_1x1.obj")
                ),
                new WavefrontObject(
                        new ResourceLocation("nyasamarailway", "models/blocks/glass_shield_3x1.obj")
                ),
                new WavefrontObject(
                        new ResourceLocation("nyasamarailway", "models/blocks/glass_shield_3x1d5.obj")
                ),
                new WavefrontObject(
                        new ResourceLocation("nyasamarailway", "models/blocks/glass_shield_1d5x1d5.obj")
                )
        };

        textureMain = new ResourceLocation("nyasamarailway", "textures/blocks/glass_shield_main.png");

        this.renderType = renderType;
    }

    public void doInterpolation(TileEntity tileEntity) {
        if (tileEntity instanceof TileEntityGlassShield) {
            TileEntityGlassShield glassShield = (TileEntityGlassShield) tileEntity;

            float max = (float) TileEntityGlassShield.PROGRESS_MAX;
            float dist = ((float) glassShield.progress) / max * MOVE_DIST;

            if (dist != glassShield.prevDist) {
                if (Math.abs(dist - glassShield.prevDist) > 1 / max * MOVE_DIST) {
                    if (dist > glassShield.prevDist) glassShield.prevDist = dist - 1 / max * MOVE_DIST;
                    else glassShield.prevDist = dist + 1 / max * MOVE_DIST;
                }
                if (dist > glassShield.prevDist) glassShield.prevDist += 1 / max * MOVE_DIST / ANIMATION_STEP;
                else if (dist < glassShield.prevDist) glassShield.prevDist -= 1 / max * MOVE_DIST / ANIMATION_STEP;
            }
        }
    }

    @Override
    public void render(@Nonnull TileEntityBase te, double x, double y, double z, float partialTicks, int destroyStage, float partial) {
        int meta = te.META;

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y  + 0.5F, (float) z + 0.5F);
        GL11.glTranslatef(0, -0.00625F, 0);

        //RendererHelper.beginSpecialLighting();

        int angle = (meta & 0x3) * 90;

        if (te instanceof TileEntityGlassShield) {
            TileEntityGlassShield glassShield = (TileEntityGlassShield) te;

            switch (renderType) {
                case SHIELD:
                    MOVE_DIST = 1.0F - (1.0F / 16.0F);
                    doInterpolation(te);

                    GL11.glPushMatrix();
                    GL11.glRotatef(angle, 0.0F, -1.0F, 0.0F);

                    GL11.glPushMatrix();
                    GL11.glTranslatef(glassShield.prevDist, 0.0F, 0.0F);
                    RendererHelper.renderWithResource(modelMain[SHIELD], textureMain);
                    GL11.glPopMatrix();

                    GL11.glPopMatrix();
                    break;
                case SHIELD_HALF:
                    MOVE_DIST = 1.0F - (1.0F / 16.0F);
                    doInterpolation(te);

                    GL11.glPushMatrix();
                    GL11.glRotatef(angle, 0.0F, -1.0F, 0.0F);

                    GL11.glPushMatrix();
                    GL11.glTranslatef(glassShield.prevDist, 0.0F, 0.0F);
                    RendererHelper.renderWithResource(modelMain[SHIELD_HALF], textureMain);
                    GL11.glPopMatrix();

                    GL11.glPopMatrix();
                    break;
                case SHIELD_1X1:
                    MOVE_DIST = 1.0F - (1.0F / 16.0F);

                    if (te instanceof GlassShield1X1.TileEntityGlassShield1X1) {
                        GlassShield1X1.TileEntityGlassShield1X1 shield1X1 = (GlassShield1X1.TileEntityGlassShield1X1) te;
                        World W = te.getWorld(); BlockPos POS = te.getPos();

                        if (!shield1X1.checkShield(W, POS.down()) && !shield1X1.checkShield(W, POS.down(2))) {
                            doInterpolation(te);

                            GL11.glPushMatrix();
                            GL11.glRotatef(angle, 0.0F, -1.0F, 0.0F);

                            GL11.glPushMatrix();
                            GL11.glTranslatef(glassShield.prevDist, 0.0F, 0.0F);
                            RendererHelper.renderWithResource(modelMain[SHIELD_1X1], textureMain);
                            GL11.glPopMatrix();

                            GL11.glPopMatrix();
                        }
                    }
                    break;
                case SHIELD_3X1:
                    MOVE_DIST = 1.0F - (1.0F / 16.0F);
                    doInterpolation(te);

                    GL11.glPushMatrix();
                    GL11.glRotatef(angle, 0.0F, -1.0F, 0.0F);
                    GL11.glPushMatrix();
                    GL11.glTranslatef(0.0F, 1.0F, 0.0F);

                    GL11.glPushMatrix();
                    GL11.glTranslatef(glassShield.prevDist, 0.0F, 0.0F);
                    RendererHelper.renderWithResource(modelMain[SHIELD_3X1], textureMain);
                    GL11.glPopMatrix();

                    GL11.glPopMatrix();
                    GL11.glPopMatrix();
                    break;
                case SHIELD_3X1D5:
                    MOVE_DIST = 1.5F - (1.0F / 16.0F);
                    doInterpolation(te);

                    GL11.glPushMatrix();
                    GL11.glRotatef(angle, 0.0F, -1.0F, 0.0F);
                    GL11.glPushMatrix();
                    GL11.glTranslatef(0.0F, 1.0F, 0.0F);

                    GL11.glPushMatrix();
                    GL11.glTranslatef(glassShield.prevDist, 0.0F, 0.0F);
                    RendererHelper.renderWithResource(modelMain[SHIELD_3X1D5], textureMain);
                    GL11.glPopMatrix();

                    GL11.glPopMatrix();
                    GL11.glPopMatrix();
                    break;
                case SHIELD_1D5X1D5:
                    MOVE_DIST = 1.5F - (1.0F / 16.0F);
                    doInterpolation(te);

                    GL11.glPushMatrix();
                    GL11.glRotatef(angle, 0.0F, -1.0F, 0.0F);

                    GL11.glPushMatrix();
                    GL11.glTranslatef(glassShield.prevDist, 0.0F, 0.0F);
                    RendererHelper.renderWithResource(modelMain[SHIELD_1D5X1D5], textureMain);
                    GL11.glPopMatrix();

                    GL11.glPopMatrix();
                    break;
                default:
                    break;
            }
        }

        RendererHelper.endSpecialLighting();

        GL11.glPopMatrix();
    }

}
