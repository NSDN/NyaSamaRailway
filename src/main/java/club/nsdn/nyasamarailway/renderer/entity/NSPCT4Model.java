package club.nsdn.nyasamarailway.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

/**
 * Created by drzzm32 on 2016.12.1.
 */

public class NSPCT4Model extends ModelBase {
    ModelRenderer Bottom;
    ModelRenderer L1;
    ModelRenderer L2;
    ModelRenderer L3;
    ModelRenderer R1;
    ModelRenderer R2;
    ModelRenderer R3;
    ModelRenderer Front1;
    ModelRenderer Front2;
    ModelRenderer Bottom1;
    ModelRenderer Bottom2;
    ModelRenderer Print21;
    ModelRenderer Print22;
    ModelRenderer Print11;
    ModelRenderer Print12;

    public NSPCT4Model() {
        textureWidth = 96;
        textureHeight = 32;

        Bottom = new ModelRenderer(this, 0, 0);
        Bottom.addBox(0F, 0F, 0F, 8, 1, 16);
        Bottom.setRotationPoint(-4F, 11F, -8F);
        Bottom.setTextureSize(96, 32);
        Bottom.mirror = true;
        setRotation(Bottom, 0F, 0F, 0F);
        L1 = new ModelRenderer(this, 0, 0);
        L1.addBox(0F, 0F, 0F, 1, 8, 16);
        L1.setRotationPoint(6F, 6F, -8F);
        L1.setTextureSize(96, 32);
        L1.mirror = true;
        setRotation(L1, 0F, 0F, 0F);
        L2 = new ModelRenderer(this, 0, 0);
        L2.addBox(0F, 0F, 0F, 2, 5, 18);
        L2.setRotationPoint(4F, 11F, -9F);
        L2.setTextureSize(96, 32);
        L2.mirror = true;
        setRotation(L2, 0F, 0F, 0F);
        L3 = new ModelRenderer(this, 0, 0);
        L3.addBox(0F, 0F, 0F, 1, 4, 22);
        L3.setRotationPoint(4F, 13F, -11F);
        L3.setTextureSize(96, 32);
        L3.mirror = true;
        setRotation(L3, 0F, 0F, 0F);
        R1 = new ModelRenderer(this, 0, 0);
        R1.addBox(0F, 0F, 0F, 1, 8, 16);
        R1.setRotationPoint(-7F, 6F, -8F);
        R1.setTextureSize(96, 32);
        R1.mirror = true;
        setRotation(R1, 0F, 0F, 0F);
        R2 = new ModelRenderer(this, 0, 0);
        R2.addBox(0F, 0F, 0F, 2, 5, 18);
        R2.setRotationPoint(-6F, 11F, -9F);
        R2.setTextureSize(96, 32);
        R2.mirror = true;
        setRotation(R2, 0F, 0F, 0F);
        R3 = new ModelRenderer(this, 0, 0);
        R3.addBox(0F, 0F, 0F, 1, 4, 22);
        R3.setRotationPoint(-5F, 13F, -11F);
        R3.setTextureSize(96, 32);
        R3.mirror = true;
        setRotation(R3, 0F, 0F, 0F);
        Front1 = new ModelRenderer(this, 0, 0);
        Front1.addBox(0F, 0F, 0F, 8, 5, 1);
        Front1.setRotationPoint(0F, 6F, -12F);
        Front1.setTextureSize(96, 32);
        Front1.mirror = true;
        setRotation(Front1, 0F, -0.5235988F, 0F);
        Front2 = new ModelRenderer(this, 0, 0);
        Front2.addBox(-8F, 0F, 0F, 8, 5, 1);
        Front2.setRotationPoint(0F, 6F, -12F);
        Front2.setTextureSize(96, 32);
        Front2.mirror = true;
        setRotation(Front2, 0F, 0.5235988F, 0F);
        Bottom1 = new ModelRenderer(this, 0, 0);
        Bottom1.addBox(0F, 0F, 0F, 6, 1, 3);
        Bottom1.setRotationPoint(0F, 11F, -11F);
        Bottom1.setTextureSize(96, 32);
        Bottom1.mirror = true;
        setRotation(Bottom1, 0F, -0.5235988F, 0F);
        Bottom2 = new ModelRenderer(this, 0, 0);
        Bottom2.addBox(-6F, 0F, 0F, 6, 1, 3);
        Bottom2.setRotationPoint(0F, 11F, -11F);
        Bottom2.setTextureSize(96, 32);
        Bottom2.mirror = true;
        setRotation(Bottom2, 0F, 0.5235988F, 0F);
        Print21 = new ModelRenderer(this, 64, 0);
        Print21.addBox(-8F, 0F, -0.1F, 8, 5, 0);
        Print21.setRotationPoint(0F, 6F, -12F);
        Print21.setTextureSize(96, 32);
        Print21.mirror = true;
        setRotation(Print21, 0F, 0.5235988F, 0F);
        Print22 = new ModelRenderer(this, 64, 5);
        Print22.addBox(0F, 0F, -0.1F, 8, 5, 0);
        Print22.setRotationPoint(0F, 6F, -12F);
        Print22.setTextureSize(96, 32);
        Print22.mirror = true;
        setRotation(Print22, 0F, -0.5235988F, 0F);
        Print11 = new ModelRenderer(this, 64, 0);
        Print11.addBox(0F, 0F, 0F, 0, 8, 16);
        Print11.setRotationPoint(7.1F, 6F, -8F);
        Print11.setTextureSize(96, 32);
        Print11.mirror = true;
        setRotation(Print11, 0F, 0F, 0F);
        Print12 = new ModelRenderer(this, 64, 8);
        Print12.addBox(0F, 0F, 0F, 0, 8, 16);
        Print12.setRotationPoint(-7.1F, 6F, -8F);
        Print12.setTextureSize(96, 32);
        Print12.mirror = true;
        setRotation(Print12, 0F, 0F, 0F);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        Bottom.render(f5);
        L1.render(f5);
        L2.render(f5);
        L3.render(f5);
        R1.render(f5);
        R2.render(f5);
        R3.render(f5);
        Front1.render(f5);
        Front2.render(f5);
        Bottom1.render(f5);
        Bottom2.render(f5);
        Print21.render(f5);
        Print22.render(f5);
        Print11.render(f5);
        Print12.render(f5);

        GL11.glPushMatrix();
        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
        Front1.render(f5);
        Front2.render(f5);
        Bottom1.render(f5);
        Bottom2.render(f5);
        Print21.render(f5);
        Print22.render(f5);
        GL11.glPopMatrix();
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
