package club.nsdn.nyasamarailway.renderer.tileentity.rail;

/**
 * Created by drzzm32 on 2016.7.23.
 */

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class RailNoSleeperStraightModel extends ModelBase {
    ModelRenderer RailPieceL1;
    ModelRenderer RailPieceL2;
    ModelRenderer RailPieceR1;
    ModelRenderer RailPieceR2;

    public RailNoSleeperStraightModel() {
        textureWidth = 32;
        textureHeight = 16;
        setTextureOffset("RailPieceL1.Base", 0, 0);
        setTextureOffset("RailPieceL1.Base1", 0, 0);
        setTextureOffset("RailPieceL1.TileEntityRail", 0, 7);
        setTextureOffset("RailPieceL2.Base", 0, 0);
        setTextureOffset("RailPieceL2.Base1", 0, 0);
        setTextureOffset("RailPieceL2.TileEntityRail", 0, 7);
        setTextureOffset("RailPieceR1.Base", 0, 0);
        setTextureOffset("RailPieceR1.Base1", 0, 0);
        setTextureOffset("RailPieceR1.TileEntityRail", 0, 7);
        setTextureOffset("RailPieceR2.Base", 0, 0);
        setTextureOffset("RailPieceR2.Base1", 0, 0);
        setTextureOffset("RailPieceR2.TileEntityRail", 0, 7);

        RailPieceL1 = new ModelRenderer(this, "RailPieceL1");
        RailPieceL1.setRotationPoint(-5.5F, 22F, -4F);
        setRotation(RailPieceL1, 0F, 0F, 0F);
        RailPieceL1.mirror = true;
        RailPieceL1.addBox("Base", -1.5F, 1F, -1.5F, 3, 1, 3);
        RailPieceL1.addBox("Base1", -1F, 0.5F, -1F, 2, 1, 2);
        RailPieceL1.addBox("TileEntityRail", -0.5F, 0F, -4F, 1, 1, 8);
        RailPieceL2 = new ModelRenderer(this, "RailPieceL2");
        RailPieceL2.setRotationPoint(-5.5F, 22F, 4F);
        setRotation(RailPieceL2, 0F, 0F, 0F);
        RailPieceL2.mirror = true;
        RailPieceL2.addBox("Base", -1.5F, 1F, -1.5F, 3, 1, 3);
        RailPieceL2.addBox("Base1", -1F, 0.5F, -1F, 2, 1, 2);
        RailPieceL2.addBox("TileEntityRail", -0.5F, 0F, -4F, 1, 1, 8);
        RailPieceR1 = new ModelRenderer(this, "RailPieceR1");
        RailPieceR1.setRotationPoint(5.5F, 22F, -4F);
        setRotation(RailPieceR1, 0F, 0F, 0F);
        RailPieceR1.mirror = true;
        RailPieceR1.addBox("Base", -1.5F, 1F, -1.5F, 3, 1, 3);
        RailPieceR1.addBox("Base1", -1F, 0.5F, -1F, 2, 1, 2);
        RailPieceR1.addBox("TileEntityRail", -0.5F, 0F, -4F, 1, 1, 8);
        RailPieceR2 = new ModelRenderer(this, "RailPieceR2");
        RailPieceR2.setRotationPoint(5.5F, 22F, 4F);
        setRotation(RailPieceR2, 0F, 0F, 0F);
        RailPieceR2.mirror = true;
        RailPieceR2.addBox("Base", -1.5F, 1F, -1.5F, 3, 1, 3);
        RailPieceR2.addBox("Base1", -1F, 0.5F, -1F, 2, 1, 2);
        RailPieceR2.addBox("TileEntityRail", -0.5F, 0F, -4F, 1, 1, 8);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(entity, f, f1, f2, f3, f4, f5);
        RailPieceL1.render(f5);
        RailPieceL2.render(f5);
        RailPieceR1.render(f5);
        RailPieceR2.render(f5);
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

