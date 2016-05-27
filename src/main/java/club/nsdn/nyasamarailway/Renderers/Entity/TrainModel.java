package club.nsdn.nyasamarailway.Renderers.Entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

/**
 * Created by drzzm32 on 2016.5.23.
 */

public class TrainModel extends ModelBase {
    ModelRenderer f1;
    ModelRenderer f2;
    ModelRenderer f3;
    ModelRenderer f4;
    ModelRenderer f5;
    ModelRenderer f6;
    ModelRenderer f7;
    ModelRenderer c1;
    ModelRenderer c2;
    ModelRenderer c3;
    ModelRenderer c4;
    ModelRenderer c5;
    ModelRenderer c6;
    ModelRenderer c7;

    public TrainModel() {
        textureWidth = 38;
        textureHeight = 17;

        f1 = new ModelRenderer(this, 0, 0);
        f1.addBox(0F, 0F, 0F, 2, 3, 2);
        f1.setRotationPoint(-1F, 16F, -33F);
        f1.setTextureSize(38, 17);
        f1.mirror = true;
        setRotation(f1, 0F, 0F, 0F);
        f2 = new ModelRenderer(this, 0, 0);
        f2.addBox(0F, 0F, 0F, 4, 2, 4);
        f2.setRotationPoint(-2F, 14F, -34F);
        f2.setTextureSize(38, 17);
        f2.mirror = true;
        setRotation(f2, 0F, 0F, 0F);
        f3 = new ModelRenderer(this, 0, 0);
        f3.addBox(0F, 0F, 0F, 2, 2, 13);
        f3.setRotationPoint(0F, 14F, -32F);
        f3.setTextureSize(38, 17);
        f3.mirror = true;
        setRotation(f3, 0F, 0.5235988F, 0F);
        f4 = new ModelRenderer(this, 0, 0);
        f4.addBox(0F, 0F, 0F, 2, 2, 13);
        f4.setRotationPoint(-1.7F, 14F, -33F);
        f4.setTextureSize(38, 17);
        f4.mirror = true;
        setRotation(f4, 0F, -0.5235988F, 0F);
        f5 = new ModelRenderer(this, 0, 0);
        f5.addBox(0F, 0F, 0F, 2, 2, 10);
        f5.setRotationPoint(-8F, 14F, -21.8F);
        f5.setTextureSize(38, 17);
        f5.mirror = true;
        setRotation(f5, -0.6457718F, 0F, 0F);
        f6 = new ModelRenderer(this, 0, 0);
        f6.addBox(0F, 0F, 0F, 2, 2, 10);
        f6.setRotationPoint(6F, 14F, -21.8F);
        f6.setTextureSize(38, 17);
        f6.mirror = true;
        setRotation(f6, -0.6457718F, 0F, 0F);
        f7 = new ModelRenderer(this, 0, 0);
        f7.addBox(0F, 0F, 0F, 17, 2, 2);
        f7.setRotationPoint(-8.5F, 14F, -22F);
        f7.setTextureSize(38, 17);
        f7.mirror = true;
        setRotation(f7, 0F, 0F, 0F);
        c1 = new ModelRenderer(this, 0, 0);
        c1.addBox(0F, 0F, 0F, 2, 2, 15);
        c1.setRotationPoint(-8F, 20F, -15F);
        c1.setTextureSize(38, 17);
        c1.mirror = true;
        setRotation(c1, 0F, 0F, 0F);
        c2 = new ModelRenderer(this, 0, 0);
        c2.addBox(0F, 0F, 0F, 2, 2, 15);
        c2.setRotationPoint(6F, 20F, -15F);
        c2.setTextureSize(38, 17);
        c2.mirror = true;
        setRotation(c2, 0F, 0F, 0F);
        c3 = new ModelRenderer(this, 0, 0);
        c3.addBox(0F, 0F, 0F, 17, 2, 2);
        c3.setRotationPoint(-8.5F, 20F, -15F);
        c3.setTextureSize(38, 17);
        c3.mirror = true;
        setRotation(c3, 0F, 0F, 0F);
        c4 = new ModelRenderer(this, 0, 0);
        c4.addBox(0F, 0F, 0F, 1, 2, 13);
        c4.setRotationPoint(6F, 20F, -12F);
        c4.setTextureSize(38, 17);
        c4.mirror = true;
        setRotation(c4, 0F, -1.256637F, 0F);
        c5 = new ModelRenderer(this, 0, 0);
        c5.addBox(0F, 0F, 0F, 13, 2, 1);
        c5.setRotationPoint(-6F, 20F, -12F);
        c5.setTextureSize(38, 17);
        c5.mirror = true;
        setRotation(c5, 0F, -0.3141593F, 0F);
        c6 = new ModelRenderer(this, 0, 0);
        c6.addBox(0F, 0F, 0F, 1, 2, 13);
        c6.setRotationPoint(6F, 20F, -6F);
        c6.setTextureSize(38, 17);
        c6.mirror = true;
        setRotation(c6, 0F, -1.256637F, 0F);
        c7 = new ModelRenderer(this, 0, 0);
        c7.addBox(0F, 0F, 0F, 13, 2, 1);
        c7.setRotationPoint(-6F, 20F, -6F);
        c7.setTextureSize(38, 17);
        c7.mirror = true;
        setRotation(c7, 0F, -0.3141593F, 0F);
    }

    public void render(Entity entity, float x, float y, float z, float Yaw, float Pitch, float v) {
        f1.render(v);
        f2.render(v);
        f3.render(v);
        f4.render(v);
        f5.render(v);
        f6.render(v);
        f7.render(v);
        c1.render(v);
        c2.render(v);
        c3.render(v);
        c4.render(v);
        c5.render(v);
        c6.render(v);
        c7.render(v);
        GL11.glPushMatrix();
        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
        f1.render(v);
        f2.render(v);
        f3.render(v);
        f4.render(v);
        f5.render(v);
        f6.render(v);
        f7.render(v);
        c1.render(v);
        c2.render(v);
        c3.render(v);
        c4.render(v);
        c5.render(v);
        c6.render(v);
        c7.render(v);
        GL11.glPopMatrix();
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
