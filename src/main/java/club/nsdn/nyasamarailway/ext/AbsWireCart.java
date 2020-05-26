package club.nsdn.nyasamarailway.ext;

import club.nsdn.nyasamarailway.entity.cart.NSPCT8W;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class AbsWireCart extends NSPCT8W {

    public static class AbsBasket extends Container {

        public AbsBasket(World world) {
            super(world);
        }

        public AbsBasket(World world, double x, double y, double z) {
            super(world, x, y, z);
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

        public boolean shouldSit() { return false; }
        public double passengerYOffset() { return 0; }
        public void passengerUpdate(@Nonnull Entity passenger) { }
    }

    public AbsWireCart(World world) {
        super(world);
    }

    public AbsWireCart(World world, double x, double y, double z) {
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
    public double getMountedYOffset() {
        return getDropDist() + 1;
    }

    public void initEntity() { }
    public void fromNBT(@Nonnull NBTTagCompound tag) { }
    public void toNBT(@Nonnull NBTTagCompound tag) { }
    public double getDropDist() { return -3.0; }

}
