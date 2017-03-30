package club.nsdn.nyasamarailway.Renderers.Entity;

import club.nsdn.nyasamarailway.Entity.NSPCT4;
import club.nsdn.nyasamarailway.TileEntities.Rail.RailMonoMagnetBase;
import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderMinecart;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

/**
 * Created by drzzm32 on 2016.5.23.
 */
public class NSPCT4Renderer extends RenderMinecart {
    private ResourceLocation cartTex = new ResourceLocation("nyasamarailway", "textures/carts/nstc_1.png");
    public ModelBase modelCart;

    public NSPCT4Renderer(ModelBase model, String texturePath) {
        super();
        cartTex = new ResourceLocation("nyasamarailway", texturePath);
        modelCart = model;
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityMinecart cart) {
        return cartTex;
    }

    @Override
    public void doRender(EntityMinecart minecart, double x, double y, double z, float Yaw, float p_doRender_9_) {
        GL11.glPushMatrix();
        this.bindEntityTexture(minecart);
        long var10 = (long)minecart.getEntityId() * 493286711L;
        var10 = var10 * var10 * 4392167121L + var10 * 98761L;
        float var12 = (((float)(var10 >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        float var13 = (((float)(var10 >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        float var14 = (((float)(var10 >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        GL11.glTranslatef(var12, var13, var14);
        double var15 = minecart.lastTickPosX + (minecart.posX - minecart.lastTickPosX) * (double)p_doRender_9_;
        double var17 = minecart.lastTickPosY + (minecart.posY - minecart.lastTickPosY) * (double)p_doRender_9_;
        double var19 = minecart.lastTickPosZ + (minecart.posZ - minecart.lastTickPosZ) * (double)p_doRender_9_;
        double var21 = 0.30000001192092896D;
        Vec3 var23 = minecart.func_70489_a(var15, var17, var19);
        float var24 = minecart.prevRotationPitch + (minecart.rotationPitch - minecart.prevRotationPitch) * p_doRender_9_;
        if(var23 != null) {
            Vec3 var25 = minecart.func_70495_a(var15, var17, var19, var21);
            Vec3 var26 = minecart.func_70495_a(var15, var17, var19, -var21);
            if(var25 == null) {
                var25 = var23;
            }

            if(var26 == null) {
                var26 = var23;
            }

            x += var23.xCoord - var15;
            y += (var25.yCoord + var26.yCoord) / 2.0D - var17;
            z += var23.zCoord - var19;
            Vec3 var27 = var26.addVector(-var25.xCoord, -var25.yCoord, -var25.zCoord);
            if(var27.lengthVector() != 0.0D) {
                var27 = var27.normalize();
                Yaw = (float)(Math.atan2(var27.zCoord, var27.xCoord) * 180.0D / 3.141592653589793D);
                var24 = (float)(Math.atan(var27.yCoord) * 73.0D);
            }
        }

        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glRotatef(180.0F - Yaw, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-var24, 0.0F, 0.0F, 1.0F);
        float var31 = (float)minecart.getRollingAmplitude() - p_doRender_9_;
        float var32 = minecart.getDamage() - p_doRender_9_;
        if(var32 < 0.0F) {
            var32 = 0.0F;
        }

        if(var31 > 0.0F) {
            GL11.glRotatef(MathHelper.sin(var31) * var31 * var32 / 10.0F * (float)minecart.getRollingDirection(), 1.0F, 0.0F, 0.0F);
        }

        int var33 = minecart.getDisplayTileOffset();
        Block var28 = minecart.func_145820_n();
        int var29 = minecart.getDisplayTileData();
        if(var28.getRenderType() != -1) {
            GL11.glPushMatrix();
            this.bindTexture(TextureMap.locationBlocksTexture);
            float var30 = 0.75F;
            GL11.glScalef(var30, var30, var30);
            GL11.glTranslatef(0.0F, (float)var33 / 16.0F, 0.0F);
            this.func_147910_a(minecart, p_doRender_9_, var28, var29);
            GL11.glPopMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.bindEntityTexture(minecart);
        }

        GL11.glScalef(-1.0F, -1.0F, 1.0F);

        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, -1.0625F, 0.0F);
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        if (minecart instanceof NSPCT4) {
            GL11.glTranslated(0.0, -((NSPCT4) minecart).shiftY, 0.0);
        }
        modelCart.render(minecart, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        GL11.glPopMatrix();

        GL11.glPopMatrix();
    }
}
