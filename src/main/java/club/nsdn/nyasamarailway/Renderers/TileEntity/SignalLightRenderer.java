package club.nsdn.nyasamarailway.Renderers.TileEntity;

/**
 * Created by drzzm32 on 2017.7.4.
 */

import club.nsdn.nyasamarailway.Renderers.RendererHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.obj.WavefrontObject;
import org.lwjgl.opengl.GL11;

public class SignalLightRenderer extends TileEntitySpecialRenderer {

    private final WavefrontObject model = new WavefrontObject(
            new ResourceLocation("nyasamarailway", "models/blocks/signal_light.obj")
    );
    private final ResourceLocation[] textures = {
            new ResourceLocation("nyasamarailway", "textures/blocks/signal_light_none.png"),
            new ResourceLocation("nyasamarailway", "textures/blocks/signal_light_r.png"),
            new ResourceLocation("nyasamarailway", "textures/blocks/signal_light_y.png"),
            new ResourceLocation("nyasamarailway", "textures/blocks/signal_light_g.png")
    };
    private static final int TEXTURE_DEF = 0;
    private static final int TEXTURE_R = 1;
    private static final int TEXTURE_Y = 2;
    private static final int TEXTURE_G = 3;

    public SignalLightRenderer() {
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

        TextureManager manager = Minecraft.getMinecraft().getTextureManager();
        int lightState = (meta >> 2) & 0x3;
        GL11.glPushMatrix();
        GL11.glScalef(0.0625F, 0.0625F, 0.0625F);
        RendererHelper.renderWithResourceAndRotation(model, angle, textures[lightState], manager);
        GL11.glPopMatrix();

        RenderHelper.enableStandardItemLighting();

        GL11.glPopMatrix();
    }

}
