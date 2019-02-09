package club.nsdn.nyasamarailway.renderer.tileentity.func;

import club.nsdn.nyasamarailway.api.signal.TileEntityGlassShield;
import club.nsdn.nyasamarailway.tileblock.signal.deco.GlassShield1X1;
import club.nsdn.nyasamatelecom.api.render.AbsTileEntitySpecialRenderer;
import club.nsdn.nyasamatelecom.api.render.RendererHelper;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import cn.ac.nya.forgeobj.WavefrontObject;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2017.9.6.
 */
public class GlassShieldRenderer extends AbsTileEntitySpecialRenderer {

    private static final int MODEL_NORMAL = 0;
    private static final int MODEL_HALF = 1;
    private static final int MODEL_1X1 = 2;
    private static final int MODEL_3X1 = 3;
    private static final int MODEL_3X1D5 = 4;
    private static final int MODEL_1D5X1D5 = 5;
    private static final int MODEL_ALBASE = 2;
    private final WavefrontObject[] modelMain;
    private final WavefrontObject[] modelMainAl;
    private final WavefrontObject[] modelCorner;

    private final ResourceLocation textureMain;
    private final ResourceLocation textureMainAl;
    private final ResourceLocation textureCorner;

    public static final int SHIELD = 0;
    public static final int SHIELD_HALF = 1;
    public static final int SHIELD_1X1 = 2;
    public static final int SHIELD_3X1 = 3;
    public static final int SHIELD_3X1D5 = 4;
    public static final int SHIELD_AL = 5;
    public static final int SHIELD_AL_HALF = 6;
    public static final int SHIELD_AL_BASE = 7;
    public static final int SHIELD_CORNER = 8;
    public static final int SHIELD_CORNER_HALF = 9;
    public static final int SHIELD_1D5X1D5 = 10;
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
        modelMainAl = new WavefrontObject[] {
                new WavefrontObject(
                        new ResourceLocation("nyasamarailway", "models/blocks/glass_shield_al.obj")
                ),
                new WavefrontObject(
                        new ResourceLocation("nyasamarailway", "models/blocks/glass_shield_al_half.obj")
                ),
                new WavefrontObject(
                        new ResourceLocation("nyasamarailway", "models/blocks/glass_shield_albase.obj")
                )
        };
        modelCorner = new WavefrontObject[] {
                new WavefrontObject(
                        new ResourceLocation("nyasamarailway", "models/blocks/glass_shield_corner.obj")
                ),
                new WavefrontObject(
                        new ResourceLocation("nyasamarailway", "models/blocks/glass_shield_corner_half.obj")
                )
        };

        textureMain = new ResourceLocation("nyasamarailway", "textures/blocks/glass_shield_main.png");
        textureMainAl = new ResourceLocation("nyasamarailway", "textures/blocks/glass_shield_al_main.png");
        textureCorner = new ResourceLocation("nyasamarailway", "textures/blocks/glass_shield_corner.png");

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

        RenderHelper.disableStandardItemLighting();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_CULL_FACE);

        if (Minecraft.isAmbientOcclusionEnabled()) {
            GL11.glShadeModel(GL11.GL_SMOOTH);
        } else {
            GL11.glShadeModel(GL11.GL_FLAT);
        }

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
                    RendererHelper.renderWithResource(modelMain[MODEL_NORMAL], textureMain);
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
                    RendererHelper.renderWithResource(modelMain[MODEL_HALF], textureMain);
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
                            RendererHelper.renderWithResource(modelMain[MODEL_1X1], textureMain);
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
                    RendererHelper.renderWithResource(modelMain[MODEL_3X1], textureMain);
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
                    RendererHelper.renderWithResource(modelMain[MODEL_3X1D5], textureMain);
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
                    RendererHelper.renderWithResource(modelMain[MODEL_1D5X1D5], textureMain);
                    GL11.glPopMatrix();

                    GL11.glPopMatrix();
                    break;
                default:
                    break;
            }
        } else {
            switch (renderType) {
                case SHIELD_AL:
                    GL11.glPushMatrix();
                    GL11.glRotatef(angle, 0.0F, -1.0F, 0.0F);
                    GL11.glPushMatrix();
                    GL11.glScalef(1.0F, 1.0F, 1.5F);
                    RendererHelper.renderWithResource(modelMainAl[MODEL_NORMAL], textureMainAl);
                    GL11.glPopMatrix();
                    GL11.glPopMatrix();
                    break;
                case SHIELD_AL_HALF:
                    GL11.glPushMatrix();
                    GL11.glRotatef(angle, 0.0F, -1.0F, 0.0F);
                    GL11.glPushMatrix();
                    GL11.glScalef(1.0F, 1.0F, 1.5F);
                    RendererHelper.renderWithResource(modelMainAl[MODEL_HALF], textureMainAl);
                    GL11.glPopMatrix();
                    GL11.glPopMatrix();
                    break;
                case SHIELD_AL_BASE:
                    GL11.glPushMatrix();
                    GL11.glRotatef(angle, 0.0F, -1.0F, 0.0F);
                    GL11.glPushMatrix();
                    GL11.glScalef(1.0F, 1.0F, 1.5F);
                    RendererHelper.renderWithResource(modelMainAl[MODEL_ALBASE], textureMainAl);
                    GL11.glPopMatrix();
                    GL11.glPopMatrix();
                    break;
                case SHIELD_CORNER:
                    RendererHelper.renderWithResourceAndRotation(modelCorner[MODEL_NORMAL], angle, textureCorner);
                    break;
                case SHIELD_CORNER_HALF:
                    RendererHelper.renderWithResourceAndRotation(modelCorner[MODEL_HALF], angle, textureCorner);
                    break;
                default:
                    break;
            }
        }

        RenderHelper.enableStandardItemLighting();

        GL11.glPopMatrix();
    }

}
