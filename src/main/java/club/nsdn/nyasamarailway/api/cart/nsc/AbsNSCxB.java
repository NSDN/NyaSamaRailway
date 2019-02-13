package club.nsdn.nyasamarailway.api.cart.nsc;

import club.nsdn.nyasamarailway.api.cart.AbsMotoCart;
import club.nsdn.nyasamarailway.network.TrainPacket;
import club.nsdn.nyasamarailway.api.rail.IMonoRail;
import club.nsdn.nyasamarailway.util.TrainController;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2019.2.10
 */
public abstract class AbsNSCxB extends AbsMotoCart implements IMonoRailCart {

    public double shiftY = DEFAULT_SHIFT;

    @Override
    public double getShiftY() {
        return shiftY;
    }

    public AbsNSCxB(World world) {
        super(world);
    }

    public AbsNSCxB(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public float getMaxCartSpeedOnRail() {
        return 3.0F;
    }

    @Override
    public abstract double getMountedYOffset();

    @Override
    public void doMotion(TrainPacket packet, EntityMinecart cart) {
        TrainController.doMotionWithAir(packet, cart);
    }

    @Override
    public void onUpdate() {
        int x = MathHelper.floor(this.posX);
        int y = MathHelper.floor(this.posY);
        int z = MathHelper.floor(this.posZ);
        BlockPos pos = new BlockPos(x, y, z);

        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof IMonoRail) {
            IMonoRail rail = (IMonoRail) tileEntity;
            int meta = rail.getMeta();
            meta &= 0x7;
            shiftY = updateShiftY(meta, shiftY);
        } else {
            boolean state;
            state = world.getTileEntity(pos.north()) instanceof IMonoRail;
            state |= world.getTileEntity(pos.south()) instanceof IMonoRail;
            state |= world.getTileEntity(pos.west()) instanceof IMonoRail;
            state |= world.getTileEntity(pos.east()) instanceof IMonoRail;

            if (!state && shiftY < 0) shiftY += 0.05;
        }

        super.onUpdate();
    }

}
