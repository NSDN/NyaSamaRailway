package club.nsdn.nyasamarailway.api.cart.nsc;

import club.nsdn.nyasamarailway.api.cart.AbsLimLoco;
import club.nsdn.nyasamarailway.network.TrainPacket;
import club.nsdn.nyasamarailway.api.rail.IMonoRail;
import club.nsdn.nyasamarailway.util.TrainController;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2019.2.10
 */
public abstract class AbsNSCxBM extends AbsLimLoco implements IMonoRailCart {

    public double shiftY = DEFAULT_SHIFT;

    @Override
    public double getShiftY() {
        return shiftY;
    }

    public AbsNSCxBM(World world) {
        super(world);
    }

    public AbsNSCxBM(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public float getMaxCartSpeedOnRail() {
        return 3.0F;
    }

    @Override
    public abstract double getMountedYOffset();

    @Override
    public boolean shouldRiderSit() {
        return true;
    }

    @Override
    public void doMotion(TrainPacket packet, EntityMinecart cart) {
        TrainController.doMotionWithAir(packet, cart);
    }

    @Override
    protected void moveAlongCurvedTrack() {
        Vec3d pos = nowEndPoint.get(nowProgress);
        Vec3d vec = nowEndPoint.get(nowProgress + 0.005);
        vec = vec.subtract(pos).normalize();

        double yaw = Math.atan2(vec.z, vec.x) * 180 / Math.PI;
        double hlen = Math.sqrt(vec.x * vec.x + vec.z * vec.z);
        double pitch = Math.atan(vec.y / hlen) * 180 / Math.PI;
        setRotation((float) yaw, (float) pitch);
        setPositionAndUpdate(pos.x, pos.y + CURVED_SHIFT, pos.z);
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
