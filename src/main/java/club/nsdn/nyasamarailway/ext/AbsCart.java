package club.nsdn.nyasamarailway.ext;

import club.nsdn.nyasamarailway.entity.cart.NSPCT9;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class AbsCart extends NSPCT9 {

    public AbsCart(World world) {
        super(world);
    }

    public AbsCart(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        initEntity();
    }

    @Override
    protected void readEntityFromNBT(@Nonnull NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);
        fromNBT(tagCompound);
    }

    @Override
    protected void writeEntityToNBT(@Nonnull NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        toNBT(tagCompound);
    }

    @Override
    public boolean shouldRiderSit() {
        return shouldSit();
    }

    @Override
    public double getMountedYOffset() {
        return passengerYOffset();
    }

    @Override
    public void updatePassenger(@Nonnull Entity entity) {
        passengerUpdate(entity);
    }

    public void initEntity() { }
    public void fromNBT(@Nonnull NBTTagCompound tag) { }
    public void toNBT(@Nonnull NBTTagCompound tag) { }
    public boolean shouldSit() { return false; }
    public double passengerYOffset() { return 0; }
    public void passengerUpdate(@Nonnull Entity passenger) { }

}
