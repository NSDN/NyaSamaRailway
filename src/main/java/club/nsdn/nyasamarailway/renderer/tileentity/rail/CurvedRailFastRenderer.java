package club.nsdn.nyasamarailway.renderer.tileentity.rail;

import club.nsdn.nyasamarailway.api.rail.TileEntityFastRailEndpoint;
import club.nsdn.nyasamarailway.api.rail.TileEntityRailEndpoint;
import club.nsdn.nyasamarailway.block.MdlCurvedRail;
import club.nsdn.nyasamarailway.block.BlockLoader;
import club.nsdn.nyasamarailway.tileblock.rail.RailEndpoint;
import club.nsdn.nyasamatelecom.api.render.AbsFastTESR;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by drzzm32 on 2019.4.13.
 */
public class CurvedRailFastRenderer extends AbsFastTESR {

    public void render(BufferBuilder buffer, double x, double y, double z, List<BakedQuad> quads) {
        buffer.setTranslation(x, y, z);

        int i = 0xF00000;
        for (BakedQuad quad: quads) {
            buffer.addVertexData(quad.getVertexData());
            buffer.putBrightness4(i, i, i, i);

            float diffuse = 1;
            if (quad.shouldApplyDiffuseLighting())
                diffuse = net.minecraftforge.client.model.pipeline.LightUtil.diffuseLight(quad.getFace());

            buffer.putColorMultiplier(diffuse, diffuse, diffuse, 4);
            buffer.putColorMultiplier(diffuse, diffuse, diffuse, 3);
            buffer.putColorMultiplier(diffuse, diffuse, diffuse, 2);
            buffer.putColorMultiplier(diffuse, diffuse, diffuse, 1);

            buffer.putPosition(0, 0, 0);
        }
    }

    @Override
    public boolean isGlobalRenderer(TileEntityBase te) {
        return true;
    }

    @Override
    public void renderTileEntityFast(@Nonnull TileEntityBase te, double x, double y, double z, float partialTicks, int destroyStage, float partial, @Nonnull BufferBuilder buffer) {
        if (te instanceof TileEntityFastRailEndpoint) {
            TileEntityFastRailEndpoint endpoint = (TileEntityFastRailEndpoint) te;

            if (endpoint.getQuads().isEmpty()) {
                BlockModelShapes modelShapes = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes();
                TextureMap textureMap = Minecraft.getMinecraft().getTextureMapBlocks();

                IBlockState endState = te.getWorld().getBlockState(te.getPos());
                MdlCurvedRail.EnumType type = MdlCurvedRail.EnumType.MONO;
                if (endState.getValue(RailEndpoint.TYPE) == RailEndpoint.EnumType.NS)
                    type = MdlCurvedRail.EnumType.NS;
                else if (endState.getValue(RailEndpoint.TYPE) == RailEndpoint.EnumType.SS)
                    type = MdlCurvedRail.EnumType.SS;
                else if (endState.getValue(RailEndpoint.TYPE) == RailEndpoint.EnumType.SS_NOR)
                    type = MdlCurvedRail.EnumType.SS_NOR;

                IBlockState state = BlockLoader.curvedRail.getDefaultState();
                IBakedModel model = modelShapes.getModelForState(state.withProperty(MdlCurvedRail.TYPE, type));
                TextureAtlasSprite texture = textureMap.getAtlasSprite("nyasamarailway:rails/rail_curved_base");

                endpoint.cookModel(model, texture);
            }

            x = x - endpoint.getPos().getX();
            y = y - endpoint.getPos().getY() - 0.5;
            z = z - endpoint.getPos().getZ();

            render(buffer, x, y, z, endpoint.getQuads());
        }
    }

}
