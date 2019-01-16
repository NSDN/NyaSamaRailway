package club.nsdn.nyasamatelecom.renderer;

import club.nsdn.nyasamatelecom.api.device.TriStateSignalBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.WavefrontObject;
import org.lwjgl.opengl.GL11;

/**
 * Created by drzzm32 on 2017.12.28.
 */
public class TriStateSignalBoxRenderer extends TileEntitySpecialRenderer {

    private final WavefrontObject modelBase = new WavefrontObject(
            new ResourceLocation("nyasamatelecom", "models/blocks/signal_box_base.obj")
    );
    private final ResourceLocation textureBase = new ResourceLocation("nyasamatelecom", "textures/blocks/tri_state_signal_box_base.png");

    private final WavefrontObject models[] = {
            new WavefrontObject(new ResourceLocation("nyasamatelecom", "models/blocks/signal_box_sign1.obj")),
            new WavefrontObject(new ResourceLocation("nyasamatelecom", "models/blocks/signal_box_sign2.obj")),
            new WavefrontObject(new ResourceLocation("nyasamatelecom", "models/blocks/signal_box_sign3.obj")),
            new WavefrontObject(new ResourceLocation("nyasamatelecom", "models/blocks/signal_box_sign4.obj"))
    };
    private final ResourceLocation textures[] = {
            new ResourceLocation("nyasamatelecom", "textures/blocks/signal_box_r.png"),
            new ResourceLocation("nyasamatelecom", "textures/blocks/signal_box_y.png"),
            new ResourceLocation("nyasamatelecom", "textures/blocks/signal_box_g.png"),
            new ResourceLocation("nyasamatelecom", "textures/blocks/signal_box_none.png"),
            new ResourceLocation("nyasamatelecom", "textures/blocks/signal_box_w.png"),
            new ResourceLocation("nyasamatelecom", "textures/blocks/signal_box_b.png")
    };
    private static final int SIGN_R = 0;
    private static final int SIGN_Y = 1;
    private static final int SIGN_G = 2;
    private static final int SIGN_NONE = 3;
    private static final int SIGN_W = 4;
    private static final int SIGN_B = 5;

    private static final int LIGHT_SGN = 0;
    private static final int LIGHT_TXD = 1;
    private static final int LIGHT_RXD = 2;
    private static final int LIGHT_TRI = 3;

    public TriStateSignalBoxRenderer() {
    }

    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float scale) {
        int meta = te.getBlockMetadata();

        boolean txState = false;
        boolean rxState = false;
        boolean sgnState = (meta & 0x8) != 0;
        boolean isEnabled = false;

        boolean inverted = false;

        boolean triStateIsNeg = false;

        if (te instanceof TriStateSignalBox.TileEntityTriStateSignalBox) {
            txState = ((TriStateSignalBox.TileEntityTriStateSignalBox) te).getTarget() != null;
            rxState = ((TriStateSignalBox.TileEntityTriStateSignalBox) te).getSender() != null;
            inverted = ((TriStateSignalBox.TileEntityTriStateSignalBox) te).inverterEnabled;
            triStateIsNeg = ((TriStateSignalBox.TileEntityTriStateSignalBox) te).triStateIsNeg;
        }

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

        GL11.glPushMatrix();

        switch (meta & 0x7) {
            case 0:
                GL11.glRotatef(0, 0.0F, -1.0F, 0.0F);
                break;
            case 1:
                GL11.glRotatef(90, 0.0F, -1.0F, 0.0F);
                break;
            case 2:
                GL11.glRotatef(180, 0.0F, -1.0F, 0.0F);
                break;
            case 3:
                GL11.glRotatef(270, 0.0F, -1.0F, 0.0F);
                break;

            case 4:
                GL11.glTranslatef(0.0F, 1.0F, 0.0F);
                GL11.glRotatef(90.0F, -1.0F, 0.0F, 0.0F);
                GL11.glTranslatef(0.0F, -1.0F, 0.0F);
                GL11.glRotatef(0, 0.0F, -1.0F, 0.0F);
                GL11.glTranslatef(0.0F, 1.0F, -1.0F);
                break;
            case 5:
                GL11.glTranslatef(0.0F, 1.0F, 0.0F);
                GL11.glRotatef(90.0F, 0.0F, 0.0F, -1.0F);
                GL11.glTranslatef(0.0F, -1.0F, 0.0F);
                GL11.glRotatef(90.0F, 0.0F, -1.0F, 0.0F);
                GL11.glTranslatef(0.0F, 1.0F, -1.0F);
                break;
            case 6:
                GL11.glTranslatef(0.0F, 1.0F, 0.0F);
                GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                GL11.glTranslatef(0.0F, -1.0F, 0.0F);
                GL11.glRotatef(180.0F, 0.0F, -1.0F, 0.0F);
                GL11.glTranslatef(0.0F, 1.0F, -1.0F);
                break;
            case 7:
                GL11.glTranslatef(0.0F, 1.0F, 0.0F);
                GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                GL11.glTranslatef(0.0F, -1.0F, 0.0F);
                GL11.glRotatef(270.0F, 0.0F, -1.0F, 0.0F);
                GL11.glTranslatef(0.0F, 1.0F, -1.0F);
                break;
        }

        RendererHelper.renderWithResourceAndRotation(modelBase, 0, textureBase);

        RendererHelper.renderWithResourceAndRotation(models[LIGHT_RXD], 0, textures[rxState ? SIGN_G : SIGN_NONE]);
        RendererHelper.renderWithResourceAndRotation(models[LIGHT_TXD], 0, textures[txState ? SIGN_Y : SIGN_NONE]);
        RendererHelper.renderWithResourceAndRotation(
            models[LIGHT_SGN], 0,
            textures[
                sgnState ? (
                    (inverted) ? SIGN_W : SIGN_R
                ) : SIGN_NONE
            ]
        );
        RendererHelper.renderWithResourceAndRotation(models[LIGHT_TRI], 0, textures[triStateIsNeg ? SIGN_W : SIGN_B]);

        GL11.glPopMatrix();

        RenderHelper.enableStandardItemLighting();

        GL11.glPopMatrix();
    }

}
