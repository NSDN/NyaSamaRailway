package club.nsdn.nyasamarailway.renderer.tileentity.rail;

import club.nsdn.nyasamarailway.tileblock.rail.RailTriSwitch;
import club.nsdn.nyasamatelecom.api.render.AbsTileEntitySpecialRenderer;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.2.15
 */
public class RailTriSwitchRendererFast extends AbsTileEntitySpecialRenderer {

    public RailTriSwitchRendererFast() {

    }

    @Override
    public void render(@Nonnull TileEntityBase te, double x, double y, double z, float partialTicks, int destroyStage, float partial) {
        GlStateManager.pushAttrib();
        GlStateManager.pushMatrix();

        GlStateManager.translate(x, y, z);
        GlStateManager.disableRescaleNormal();

        GlStateManager.pushMatrix();
        {
            if (te.getBlockType() instanceof RailTriSwitch) {
                RailTriSwitch blockSwitch = (RailTriSwitch) te.getBlockType();
                if (te instanceof RailTriSwitch.TileEntityRailTriSwitch) {
                    RailTriSwitch.TileEntityRailTriSwitch triSwitch = (RailTriSwitch.TileEntityRailTriSwitch) te;

                    float angle = 0.0F;
                    if (triSwitch.direction != null) {
                        switch (triSwitch.direction) {
                            case SOUTH:
                                angle = 0.0F;
                                break;
                            case WEST:
                                angle = 90.0F;
                                break;
                            case NORTH:
                                angle = 180.0F;
                                break;
                            case EAST:
                                angle = 270.0F;
                                break;
                        }
                    }

                    IBlockState state = blockSwitch.getDefaultState();
                    switch (triSwitch.prevState) {
                        case RailTriSwitch.TileEntityRailTriSwitch.STATE_POS:
                            state = blockSwitch.getDefaultState().withProperty(RailTriSwitch.SHAPE, BlockRailBase.EnumRailDirection.NORTH_WEST);
                            break;
                        case RailTriSwitch.TileEntityRailTriSwitch.STATE_NEG:
                            state = blockSwitch.getDefaultState().withProperty(RailTriSwitch.SHAPE, BlockRailBase.EnumRailDirection.NORTH_EAST);
                            break;
                        case RailTriSwitch.TileEntityRailTriSwitch.STATE_ZERO:
                            state = blockSwitch.getDefaultState().withProperty(RailTriSwitch.SHAPE, BlockRailBase.EnumRailDirection.NORTH_SOUTH);
                            break;
                    }

                    GL11.glTranslated(0.5, 0, 0.5);
                    GL11.glRotatef(angle, 0.0F, -1.0F, 0.0F);
                    {
                        RenderHelper.disableStandardItemLighting();
                        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                        if (Minecraft.isAmbientOcclusionEnabled()) {
                            GlStateManager.shadeModel(GL11.GL_SMOOTH);
                        } else {
                            GlStateManager.shadeModel(GL11.GL_FLAT);
                        }

                        World world = te.getWorld();
                        Tessellator tessellator = Tessellator.getInstance();
                        BufferBuilder bufferBuilder = tessellator.getBuffer();
                        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

                        GlStateManager.translate(-te.getPos().getX(), -te.getPos().getY(), -te.getPos().getZ());
                        GL11.glTranslated(-0.5, 0, -0.5);

                        BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
                        IBakedModel model = dispatcher.getModelForState(state);
                        dispatcher.getBlockModelRenderer().renderModel(world, model, state, te.getPos(), bufferBuilder, true);
                        tessellator.draw();

                        RenderHelper.enableStandardItemLighting();
                    }
                }
            }
        }
        GlStateManager.popMatrix();

        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

}
