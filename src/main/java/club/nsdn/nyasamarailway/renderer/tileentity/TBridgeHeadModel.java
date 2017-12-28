package club.nsdn.nyasamarailway.renderer.tileentity;

/**
 * Created by drzzm32 on 2016.5.10.
 */

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class TBridgeHeadModel extends ModelBase {
    ModelRenderer Shape1;
    ModelRenderer Shape2;
    ModelRenderer Shape3;
    ModelRenderer Shape4;
    ModelRenderer Shape5;
    ModelRenderer Shape6;
    boolean hasRib;

    public TBridgeHeadModel(boolean hasRib) {
        textureWidth = 64;
        textureHeight = 24;

        Shape1 = new ModelRenderer(this, 0, 0);
        Shape1.addBox(0F, 0F, 0F, 16, 8, 16);
        Shape1.setRotationPoint(-8F, 8F, -8F);
        Shape1.setTextureSize(64, 24);
        Shape1.mirror = true;
        setRotation(Shape1, 0F, 0F, 0F);
        Shape2 = new ModelRenderer(this, 0, 0);
        Shape2.addBox(0F, 0F, 0F, 8, 8, 16);
        Shape2.setRotationPoint(-4F, 16F, -8F);
        Shape2.setTextureSize(64, 24);
        Shape2.mirror = true;
        setRotation(Shape2, 0F, 0F, 0F);

        if (hasRib) {
            Shape3 = new ModelRenderer(this, 0, 0);
            Shape3.addBox(0F, 0F, 0F, 2, 8, 2);
            Shape3.setRotationPoint(4F, 16F, -1F);
            Shape3.setTextureSize(64, 24);
            Shape3.mirror = true;
            setRotation(Shape3, 0F, 0F, 0F);
            Shape4 = new ModelRenderer(this, 0, 0);
            Shape4.addBox(0F, 0F, 0F, 2, 8, 2);
            Shape4.setRotationPoint(-6F, 16F, -1F);
            Shape4.setTextureSize(64, 24);
            Shape4.mirror = true;
            setRotation(Shape4, 0F, 0F, 0F);
            Shape5 = new ModelRenderer(this, 0, 0);
            Shape5.addBox(0F, 0F, 0F, 2, 2, 2);
            Shape5.setRotationPoint(4F, 16F, -1F);
            Shape5.setTextureSize(64, 24);
            Shape5.mirror = true;
            setRotation(Shape5, 0F, 0F, -0.7853982F);
            Shape6 = new ModelRenderer(this, 0, 0);
            Shape6.addBox(0F, 0F, 0F, 2, 2, 2);
            Shape6.setRotationPoint(-7F, 16F, -1F);
            Shape6.setTextureSize(64, 24);
            Shape6.mirror = true;
            setRotation(Shape6, 0F, 0F, -0.7853982F);
            this.hasRib = hasRib;
        }

    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(entity, f, f1, f2, f3, f4, f5);
        Shape1.render(f5);
        Shape2.render(f5);

        if (hasRib) {
            Shape3.render(f5);
            Shape4.render(f5);
            Shape5.render(f5);
            Shape6.render(f5);
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

