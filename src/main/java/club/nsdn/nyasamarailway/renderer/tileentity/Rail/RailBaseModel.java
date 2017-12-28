package club.nsdn.nyasamarailway.renderer.tileentity.Rail;

/**
 * Created by drzzm32 on 2016.7.23.
 */

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class RailBaseModel extends ModelBase {
    ModelRenderer Shape1;

    public RailBaseModel() {
        textureWidth = 64;
        textureHeight = 24;

        Shape1 = new ModelRenderer(this, 0, 0);
        Shape1.addBox(0F, 0F, 0F, 16, 8, 16);
        Shape1.setRotationPoint(-8F, 16F, -8F);
        Shape1.setTextureSize(64, 24);
        Shape1.mirror = true;
        setRotation(Shape1, 0F, 0F, 0F);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(entity, f, f1, f2, f3, f4, f5);
        Shape1.render(f5);
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

