package club.nsdn.nyasamarailway.api.rail;

import club.nsdn.nyasamarailway.util.Vertex;
import cn.ac.nya.forgeobj.WavefrontObject;
import cn.ac.nya.mutable.MutableModel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by drzzm32 on 2019.4.13.
 */
public class TileEntityFastRailEndpoint extends TileEntityRailEndpoint {

    @Override
    public boolean hasFastRenderer() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    protected LinkedList<BakedQuad> bakedQuads;

    @SideOnly(Side.CLIENT)
    public double getRenderStep() { return 0.25; }

    @SideOnly(Side.CLIENT)
    public List<BakedQuad> getQuads() {
        if (bakedQuads == null)
            bakedQuads = new LinkedList<>();
        return bakedQuads;
    }

    @SideOnly(Side.CLIENT)
    public void cookModel(@Nonnull IBakedModel model, TextureAtlasSprite texture) {
        Vec3d vec, nex; double step = this.getRenderStep();
        for (double d = 0; d <= this.len(); d += step) {
            if (d == this.len()) {
                vec = this.get(d - step / 100.0);
                nex = this.get(d);
                nex = nex.subtract(vec);
                vec = this.get(d);
            } else {
                vec = this.get(d);
                nex = this.get(d + step);
                nex = nex.subtract(vec);
            }

            double yaw = Math.atan2(nex.z, nex.x);
            double hlen = Math.sqrt(nex.x * nex.x + nex.z * nex.z);
            double pitch = Math.atan(nex.y / hlen);

            List<BakedQuad> quads = model.getQuads(null, null, 0);
            MutableModel mutableModel = new MutableModel(quads);
            mutableModel.rotateZ((float) -pitch).rotateY((float) yaw).translate(vec.x, vec.y, vec.z);
            mutableModel.setTexture(texture);

            bakedQuads.addAll(mutableModel.getBakedQuads());
        }
    }

    @SideOnly(Side.CLIENT)
    public void clearModel() {
        bakedQuads.clear();
    }


    @Deprecated
    @SideOnly(Side.CLIENT)
    public final LinkedList<Vertex> getVertices() { return new LinkedList<>(); }

    @Deprecated
    @SideOnly(Side.CLIENT)
    public final void cookVertices(@Nonnull WavefrontObject model) { }

    @Deprecated
    @SideOnly(Side.CLIENT)
    public final void clearVertices() { }

}
