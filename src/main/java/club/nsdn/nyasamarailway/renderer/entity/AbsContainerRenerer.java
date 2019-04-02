package club.nsdn.nyasamarailway.renderer.entity;

import club.nsdn.nyasamarailway.api.cart.AbsContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

/**
 * Created by drzzm32 on 2019.4.2.
 */
public abstract class AbsContainerRenerer extends Render<AbsContainer> {

    public static IRenderFactory<AbsContainer> FACTORY_DUMMY = (RenderManager manager) -> new AbsContainerRenerer(manager) {
        @Override
        public void render(AbsContainer container, double x, double y, double z, float yaw) {

        }
    };

    public AbsContainerRenerer(RenderManager manager) {
        super(manager);
        this.shadowSize = 0.5F;
    }

    @Override
    protected ResourceLocation getEntityTexture(AbsContainer train) {
        return new ResourceLocation("textures/entity/minecart.png");
    }

    @Override
    public void doRender(AbsContainer container, double x, double y, double z, float yaw, float v) {
        GlStateManager.pushMatrix();
        this.bindEntityTexture(container);

        GlStateManager.translate((float) x, (float) y + 0.375F + 0.0625F, (float) z);
        GlStateManager.rotate(180.0F - container.rotationYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-container.rotationPitch, 0.0F, 0.0F, 1.0F);

        GlStateManager.pushMatrix();
        render(container, x, y, z, yaw);
        GlStateManager.popMatrix();

        GlStateManager.popMatrix();
    }

    public abstract void render(AbsContainer container, double x, double y, double z, float yaw);

}
