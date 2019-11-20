package club.nsdn.nyasamarailway.api.cart;

import club.nsdn.nyasamarailway.api.cart.nsc.IMonoRailCart;
import club.nsdn.nyasamarailway.api.rail.IMonoRail;
import club.nsdn.nyasamarailway.api.rail.TileEntityRailEndpoint;
import net.minecraft.entity.MoverType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2019.2.10
 */

/** for monorail bogie only */
public abstract class AbsMonoCart extends AbsCartBase implements IMonoRailCart {

    public double shiftY = DEFAULT_SHIFT;

    @Override
    public double getShiftY() {
        return shiftY;
    }

    public AbsMonoCart(World world) {
        super(world);
    }

    public AbsMonoCart(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public float getMaxCartSpeedOnRail() {
        return 3.0F;
    }

    @Override
    public abstract double getMountedYOffset();

    @Override
    protected TileEntityRailEndpoint getNearbyEndpoint() {
        int x = MathHelper.floor(this.posX);
        int y = MathHelper.floor(this.posY);
        int z = MathHelper.floor(this.posZ);

        BlockPos pos = new BlockPos(x, y, z);
        TileEntityRailEndpoint endpoint = null;

        pos = pos.down();

        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityRailEndpoint)
            endpoint = (TileEntityRailEndpoint) tileEntity;

        return endpoint;
    }

    @Override
    protected void moveAlongCurvedTrack() {
        Vec3d pos = nowEndPoint.get(nowProgress);
        Vec3d vec = nowEndPoint.get(nowProgress + 0.005);
        vec = vec.subtract(pos).normalize();

        double yaw = Math.atan2(vec.z, vec.x) * 180 / Math.PI;
        double hlen = Math.sqrt(vec.x * vec.x + vec.z * vec.z);
        double pitch = Math.atan(vec.y / hlen) * 180 / Math.PI;

        double vx = pos.x - this.posX;
        double vy = pos.y + CURVED_SHIFT - this.posY;
        double vz = pos.z - this.posZ;
        this.move(MoverType.SELF, vx, vy, vz);

        setPosition(pos.x, pos.y + CURVED_SHIFT, pos.z);
        setRotation((float) yaw, (float) pitch);
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
