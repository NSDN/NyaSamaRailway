package club.nsdn.nyasamarailway.renderer.tileentity;

/**
 * Created by drzzm32 on 2016.5.10.
 */

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

public class TBridgeShoulderModel extends ModelBase {
    ModelRenderer Shape1;
    ModelRenderer Shape2;
    ModelRenderer Shape3;

    public TBridgeShoulderModel() {
        textureWidth = 56;
        textureHeight = 24;

        Shape1 = new ModelRenderer(this, 0, 0);
        Shape1.addBox(0F, 0F, 0F, 4, 8, 16);
        Shape1.setRotationPoint(-8F, 16F, -8F);
        Shape1.setTextureSize(64, 24);
        Shape1.mirror = true;
        setRotation(Shape1, 0F, 0F, 0F);
        Shape2 = new ModelRenderer(this, 0, 0);
        Shape2.addBox(0F, 0F, 0F, 12, 4, 16);
        Shape2.setRotationPoint(-4F, 20F, -8F);
        Shape2.setTextureSize(64, 24);
        Shape2.mirror = true;
        setRotation(Shape2, 0F, 0F, 0F);
        Shape3 = new ModelRenderer(this, 0, 0);
        Shape3.addBox(0F, 0F, 0F, 12, 4, 16);
        Shape3.setRotationPoint(-4F, 16F, -8F);
        Shape3.setTextureSize(64, 24);
        Shape3.mirror = true;
        setRotation(Shape3, 0F, 0F, 0.3490659F);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(entity, f, f1, f2, f3, f4, f5);

        GL11.glPushMatrix();
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        Shape1.render(f5);
        Shape2.render(f5);
        Shape3.render(f5);
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

