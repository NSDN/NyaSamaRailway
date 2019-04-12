package club.nsdn.nyasamarailway.renderer.tileentity.rail;

import club.nsdn.nyasamarailway.api.rail.TileEntityRailEndpoint;
import club.nsdn.nyasamarailway.block.MdlCurvedRail;
import club.nsdn.nyasamarailway.block.BlockLoader;
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
 * Created by drzzm32 on 2019.3.17.
 */
public class CurvedRailRenderer extends AbsFastTESR {

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
        if (te instanceof TileEntityRailEndpoint) {
            TileEntityRailEndpoint endpoint = (TileEntityRailEndpoint) te;

            if (endpoint.getQuads().isEmpty()) {
                BlockModelShapes modelShapes = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes();
                TextureMap textureMap = Minecraft.getMinecraft().getTextureMapBlocks();

                IBlockState state = BlockLoader.curvedRail.getDefaultState();
                IBakedModel model = modelShapes.getModelForState(state.withProperty(MdlCurvedRail.TYPE, MdlCurvedRail.EnumType.MONO));
                TextureAtlasSprite texture = textureMap.getAtlasSprite("nyasamarailway:rails/mono_rail_curved");

                endpoint.cookModel(model, texture);
            }

            x = x - endpoint.getPos().getX();
            y = y - endpoint.getPos().getY() + 1.0;
            z = z - endpoint.getPos().getZ();

            render(buffer, x, y, z, endpoint.getQuads());
        }
    }

}
