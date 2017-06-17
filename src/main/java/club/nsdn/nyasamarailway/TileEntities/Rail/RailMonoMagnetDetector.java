package club.nsdn.nyasamarailway.TileEntities.Rail;

import club.nsdn.nyasamarailway.Blocks.BlockLoader;
import club.nsdn.nyasamarailway.Blocks.TileEntityRailReceiver;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityMinecartCommandBlock;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.thewdj.physics.Point3D;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by drzzm32 on 2017.1.13.
 */
public class RailMonoMagnetDetector extends RailMonoMagnetBase {

    public static class TileEntityRail extends TileEntityRailReceiver implements RailMonoMagnetPowerable {

        @Override
        public boolean shouldRenderInPass(int pass) {
            return true;
        }

        @Override
        public void writeToNBT(NBTTagCompound tagCompound) {
            super.writeToNBT(tagCompound);
        }

        @Override
        public void readFromNBT(NBTTagCompound tagCompound) {
            super.readFromNBT(tagCompound);
        }

        @Override
        public Packet getDescriptionPacket() {
            return super.getDescriptionPacket();
        }

        @Override
        public void onDataPacket(NetworkManager manager, S35PacketUpdateTileEntity packet) {
            super.onDataPacket(manager, packet);
        }

    }

    public final int delaySecond;
    public static LinkedHashMap<Point3D, Integer> tmpDelay;

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityRail();
    }

    public RailMonoMagnetDetector() {
        super(true, "RailMonoMagnetDetector", "rail_mono_magnet_detector");
        delaySecond = 0;
        tmpDelay = new LinkedHashMap<Point3D, Integer>();
    }

    public RailMonoMagnetDetector(int delay) {
        super(true, "RailMonoMagnetDetector" + Integer.toString(delay) + "s", "rail_mono_magnet_detector");
        delaySecond = delay;
        tmpDelay = new LinkedHashMap<Point3D, Integer>();
        setCreativeTab(null);
    }

    public RailMonoMagnetDetector(String name, String icon) {
        super(true, name, icon);
        delaySecond = 0;
        tmpDelay = new LinkedHashMap<Point3D, Integer>();
    }

    @Override
    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
        return Item.getItemFromBlock(BlockLoader.railMonoMagnetDetector);
    }

    @Override
    public int tickRate(World world) {
        return 20;
    }

    @Override
    public boolean canProvidePower()
    {
        return true;
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int i)
    {
        return (world.getBlockMetadata(x, y, z) & 8) != 0 ? 15 : 0;
    }

    @Override
    public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int i)
    {
        return (world.getBlockMetadata(x, y, z) & 8) == 0 ? 0 : (i == 1 ? 15 : 0);
    }

    @Override
    public boolean hasComparatorInputOverride()
    {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int i)
    {
        if ((world.getBlockMetadata(x, y, z) & 8) > 0)
        {
            float f = 0.125F;
            List list = world.getEntitiesWithinAABB(EntityMinecartCommandBlock.class, AxisAlignedBB.getBoundingBox((double)((float)x + f), (double)y, (double)((float)z + f), (double)((float)(x + 1) - f), (double)((float)(y + 1) - f), (double)((float)(z + 1) - f)));

            if (list.size() > 0)
            {
                return ((EntityMinecartCommandBlock)list.get(0)).func_145822_e().func_145760_g();
            }

            List list1 = world.selectEntitiesWithinAABB(EntityMinecart.class, AxisAlignedBB.getBoundingBox((double)((float)x + f), (double)y, (double)((float)z + f), (double)((float)(x + 1) - f), (double)((float)(y + 1) - f), (double)((float)(z + 1) - f)), IEntitySelector.selectInventories);

            if (list1.size() > 0)
            {
                return Container.calcRedstoneFromInventory((IInventory)list1.get(0));
            }
        }

        return 0;
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        if (!world.isRemote) {
            int meta = world.getBlockMetadata(x, y, z);
            if ((meta & 8) == 0) {
                this.setRailOutput(world, x, y, z, meta);
            }
        }
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        this.setRailOutput(world, x, y, z, world.getBlockMetadata(x, y, z));
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (!world.isRemote) {
            int meta = world.getBlockMetadata(x, y, z);
            if ((meta & 8) != 0) {
                this.setRailOutput(world, x, y, z, meta);
            }
        }
    }

    public int getDelaySecond() {
        return delaySecond;
    }

    public void setRailOutput(World world, int x, int y, int z, int meta) {
        boolean isPowered = (meta & 8) != 0;
        boolean hasCart = false;
        boolean isEnabled = false;
        float bBoxSize = 0.125F;
        List bBox = world.getEntitiesWithinAABB(EntityMinecart.class, AxisAlignedBB.getBoundingBox((double) ((float) x + bBoxSize), (double) y, (double) ((float) z + bBoxSize), (double) ((float) (x + 1) - bBoxSize), (double) ((float) (y + 1) - bBoxSize), (double) ((float) (z + 1) - bBoxSize)));
        if (!bBox.isEmpty()) {
            hasCart = true;
        }

        if (hasCart && !isPowered) {
            if (delaySecond != 0) {
                Point3D p = new Point3D(x, y, z);
                if (!tmpDelay.containsKey(p)) {
                    tmpDelay.put(p, 0);
                }
                if (tmpDelay.get(p) < delaySecond * 20)
                    tmpDelay.put(p, tmpDelay.get(p) + 1);
                else {
                    if (getRailDirection(world, x, y, z) == RailDirection.NS) {
                        if (world.isBlockIndirectlyGettingPowered(x - 1, y, z) || world.isBlockIndirectlyGettingPowered(x + 1, y, z) ||
                                world.isBlockIndirectlyGettingPowered(x - 1, y - 1, z) || world.isBlockIndirectlyGettingPowered(x + 1, y - 1, z)) {
                            isEnabled = true;
                        }
                    } else {
                        if (world.isBlockIndirectlyGettingPowered(x, y, z - 1) || world.isBlockIndirectlyGettingPowered(x, y, z + 1) ||
                                world.isBlockIndirectlyGettingPowered(x, y - 1, z - 1) || world.isBlockIndirectlyGettingPowered(x, y - 1, z + 1)) {
                            isEnabled = true;
                        }
                    }
                    if (world.getTileEntity(x, y, z) instanceof TileEntityRailReceiver) {
                        TileEntityRailReceiver railReceiver = (TileEntityRailReceiver) world.getTileEntity(x, y, z);
                        if (railReceiver.senderRailIsPowered()) isEnabled = true;
                    }

                    if (!isEnabled) {
                        world.setBlockMetadataWithNotify(x, y, z, meta | 8, 3);
                        world.notifyBlocksOfNeighborChange(x, y, z, this);
                        world.notifyBlocksOfNeighborChange(x, y - 1, z, this);
                        world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
                        tmpDelay.put(p, 0);
                    }
                }
            } else {
                world.setBlockMetadataWithNotify(x, y, z, meta | 8, 3);
                world.notifyBlocksOfNeighborChange(x, y, z, this);
                world.notifyBlocksOfNeighborChange(x, y - 1, z, this);
                world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
            }
        } else if (!hasCart && !isPowered && delaySecond != 0) {
            if (tmpDelay.containsKey(new Point3D(x, y, z)))
                tmpDelay.put(new Point3D(x, y, z), 0);
        }

        if (!hasCart && isPowered) {
            world.setBlockMetadataWithNotify(x, y, z, meta & 7, 3);
            world.notifyBlocksOfNeighborChange(x, y, z, this);
            world.notifyBlocksOfNeighborChange(x, y - 1, z, this);
            world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
        }

        if (hasCart) {
            world.scheduleBlockUpdate(x, y, z, this, 20);
        }

        world.func_147453_f(x, y, z, this);
    }

}
