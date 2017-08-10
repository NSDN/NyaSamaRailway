package club.nsdn.nyasamarailway.Renderers.TileEntity;

/**
 * Created by drzzm32 on 2017.8.9.
 */

import club.nsdn.nyasamarailway.Renderers.RendererHelper;
import club.nsdn.nyasamarailway.TileEntities.TileEntityRailActuator;
import club.nsdn.nyasamarailway.TileEntities.TileEntityRailSender;
import club.nsdn.nyasamarailway.TileEntities.TileEntitySignalBox;
import club.nsdn.nyasamarailway.TileEntities.TileEntitySignalLight;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.WavefrontObject;
import org.lwjgl.opengl.GL11;

public class SignalBoxRenderer extends TileEntitySpecialRenderer {

    private final WavefrontObject modelBase = new WavefrontObject(
            new ResourceLocation("nyasamarailway", "models/blocks/signal_box_base.obj")
    );
    private final WavefrontObject modelBtn;
    private final ResourceLocation textureBase = new ResourceLocation("nyasamarailway", "textures/blocks/signal_box_base.png");

    private final WavefrontObject models[] = {
            new WavefrontObject(new ResourceLocation("nyasamarailway", "models/blocks/signal_box_sign1.obj")),
            new WavefrontObject(new ResourceLocation("nyasamarailway", "models/blocks/signal_box_sign2.obj")),
            new WavefrontObject(new ResourceLocation("nyasamarailway", "models/blocks/signal_box_sign3.obj"))
    };
    private final ResourceLocation textures[] = {
            new ResourceLocation("nyasamarailway", "textures/blocks/signal_box_r.png"),
            new ResourceLocation("nyasamarailway", "textures/blocks/signal_box_y.png"),
            new ResourceLocation("nyasamarailway", "textures/blocks/signal_box_g.png"),
            new ResourceLocation("nyasamarailway", "textures/blocks/signal_box_none.png")
    };
    private static final int SIGN_R = 0;
    private static final int SIGN_Y = 1;
    private static final int SIGN_G = 2;
    private static final int SIGN_NONE = 3;

    public SignalBoxRenderer(boolean isSender) {
        if (isSender) {
            modelBtn =  new WavefrontObject(
                    new ResourceLocation("nyasamarailway", "models/blocks/signal_box_btn.obj")
            );
        } else {
            modelBtn = null;
        }
    }

    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float scale) {
        int meta = te.getBlockMetadata();

        boolean txState;
        boolean rxState;
        boolean sgnState = (meta & 0x8) != 0;

        if (te instanceof TileEntityRailActuator) {
            txState = ((TileEntityRailActuator) te).getTarget() != null;
            rxState = ((TileEntityRailActuator) te).getSenderRail() != null;
        } else if (te instanceof TileEntityRailSender) {
            txState = ((TileEntityRailSender) te).getTarget() != null;
            rxState = ((TileEntityRailSender) te).getTransceiverRail() != null;
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

        TextureManager manager = Minecraft.getMinecraft().getTextureManager();
        GL11.glPushMatrix();
        GL11.glScalef(0.0625F, 0.0625F, 0.0625F);

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
                GL11.glTranslatef(0.0F, -0.125F, 0.0F);
                GL11.glRotatef(0, 0.0F, -1.0F, 0.0F);
                break;
            case 5:
                GL11.glTranslatef(0.0F, 1.0F, 0.0F);
                GL11.glRotatef(90.0F, 0.0F, 0.0F, -1.0F);
                GL11.glTranslatef(0.0F, -1.0F, 0.0F);
                GL11.glTranslatef(0.0F, -0.125F, 0.0F);
                GL11.glRotatef(90.0F, 0.0F, -1.0F, 0.0F);
                break;
            case 6:
                GL11.glTranslatef(0.0F, 1.0F, 0.0F);
                GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                GL11.glTranslatef(0.0F, -1.0F, 0.0F);
                GL11.glTranslatef(0.0F, -0.125F, 0.0F);
                GL11.glRotatef(180.0F, 0.0F, -1.0F, 0.0F);
                break;
            case 7:
                GL11.glTranslatef(0.0F, 1.0F, 0.0F);
                GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                GL11.glTranslatef(0.0F, -1.0F, 0.0F);
                GL11.glTranslatef(0.0F, -0.125F, 0.0F);
                GL11.glRotatef(270.0F, 0.0F, -1.0F, 0.0F);
                break;
        }

        RendererHelper.renderWithResourceAndRotation(modelBase, 0, textureBase, manager);
        if (modelBtn != null) RendererHelper.renderWithResourceAndRotation(modelBtn, 0, textureBase, manager);
        RendererHelper.renderWithResourceAndRotation(models[SIGN_G], 0, textures[rxState ? SIGN_G : SIGN_NONE], manager);
        RendererHelper.renderWithResourceAndRotation(models[SIGN_Y], 0, textures[txState ? SIGN_Y : SIGN_NONE], manager);
        RendererHelper.renderWithResourceAndRotation(models[SIGN_R], 0, textures[sgnState ? SIGN_R : SIGN_NONE], manager);

        GL11.glPopMatrix();
        GL11.glPopMatrix();

        RenderHelper.enableStandardItemLighting();

        GL11.glPopMatrix();
    }

}
