package club.nsdn.nyasamarailway.renderer.tileentity.Rail;

/**
 * Created by drzzm32 on 2016.11.29.
 */

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class RailMonoTurnedModel extends ModelBase {
    ModelRenderer Base;
    ModelRenderer Left;
    ModelRenderer Right;
    ModelRenderer BottomL;
    ModelRenderer BottomR;
    ModelRenderer LeftFace;
    ModelRenderer RightFace;

    public RailMonoTurnedModel() {
        textureWidth = 96;
        textureHeight = 48;

        Base = new ModelRenderer(this, 0, 0);
        Base.addBox(0F, 0F, 0F, 4, 12, 16);
        Base.setRotationPoint(-3.3F, 12F, -8F);
        Base.setTextureSize(64, 32);
        Base.mirror = true;
        setRotation(Base, 0F, 0.7853982F, 0F);
        Left = new ModelRenderer(this, 0, 0);
        Left.addBox(0F, 0F, 0F, 2, 4, 20);
        Left.setRotationPoint(-6F, 12F, -8F);
        Left.setTextureSize(64, 32);
        Left.mirror = true;
        setRotation(Left, 0F, 0.7853982F, 0F);
        Right = new ModelRenderer(this, 0, 0);
        Right.addBox(0F, 0F, 0F, 2, 4, 9);
        Right.setRotationPoint(2.3F, 12F, -8F);
        Right.setTextureSize(64, 32);
        Right.mirror = true;
        setRotation(Right, 0F, 0.7853982F, 0F);
        BottomL = new ModelRenderer(this, 0, 0);
        BottomL.addBox(0F, 0F, 0F, 6, 6, 16);
        BottomL.setRotationPoint(-3.3F, 19F, -8F);
        BottomL.setTextureSize(64, 32);
        BottomL.mirror = true;
        setRotation(BottomL, 0F, 0.7853982F, 0.5934119F);
        BottomR = new ModelRenderer(this, 0, 0);
        BottomR.addBox(-6F, 0F, 0F, 6, 6, 9);
        BottomR.setRotationPoint(2.3F, 19F, -8F);
        BottomR.setTextureSize(64, 32);
        BottomR.mirror = true;
        setRotation(BottomR, 0F, 0.7853982F, -0.5934119F);
        LeftFace = new ModelRenderer(this, 48, 0);
        LeftFace.addBox(-0.1F, 0F, 0F, 0, 4, 20);
        LeftFace.setRotationPoint(-6F, 12F, -8F);
        LeftFace.setTextureSize(64, 32);
        LeftFace.mirror = true;
        setRotation(LeftFace, 0F, 0.7853982F, 0F);
        RightFace = new ModelRenderer(this, 48, 32);
        RightFace.addBox(2.1F, 0F, 0F, 0, 4, 9);
        RightFace.setRotationPoint(2.3F, 12F, -8F);
        RightFace.setTextureSize(64, 32);
        RightFace.mirror = true;
        setRotation(RightFace, 0F, 0.7853982F, 0F);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(entity, f, f1, f2, f3, f4, f5);
        Base.render(f5);
        Left.render(f5);
        Right.render(f5);
        BottomL.render(f5);
        BottomR.render(f5);
        LeftFace.render(f5);
        RightFace.render(f5);
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

