package club.nsdn.nyasamarailway.Renderers.TileEntity;

/**
 * Created by drzzm32 on 2016.5.22.
 */

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class PlatformModel extends ModelBase {
    public ModelRenderer Shape1;
    public ModelRenderer Shape2;

    public PlatformModel() {
        textureWidth = 56;
        textureHeight = 28;

        Shape1 = new ModelRenderer(this, 0, 0);
        Shape1.addBox(0F, 0F, 0F, 16, 16, 12, 0F);
        Shape1.setRotationPoint(-8F, 8F, -4F);
        Shape1.rotateAngleX = 0F;
        Shape1.rotateAngleY = 0F;
        Shape1.rotateAngleZ = 0F;
        Shape1.mirror = false;
        Shape2 = new ModelRenderer(this, 0, 0);
        Shape2.addBox(0F, 0F, 0F, 16, 2, 2, 0F);
        Shape2.setRotationPoint(-8F, 8F, -6F);
        Shape2.rotateAngleX = 0F;
        Shape2.rotateAngleY = 0F;
        Shape2.rotateAngleZ = 0F;
        Shape2.mirror = false;
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(entity, f, f1, f2, f3, f4, f5);
        Shape1.render(f5);
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

