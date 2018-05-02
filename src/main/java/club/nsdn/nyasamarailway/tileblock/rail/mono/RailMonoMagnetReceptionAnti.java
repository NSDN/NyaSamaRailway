package club.nsdn.nyasamarailway.tileblock.rail.mono;

import club.nsdn.nyasamarailway.block.rail.IRailDirectional;
import club.nsdn.nyasamarailway.entity.LocoBase;
import club.nsdn.nyasamarailway.entity.MinecartBase;
import club.nsdn.nyasamarailway.entity.cart.*;
import club.nsdn.nyasamarailway.item.tool.ItemNTP32Bit;
import club.nsdn.nyasamarailway.item.tool.ItemNTP8Bit;
import club.nsdn.nyasamarailway.util.RailReceptionCore;
import club.nsdn.nyasamarailway.util.TrainController;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityReceiver;
import club.nsdn.nyasamatelecom.api.util.NSASM;
import club.nsdn.nyasamatelecom.api.util.Util;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import org.thewdj.physics.Dynamics;

import java.util.List;
import java.util.Random;

/**
 * Created by drzzm32 on 2017.1.13.
 */
public class RailMonoMagnetReceptionAnti extends RailMonoMagnetPowered implements IRailDirectional {

    public static class TileEntityRail extends club.nsdn.nyasamarailway.tileblock.signal.TileEntityRailReception implements RailMonoMagnetPowerable {

        @Override
        @SideOnly(Side.CLIENT)
        public AxisAlignedBB getRenderBoundingBox()
        {
            return AxisAlignedBB
                    .getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1)
                    .expand(4, 4, 4);
        }

    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityRail();
    }

    public RailMonoMagnetReceptionAnti() {
        super("RailMonoMagnetReceptionAnti", "rail_mono_magnet_reception_anti");
    }

    public boolean isForward() {
        return false;
    }

    public boolean checkNearbySameRail(World world, int x, int y, int z) {
        return world.getBlock(x + 1, y, z) == this || world.getBlock(x - 1, y, z) == this ||
                world.getBlock(x, y, z + 1) == this || world.getBlock(x, y, z - 1) == this;
    }

    public void registerCart(TileEntityRail rail, EntityMinecart cart) {
        rail.cartType = cart.getClass().getName();
        if (cart instanceof NSPCT9) rail.extInfo = ((NSPCT9) cart).getExtendedInfo();
    }

    public void spawnCart(World world, int x, int y, int z) {
        TileEntityRail rail = null;
        if (world.getTileEntity(x, y, z) instanceof TileEntityRail) {
            rail = (TileEntityRail) world.getTileEntity(x, y, z);
        }
        if (rail != null) {
            if (rail.cartType.isEmpty()) return;
            if (rail.cartType.equals("loco")) return;

            if (rail.cartType.equals(NSPCT4.class.getName())) {
                MinecartBase cart = new NSPCT4(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);
                world.spawnEntityInWorld(cart);
            } else if (rail.cartType.equals(NSPCT5.class.getName())) {
                MinecartBase cart = new NSPCT5(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);
                world.spawnEntityInWorld(cart);
            } else if (rail.cartType.equals(NSPCT6W.class.getName())) {
                NSPCT6W.doSpawn(world, x, y, z, "");
            } else if (rail.cartType.equals(NSPCT8W.class.getName())) {
                NSPCT8W.doSpawn(world, x, y, z, "");
            } else if (rail.cartType.equals(NSPCT9.class.getName())) {
                NSPCT9 cart = new NSPCT9(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);
                cart.setExtendedInfo(rail.extInfo);
                world.spawnEntityInWorld(cart);
            }
        }
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        world.scheduleBlockUpdate(x, y, z, this, 1);
    }

    @Override
    public int tickRate(World world) {
        return 20;
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (!world.isRemote) {
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
            boolean hasCart = !bBox.isEmpty();

            TileEntityRail rail = null;
            if (world.getTileEntity(x, y, z) instanceof TileEntityRail) {
                rail = (TileEntityRail) world.getTileEntity(x, y, z);
            }
            if (rail != null && !checkNearbySameRail(world, x, y, z)) {
                if (rail.cartType.equals("loco")) {
                    if (!hasCart) {
                        rail.reset();
                    } else {
                        if (rail.getTarget() != null) {
                            rail.controlTarget(rail.doorCtrl);
                        }

                        for (Object obj : bBox) {
                            if (obj instanceof LocoBase) {
                                onLocoPass((LocoBase) obj, rail);
                                break;
                            }
                        }
                    }
                } else {
                    if (hasCart) {
                        for (Object obj : bBox) {
                            if (obj instanceof EntityMinecart) {
                                if (((EntityMinecart) obj).riddenByEntity == null) {
                                    EntityMinecart cart = (EntityMinecart) obj;
                                    cart.motionX = 0.0D;
                                    cart.motionZ = 0.0D;
                                    cart.setPosition(x + 0.5, y + 0.5, z + 0.5);

                                    rail.reset();

                                    rail.prev = true;
                                } else if (rail.prev) {
                                    rail.prev = false;
                                    //rail.delay = DELAY_TIME * 15 - 1;
                                }
                                break;
                            }
                        }
                    } else {
                        rail.count += 1;
                        if (rail.count >= TileEntityRail.SPAWN_DELAY * 20) {
                            rail.reset();
                            spawnCart(world, x, y, z);
                        }
                    }
                }
            }

            world.scheduleBlockUpdate(x, y, z, this, 1);
        }
        super.updateTick(world, x, y, z, random);
    }

    public boolean timeExceed(World world, int x, int y, int z) {
        if (world.getTileEntity(x, y, z) instanceof TileEntityRail) {
            TileEntityRail rail = (TileEntityRail) world.getTileEntity(x, y, z);
            return rail.delay >= rail.setDelay * 20;
        }
        return false;
    }

    public void onLocoPass(LocoBase loco, TileEntityRail rail) {
        double maxV = 0.2;
        int x = rail.xCoord, y = rail.yCoord, z = rail.zCoord;
        World world = loco.worldObj;

        if (loco.Velocity > 0 && !rail.enable) {
            if (loco.Velocity > maxV) {
                // speed down
                loco.setEnginePower(0); loco.setEngineBrake(1);
            } else {
                // stop
                loco.setEnginePower(0); loco.setEngineBrake(1);
                rail.doorCtrl = true;

                rail.enable = true;
                loco.setPosition(x + 0.5, y + 0.5, z + 0.5);
                if (rail.setDelay == 10) world.playSoundAtEntity(loco, "nyasamarailway:info.reception.pause", 0.5F, 1.0F);
            }
        } else {
            if (rail.delay < rail.setDelay * 20 && rail.enable) {
                boolean isEnabled = false;

                if (rail.getSender() != null)
                    isEnabled = rail.senderIsPowered();

                if (!isEnabled) rail.delay += 1;
                else {
                    rail.count += 1;

                    if (rail.delay + rail.count == rail.setDelay * 15) {
                        rail.delay = rail.setDelay * 15 - 1;
                        rail.count += 1;
                        world.playSoundAtEntity(loco, "nyasamarailway:info.reception.delay", 0.5F, 1.0F);
                    }
                }

                if (rail.delay == rail.setDelay * 15) {
                    rail.count = 0;
                    rail.doorCtrl = false;
                    world.playSoundAtEntity(loco, "nyasamarailway:info.reception.ready", 0.5F, 1.0F);
                }

                if (loco.Velocity > maxV) {
                    // keep speed down
                    loco.setEnginePower(0); loco.setEngineBrake(1);
                } else {
                    // keep stop
                    loco.setEnginePower(0); loco.setEngineBrake(1);
                    loco.setPosition(x + 0.5, y + 0.5, z + 0.5);
                }
            } else {
                // start, dir = neg, -x | +z
                if (getRailDirection(world, x, y, z) == RailDirection.NS) {
                    loco.setEngineDir(-(int) Math.signum(Math.sin(TrainController.calcYaw(loco) * Math.PI / 180.0)));
                } else {
                    loco.setEngineDir(-(int) Math.signum(Math.cos(TrainController.calcYaw(loco) * Math.PI / 180.0)));
                }
                loco.setEnginePower(1); loco.setEngineBrake(10);
            }
        }
    }

    @Override
    public void onMinecartPass(World world, EntityMinecart cart, int x, int y, int z) {
        boolean playerDetectable = false;
        boolean hasPlayer = false;
        EntityPlayer player = null;
        if (!checkNearbySameRail(world, x, y, z)) playerDetectable = true;
        if (cart.riddenByEntity instanceof EntityPlayer) {
            player = (EntityPlayer) cart.riddenByEntity;
            ItemStack stack = ((EntityPlayer) cart.riddenByEntity).getCurrentEquippedItem();
            if (stack != null) {
                if (stack.getItem() instanceof ItemNTP8Bit ||
                        stack.getItem() instanceof ItemNTP32Bit) {
                    return;
                }
            }
            hasPlayer = true;
        } else if (cart.riddenByEntity instanceof EntityMinecart) {
            EntityMinecart ncart = (EntityMinecart) cart.riddenByEntity;

            if (ncart.riddenByEntity instanceof EntityPlayer) {
                player = (EntityPlayer) ncart.riddenByEntity;
                ItemStack stack = ((EntityPlayer) ncart.riddenByEntity).getCurrentEquippedItem();
                if (stack != null) {
                    if (stack.getItem() instanceof ItemNTP8Bit ||
                            stack.getItem() instanceof ItemNTP32Bit) {
                        return;
                    }
                }
                hasPlayer = true;
            }
        }

        double maxV;
        if (!playerDetectable) {
            maxV = 0.1;
            if (isRailPowered(world, x, y, z)) {
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
                        cart.motionZ = 0.0D;
                    } else {
                        cart.motionX = 0.0D;
                    }
                }
            }
        } else {
            maxV = 0.2;
            TileEntityRail rail = null;
            if (world.getTileEntity(x, y, z) instanceof TileEntityRail) {
                rail = (TileEntityRail) world.getTileEntity(x, y, z);
            }
            if (rail != null) {
                if (rail.cartType.equals("loco")) {
                    return;
                }

                if (rail.cartType.isEmpty() && (cart.motionX * cart.motionX + cart.motionZ * cart.motionZ == 0))
                    registerCart(rail, cart);

                if (!world.isBlockIndirectlyGettingPowered(x, y, z)) {
                    if (hasPlayer) {
                        if ((cart.motionX * cart.motionX + cart.motionZ * cart.motionZ > 0) && !rail.enable) {
                            if ((Math.abs(cart.motionX) > maxV / 2) || (Math.abs(cart.motionZ) > maxV / 2)) {
                                cart.motionX = (Math.signum(cart.motionX) * Dynamics.LocoMotions.calcVelocityDown(Math.abs(cart.motionX), 0.1D, 1.0D, 1.0D, 1.0D, 0.05D, 0.02D));
                                cart.motionZ = (Math.signum(cart.motionZ) * Dynamics.LocoMotions.calcVelocityDown(Math.abs(cart.motionZ), 0.1D, 1.0D, 1.0D, 1.0D, 0.05D, 0.02D));
                            } else {
                                rail.enable = true;
                                if (getRailDirection(world, x, y, z) == RailDirection.NS) {
                                    cart.motionZ = 0.0D;
                                } else {
                                    cart.motionX = 0.0D;
                                }
                                cart.setPosition(x + 0.5, y + 0.5, z + 0.5);
                                if (player instanceof EntityPlayerMP && rail.setDelay == 10) {
                                    player.addChatComponentMessage(
                                            new ChatComponentTranslation("info.reception.pause", rail.setDelay)
                                    );
                                    world.playSoundAtEntity(cart, "nyasamarailway:info.reception.pause", 0.5F, 1.0F);
                                }
                            }
                        } else {
                            if (rail.delay < rail.setDelay * 20 && rail.enable) {
                                boolean isEnabled = false;

                                if (world.getTileEntity(x, y, z) instanceof TileEntityReceiver) {
                                    TileEntityReceiver railReceiver = (TileEntityReceiver) world.getTileEntity(x, y, z);
                                    if (railReceiver.senderIsPowered()) isEnabled = true;
                                }

                                if (!isEnabled) rail.delay += 1;
                                else {
                                    rail.count += 1;

                                    if (rail.delay + rail.count == rail.setDelay * 15) {
                                        rail.delay = rail.setDelay * 15 - 1;
                                        rail.count += 1;
                                        if (player instanceof EntityPlayerMP) {
                                            player.addChatComponentMessage(
                                                    new ChatComponentTranslation("info.reception.delay")
                                            );
                                            world.playSoundAtEntity(cart, "nyasamarailway:info.reception.delay", 0.5F, 1.0F);
                                        }
                                    }
                                }

                                if (rail.delay == rail.setDelay * 15) {
                                    rail.count = 0;
                                    if (player instanceof EntityPlayerMP) {
                                        player.addChatComponentMessage(
                                                new ChatComponentTranslation("info.reception.ready")
                                        );
                                        world.playSoundAtEntity(cart, "nyasamarailway:info.reception.ready", 0.5F, 1.0F);
                                    }
                                }

                                cart.motionX = 0.0D;
                                cart.motionZ = 0.0D;
                                cart.setPosition(x + 0.5, y + 0.5, z + 0.5);
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
                        cart.motionX = 0.0D;
                        cart.motionZ = 0.0D;
                        cart.setPosition(x + 0.5, y + 0.5, z + 0.5);

                        rail.reset();
                    }
                } else {
                    if (cart.motionX * cart.motionX + cart.motionZ * cart.motionZ > 0) {
                        if (getRailDirection(world, x, y, z) == RailDirection.NS) {
                            if (cart.posZ - 0.5 < z) {
                                cart.killMinecart(new DamageSource("nsr"));
                            }
                        } else {
                            if (cart.posX - 0.5 > x) {
                                cart.killMinecart(new DamageSource("nsr"));
                            }
                        }
                    }
                }
            }
        }

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
            TileEntityRail rail = null;
            if (world.getTileEntity(x, y, z) instanceof TileEntityRail) {
                rail = (TileEntityRail) world.getTileEntity(x, y, z);
            }
            if (rail != null) {
                if (!rail.cartType.isEmpty() && !world.isRemote) {
                    if (!hasCart && (isRailPowered(world, x - 1, y, z) || isRailPowered(world, x, y, z + 1))) {
                        spawnCart(world, x, y, z);
                        rail.reset();
                    }

                    if (hasCart && (isRailPowered(world, x + 1, y, z) || isRailPowered(world, x, y, z - 1))) {
                        EntityMinecart cart = getMinecart(world, x, y, z);
                        if (cart == null) return;
                        cart.killMinecart(new DamageSource("nsr"));
                    }
                }
            }
        }

    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (world.getTileEntity(x, y, z) == null) return false;
        if (world.getTileEntity(x, y, z) instanceof club.nsdn.nyasamarailway.tileblock.signal.TileEntityRailReception) {
            club.nsdn.nyasamarailway.tileblock.signal.TileEntityRailReception rail = (club.nsdn.nyasamarailway.tileblock.signal.TileEntityRailReception) world.getTileEntity(x, y, z);

            ItemStack stack = player.getCurrentEquippedItem();
            if (stack != null) {
                NBTTagList list = Util.getTagListFromNGT(stack);
                if (list == null) return false;

                if (!world.isRemote) {
                    String[][] code = NSASM.getCode(list);
                    new RailReceptionCore(code) {
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
                        public club.nsdn.nyasamarailway.tileblock.signal.TileEntityRailReception getRail() {
                            return rail;
                        }
                    }.run();
                }

                return true;
            }
        }

        return false;
    }

}
