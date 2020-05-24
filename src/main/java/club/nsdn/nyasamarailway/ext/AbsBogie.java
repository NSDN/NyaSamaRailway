package club.nsdn.nyasamarailway.ext;

import club.nsdn.nyasamarailway.entity.cart.NSBT2;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class AbsBogie extends NSBT2 {

    public AbsBogie(World world) {
        super(world);
    }

    public AbsBogie(World world, double x, double y, double z) {
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

    public void initEntity() { }
    public void fromNBT(@Nonnull NBTTagCompound tag) { }
    public void toNBT(@Nonnull NBTTagCompound tag) { }

}
