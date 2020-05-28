package club.nsdn.nyasamarailway.ext;

import club.nsdn.nyasamarailway.entity.loco.NSPCT9M;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class AbsLoco extends NSPCT9M {

    public AbsLoco(World world) {
        super(world);
    }

    public AbsLoco(World world, double x, double y, double z) {
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

    @Override
    protected void removePassenger(Entity entity) {
        BlockPos pos = passengerRemove(entity);
        if (pos == null) {
            super.removePassenger(entity);
            return;
        }

        super.removePassenger(entity);
        entity.setPositionAndUpdate(
                pos.getX() + 0.5,
                pos.getY() + 0.1,
                pos.getZ() + 0.5
        );
    }

    public void initEntity() { }
    public void fromNBT(@Nonnull NBTTagCompound tag) { }
    public void toNBT(@Nonnull NBTTagCompound tag) { }
    public boolean shouldSit() { return false; }
    public double passengerYOffset() { return 0; }
    public void passengerUpdate(@Nonnull Entity passenger) { }
    public BlockPos passengerRemove(@Nonnull Entity passenger) { return null; }

}
