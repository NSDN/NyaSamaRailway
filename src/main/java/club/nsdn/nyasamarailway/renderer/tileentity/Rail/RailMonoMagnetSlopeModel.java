package club.nsdn.nyasamarailway.renderer.tileentity.Rail;

/**
 * Created by drzzm32 on 2016.12.2.
 */

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

public class RailMonoMagnetSlopeModel extends ModelBase {

    ModelRenderer Body;

    public RailMonoMagnetSlopeModel() {
        textureWidth = 8;
        textureHeight = 31;

        Body = new ModelRenderer(this, -31, 0);
        Body.addBox(0F, 0F, 0F, 8, 0, 31);
        Body.setRotationPoint(-4F, 49.5F, -14.1F);
        Body.setTextureSize(64, 32);
        Body.mirror = false;
        setRotation(Body, 0.7853982F, 0F, 0F);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(entity, f, f1, f2, f3, f4, f5);

        if (entity != null) {
            GL11.glPushMatrix();
            GL11.glRotatef(180.0F, 0.0F, 1.0F, 1.0F);
            GL11.glTranslatef(0.0F, -2.61F, 2.61F);
            Body.render(f5);
            GL11.glPopMatrix();
        } else {
            Body.render(f5);
        }
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public void setRotationAngles(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
    }


}

