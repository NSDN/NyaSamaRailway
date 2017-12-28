package club.nsdn.nyasamarailway.renderer.tileentity;

import club.nsdn.nyasamarailway.renderer.RendererHelper;
import club.nsdn.nyasamarailway.tileblock.signal.block.BlockSignalLight;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.WavefrontObject;
import org.lwjgl.opengl.GL11;

/**
 * Created by drzzm32 on 2017.10.5.
 */
public class BiSignalLightRenderer extends TileEntitySpecialRenderer {

    private final WavefrontObject modelBase = new WavefrontObject(
            new ResourceLocation("nyasamarailway", "models/blocks/bi_signal_light_base.obj")
    );
    private final WavefrontObject modelLight[] = {
            new WavefrontObject(
                    new ResourceLocation("nyasamarailway", "models/blocks/bi_signal_light_f.obj")
            ),
            new WavefrontObject(
                    new ResourceLocation("nyasamarailway", "models/blocks/bi_signal_light_t.obj")
            )
    };
    private final ResourceLocation textureBase = new ResourceLocation("nyasamarailway", "textures/blocks/signal_light_base.png");
    private final ResourceLocation[] textureLight = {
            new ResourceLocation("nyasamarailway", "textures/blocks/signal_box_none.png"),
            new ResourceLocation("nyasamarailway", "textures/blocks/signal_box_r.png"),
            new ResourceLocation("nyasamarailway", "textures/blocks/signal_box_y.png"),
            new ResourceLocation("nyasamarailway", "textures/blocks/signal_box_g.png"),
            new ResourceLocation("nyasamarailway", "textures/blocks/signal_box_w.png"),
            new ResourceLocation("nyasamarailway", "textures/blocks/signal_box_b.png"),
            new ResourceLocation("nyasamarailway", "textures/blocks/signal_box_p.png")
    };

    public BiSignalLightRenderer() {
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

        int lightPos = 0;
        int lightState = (meta >> 2) & 0x3;

        if (te instanceof BlockSignalLight.SignalLight) {
            BlockSignalLight.SignalLight signalLight = (BlockSignalLight.SignalLight) te;
            if (signalLight.lightType.equals("white&blue")) {
                if (lightState == 2) lightState = 4;
                else if (lightState == 3) lightState = 5;
                else lightState = 0;
            } else if (signalLight.lightType.equals("yellow&purple")) {
                if (lightState == 2) lightState = 2;
                else if (lightState == 3) lightState = 6;
                else lightState = 0;
            }

            if (signalLight.isPowered || signalLight.senderIsPowered())
                lightPos = 1;
            else lightPos = 0;
        }

        RendererHelper.renderWithResourceAndRotation(modelBase, angle, textureBase);
        RendererHelper.renderWithResourceAndRotation(modelLight[0], angle, textureLight[lightPos == 0 ? lightState : 0]);
        RendererHelper.renderWithResourceAndRotation(modelLight[1], angle, textureLight[lightPos == 1 ? lightState : 0]);

        RenderHelper.enableStandardItemLighting();

        GL11.glPopMatrix();
    }

}
