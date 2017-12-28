package club.nsdn.nyasamarailway.renderer.tileentity;

/**
 * Created by drzzm32 on 2016.5.5.
 */

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class TrackPlateModel extends ModelBase {
    ModelRenderer Shape3;
    ModelRenderer Shape8;
    ModelRenderer Shape9;

    public TrackPlateModel() {
        textureWidth = 64;
        textureHeight = 30;

        Shape3 = new ModelRenderer(this, 0, 0);
        Shape3.addBox(0F, 0F, 0F, 16, 14, 16);
        Shape3.setRotationPoint(-8F, 10F, -8F);
        Shape3.setTextureSize(64, 30);
        Shape3.mirror = true;
        setRotation(Shape3, 0F, 0F, 0F);
        Shape8 = new ModelRenderer(this, 0, 0);
        Shape8.addBox(0F, 0F, 0F, 4, 2, 16);
        Shape8.setRotationPoint(-7F, 8F, -8F);
        Shape8.setTextureSize(64, 30);
        Shape8.mirror = true;
        setRotation(Shape8, 0F, 0F, 0F);
        Shape9 = new ModelRenderer(this, 0, 0);
        Shape9.addBox(0F, 0F, 0F, 4, 2, 16);
        Shape9.setRotationPoint(3F, 8F, -8F);
        Shape9.setTextureSize(64, 30);
        Shape9.mirror = true;
        setRotation(Shape9, 0F, 0F, 0F);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(entity, f, f1, f2, f3, f4, f5);
        Shape3.render(f5);
        Shape8.render(f5);
        Shape9.render(f5);
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

