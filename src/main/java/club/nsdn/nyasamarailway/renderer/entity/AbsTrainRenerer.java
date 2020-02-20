package club.nsdn.nyasamarailway.renderer.entity;

import club.nsdn.nyasamarailway.api.cart.AbsTrainBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

/**
 * Created by drzzm32 on 2019.2.27
 */
public abstract class AbsTrainRenerer extends Render<AbsTrainBase> {

    public static IRenderFactory<AbsTrainBase> FACTORY_DUMMY = (RenderManager manager) -> new AbsTrainRenerer(manager) {
        @Override
        public void render(AbsTrainBase train, double x, double y, double z, float yaw) {

        }
    };

    public AbsTrainRenerer(RenderManager manager) {
        super(manager);
        this.shadowSize = 0.5F;
    }

    @Override
    protected ResourceLocation getEntityTexture(AbsTrainBase train) {
        return new ResourceLocation("textures/entity/minecart.png");
    }

    @Override
    public void doRender(AbsTrainBase train, double x, double y, double z, float yaw, float v) {
        GlStateManager.pushMatrix();
        this.bindEntityTexture(train);

        GlStateManager.translate((float) x, (float) y + 0.375F + 0.0625F, (float) z);
        GlStateManager.translate(0, train.getRenderYOffset(), 0);
        GlStateManager.translate(0, -train.getRenderFixOffset(), 0);
        GlStateManager.rotate(180.0F - train.rotationYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-train.rotationPitch, 0.0F, 0.0F, 1.0F);

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, train.getRenderFixOffset(), 0);
        GlStateManager.pushAttrib();
        render(train, x, y, z, yaw);
        GlStateManager.popAttrib();
        GlStateManager.popMatrix();

        GlStateManager.popMatrix();
    }

    public abstract void render(AbsTrainBase train, double x, double y, double z, float yaw);

}
