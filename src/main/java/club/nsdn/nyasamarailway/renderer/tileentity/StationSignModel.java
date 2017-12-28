package club.nsdn.nyasamarailway.renderer.tileentity;

/**
 * Created by drzzm32 on 2016.7.5.
 */

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class StationSignModel extends ModelBase {
    ModelRenderer Shape1;
    ModelRenderer Shape2;
    ModelRenderer Shape3;
    ModelRenderer Shape4;
    ModelRenderer Shape5;

    public StationSignModel() {
        textureWidth = 48;
        textureHeight = 48;

        Shape1 = new ModelRenderer(this, 0, 0);
        Shape1.addBox(-1F, 0F, -1F, 2, 32, 2);
        Shape1.setRotationPoint(11F, -16F, 0F);
        Shape1.setTextureSize(48, 48);
        Shape1.mirror = true;
        setRotation(Shape1, 0F, 0F, 0F);
        Shape2 = new ModelRenderer(this, 0, 0);
        Shape2.addBox(-1F, 0F, -1F, 2, 32, 2);
        Shape2.setRotationPoint(-11F, -16F, 0F);
        Shape2.setTextureSize(48, 48);
        Shape2.mirror = true;
        setRotation(Shape2, 0F, 0F, 0F);
        Shape3 = new ModelRenderer(this, 0, 0);
        Shape3.addBox(0F, 0F, 0F, 20, 14, 1);
        Shape3.setRotationPoint(-10F, -16F, -0.5F);
        Shape3.setTextureSize(48, 48);
        Shape3.mirror = true;
        setRotation(Shape3, 0F, 0F, 0F);
        Shape4 = new ModelRenderer(this, 0, 36);
        Shape4.addBox(-1F, 0F, -1F, 2, 8, 2);
        Shape4.setRotationPoint(11F, 16F, 0F);
        Shape4.setTextureSize(48, 48);
        Shape4.mirror = true;
        setRotation(Shape4, 0F, 0F, 0F);
        Shape5 = new ModelRenderer(this, 0, 36);
        Shape5.addBox(-1F, 0F, -1F, 2, 8, 2);
        Shape5.setRotationPoint(-11F, 16F, 0F);
        Shape5.setTextureSize(48, 48);
        Shape5.mirror = true;
        setRotation(Shape5, 0F, 0F, 0F);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(entity, f, f1, f2, f3, f4, f5);
        Shape1.render(f5);
        Shape2.render(f5);
        Shape3.render(f5);
        Shape4.render(f5);
        Shape5.render(f5);
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

