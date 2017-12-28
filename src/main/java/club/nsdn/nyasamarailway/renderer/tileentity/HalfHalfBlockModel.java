package club.nsdn.nyasamarailway.renderer.tileentity;

/**
 * Created by drzzm32 on 2016.5.10.
 */

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class HalfHalfBlockModel extends ModelBase {
    ModelRenderer Shape2;

    public HalfHalfBlockModel() {
        textureWidth = 64;
        textureHeight = 20;

        Shape2 = new ModelRenderer(this, 0, 0);
        Shape2.addBox(0F, 0F, 0F, 16, 4, 16);
        Shape2.setRotationPoint(-8F, 20F, -8F);
        Shape2.setTextureSize(64, 20);
        Shape2.mirror = true;
        setRotation(Shape2, 0F, 0F, 0F);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(entity, f, f1, f2, f3, f4, f5);
        Shape2.render(f5);
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

