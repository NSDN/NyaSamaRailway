package club.nsdn.nyasamarailway.tileblock.signal.trackside;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.network.NetworkWrapper;
import club.nsdn.nyasamarailway.tileblock.signal.ITrackSide;
import club.nsdn.nyasamarailway.tileblock.signal.TileEntityTrackSideBlocking;
import club.nsdn.nyasamatelecom.api.util.NSASM;
import club.nsdn.nyasamatelecom.api.util.Util;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.LinkedHashMap;
import java.util.Random;

/**
 * Created by drzzm32 on 2019.1.6.
 */
public class TrackSideBlockingHs extends AbsTrackSide {

    public static class Blocking extends TileEntityTrackSideBlocking {

        public static abstract class BlockingCore extends NSASM {

            public BlockingCore(String[][] code) {
                super(code);
            }

            @Override
            public SimpleNetworkWrapper getWrapper() {
                return NetworkWrapper.instance;
            }

            @Override
            public void loadFunc(LinkedHashMap<String, Operator> funcList) {
                funcList.put("inv", ((dst, src) -> {
                    if (src != null) return Result.ERR;
                    if (dst != null) return Result.ERR;

                    getBlocking().invert = !getBlocking().invert;

                    return Result.OK;
                }));
            }

            public abstract Blocking getBlocking();

        }

        protected boolean prevInv;

        @Override
        protected boolean hasChanged() {
            return super.hasChanged() || prevInv != isInvert();
        }

        @Override
        protected void updateChanged() {
            super.updateChanged();
            prevInv = isInvert();
        }

        @Override
        public boolean hasInvert() {
            return true;
        }

        @Override
        public boolean isInvert() {
            return invert;
        }

        public boolean invert = false;

        @Override
        public void fromNBT(NBTTagCompound tagCompound) {
            invert = tagCompound.getBoolean("invert");
            super.fromNBT(tagCompound);
        }

        @Override
        public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
            tagCompound.setBoolean("invert", invert);
            return super.toNBT(tagCompound);
        }

        public static boolean configure(World world, int x, int y, int z, EntityPlayer player) {
            if (world.getTileEntity(x, y, z) == null) return false;
            if (world.getTileEntity(x, y, z) instanceof Blocking) {
                Blocking blocking = (Blocking) world.getTileEntity(x, y, z);

                ItemStack stack = player.getCurrentEquippedItem();
                if (stack != null) {
                    NBTTagList list = Util.getTagListFromNGT(stack);
                    if (list == null) return false;

                    if (!world.isRemote) {
                        String[][] code = NSASM.getCode(list);
                        new BlockingCore(code) {
                            @Override
                            public World getWorld() {
                                return world;
                            }

                            @Override
                            public double getX() {
                                return x;
                            }

                            @Override
                            public double getY() {
                                return y;
                            }

                            @Override
                            public double getZ() {
                                return z;
                            }

                            @Override
                            public EntityPlayer getPlayer() {
                                return player;
                            }

                            @Override
                            public Blocking getBlocking() {
                                return blocking;
                            }
                        }.run();
                    }

                    return true;
                }
            }

            return false;
        }

        @Override
        public boolean nearbyHasMinecart() {
            boolean result = false;
            ForgeDirection offset;

            TileEntityTrackSideBlocking blocking = getNearby(direction);
            if (blocking != null) {
                if (blocking.isInvert())
                    offset = blocking.direction.getOpposite();
                else
                    offset = blocking.direction;
                result = ITrackSide.hasMinecart(blocking, blocking.direction, offset);
            }
            blocking = getNearby(direction.getOpposite());
            if (blocking != null) {
                if (blocking.isInvert())
                    offset = blocking.direction.getOpposite();
                else
                    offset = blocking.direction;
                result |= ITrackSide.hasMinecart(blocking, blocking.direction, offset);
            }

            return result;
        }

        public static void tick(World world, int x, int y, int z) {
            if (world.isRemote) return;
            if (world.getTileEntity(x, y, z) == null) return;
            if (world.getTileEntity(x, y, z) instanceof Blocking) {
                Blocking blocking = (Blocking) world.getTileEntity(x, y, z);

                ForgeDirection offset = blocking.isInvert() ? blocking.direction.getOpposite() : blocking.direction;
                boolean hasCart = ITrackSide.hasMinecart(blocking, blocking.direction, offset);
                boolean hasPowered = ITrackSide.hasPowered(blocking);
                if (blocking.getTransceiver() != null) {
                    if (hasCart && !hasPowered) {
                        if (ITrackSide.nearbyHasPowered(blocking)) {
                            ITrackSide.setPowered(blocking, true);
                            ITrackSide.setPowered(blocking.getTransceiver(), true);
                        }
                    }
                } else {
                    if (hasCart && !hasPowered) {
                        ITrackSide.setPowered(blocking, true);
                    }

                    if (!hasCart && hasPowered) {
                        ITrackSide.setPowered(blocking, false);

                        if (!blocking.nearbyHasMinecart()) {
                            TileEntity[] tiles = blocking.getNearby();
                            for (TileEntity tile : tiles) {
                                if (tile instanceof TileEntityTrackSideBlocking) {
                                    TileEntityTrackSideBlocking tileBlocking = (TileEntityTrackSideBlocking) tile;
                                    ITrackSide.setPowered(tileBlocking, false);
                                    if (tileBlocking.getTransceiver() != null)
                                        ITrackSide.setPowered(tileBlocking.getTransceiver(), false);
                                }
                            }
                        }
                    }
                }

                if (blocking.hasChanged()) {
                    blocking.updateChanged();
                    world.markBlockForUpdate(x, y, z);
                }

                world.scheduleBlockUpdate(x, y, z, blocking.getBlockType(), 1);
            }
        }

    }

    public TrackSideBlockingHs() {
        super("TrackSideBlockingHs", "track_side_blocking_hs");
        setCreativeTab(CreativeTabLoader.tabNSTest);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new Blocking();
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        Blocking.tick(world, x, y, z);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        return Blocking.configure(world, x, y, z, player);
    }

}
