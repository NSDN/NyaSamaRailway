package club.nsdn.nyasamarailway.Renderers.Entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

/**
 * Created by drzzm32 on 2016.12.5.
 */

public class NSPCT5HeadModel extends ModelBase {

    ModelRenderer Shape0;
    ModelRenderer Shape1;
    ModelRenderer Shape2;
    ModelRenderer Shape21;
    ModelRenderer Shape22;
    ModelRenderer Shape23;
    ModelRenderer Shape24;
    ModelRenderer Shape25;
    ModelRenderer Shape27;
    ModelRenderer Shape3;
    ModelRenderer Shape31;
    ModelRenderer Shape32;
    ModelRenderer Shape33;
    ModelRenderer Shape34;
    ModelRenderer Shape35;
    ModelRenderer Shape37;
    ModelRenderer Shape4;
    ModelRenderer Shape41;
    ModelRenderer Shape42;
    ModelRenderer Shape5;
    ModelRenderer Shape51;
    ModelRenderer Shape52;
    ModelRenderer Piece1;
    ModelRenderer Piece2;
    ModelRenderer ShapeHead1;
    ModelRenderer ShapeHead2;
    ModelRenderer ShapeHead3;
    ModelRenderer ShapeHead4;
    ModelRenderer ShapeHead5;
    ModelRenderer ShapeHead6;
    ModelRenderer ShapeHead7;
    ModelRenderer ShapeHead8;
    ModelRenderer ShapeHead9;
    ModelRenderer ShapeHead10;

    public NSPCT5HeadModel() {
        textureWidth = 128;
        textureHeight = 32;
        setTextureOffset("Piece1.ShapeC", 0, 0);
        setTextureOffset("Piece2.ShapeC", 0, 0);

        Shape0 = new ModelRenderer(this, 0, 0);
        Shape0.addBox(0F, 0F, 0F, 14, 2, 16);
        Shape0.setRotationPoint(-7F, -15.6F, -8F);
        Shape0.setTextureSize(128, 32);
        Shape0.mirror = true;
        setRotation(Shape0, 0F, 0F, 0F);
        Shape1 = new ModelRenderer(this, 0, 0);
        Shape1.addBox(0F, 0F, 0F, 12, 2, 16);
        Shape1.setRotationPoint(-6F, 9F, -8F);
        Shape1.setTextureSize(128, 32);
        Shape1.mirror = true;
        setRotation(Shape1, 0F, 0F, 0F);
        Shape2 = new ModelRenderer(this, 0, 0);
        Shape2.addBox(0F, 0F, 0F, 2, 8, 16);
        Shape2.setRotationPoint(-6F, 11F, -8F);
        Shape2.setTextureSize(128, 32);
        Shape2.mirror = true;
        setRotation(Shape2, 0F, 0F, 0F);
        Shape21 = new ModelRenderer(this, 0, 0);
        Shape21.addBox(0F, 0F, 0F, 1, 2, 16);
        Shape21.setRotationPoint(-5F, 19F, -8F);
        Shape21.setTextureSize(128, 32);
        Shape21.mirror = true;
        setRotation(Shape21, 0F, 0F, 0F);
        Shape22 = new ModelRenderer(this, 0, 0);
        Shape22.addBox(0F, 0F, 0F, 2, 6, 16);
        Shape22.setRotationPoint(-8F, 9F, -8F);
        Shape22.setTextureSize(128, 32);
        Shape22.mirror = true;
        setRotation(Shape22, 0F, 0F, 0F);
        Shape23 = new ModelRenderer(this, 0, 0);
        Shape23.addBox(0F, 0F, 0F, 1, 2, 16);
        Shape23.setRotationPoint(-7F, 15F, -8F);
        Shape23.setTextureSize(128, 32);
        Shape23.mirror = true;
        setRotation(Shape23, 0F, 0F, 0F);
        Shape24 = new ModelRenderer(this, 0, 0);
        Shape24.addBox(-2F, -10F, 0F, 2, 10, 16);
        Shape24.setRotationPoint(-7F, 12F, -8F);
        Shape24.setTextureSize(128, 32);
        Shape24.mirror = true;
        setRotation(Shape24, 0F, 0F, -0.3141593F);
        Shape25 = new ModelRenderer(this, 0, 0);
        Shape25.addBox(0F, 0F, 0F, 1, 2, 16);
        Shape25.setRotationPoint(-9F, 12F, -8F);
        Shape25.setTextureSize(128, 32);
        Shape25.mirror = true;
        setRotation(Shape25, 0F, 0F, 0F);
        Shape27 = new ModelRenderer(this, 0, 0);
        Shape27.addBox(0F, -10F, -2F, 6, 10, 2);
        Shape27.setRotationPoint(-12F, -6.9F, -8F);
        Shape27.setTextureSize(128, 32);
        Shape27.mirror = true;
        setRotation(Shape27, 0F, 0F, 0.5235988F);
        Shape3 = new ModelRenderer(this, 0, 0);
        Shape3.addBox(0F, 0F, 0F, 2, 8, 16);
        Shape3.setRotationPoint(4F, 11F, -8F);
        Shape3.setTextureSize(128, 32);
        Shape3.mirror = true;
        setRotation(Shape3, 0F, 0F, 0F);
        Shape31 = new ModelRenderer(this, 0, 0);
        Shape31.addBox(0F, 0F, 0F, 1, 2, 16);
        Shape31.setRotationPoint(4F, 19F, -8F);
        Shape31.setTextureSize(128, 32);
        Shape31.mirror = true;
        setRotation(Shape31, 0F, 0F, 0F);
        Shape32 = new ModelRenderer(this, 0, 0);
        Shape32.addBox(0F, 0F, 0F, 2, 6, 16);
        Shape32.setRotationPoint(6F, 9F, -8F);
        Shape32.setTextureSize(128, 32);
        Shape32.mirror = true;
        setRotation(Shape32, 0F, 0F, 0F);
        Shape33 = new ModelRenderer(this, 0, 0);
        Shape33.addBox(0F, 0F, 0F, 1, 2, 16);
        Shape33.setRotationPoint(6F, 15F, -8F);
        Shape33.setTextureSize(128, 32);
        Shape33.mirror = true;
        setRotation(Shape33, 0F, 0F, 0F);
        Shape34 = new ModelRenderer(this, 0, 0);
        Shape34.addBox(0F, -10F, 0F, 2, 10, 16);
        Shape34.setRotationPoint(7F, 12F, -8F);
        Shape34.setTextureSize(128, 32);
        Shape34.mirror = true;
        setRotation(Shape34, 0F, 0F, 0.3141593F);
        Shape35 = new ModelRenderer(this, 0, 0);
        Shape35.addBox(0F, 0F, 0F, 1, 2, 16);
        Shape35.setRotationPoint(8F, 12F, -8F);
        Shape35.setTextureSize(128, 32);
        Shape35.mirror = true;
        setRotation(Shape35, 0F, 0F, 0F);
        Shape37 = new ModelRenderer(this, 0, 0);
        Shape37.addBox(-2F, -10F, 0F, 2, 10, 16);
        Shape37.setRotationPoint(12F, -6.9F, -8F);
        Shape37.setTextureSize(128, 32);
        Shape37.mirror = true;
        setRotation(Shape37, 0F, 0F, -0.5235988F);
        Shape4 = new ModelRenderer(this, 96, -16);
        Shape4.addBox(0F, 0F, 0F, 0, 8, 16);
        Shape4.setRotationPoint(11.8F, 4F, -8F);
        Shape4.setTextureSize(128, 32);
        Shape4.mirror = true;
        setRotation(Shape4, 0F, 0F, 0.3141593F);
        Shape41 = new ModelRenderer(this, 96, 2);
        Shape41.addBox(0F, 0F, 0F, 0, 8, 16);
        Shape41.setRotationPoint(7.5F, -15F, -8F);
        Shape41.setTextureSize(128, 32);
        Shape41.mirror = true;
        setRotation(Shape41, 0F, 0F, -0.5235988F);
        Shape42 = new ModelRenderer(this, 62, -7);
        Shape42.addBox(0F, 0F, 0F, 0, 8, 16);
        Shape42.setRotationPoint(12.1F, -6F, -8F);
        Shape42.setTextureSize(128, 32);
        Shape42.mirror = true;
        setRotation(Shape42, 0F, 0F, 0F);
        Shape5 = new ModelRenderer(this, 112, -16);
        Shape5.addBox(0F, 0F, 0F, 0, 8, 16);
        Shape5.setRotationPoint(-11.8F, 4F, -8F);
        Shape5.setTextureSize(128, 32);
        Shape5.mirror = true;
        setRotation(Shape5, 0F, 0F, -0.3141593F);
        Shape51 = new ModelRenderer(this, 112, 2);
        Shape51.addBox(0F, 0F, 0F, 0, 8, 16);
        Shape51.setRotationPoint(-7.5F, -15F, -8F);
        Shape51.setTextureSize(128, 32);
        Shape51.mirror = true;
        setRotation(Shape51, 0F, 0F, 0.5235988F);
        Shape52 = new ModelRenderer(this, 78, -7);
        Shape52.addBox(0F, 0F, 0F, 0, 8, 16);
        Shape52.setRotationPoint(-12.1F, -6F, -8F);
        Shape52.setTextureSize(128, 32);
        Shape52.mirror = true;
        setRotation(Shape52, 0F, 0F, 0F);
        Piece1 = new ModelRenderer(this, "Piece1");
        Piece1.setRotationPoint(-12F, -6.9F, -8F);
        setRotation(Piece1, 0F, 0F, 0F);
        Piece1.mirror = true;
        Piece1.addBox("ShapeC", 0F, 0F, 0F, 2, 10, 16);
        Piece2 = new ModelRenderer(this, "Piece2");
        Piece2.setRotationPoint(10F, -6.9F, -8F);
        setRotation(Piece2, 0F, 0F, 0F);
        Piece2.mirror = true;
        Piece2.addBox("ShapeC", 0F, 0F, 0F, 2, 10, 16);
        ShapeHead1 = new ModelRenderer(this, 0, 0);
        ShapeHead1.addBox(-6F, -10F, -2F, 6, 10, 2);
        ShapeHead1.setRotationPoint(12F, -6.9F, -8F);
        ShapeHead1.setTextureSize(128, 32);
        ShapeHead1.mirror = true;
        setRotation(ShapeHead1, 0F, 0F, -0.5235988F);
        ShapeHead2 = new ModelRenderer(this, 0, 0);
        ShapeHead2.addBox(0F, -10F, 0F, 2, 10, 16);
        ShapeHead2.setRotationPoint(-12F, -6.9F, -8F);
        ShapeHead2.setTextureSize(128, 32);
        ShapeHead2.mirror = true;
        setRotation(ShapeHead2, 0F, 0F, 0.5235988F);
        ShapeHead3 = new ModelRenderer(this, 0, 0);
        ShapeHead3.addBox(0F, 0F, -2F, 14, 7, 2);
        ShapeHead3.setRotationPoint(-7F, -15.6F, -8F);
        ShapeHead3.setTextureSize(128, 32);
        ShapeHead3.mirror = true;
        setRotation(ShapeHead3, 0F, 0F, 0F);
        ShapeHead4 = new ModelRenderer(this, 0, 0);
        ShapeHead4.addBox(0F, 0F, -2F, 6, 10, 2);
        ShapeHead4.setRotationPoint(-12F, -6.9F, -8F);
        ShapeHead4.setTextureSize(128, 32);
        ShapeHead4.mirror = true;
        setRotation(ShapeHead4, 0F, 0F, 0F);
        ShapeHead5 = new ModelRenderer(this, 0, 0);
        ShapeHead5.addBox(-4F, 0F, -2F, 6, 10, 2);
        ShapeHead5.setRotationPoint(10F, -6.9F, -8F);
        ShapeHead5.setTextureSize(128, 32);
        ShapeHead5.mirror = true;
        setRotation(ShapeHead5, 0F, 0F, 0F);
        ShapeHead6 = new ModelRenderer(this, 0, 0);
        ShapeHead6.addBox(-2F, -10F, -2F, 6, 10, 2);
        ShapeHead6.setRotationPoint(-7F, 12F, -8F);
        ShapeHead6.setTextureSize(128, 32);
        ShapeHead6.mirror = true;
        setRotation(ShapeHead6, 0F, 0F, -0.3141593F);
        ShapeHead7 = new ModelRenderer(this, 0, 0);
        ShapeHead7.addBox(-4F, -10F, -2F, 6, 10, 2);
        ShapeHead7.setRotationPoint(7F, 12F, -8F);
        ShapeHead7.setTextureSize(128, 32);
        ShapeHead7.mirror = true;
        setRotation(ShapeHead7, 0F, 0F, 0.3141593F);
        ShapeHead8 = new ModelRenderer(this, 0, 0);
        ShapeHead8.addBox(0F, 0F, -2F, 18, 4, 2);
        ShapeHead8.setRotationPoint(-9F, 7F, -8F);
        ShapeHead8.setTextureSize(128, 32);
        ShapeHead8.mirror = true;
        setRotation(ShapeHead8, 0F, 0F, 0F);
        ShapeHead9 = new ModelRenderer(this, 0, 0);
        ShapeHead9.addBox(0F, 0F, -2F, 2, 17, 2);
        ShapeHead9.setRotationPoint(-6F, -10F, -8F);
        ShapeHead9.setTextureSize(128, 32);
        ShapeHead9.mirror = true;
        setRotation(ShapeHead9, 0F, 0F, 0F);
        ShapeHead10 = new ModelRenderer(this, 0, 0);
        ShapeHead10.addBox(0F, 0F, -2F, 2, 17, 2);
        ShapeHead10.setRotationPoint(4F, -10F, -8F);
        ShapeHead10.setTextureSize(128, 32);
        ShapeHead10.mirror = true;
        setRotation(ShapeHead10, 0F, 0F, 0F);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        Shape0.render(f5);
        Shape1.render(f5);
        Shape2.render(f5);
        Shape21.render(f5);
        Shape22.render(f5);
        Shape23.render(f5);
        Shape24.render(f5);
        Shape25.render(f5);
        Shape27.render(f5);
        Shape3.render(f5);
        Shape31.render(f5);
        Shape32.render(f5);
        Shape33.render(f5);
        Shape34.render(f5);
        Shape35.render(f5);
        Shape37.render(f5);
        Shape4.render(f5);
        Shape41.render(f5);
        Shape42.render(f5);
        Shape5.render(f5);
        Shape51.render(f5);
        Shape52.render(f5);
        Piece1.render(f5);
        Piece2.render(f5);
        ShapeHead1.render(f5);
        ShapeHead2.render(f5);
        ShapeHead3.render(f5);
        ShapeHead4.render(f5);
        ShapeHead5.render(f5);
        ShapeHead6.render(f5);
        ShapeHead7.render(f5);
        ShapeHead8.render(f5);
        ShapeHead9.render(f5);
        ShapeHead10.render(f5);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
