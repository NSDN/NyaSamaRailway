package club.nsdn.nyasamarailway.renderer.entity;

import club.nsdn.nyasamarailway.api.cart.IBogie;
import club.nsdn.nyasamarailway.api.cart.INamedCart;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.client.registry.IRenderFactory;

/**
 * Created by drzzm32 on 2019.2.10
 */
public abstract class AbsCartRenerer extends Render<EntityMinecart> {

    public static IRenderFactory<EntityMinecart> FACTORY_DUMMY = (RenderManager manager) -> new AbsCartRenerer(manager) {
        @Override
        public void render(EntityMinecart cart, double x, double y, double z, float yaw) {

        }
    };

    public AbsCartRenerer(RenderManager manager) {
        super(manager);
        this.shadowSize = 0.5F;
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityMinecart cart) {
        return new ResourceLocation("textures/entity/minecart.png");
    }

    @Override
    public void doRender(EntityMinecart cart, double x, double y, double z, float yaw, float v) {
        GlStateManager.pushMatrix();
        this.bindEntityTexture(cart);
        long id = (long)cart.getEntityId() * 493286711L;
        id = id * id * 4392167121L + id * 98761L;
        float pX = (((float)(id >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        float pY = (((float)(id >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        float pZ = (((float)(id >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        GlStateManager.translate(pX, pY, pZ);
        double posX = cart.lastTickPosX + (cart.posX - cart.lastTickPosX) * (double)v;
        double posY = cart.lastTickPosY + (cart.posY - cart.lastTickPosY) * (double)v;
        double posZ = cart.lastTickPosZ + (cart.posZ - cart.lastTickPosZ) * (double)v;
        Vec3d pos = cart.getPos(posX, posY, posZ);
        float pitch = cart.prevRotationPitch + (cart.rotationPitch - cart.prevRotationPitch) * v;
        if (pos != null) {
            Vec3d posOffset1 = cart.getPosOffset(posX, posY, posZ, 0.30000001192092896D);
            Vec3d posOffset2 = cart.getPosOffset(posX, posY, posZ, -0.30000001192092896D);
            if (posOffset1 == null) {
                posOffset1 = pos;
            }

            if (posOffset2 == null) {
                posOffset2 = pos;
            }

            x += pos.x - posX;
            y += (posOffset1.y + posOffset2.y) / 2.0D - posY;
            z += pos.z - posZ;
            Vec3d vector = posOffset2.addVector(-posOffset1.x, -posOffset1.y, -posOffset1.z);
            if (vector.lengthVector() != 0.0D) {
                vector = vector.normalize();
                yaw = (float)(Math.atan2(vector.z, vector.x) * 180.0D / 3.141592653589793D);
                pitch = (float)(Math.atan(vector.y) * 73.0D);
            }
        }

        GlStateManager.translate(0, 0.0625, 0);
        GlStateManager.translate((float)x, (float)y + 0.375F, (float)z);
        if (cart instanceof IBogie)
            GlStateManager.translate(0, -((IBogie) cart).getRenderFixOffset(), 0);
        GlStateManager.rotate(180.0F - yaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-pitch, 0.0F, 0.0F, 1.0F);
        float roll = (float)cart.getRollingAmplitude() - v;
        float damage = cart.getDamage() - v;
        if (damage < 0.0F) {
            damage = 0.0F;
        }

        if (roll > 0.0F) {
            GlStateManager.rotate(MathHelper.sin(roll) * roll * damage / 10.0F * (float)cart.getRollingDirection(), 1.0F, 0.0F, 0.0F);
        }

        GlStateManager.pushMatrix();
        if (cart instanceof IBogie)
            GlStateManager.translate(0, ((IBogie) cart).getRenderFixOffset(), 0);
        render(cart, x, y, z, yaw);
        GlStateManager.popMatrix();

        GlStateManager.popMatrix();

        if (cart instanceof INamedCart)
            renderLivingLabel(cart, ((INamedCart) cart).getCartName(), x, y, z, 64);
    }

    public abstract void render(EntityMinecart cart, double x, double y, double z, float yaw);

}
