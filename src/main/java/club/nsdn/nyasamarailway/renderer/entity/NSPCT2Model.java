package club.nsdn.nyasamarailway.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Created by drzzm32 on 2016.5.26.
 */

public class NSPCT2Model extends ModelBase {

    ModelRenderer Top;
    ModelRenderer Bottom;
    ModelRenderer Left1;
    ModelRenderer Left2;
    ModelRenderer Left3;
    ModelRenderer Left4;
    ModelRenderer Right1;
    ModelRenderer Right2;
    ModelRenderer Right3;
    ModelRenderer Right4;
    ModelRenderer BottomLeft;
    ModelRenderer BottomRight;
    ModelRenderer TopLeft;
    ModelRenderer TopRight;

    public NSPCT2Model() {
        textureWidth = 128;
        textureHeight = 128;

        Top = new ModelRenderer(this, 0, 0);
        Top.addBox(0F, 0F, 0F, 35, 1, 24, 0F);
        Top.setRotationPoint(-17.53333F, -41.3F, -12F);
        Top.rotateAngleX = 0F;
        Top.rotateAngleY = 0F;
        Top.rotateAngleZ = 0F;
        Top.mirror = false;
        Bottom = new ModelRenderer(this, 0, 0);
        Bottom.addBox(0F, 0F, 0F, 32, 1, 24, 0F);
        Bottom.setRotationPoint(-16F, 18F, -12F);
        Bottom.rotateAngleX = 0F;
        Bottom.rotateAngleY = 0F;
        Bottom.rotateAngleZ = 0F;
        Bottom.mirror = false;
        Left1 = new ModelRenderer(this, 0, 0);
        Left1.addBox(0F, 0F, 0F, 1, 12, 16, 0F);
        Left1.setRotationPoint(23F, -35F, -8F);
        Left1.rotateAngleX = 0F;
        Left1.rotateAngleY = 0F;
        Left1.rotateAngleZ = 0F;
        Left1.mirror = false;
        Left2 = new ModelRenderer(this, 0, 0);
        Left2.addBox(0F, 0F, 0F, 1, 12, 16, 0F);
        Left2.setRotationPoint(23F, -7F, -8F);
        Left2.rotateAngleX = 0F;
        Left2.rotateAngleY = 0F;
        Left2.rotateAngleZ = 0F;
        Left2.mirror = false;
        Left3 = new ModelRenderer(this, 0, 0);
        Left3.addBox(0F, 0F, 0F, 1, 40, 4, 0F);
        Left3.setRotationPoint(23F, -35F, -12F);
        Left3.rotateAngleX = 0F;
        Left3.rotateAngleY = 0F;
        Left3.rotateAngleZ = 0F;
        Left3.mirror = false;
        Left4 = new ModelRenderer(this, 0, 0);
        Left4.addBox(0F, 0F, 0F, 1, 40, 4, 0F);
        Left4.setRotationPoint(23F, -35F, 8F);
        Left4.rotateAngleX = 0F;
        Left4.rotateAngleY = 0F;
        Left4.rotateAngleZ = 0F;
        Left4.mirror = false;
        Right1 = new ModelRenderer(this, 0, 0);
        Right1.addBox(0F, 0F, 0F, 1, 12, 16, 0F);
        Right1.setRotationPoint(-24F, -35F, -8F);
        Right1.rotateAngleX = 0F;
        Right1.rotateAngleY = 0F;
        Right1.rotateAngleZ = 0F;
        Right1.mirror = false;
        Right2 = new ModelRenderer(this, 0, 0);
        Right2.addBox(0F, 0F, 0F, 1, 12, 16, 0F);
        Right2.setRotationPoint(-24F, -7F, -8F);
        Right2.rotateAngleX = 0F;
        Right2.rotateAngleY = 0F;
        Right2.rotateAngleZ = 0F;
        Right2.mirror = false;
        Right3 = new ModelRenderer(this, 0, 0);
        Right3.addBox(0F, 0F, 0F, 1, 40, 4, 0F);
        Right3.setRotationPoint(-24F, -35F, -12F);
        Right3.rotateAngleX = 0F;
        Right3.rotateAngleY = 0F;
        Right3.rotateAngleZ = 0F;
        Right3.mirror = false;
        Right4 = new ModelRenderer(this, 0, 0);
        Right4.addBox(0F, 0F, 0F, 1, 40, 4, 0F);
        Right4.setRotationPoint(-24F, -35F, 8F);
        Right4.rotateAngleX = 0F;
        Right4.rotateAngleY = 0F;
        Right4.rotateAngleZ = 0F;
        Right4.mirror = false;
        BottomLeft = new ModelRenderer(this, 0, 0);
        BottomLeft.addBox(0F, -1F, 0F, 16, 1, 24, 0F);
        BottomLeft.setRotationPoint(16F, 19F, -12F);
        BottomLeft.rotateAngleX = 0F;
        BottomLeft.rotateAngleY = 0F;
        BottomLeft.rotateAngleZ = -1.047198F;
        BottomLeft.mirror = false;
        BottomRight = new ModelRenderer(this, 0, 0);
        BottomRight.addBox(0F, 0F, 0F, 16, 1, 24, 0F);
        BottomRight.setRotationPoint(-16F, 19F, -12F);
        BottomRight.rotateAngleX = 0F;
        BottomRight.rotateAngleY = 0F;
        BottomRight.rotateAngleZ = -2.094395F;
        BottomRight.mirror = false;
        TopLeft = new ModelRenderer(this, 0, 0);
        TopLeft.addBox(0F, 0F, 0F, 9, 1, 24, 0F);
        TopLeft.setRotationPoint(17.6F, -41.3F, -12F);
        TopLeft.rotateAngleX = 0F;
        TopLeft.rotateAngleY = 0F;
        TopLeft.rotateAngleZ = 0.7853982F;
        TopLeft.mirror = false;
        TopRight = new ModelRenderer(this, 0, 0);
        TopRight.addBox(0F, 0F, 0F, 1, 9, 24, 0F);
        TopRight.setRotationPoint(-17.6F, -41.3F, -12F);
        TopRight.rotateAngleX = 0F;
        TopRight.rotateAngleY = 0F;
        TopRight.rotateAngleZ = 0.7853982F;
        TopRight.mirror = false;
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        Top.render(f5);
        Bottom.render(f5);
        Left1.render(f5);
        Left2.render(f5);
        Left3.render(f5);
        Left4.render(f5);
        Right1.render(f5);
        Right2.render(f5);
        Right3.render(f5);
        Right4.render(f5);
        BottomLeft.render(f5);
        BottomRight.render(f5);
        TopLeft.render(f5);
        TopRight.render(f5);
    }
}
