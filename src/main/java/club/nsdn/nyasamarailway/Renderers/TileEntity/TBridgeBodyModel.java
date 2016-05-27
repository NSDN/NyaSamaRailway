package club.nsdn.nyasamarailway.Renderers.TileEntity;

/**
 * Created by drzzm32 on 2016.5.10.
 */

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class TBridgeBodyModel extends ModelBase {
    ModelRenderer Shape2;
    ModelRenderer Shape3;
    ModelRenderer Shape4;
    boolean hasRib;

    public TBridgeBodyModel(boolean hasRib) {
        textureWidth = 48;
        textureHeight = 32;

        Shape2 = new ModelRenderer(this, 0, 0);
        Shape2.addBox(0F, 0F, 0F, 8, 16, 16);
        Shape2.setRotationPoint(-4F, 8F, -8F);
        Shape2.setTextureSize(48, 32);
        Shape2.mirror = true;
        setRotation(Shape2, 0F, 0F, 0F);

        if (hasRib) {
            Shape3 = new ModelRenderer(this, 0, 0);
            Shape3.addBox(0F, 0F, 0F, 2, 16, 2);
            Shape3.setRotationPoint(4F, 8F, -1F);
            Shape3.setTextureSize(48, 32);
            Shape3.mirror = true;
            setRotation(Shape3, 0F, 0F, 0F);
            Shape4 = new ModelRenderer(this, 0, 0);
            Shape4.addBox(0F, 0F, 0F, 2, 16, 2);
            Shape4.setRotationPoint(-6F, 8F, -1F);
            Shape4.setTextureSize(48, 32);
            Shape4.mirror = true;
            setRotation(Shape4, 0F, 0F, 0F);
            this.hasRib = hasRib;
        }
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(entity, f, f1, f2, f3, f4, f5);
        Shape2.render(f5);
        if (hasRib) {
            Shape3.render(f5);
            Shape4.render(f5);
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

