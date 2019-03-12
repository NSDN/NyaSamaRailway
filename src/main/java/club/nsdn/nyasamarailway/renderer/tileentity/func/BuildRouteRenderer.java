package club.nsdn.nyasamarailway.renderer.tileentity.func;

import club.nsdn.nyasamarailway.tileblock.func.TileEntityBuildEndpoint;
import club.nsdn.nyasamatelecom.api.render.AbsTileEntitySpecialRenderer;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by drzzm32 on 2019.3.10.
 */
public class BuildRouteRenderer extends AbsTileEntitySpecialRenderer {

    @Override
    public boolean isGlobalRenderer(TileEntity te) {
        return true;
    }

    private static BuildRouteRenderer instance;
    public static BuildRouteRenderer instance() {
        if (instance == null)
            instance = new BuildRouteRenderer();
        return instance;
    }

    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent renderWorldLastEvent) {
        if (Minecraft.getMinecraft().world == null)
            return;

        List<TileEntity> list = Minecraft.getMinecraft().world.loadedTileEntityList;
        EntityPlayerSP player = Minecraft.getMinecraft().player;

        for (TileEntity tileEntity : list) {
            if (tileEntity instanceof TileEntityBuildEndpoint)
                render((TileEntityBase) tileEntity,
                        tileEntity.getPos().getX() - player.chasingPosX,
                        tileEntity.getPos().getY() - player.chasingPosY,
                        tileEntity.getPos().getZ() - player.chasingPosZ,
                        0, 0, 0
                );
        }
    }

    @Override
    public void render(@Nonnull TileEntityBase te, double x, double y, double z, float partialTicks, int destroyStage, float partial) {
        if (te instanceof TileEntityBuildEndpoint) {
            TileEntityBuildEndpoint endpoint = (TileEntityBuildEndpoint) te;
            BlockPos pos = endpoint.getPos();

            GL11.glPushMatrix();
            GL11.glTranslated(x - pos.getX(), y - pos.getY(), z - pos.getZ());

            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glLineWidth(4.0F);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDepthMask(false);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(false);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();

            buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);

            Vec3d vec;
            float r = 0.0F, g = 1.0F, b = 0.0F, a = 0.5F;
            endpoint.reset();
            while (endpoint.hasNext()) {
                vec = endpoint.next();
                buffer.pos(vec.x, vec.y, vec.z).color(r, g, b, a).endVertex();
            }

            tessellator.draw();

            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glPopAttrib();

            GL11.glPopMatrix();
        }
    }

}
