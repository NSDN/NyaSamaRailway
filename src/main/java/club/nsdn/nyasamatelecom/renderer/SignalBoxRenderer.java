package club.nsdn.nyasamatelecom.renderer;

import club.nsdn.nyasamatelecom.api.device.SignalBox;
import club.nsdn.nyasamatelecom.api.device.SignalBoxGetter;
import club.nsdn.nyasamatelecom.api.device.SignalBoxSender;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityActuator;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityMultiSender;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityReceiver;
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
public class SignalBoxRenderer extends TileEntitySpecialRenderer {

    private final WavefrontObject modelBase = new WavefrontObject(
            new ResourceLocation("nyasamatelecom", "models/blocks/signal_box_base.obj")
    );
    private final WavefrontObject modelBtn;
    private final WavefrontObject modelBtnLight;
    private final ResourceLocation textureBase;

    private final WavefrontObject models[] = {
            new WavefrontObject(new ResourceLocation("nyasamatelecom", "models/blocks/signal_box_sign1.obj")),
            new WavefrontObject(new ResourceLocation("nyasamatelecom", "models/blocks/signal_box_sign2.obj")),
            new WavefrontObject(new ResourceLocation("nyasamatelecom", "models/blocks/signal_box_sign3.obj"))
    };
    private final ResourceLocation textures[] = {
            new ResourceLocation("nyasamatelecom", "textures/blocks/signal_box_r.png"),
            new ResourceLocation("nyasamatelecom", "textures/blocks/signal_box_y.png"),
            new ResourceLocation("nyasamatelecom", "textures/blocks/signal_box_g.png"),
            new ResourceLocation("nyasamatelecom", "textures/blocks/signal_box_none.png"),
            new ResourceLocation("nyasamatelecom", "textures/blocks/signal_box_w.png")
    };
    private static final int SIGN_R = 0;
    private static final int SIGN_Y = 1;
    private static final int SIGN_G = 2;
    private static final int SIGN_NONE = 3;
    private static final int SIGN_W = 4;

    public SignalBoxRenderer(boolean hasButton) {
        textureBase = new ResourceLocation("nyasamatelecom", "textures/blocks/signal_box_base.png");

        if (hasButton) {
            modelBtn = new WavefrontObject(
                    new ResourceLocation("nyasamatelecom", "models/blocks/signal_box_btn.obj")
            );
            modelBtnLight = new WavefrontObject(
                    new ResourceLocation("nyasamatelecom", "models/blocks/signal_box_btn_light.obj")
            );
        } else {
            modelBtn = null;
            modelBtnLight = null;
        }
    }

    public SignalBoxRenderer(boolean hasButton, String texture) {
        textureBase = new ResourceLocation("nyasamatelecom", "textures/blocks/" + texture + ".png");

        if (hasButton) {
            modelBtn = new WavefrontObject(
                    new ResourceLocation("nyasamatelecom", "models/blocks/signal_box_btn.obj")
            );
            modelBtnLight = new WavefrontObject(
                    new ResourceLocation("nyasamatelecom", "models/blocks/signal_box_btn_light.obj")
            );
        } else {
            modelBtn = null;
            modelBtnLight = null;
        }
    }

    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float scale) {
        int meta = te.getBlockMetadata();

        boolean txState;
        boolean rxState;
        boolean sgnState = (meta & 0x8) != 0;
        boolean isEnabled = false;

        boolean inverted = false;

        if (te instanceof TileEntityActuator) {
            txState = ((TileEntityActuator) te).getTarget() != null;
            rxState = ((TileEntityActuator) te).getSender() != null;
            if (te instanceof SignalBox.TileEntitySignalBox) {
                inverted = ((SignalBox.TileEntitySignalBox) te).inverterEnabled;
                sgnState = ((SignalBox.TileEntitySignalBox) te).isEnabled;
            }
        } else if (te instanceof TileEntityReceiver) {
            txState = false;
            rxState = ((TileEntityReceiver) te).getSender() != null;
            if (te instanceof SignalBoxGetter.TileEntitySignalBoxGetter) {
                sgnState = ((SignalBoxGetter.TileEntitySignalBoxGetter) te).isEnabled;
            }
        } else if (te instanceof TileEntityMultiSender && modelBtn == null) {
            txState = ((TileEntityMultiSender) te).targetCount > 0;
            rxState = false;
        } else if (te instanceof TileEntityMultiSender) {
            if (te instanceof SignalBoxSender.TileEntitySignalBoxSender) {
                isEnabled = ((SignalBoxSender.TileEntitySignalBoxSender) te).isEnabled;
            }
            txState = ((TileEntityMultiSender) te).targetCount > 0;
            rxState = ((TileEntityMultiSender) te).getTransceiver() != null;
        } else return;

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
        if (modelBtn != null) {
            RendererHelper.renderWithResourceAndRotation(modelBtn, 0, textureBase);
            RendererHelper.renderWithResourceAndRotation(modelBtnLight, 0, textures[isEnabled ? SIGN_W : SIGN_NONE]);
        }
        RendererHelper.renderWithResourceAndRotation(models[SIGN_G], 0, textures[rxState ? SIGN_G : SIGN_NONE]);
        RendererHelper.renderWithResourceAndRotation(models[SIGN_Y], 0, textures[txState ? SIGN_Y : SIGN_NONE]);
        RendererHelper.renderWithResourceAndRotation(
            models[SIGN_R], 0,
            textures[
                sgnState ? (
                    (inverted && modelBtn == null) ? SIGN_W : SIGN_R
                ) : SIGN_NONE
            ]
        );

        GL11.glPopMatrix();

        RenderHelper.enableStandardItemLighting();

        GL11.glPopMatrix();
    }

}
