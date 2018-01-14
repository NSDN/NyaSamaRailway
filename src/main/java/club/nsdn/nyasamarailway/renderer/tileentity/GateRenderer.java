package club.nsdn.nyasamarailway.renderer.tileentity;

import club.nsdn.nyasamarailway.renderer.RendererHelper;
import club.nsdn.nyasamarailway.tileblock.functional.BlockGateDoor;
import club.nsdn.nyasamarailway.tileblock.signal.block.BlockGateFront;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.WavefrontObject;
import org.lwjgl.opengl.GL11;

/**
 * Created by drzzm32 on 2017.9.4.
 */
public class GateRenderer extends TileEntitySpecialRenderer {

    private final WavefrontObject modelBase;
    private final WavefrontObject modelFrontMain;
    private final WavefrontObject modelDoorAxis;
    private final WavefrontObject modelDoorLeft;
    private final WavefrontObject modelDoorRight;

    private static final int LIGHT_R = 0;
    private static final int LIGHT_G = 1;
    private final WavefrontObject modelLight[];

    private static final int SCREEN_TOP = 0;
    private static final int SCREEN_1 = 1;
    private static final int SCREEN_2 = 2;
    private static final int SCREEN_3 = 3;
    private final WavefrontObject modelScreen[];

    private final ResourceLocation textureMain;

    private static final int TEXT_E = 10;
    private static final int TEXT_R = 11;
    private static final int TEXT_SUB = 12;
    private static final int TEXT_N = 13;
    private static final int TEXT_U = 14;
    private static final int TEXT_L = 15;
    private final ResourceLocation textureText[];

    private final WavefrontObject modelBaseTop;
    private final ResourceLocation textureBaseTop;

    public static final int GATE_BASE = 0;
    public static final int GATE_DOOR = 1;
    public static final int GATE_FRONT_N = 2;
    public static final int GATE_FRONT = 3;
    private final int renderType;

    public static final float ANIMATION_STEP = 10;

    public GateRenderer(int renderType) {
        modelBase = new WavefrontObject(
                new ResourceLocation("nyasamarailway", "models/blocks/gate/gate_base.obj")
        );
        modelFrontMain = new WavefrontObject(
                new ResourceLocation("nyasamarailway", "models/blocks/gate/gate_front_main.obj")
        );
        modelDoorAxis = new WavefrontObject(
                new ResourceLocation("nyasamarailway", "models/blocks/gate/gate_door_axis.obj")
        );
        modelDoorLeft = new WavefrontObject(
                new ResourceLocation("nyasamarailway", "models/blocks/gate/gate_door_left.obj")
        );
        modelDoorRight = new WavefrontObject(
                new ResourceLocation("nyasamarailway", "models/blocks/gate/gate_door_right.obj")
        );

        modelLight = new WavefrontObject[] {
                new WavefrontObject(
                        new ResourceLocation("nyasamarailway", "models/blocks/gate/gate_front_light_r.obj")
                ),
                new WavefrontObject(
                        new ResourceLocation("nyasamarailway", "models/blocks/gate/gate_front_light_g.obj")
                )
        };

        modelScreen = new WavefrontObject[] {
                new WavefrontObject(
                        new ResourceLocation("nyasamarailway", "models/blocks/gate/gate_screen_info.obj")
                ),
                new WavefrontObject(
                        new ResourceLocation("nyasamarailway", "models/blocks/gate/gate_screen_1.obj")
                ),
                new WavefrontObject(
                        new ResourceLocation("nyasamarailway", "models/blocks/gate/gate_screen_2.obj")
                ),
                new WavefrontObject(
                        new ResourceLocation("nyasamarailway", "models/blocks/gate/gate_screen_3.obj")
                )
        };

        textureMain = new ResourceLocation("nyasamarailway", "textures/blocks/gate/gate_main.png");

        textureText = new ResourceLocation[16];
        for (int i = 0; i < 10; i++)
            textureText[i] = new ResourceLocation("nyasamarailway", "textures/blocks/gate/gate_text_" + i + ".png");
        textureText[TEXT_E] = new ResourceLocation("nyasamarailway", "textures/blocks/gate/gate_text_e.png");
        textureText[TEXT_R] = new ResourceLocation("nyasamarailway", "textures/blocks/gate/gate_text_r.png");
        textureText[TEXT_SUB] = new ResourceLocation("nyasamarailway", "textures/blocks/gate/gate_text_sub.png");
        textureText[TEXT_N] = new ResourceLocation("nyasamarailway", "textures/blocks/gate/gate_text_n.png");
        textureText[TEXT_U] = new ResourceLocation("nyasamarailway", "textures/blocks/gate/gate_text_u.png");
        textureText[TEXT_L] = new ResourceLocation("nyasamarailway", "textures/blocks/gate/gate_text_l.png");

        modelBaseTop = new WavefrontObject(
                new ResourceLocation("nyasamarailway", "models/blocks/gate/gate_base_top.obj")
        );
        textureBaseTop = new ResourceLocation("nyasamarailway", "textures/blocks/gate/gate_base_top.png");

        this.renderType = renderType;
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

        int meta = te.getWorldObj().getBlockMetadata(te.xCoord, te.yCoord, te.zCoord);
        int angle = (meta & 0x3) * 90;

        switch (renderType) {
            case GATE_BASE:
                RendererHelper.renderWithResourceAndRotation(modelBase, angle, textureMain);
                RendererHelper.renderWithResourceAndRotation(modelBaseTop, angle, textureBaseTop);
                break;
            case GATE_DOOR:
                if (te instanceof BlockGateDoor.GateDoor) {
                    BlockGateDoor.GateDoor gateDoor = (BlockGateDoor.GateDoor) te;

                    float max = (float) BlockGateDoor.GateDoor.PROGRESS_MAX;
                    float dist = ((float) gateDoor.progress) / max * 0.5F;

                    if (dist != gateDoor.prevDist) {
                        if (Math.abs(dist - gateDoor.prevDist) > 1 / max * 0.5F) {
                            if (dist > gateDoor.prevDist) gateDoor.prevDist = dist - 1 / max * 0.5F;
                            else gateDoor.prevDist = dist + 1 / max * 0.5F;
                        }
                        if (dist > gateDoor.prevDist) gateDoor.prevDist += 1 / max * 0.5F / ANIMATION_STEP;
                        else if (dist < gateDoor.prevDist) gateDoor.prevDist -= 1 / max * 0.5F / ANIMATION_STEP;
                    }

                    RendererHelper.renderWithResourceAndRotation(modelDoorAxis, angle, textureMain);
                    GL11.glPushMatrix();
                    GL11.glRotatef(angle, 0.0F, -1.0F, 0.0F);

                    GL11.glPushMatrix();
                    GL11.glTranslatef(gateDoor.prevDist, 0.0F, 0.0F);
                    RendererHelper.renderWithResource(modelDoorLeft, textureMain);
                    GL11.glPopMatrix();

                    GL11.glPushMatrix();
                    GL11.glTranslatef(-gateDoor.prevDist, 0.0F, 0.0F);
                    RendererHelper.renderWithResource(modelDoorRight, textureMain);
                    GL11.glPopMatrix();

                    GL11.glPopMatrix();
                }
                break;
            case GATE_FRONT_N:
                RendererHelper.renderWithResourceAndRotation(modelFrontMain, angle, textureMain);
                break;
            case GATE_FRONT:
                if (te instanceof BlockGateFront.GateFront) {
                    boolean isEnabled = ((BlockGateFront.GateFront) te).isEnabled;
                    int over = ((BlockGateFront.GateFront) te).over;

                    RendererHelper.renderWithResourceAndRotation(modelFrontMain, angle, textureMain);
                    RendererHelper.renderWithResourceAndRotation(modelScreen[SCREEN_TOP], angle, textureMain);
                    RendererHelper.renderWithResourceAndRotation(modelLight[isEnabled ? LIGHT_G : LIGHT_R], angle, textureMain);

                    if (over == -1) {
                        RendererHelper.renderWithResourceAndRotation(modelScreen[SCREEN_1], angle, textureText[TEXT_SUB]);
                        RendererHelper.renderWithResourceAndRotation(modelScreen[SCREEN_2], angle, textureText[TEXT_SUB]);
                        RendererHelper.renderWithResourceAndRotation(modelScreen[SCREEN_3], angle, textureText[TEXT_SUB]);
                    } else if (over == -2) {
                        RendererHelper.renderWithResourceAndRotation(modelScreen[SCREEN_1], angle, textureText[TEXT_N]);
                        RendererHelper.renderWithResourceAndRotation(modelScreen[SCREEN_2], angle, textureText[TEXT_U]);
                        RendererHelper.renderWithResourceAndRotation(modelScreen[SCREEN_3], angle, textureText[TEXT_L]);
                    } else {
                        if (over > 999) over = 999;
                        RendererHelper.renderWithResourceAndRotation(modelScreen[SCREEN_1], angle, textureText[over / 100]);
                        RendererHelper.renderWithResourceAndRotation(modelScreen[SCREEN_2], angle, textureText[(over % 100) / 10]);
                        RendererHelper.renderWithResourceAndRotation(modelScreen[SCREEN_3], angle, textureText[over % 10]);
                    }
                }
                break;
            default:
                break;
        }

        RenderHelper.enableStandardItemLighting();

        GL11.glPopMatrix();
    }

}
