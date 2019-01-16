package club.nsdn.nyasamatelecom.api.tileentity;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

/**
 * Created by drzzm32 on 2018.12.13.
 */
public class TileEntityBase extends TileEntity implements ITickable {

    public int META = 0;
    protected int METAMAX = 12;
    protected AxisAlignedBB AABB = Block.FULL_BLOCK_AABB;
    protected Vec3d SIZE = new Vec3d(1, 1, 1);

    protected void setBlockBounds(double x1, double y1, double z1, double x2, double y2, double z2) {
        AABB = new AxisAlignedBB(x1, y1, z1, x2, y2, z2);
    }

    protected void setBoundsByXYZ(double x1, double y1, double z1, double x2, double y2, double z2) {
        switch (META % METAMAX) {
            case 0:
                setBlockBounds(x1, y1, z1, x2, y2, z2);
                break;
            case 1:
                setBlockBounds(1.0 - z2, y1, x1, 1.0 - z1, y2, x2);
                break;
            case 2:
                setBlockBounds(1.0 - x2, y1, 1.0 - z2, 1.0 - x1, y2, 1.0 - z1);
                break;
            case 3:
                setBlockBounds(z1, y1, 1.0 - x2, z2, y2, 1.0 - x1);
                break;

            case 4:
                setBlockBounds(1.0 - x2, z1, 1.0 - y2, 1.0 - x1, z2, 1.0 - y1);
                break;
            case 5:
                setBlockBounds(y1, z1, 1.0 - x2, y2, z2, 1.0 - x1);
                break;
            case 6:
                setBlockBounds(x1, z1, y1, x2, z2, y2);
                break;
            case 7:
                setBlockBounds(1.0 - y2, z1, x1, 1.0 - y1, z2, x2);
                break;

            case 8:
                setBlockBounds(x1, 1.0 - y2, z1, x2, 1.0 - y1, z2);
                break;
            case 9:
                setBlockBounds(1.0 - z2, 1.0 - y2, x1, 1.0 - z1, 1.0 - y1, x2);
                break;
            case 10:
                setBlockBounds(1.0 - x2, 1.0 - y2, 1.0 - z2, 1.0 - x1, 1.0 - y1, 1.0 - z1);
                break;
            case 11:
                setBlockBounds(z1, 1.0 - y2, 1.0 - x2, z2, 1.0 - y1, 1.0 - x1);
                break;
        }
    }

    protected void updateBounds() {
        setBoundsByXYZ(
            0.5 - SIZE.x / 2, 0.0, 0.5 - SIZE.z / 2,
            0.5 + SIZE.x / 2, SIZE.y, 0.5 + SIZE.z / 2
        );
        refresh();
    }

    public void refresh() {
        getWorld().notifyBlockUpdate(getPos(), getBlockType().getDefaultState(), getBlockType().getDefaultState(), 2);
    }

    public TileEntityBase() {  }

    protected void setInfo(int metaMax, double x, double y, double z) {
        METAMAX = metaMax;
        SIZE = new Vec3d(x, y, z);
        updateBounds();
    }

    public AxisAlignedBB AABB() {
        updateBounds();
        return AABB;
    }

    @Override
    public void update() {

    }

    public void fromNBT(NBTTagCompound tagCompound) {
        META = tagCompound.getInteger("Meta");
        METAMAX = tagCompound.getInteger("MetaMax");
        double x, y, z;
        x = tagCompound.getDouble("SizeX");
        y = tagCompound.getDouble("SizeY");
        z = tagCompound.getDouble("SizeZ");
        SIZE = new Vec3d(x, y, z);
        updateBounds();
    }

    public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
        tagCompound.setInteger("Meta", META);
        tagCompound.setInteger("MetaMax", METAMAX);
        tagCompound.setDouble("SizeX", SIZE.x);
        tagCompound.setDouble("SizeY", SIZE.y);
        tagCompound.setDouble("SizeZ", SIZE.z);
        return tagCompound;
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        fromNBT(tagCompound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        toNBT(tagCompound);
        return tagCompound;
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound tagCompound = new NBTTagCompound();
        toNBT(tagCompound);
        return new SPacketUpdateTileEntity(getPos(), 1, tagCompound);
    }

    @Override
    public void onDataPacket(NetworkManager manager, SPacketUpdateTileEntity packet) {
        NBTTagCompound tagCompound = packet.getNbtCompound();
        fromNBT(tagCompound);
    }

    public void onDestroy() {

    }

}
