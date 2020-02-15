package club.nsdn.nyasamarailway.renderer.tileentity.rail;

import club.nsdn.nyasamarailway.api.rail.IBaseRail;
import club.nsdn.nyasamarailway.tileblock.rail.VirtualRail;
import club.nsdn.nyasamatelecom.api.render.AbsTileEntitySpecialRenderer;
import club.nsdn.nyasamatelecom.api.render.RendererHelper;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import cn.ac.nya.forgeobj.WavefrontObject;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2020.2.15
 */
public class VirtualRailRenderer extends AbsTileEntitySpecialRenderer {

    private final WavefrontObject model = new WavefrontObject(new ResourceLocation(
            "nyasamarailway", "models/rails/virtual_rail.obj"));
    private final ResourceLocation texture = new ResourceLocation(
            "nyasamarailway", "textures/rails/virtual_rail.png");

    @Override
    public void render(@Nonnull TileEntityBase te, double x, double y, double z, float partialTicks, int destroyStage, float partial) {
        float angle = 0;
        if (te instanceof VirtualRail.TileEntityVirtualRail)
            angle = ((VirtualRail.TileEntityVirtualRail) te).actualDir;

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y  + 0.5F, (float) z + 0.5F);

        //RendererHelper.beginSpecialLighting();

        GL11.glPushMatrix();
        if (te.getWorld().getTileEntity(te.getPos().down()) instanceof IBaseRail)
            GL11.glTranslatef(0.0F, -0.25F, 0.0F);

        RendererHelper.renderWithResourceAndRotation(model, angle, texture);

        GL11.glPopMatrix();

        //RendererHelper.endSpecialLighting();

        GL11.glPopMatrix();
    }

}
