package club.nsdn.nyasamarailway.renderer.entity;

import club.nsdn.nyasamarailway.entity.cart.NSPCT8W;
import club.nsdn.nyasamarailway.renderer.RendererHelper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.entity.RenderMinecart;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.model.obj.WavefrontObject;
import org.lwjgl.opengl.GL11;

/**
 * Created by drzzm32 on 2018.2.24.
 */
public class NSPCT8WRenderer extends RenderMinecart {

    private final WavefrontObject modelBase = new WavefrontObject(
            new ResourceLocation("nyasamarailway", "models/carts/nspc_8w_base.obj")
    );
    private final WavefrontObject modelPrint = new WavefrontObject(
            new ResourceLocation("nyasamarailway", "models/carts/nspc_8w_print.obj")
    );
    private final WavefrontObject modelHead = new WavefrontObject(
            new ResourceLocation("nyasamarailway", "models/carts/nspc_8w_head.obj")
    );

    private final ResourceLocation textureBase = new ResourceLocation(
            "nyasamarailway", "textures/carts/nspc_8w_base.png"
    );
    private final ResourceLocation texturePrint = new ResourceLocation(
            "nyasamarailway", "textures/carts/nspc_8w_print.png"
    );

    public NSPCT8WRenderer() {
        super();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityMinecart cart) {
        return textureBase;
    }

    @Override
    public void doRender(EntityMinecart minecart, double x, double y, double z, float iYaw, float iPitch) {
        GL11.glPushMatrix();
        this.bindEntityTexture(minecart);
        long id = (long)minecart.getEntityId() * 493286711L;
        id = id * id * 4392167121L + id * 98761L;
        float tx = (((float)(id >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        float ty = (((float)(id >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        float tz = (((float)(id >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        GL11.glTranslatef(tx, ty, tz);
        double vx = minecart.lastTickPosX + (minecart.posX - minecart.lastTickPosX) * (double)iPitch;
        double vy = minecart.lastTickPosY + (minecart.posY - minecart.lastTickPosY) * (double)iPitch;
        double vz = minecart.lastTickPosZ + (minecart.posZ - minecart.lastTickPosZ) * (double)iPitch;
        double par = 0.30000001192092896D;
        Vec3 vec = minecart.func_70489_a(vx, vy, vz);
        float pitch = minecart.prevRotationPitch + (minecart.rotationPitch - minecart.prevRotationPitch) * iPitch;
        if(vec != null) {
            Vec3 vecA = minecart.func_70495_a(vx, vy, vz, par);
            Vec3 vecB = minecart.func_70495_a(vx, vy, vz, -par);
            if(vecA == null) {
                vecA = vec;
            }

            if(vecB == null) {
                vecB = vec;
            }

            x += vec.xCoord - vx;
            y += (vecA.yCoord + vecB.yCoord) / 2.0D - vy;
            z += vec.zCoord - vz;
            Vec3 vecBA = vecB.addVector(-vecA.xCoord, -vecA.yCoord, -vecA.zCoord);
            if(vecBA.lengthVector() != 0.0D) {
                vecBA = vecBA.normalize();
                iYaw = (float)(Math.atan2(vecBA.zCoord, vecBA.xCoord) * 180.0D / 3.141592653589793D);
                pitch = (float)(Math.atan(vecBA.yCoord) * 73.0D);
            }
        }

        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glRotatef(180.0F - iYaw, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-pitch, 0.0F, 0.0F, 1.0F);
        float rollA = (float)minecart.getRollingAmplitude() - iPitch;
        float rollB = minecart.getDamage() - iPitch;
        if(rollB < 0.0F) {
            rollB = 0.0F;
        }

        if(rollA > 0.0F) {
            GL11.glRotatef(MathHelper.sin(rollA) * rollA * rollB / 10.0F * (float)minecart.getRollingDirection(), 1.0F, 0.0F, 0.0F);
        }

        int tileOffset = minecart.getDisplayTileOffset();
        Block block = minecart.func_145820_n();
        int tileData = minecart.getDisplayTileData();
        if(block.getRenderType() != -1) {
            GL11.glPushMatrix();
            this.bindTexture(TextureMap.locationBlocksTexture);
            float var30 = 0.75F;
            GL11.glScalef(var30, var30, var30);
            GL11.glTranslatef(0.0F, (float)tileOffset / 16.0F, 0.0F);
            this.func_147910_a(minecart, iPitch, block, tileData);
            GL11.glPopMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.bindEntityTexture(minecart);
        }

        if (minecart instanceof NSPCT8W) {
            NSPCT8W cart = (NSPCT8W) minecart;
            GL11.glTranslated(0.0, cart.getShiftYCnt(), 0.0);
            RendererHelper.renderWithResource(modelHead, textureBase);
        } else if (minecart instanceof NSPCT8W.Container) {
            RendererHelper.renderWithResource(modelBase, textureBase);
            RendererHelper.renderWithResource(modelPrint, texturePrint);
        }

        GL11.glPopMatrix();
    }
}
