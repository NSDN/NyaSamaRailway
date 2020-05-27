package club.nsdn.nyasamarailway.renderer.tileentity.rail;

import club.nsdn.nyasamarailway.tileblock.rail.WireRailV;
import club.nsdn.nyasamatelecom.api.render.AbsFastTESR;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import cn.ac.nya.rawmdl.RawQuadCube;
import cn.ac.nya.rawmdl.RawQuadGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;
import java.util.LinkedList;

/**
 * Created by drzzm32 on 2020.5.27.
 */
public class WireRailVRenderer extends AbsFastTESR {

    public WireRailVRenderer() {
    }

    public void render(BufferBuilder buffer, double x, double y, double z, LinkedList<BakedQuad> quads) {
        buffer.setTranslation(x, y, z);

        int i = 15728640;
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
    public void renderTileEntityFast(
            @Nonnull TileEntityBase te,
            double x, double y, double z,
            float partialTicks, int destroyStage, float partial,
            @Nonnull BufferBuilder buffer
    ) {
        if (te instanceof WireRailV.TileEntityWireRailV) {
            WireRailV.TileEntityWireRailV wireNode = (WireRailV.TileEntityWireRailV) te;

            Vec3d offset = new Vec3d(0.5, 0.5, 0.5);
            offset = offset.addVector(0, 0.5 - 0.03125, 0);

            if (wireNode.srcQuads == null)
                wireNode.srcQuads = new LinkedList<>();
            if (wireNode.dstQuads == null)
                wireNode.dstQuads = new LinkedList<>();

            Vec3d theVec = new Vec3d(wireNode.getPos()).add(offset);
            Vec3d srcVec = null, dstVec = null;
            if (wireNode.getSender() != null) {
                BlockPos senderPos = wireNode.getSender().getPos();
                srcVec = new Vec3d(senderPos).add(offset);
            } else if (!wireNode.srcQuads.isEmpty()) wireNode.srcQuads.clear();
            if (wireNode.getTarget() != null) {
                BlockPos targetPos = wireNode.getTarget().getPos();
                dstVec = new Vec3d(targetPos).add(offset);
            } else if (!wireNode.dstQuads.isEmpty()) wireNode.dstQuads.clear();

            TextureAtlasSprite texture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(
                    "nyasamarailway:block/rail_wire_v"
            );

            x = x - wireNode.getPos().getX();
            y = y - wireNode.getPos().getY();
            z = z - wireNode.getPos().getZ();

            if (srcVec != null) {
                if (wireNode.srcQuads.isEmpty()) {
                    RawQuadGroup group = renderCatenary(srcVec, theVec, texture);
                    group.bake(wireNode.srcQuads);
                }
                render(buffer, x, y, z, wireNode.srcQuads);
            }

            if (dstVec != null) {
                if (wireNode.dstQuads.isEmpty()) {
                    RawQuadGroup group = renderCatenary(theVec, dstVec, texture);
                    group.bake(wireNode.dstQuads);
                }
                render(buffer, x, y, z, wireNode.dstQuads);
            }

        }
    }

    public static RawQuadGroup renderCatenary(Vec3d from, Vec3d to, TextureAtlasSprite texture) {
        RawQuadGroup ret = new RawQuadGroup();

        float size = 0.0625F;
        float heigh = 0.5F;

        Vec3d vec = to.subtract(from);
        double len = vec.lengthVector();
        double hlen = Math.sqrt(vec.x * vec.x + vec.z * vec.z);
        int steps = MathHelper.floor(hlen) / 2;
        float step = (float) hlen / (float) steps;
        float angle = (float) MathHelper.atan2(vec.y, hlen);
        if (hlen == 0) angle = (vec.y > 0) ? 0 : (float) Math.PI;

        ret.add((new RawQuadCube(size, (float) len, size, texture))
                .rotateAroundZ(angle * 180F / (float) Math.PI)
        );
        ret.add((new RawQuadCube(size, (float) len, size, texture))
                .rotateAroundZ(angle * 180F / (float) Math.PI)
                .translateCoord(heigh - size, 0, 0)
        );

        for (int i = 1; i < steps; i++) {
            ret.add((new RawQuadCube(heigh - size, size / 2, size / 2, texture))
                    .translateCoord(heigh / 2 - size / 2, step * i, 0)
                    .rotateAroundZ(angle * 180F / (float) Math.PI)
            );
        }

        ret.rotateToVec((float) from.x, 0, (float) from.z, (float) to.x, 0, (float) to.z);
        ret.translateCoord((float) from.x, (float) from.y, (float) from.z);
        return ret;
    }

}
