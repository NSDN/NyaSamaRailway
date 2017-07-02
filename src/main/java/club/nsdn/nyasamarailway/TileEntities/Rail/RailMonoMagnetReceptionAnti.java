package club.nsdn.nyasamarailway.TileEntities.Rail;

import club.nsdn.nyasamarailway.Blocks.IRailDirectional;
import club.nsdn.nyasamarailway.Blocks.TileEntityRailReceiver;
import club.nsdn.nyasamarailway.Entity.*;
import club.nsdn.nyasamarailway.Items.ItemTrainController32Bit;
import club.nsdn.nyasamarailway.Items.ItemTrainController8Bit;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import org.thewdj.physics.Dynamics;
import org.thewdj.physics.Point3D;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by drzzm32 on 2017.1.13.
 */
public class RailMonoMagnetReceptionAnti extends RailMonoMagnetPowered implements IRailDirectional {

    public static class TileEntityRail extends TileEntityRailReceiver implements RailMonoMagnetPowerable {

        public String cartType = "";

        @Override
        public void fromNBT(NBTTagCompound tagCompound) {
            cartType = tagCompound.getString("cartType");
            super.fromNBT(tagCompound);
        }

        @Override
        public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
            tagCompound.setString("cartType", cartType);
            return super.toNBT(tagCompound);
        }

    }

    public LinkedHashMap<Point3D, Integer> tmpDelay;
    public LinkedHashMap<Point3D, Boolean> delayENB;
    public final int delay = 5;

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityRail();
    }

    public RailMonoMagnetReceptionAnti() {
        super("RailMonoMagnetReceptionAnti", "rail_mono_magnet_reception_anti");
        tmpDelay = new LinkedHashMap<Point3D, Integer>();
        delayENB = new LinkedHashMap<Point3D, Boolean>();
    }

    public boolean isForward() {
        return false;
    }

    public boolean checkNearbySameRail(World world, int x, int y, int z) {
        return world.getBlock(x + 1, y, z) == this || world.getBlock(x - 1, y, z) == this ||
                world.getBlock(x, y, z + 1) == this || world.getBlock(x, y, z - 1) == this;
    }

    public void setRailVerticalOutput(World world, int x, int y, int z, boolean value) {
        int meta = world.getBlockMetadata(x, y, z);
        if (value) {
            if (getRailDirection(world, x, y, z) == RailDirection.NS) {
                world.setBlockMetadataWithNotify(x - 1, y, z, meta | 8, 3);
                world.setBlockMetadataWithNotify(x + 1, y, z, meta | 8, 3);
                world.setBlockMetadataWithNotify(x, y - 1, z, meta | 8, 3);
            } else {
                world.setBlockMetadataWithNotify(x, y, z - 1, meta | 8, 3);
                world.setBlockMetadataWithNotify(x, y, z + 1, meta | 8, 3);
                world.setBlockMetadataWithNotify(x, y - 1, z, meta | 8, 3);
            }
        } else {
            if (getRailDirection(world, x, y, z) == RailDirection.NS) {
                world.setBlockMetadataWithNotify(x - 1, y, z, meta & 7, 3);
                world.setBlockMetadataWithNotify(x + 1, y, z, meta & 7, 3);
                world.setBlockMetadataWithNotify(x, y - 1, z, meta & 7, 3);
            } else {
                world.setBlockMetadataWithNotify(x, y, z - 1, meta & 7, 3);
                world.setBlockMetadataWithNotify(x, y, z + 1, meta & 7, 3);
                world.setBlockMetadataWithNotify(x, y - 1, z, meta & 7, 3);
            }
        }

    }

    @Override
    public void onMinecartPass(World world, EntityMinecart cart, int x, int y, int z) {
        boolean playerDetectable = false;
        boolean hasPlayer = false;
        if (!checkNearbySameRail(world, x, y, z)) playerDetectable = true;
        if (cart.riddenByEntity instanceof EntityPlayer) {
            ItemStack stack = ((EntityPlayer) cart.riddenByEntity).getCurrentEquippedItem();
            if (stack != null) {
                if (stack.getItem() instanceof ItemTrainController8Bit ||
                        stack.getItem() instanceof ItemTrainController32Bit) {
                    return;
                }
            }
            hasPlayer = true;
        }

        double maxV;
        if (!playerDetectable) {
            maxV = 0.1;
            if (world.getBlockMetadata(x, y, z) >= 8) {
                if (getRailDirection(world, x, y, z) == RailDirection.NS) {
                    if (cart.motionZ < -maxV) { //cart.motionZ < -maxV
                        if (cart.motionZ > -maxV * 1.5) cart.motionZ = -maxV * 1.5;
                    } else {
                        if (cart.motionZ <= 0) cart.motionZ = 0.005;
                        if (cart.motionZ < maxV)
                            cart.motionZ = Dynamics.LocoMotions.calcVelocityUp(Math.abs(cart.motionZ), 0.1, 1.0, 0.1, 0.02);
                    }
                } else {
                    if (cart.motionX > maxV) { //cart.motionX > maxV
                        if (cart.motionX < maxV * 1.5) cart.motionX = maxV * 1.5;
                    } else {
                        if (cart.motionX >= 0) cart.motionX = -0.005;
                        if (cart.motionX > -maxV)
                            cart.motionX = -Dynamics.LocoMotions.calcVelocityUp(Math.abs(cart.motionX), 0.1, 1.0, 0.1, 0.02);
                    }
                }
            } else {
                if (Math.abs(cart.motionX) > maxV || Math.abs(cart.motionZ) > maxV) {
                    cart.motionX = Math.signum(cart.motionX) * Dynamics.LocoMotions.calcVelocityDown(Math.abs(cart.motionX), 0.1, 1.0, 1.0, 1.0, 0.05, 0.02);
                    cart.motionZ = Math.signum(cart.motionZ) * Dynamics.LocoMotions.calcVelocityDown(Math.abs(cart.motionZ), 0.1, 1.0, 1.0, 1.0, 0.05, 0.02);
                } else {
                    if (getRailDirection(world, x, y, z) == RailDirection.NS) {
                        cart.motionZ = 0;
                    } else {
                        cart.motionX = 0;
                    }
                }
            }
        } else {
            maxV = 0.2;
            RailMonoMagnetReceptionAnti.TileEntityRail rail = null;
            if (world.getTileEntity(x, y, z) instanceof RailMonoMagnetReceptionAnti.TileEntityRail) {
                rail = (RailMonoMagnetReceptionAnti.TileEntityRail) world.getTileEntity(x, y, z);
            }
            if (rail != null) {
                if (rail.cartType.isEmpty() && (cart.motionX * cart.motionX + cart.motionZ * cart.motionZ == 0))
                    rail.cartType = cart.getClass().getName();
            }
            if (!world.isBlockIndirectlyGettingPowered(x, y, z)) {
                Point3D p = new Point3D(x, y, z);
                if (!tmpDelay.containsKey(p)) {
                    tmpDelay.put(p, 0);
                }
                if (!delayENB.containsKey(p)) {
                    delayENB.put(p, false);
                }
                if (hasPlayer) {
                    if ((cart.motionX * cart.motionX + cart.motionZ * cart.motionZ > 0) && (tmpDelay.get(p) == 0)) {
                        if ((Math.abs(cart.motionX) > maxV / 2) || (Math.abs(cart.motionZ) > maxV / 2)) {
                            cart.motionX = (Math.signum(cart.motionX) * Dynamics.LocoMotions.calcVelocityDown(Math.abs(cart.motionX), 0.1D, 1.0D, 1.0D, 1.0D, 0.05D, 0.02D));
                            cart.motionZ = (Math.signum(cart.motionZ) * Dynamics.LocoMotions.calcVelocityDown(Math.abs(cart.motionZ), 0.1D, 1.0D, 1.0D, 1.0D, 0.05D, 0.02D));
                            delayENB.put(p, true);
                        } else {
                            if (getRailDirection(world, x, y, z) == RailDirection.NS) {
                                cart.motionZ = 0.0D;
                            } else {
                                cart.motionX = 0.0D;
                            }
                        }

                    } else {
                        if (tmpDelay.get(p) < delay * 20 && delayENB.get(p)) {
                            boolean isEnabled = false;

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

                            if (!isEnabled) tmpDelay.put(p, tmpDelay.get(p) + 1);
                            if ((Math.abs(cart.motionX) > maxV / 2) || (Math.abs(cart.motionZ) > maxV / 2)) {
                                cart.motionX = (Math.signum(cart.motionX) * Dynamics.LocoMotions.calcVelocityDown(Math.abs(cart.motionX), 0.1D, 1.0D, 1.0D, 1.0D, 0.01D, 0.02D));
                                cart.motionZ = (Math.signum(cart.motionZ) * Dynamics.LocoMotions.calcVelocityDown(Math.abs(cart.motionZ), 0.1D, 1.0D, 1.0D, 1.0D, 0.01D, 0.02D));
                            } else {
                                if (getRailDirection(world, x, y, z) == RailDirection.NS) {
                                    cart.motionZ = 0.0D;
                                } else {
                                    cart.motionX = 0.0D;
                                }
                            }
                        } else {
                            if (getRailDirection(world, x, y, z) == RailDirection.NS) {
                                if (cart.motionZ < -maxV) {
                                    if (cart.motionZ > -maxV * 1.5D) cart.motionZ = (-maxV * 1.5D);
                                } else {
                                    if (cart.motionZ <= 0.0D) cart.motionZ = 0.005D;
                                    if (cart.motionZ < maxV) {
                                        cart.motionZ = Dynamics.LocoMotions.calcVelocityUp(Math.abs(cart.motionZ), 0.1D, 1.0D, 0.1D, 0.02D);
                                    }
                                }
                            } else {
                                if (cart.motionX > maxV) {
                                    if (cart.motionX < maxV * 1.5D) cart.motionX = (maxV * 1.5D);
                                } else {
                                    if (cart.motionX >= 0.0D) cart.motionX = -0.005D;
                                    if (cart.motionX > -maxV) {
                                        cart.motionX = -Dynamics.LocoMotions.calcVelocityUp(Math.abs(cart.motionX), 0.1D, 1.0D, 0.1D, 0.02D);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    tmpDelay.put(p, 0);
                    delayENB.put(p, false);
                }
            } else {
                if (cart.motionX * cart.motionX + cart.motionZ * cart.motionZ > 0) {
                    if (getRailDirection(world, x, y, z) == RailDirection.NS) {
                        if (cart.posZ - 0.5 < z) {
                            cart.setDead();
                            world.removeEntity(cart);
                        }
                    } else {
                        if (cart.posX - 0.5 > x) {
                            cart.setDead();
                            world.removeEntity(cart);
                        }
                    }
                }
            }
        }

    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        RailMonoMagnetReceptionAnti.TileEntityRail rail = null;
        if (world.getTileEntity(x, y, z) instanceof RailMonoMagnetReceptionAnti.TileEntityRail) {
            rail = (RailMonoMagnetReceptionAnti.TileEntityRail) world.getTileEntity(x, y, z);
        }
        if (rail != null) {
            if (player.isSneaking() && !world.isRemote) {
                rail.cartType = "";
                player.addChatComponentMessage(new ChatComponentTranslation("info.reception.cleared"));
                return true;
            }
        }
        return false;
    }

    public EntityMinecart getMinecart(World world, int x, int y, int z) {
        float bBoxSize = 0.125F;
        List bBox = world.getEntitiesWithinAABB(
                EntityMinecart.class,
                AxisAlignedBB.getBoundingBox((double) ((float) x + bBoxSize),
                        (double) y,
                        (double) ((float) z + bBoxSize),
                        (double) ((float) (x + 1) - bBoxSize),
                        (double) ((float) (y + 1) - bBoxSize),
                        (double) ((float) (z + 1) - bBoxSize))
        );
        for (Object i : bBox.toArray()) {
            if (i instanceof EntityMinecart)
                return (EntityMinecart) i;
        }
        return null;
    }

    @Override
    public void onRailPowered(World world, int x, int y, int z, int meta, boolean hasCart) {
        boolean playerDetectable = false;
        if (!checkNearbySameRail(world, x, y, z)) playerDetectable = true;
        if (playerDetectable) {
            RailMonoMagnetReceptionAnti.TileEntityRail rail = null;
            if (world.getTileEntity(x, y, z) instanceof RailMonoMagnetReceptionAnti.TileEntityRail) {
                rail = (RailMonoMagnetReceptionAnti.TileEntityRail) world.getTileEntity(x, y, z);
            }
            if (rail != null) {
                if (!rail.cartType.isEmpty() && !world.isRemote) {
                    if (!hasCart && (isRailPowered(world, x - 1, y, z) || isRailPowered(world, x, y, z + 1))) {
                        if (rail.cartType.equals(MinecartBase.class.getName())) {
                            MinecartBase cart = new MinecartBase(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);
                            world.spawnEntityInWorld(cart);
                        } else if (rail.cartType.equals(NSBT1.class.getName())) {
                            MinecartBase cart = new NSBT1(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);
                            world.spawnEntityInWorld(cart);
                        } else if (rail.cartType.equals(NSPCT1.class.getName())) {
                            MinecartBase cart = new NSPCT1(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);
                            world.spawnEntityInWorld(cart);
                        } else if (rail.cartType.equals(NSPCT2.class.getName())) {
                            MinecartBase cart = new NSPCT2(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);
                            world.spawnEntityInWorld(cart);
                        } else if (rail.cartType.equals(NSPCT3.class.getName())) {
                            MinecartBase cart = new NSPCT3(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);
                            world.spawnEntityInWorld(cart);
                        } else if (rail.cartType.equals(NSPCT4.class.getName())) {
                            MinecartBase cart = new NSPCT4(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);
                            world.spawnEntityInWorld(cart);
                        } else if (rail.cartType.equals(NSPCT5.class.getName())) {
                            MinecartBase cart = new NSPCT5(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);
                            world.spawnEntityInWorld(cart);
                        } else if (rail.cartType.equals(NSPCT5L.class.getName())) {
                            MinecartBase cart = new NSPCT5L(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);
                            world.spawnEntityInWorld(cart);
                        } else {
                            EntityMinecart cart = EntityMinecartEmpty.createMinecart(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5, -1);
                            world.spawnEntityInWorld(cart);
                        }
                        Point3D p = new Point3D(x, y, z);
                        tmpDelay.put(p, 0);
                        delayENB.put(p, false);
                    }

                    if (hasCart && (isRailPowered(world, x + 1, y, z) || isRailPowered(world, x, y, z - 1))) {
                        EntityMinecart cart = getMinecart(world, x, y, z);
                        if (cart == null) return;
                        cart.setDead();
                        world.removeEntity(cart);
                    }
                }
            }
        }

    }

}
