package club.nsdn.nyasamarailway.entity.loco;

import club.nsdn.nyasamarailway.api.cart.AbsLimLoco;
import club.nsdn.nyasamarailway.api.cart.IExtendedInfoCart;
import club.nsdn.nyasamarailway.api.cart.nsc.IMonoRailCart;
import club.nsdn.nyasamarailway.api.rail.TileEntityRailEndpoint;
import club.nsdn.nyasamarailway.network.TrainPacket;
import club.nsdn.nyasamarailway.api.rail.IMonoRail;
import club.nsdn.nyasamarailway.util.HashMap;
import club.nsdn.nyasamarailway.util.TrainController;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class NSPCT4M extends AbsLimLoco implements IMonoRailCart, IExtendedInfoCart {

    public String extenedInfo = "";
    private static final DataParameter<String> EXT = EntityDataManager.createKey(NSPCT4M.class, DataSerializers.STRING);

    public String getExtendedInfo() {
        return dataManager.get(EXT);
    }

    public void setExtendedInfo(String info) {
        this.extenedInfo = info;
        dataManager.set(EXT, info);
    }

    @Override
    public String getExtendedInfo(String key) {
        HashMap info = new HashMap();
        info.fromString(getExtendedInfo());
        if (info.containsKey(key))
            return info.get(key);
        return "";
    }

    @Override
    public void setExtendedInfo(String key, String data) {
        HashMap info = new HashMap();
        info.fromString(getExtendedInfo());
        info.remove(key);
        info.put(key, data);
        setExtendedInfo(info.toString());
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);

        setExtendedInfo(tagCompound.getString("ExtendedInfo"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);

        tagCompound.setString("ExtendedInfo", getExtendedInfo());
    }

    @Override
    protected void entityInit() {
        super.entityInit();

        dataManager.register(EXT, "");
    }

    public double shiftY = DEFAULT_SHIFT;

    @Override
    public double getShiftY() {
        return shiftY;
    }

    public NSPCT4M(World world) {
        super(world);
    }

    public NSPCT4M(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public float getMaxCartSpeedOnRail() {
        return 3.0F;
    }

    @Override
    public double getMountedYOffset() {
        return -0.3 + shiftY;
    }

    @Override
    public boolean shouldRiderSit() {
        return true;
    }

    @Nonnull
    @Override
    public ItemStack getCartItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public void doMotion(TrainPacket packet, EntityMinecart cart) {
        TrainController.doMotionWithAir(packet, cart);
    }

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
        double vy = pos.y - this.posY;
        double vz = pos.z - this.posZ;
        this.move(MoverType.SELF, vx, vy, vz);

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
