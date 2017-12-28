package club.nsdn.nyasamarailway.renderer.tileentity.Rail;

/**
 * Created by drzzm32 on 2016.12.2.
 */

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

public class RailMonoMagnetTurnedModel extends ModelBase {

    ModelRenderer Body;

    public RailMonoMagnetTurnedModel() {
        textureWidth = 6;
        textureHeight = 17;

        Body = new ModelRenderer(this, -17, 0);
        Body.addBox(0F, 15.9F, 0F, 6, 0, 17);
        Body.setRotationPoint(-4F, 12F, -8F);
        Body.setTextureSize(6, 17);
        Body.mirror = true;
        setRotation(Body, 0F, 0.7853982F, 0F);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(entity, f, f1, f2, f3, f4, f5);
        GL11.glPushMatrix();
        GL11.glTranslatef(0.5F, 0.0F, -0.5F);
        GL11.glTranslatef(0.0075F, 0.0F, -0.0075F);
        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
        Body.render(f5);
        GL11.glPopMatrix();
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

