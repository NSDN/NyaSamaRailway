package club.nsdn.nyasamarailway.api.cart;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by drzzm32 on 2020.5.24.
 */
public class CartPart<Parent extends Entity> extends Entity {

    public final Parent parent;
    public final String partName;
    public final Vec3d offset;

    public CartPart(Parent parent, String name, Vec3d offset) {
        super(parent.world);
        this.parent = parent;
        this.partName = name;
        this.setSize(parent.width, parent.height);
        this.offset = new Vec3d(offset.x, offset.y, offset.z);
    }

    public CartPart(Parent parent, String name, float width, float height, Vec3d offset) {
        super(parent.world);
        this.parent = parent;
        this.partName = name;
        this.setSize(width, height);
        this.offset = new Vec3d(offset.x, offset.y, offset.z);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        Vec3d vec = Vec3d.ZERO;
        if (parent instanceof IPartParent)
            vec = ((IPartParent) parent).getOffset(offset);
        setLocationAndAngles(vec.x, vec.y, vec.z, 0.0F, 0.0F);

        if (parent.isDead) this.setDead();
    }

    @Override
    protected void entityInit() { }

    @Override
    protected void readEntityFromNBT(@Nonnull NBTTagCompound tagCompound) { }

    @Override
    protected void writeEntityToNBT(@Nonnull NBTTagCompound tagCompound) { }

    @Override
    public boolean shouldRenderInPass(int pass) { return false; }

    @Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox() {
        return this.getEntityBoundingBox();
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean processInitialInteract(@Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
        return parent.processInitialInteract(player, hand);
    }

    @Override
    public boolean attackEntityFrom(@Nonnull DamageSource source, float v) {
        return parent.attackEntityFrom(source, v);
    }

    @Override
    public boolean isEntityEqual(@Nonnull Entity entity) {
        return this == entity || parent == entity;
    }

}
