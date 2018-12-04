package club.nsdn.nyasamarailway.tileblock.signal;

import club.nsdn.nyasamarailway.block.rail.IRailReception;
import club.nsdn.nyasamarailway.entity.IExtendedInfoCart;
import club.nsdn.nyasamarailway.entity.LocoBase;
import club.nsdn.nyasamarailway.util.RailReceptionCore;
import club.nsdn.nyasamarailway.util.TrainController;
import club.nsdn.nyasamatelecom.api.device.SignalBoxGetter;
import club.nsdn.nyasamatelecom.api.device.SignalBoxSender;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityActuator;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityReceiver;
import club.nsdn.nyasamatelecom.api.util.NSASM;
import club.nsdn.nyasamatelecom.api.util.Util;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import org.thewdj.physics.Dynamics;
import org.thewdj.telecom.IPassive;

import java.util.List;

/**
 * Created by drzzm32 on 2018.4.6.
 */
public abstract class TileEntityRailReception extends TileEntityActuator {

    public static final int SPAWN_DELAY = 10;

    public int delay = 0;
    public int count = 0;
    public boolean enable = false;
    public boolean prev = false;
    public boolean doorCtrl = false;

    public String cartType = "";
    public String extInfo = "";
    public int setDelay = 10;

    @Override
    public void fromNBT(NBTTagCompound tagCompound) {
        cartType = tagCompound.getString("cartType");
        extInfo = tagCompound.getString("extInfo");
        setDelay = tagCompound.getInteger("setDelay");
        if (setDelay == 0) setDelay = 10; // for old devices
        doorCtrl = tagCompound.getBoolean("doorCtrl");
        super.fromNBT(tagCompound);
    }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
        tagCompound.setString("cartType", cartType);
        tagCompound.setString("extInfo", extInfo);
        tagCompound.setInteger("setDelay", setDelay);
        tagCompound.setBoolean("doorCtrl", doorCtrl);
        return super.toNBT(tagCompound);
    }

    @Override
    public void controlTarget(boolean state) {
        if (!setTargetSender(state)) {
            if (!setTargetGetter(state)) {
                if (getTarget() != null) {
                    TileEntity tileEntity = getTarget();
                    if (tileEntity instanceof TileEntityReceiver) {
                        if (tileEntity instanceof IPassive) {
                            super.controlTarget(state);
                        }
                    } else {
                        super.controlTarget(state);
                    }
                }
            }
        }
    }

    private boolean setTargetSender(boolean state) {
        TileEntity target = getTarget();
        if (target == null) return false;

        if (target instanceof SignalBoxSender.TileEntitySignalBoxSender) {
            ((SignalBoxSender.TileEntitySignalBoxSender) target).isEnabled = state;
            return true;
        }
        return false;
    }

    private boolean setTargetGetter(boolean state) {
        TileEntity target = getTarget();
        if (target == null) return false;

        if (target instanceof SignalBoxGetter.TileEntitySignalBoxGetter) {
            ((SignalBoxGetter.TileEntitySignalBoxGetter) target).isEnabled = state;
            return true;
        }
        return false;
    }

    public enum RailDirection {
        NONE,
        WE, //West-East
        NS //North-South
    }

    private static RailDirection getRailDirection(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        if ((meta & 2) == 0 && (meta & 4) == 0) {
            return ((meta & 1) == 0) ? RailDirection.NS : RailDirection.WE;
        } else if ((meta & 2) > 0) {
            return RailDirection.WE;
        } else if ((meta & 4) > 0) {
            return RailDirection.NS;
        }
        return RailDirection.NONE;
    }

    private static boolean isRailPowered(World world, int x, int y, int z) {
        if (world.getBlock(x, y, z) instanceof BlockRailBase) {
            if (((BlockRailBase) world.getBlock(x, y, z)).isPowered()) {
                return (world.getBlockMetadata(x, y, z) & 8) > 0;
            }
        }
        return false;
    }

    private static void registerCart(TileEntityRailReception rail, EntityMinecart cart) {
        rail.cartType = cart.getClass().getName();
        if (cart instanceof IExtendedInfoCart) rail.extInfo = ((IExtendedInfoCart) cart).getExtendedInfo();
    }

    public void reset() {
        count = 0;
        delay = 0;
        doorCtrl = false;
        enable = false;
    }

    public static boolean configure(World world, int x, int y, int z, EntityPlayer player) {
        if (world.getTileEntity(x, y, z) == null) return false;
        if (world.getTileEntity(x, y, z) instanceof TileEntityRailReception) {
            TileEntityRailReception rail = (TileEntityRailReception) world.getTileEntity(x, y, z);

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
                        public TileEntityRailReception getRail() {
                            return rail;
                        }
                    }.run();
                }

                return true;
            }
        }

        return false;
    }

    public abstract void spawn(World world, int x, int y, int z);

    protected static void loco(boolean isAnti, LocoBase loco, TileEntityRailReception rail) {
        if (rail == null) return; if (loco == null) return;

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
                if (isAnti) {
                    if (getRailDirection(world, x, y, z) == RailDirection.NS) {
                        loco.setEngineDir(-(int) Math.signum(Math.sin(TrainController.calcYaw(loco) * Math.PI / 180.0)));
                    } else {
                        loco.setEngineDir(-(int) Math.signum(Math.cos(TrainController.calcYaw(loco) * Math.PI / 180.0)));
                    }
                } else {
                    if (getRailDirection(world, x, y, z) == RailDirection.NS) {
                        loco.setEngineDir((int) Math.signum(Math.sin(TrainController.calcYaw(loco) * Math.PI / 180.0)));
                    } else {
                        loco.setEngineDir((int) Math.signum(Math.cos(TrainController.calcYaw(loco) * Math.PI / 180.0)));
                    }
                }
                loco.setEnginePower(1); loco.setEngineBrake(10);
            }
        }
    }

    public static void tick(boolean isAnti, List bBox, TileEntityRailReception rail, boolean playerDetectable) {
        if (rail == null) return;

        World world = rail.worldObj;
        int x = rail.xCoord, y = rail.yCoord, z = rail.zCoord;
        boolean hasCart = !bBox.isEmpty();

        if (!playerDetectable) {
            if (rail.getSender() != null && rail.getBlockType() instanceof IRailReception) {
                IRailReception blockRail = (IRailReception) rail.getBlockType();
                if (!isRailPowered(world, x, y, z) && rail.senderIsPowered()) {
                    blockRail.setRailState(world, x, y, z, true);
                } else if (isRailPowered(world, x, y, z) && !rail.senderIsPowered()) {
                    blockRail.setRailState(world, x, y, z, false);
                }
            }
        } else {
            if (rail.cartType.equals("loco")) {
                if (!hasCart) {
                    rail.reset();
                } else {
                    if (rail.getTarget() != null) {
                        rail.controlTarget(rail.doorCtrl);
                    }

                    for (Object obj : bBox) {
                        if (obj instanceof LocoBase) {
                            loco(isAnti, (LocoBase) obj, rail);
                            break;
                        }
                    }
                }
            } else {
                if (hasCart) {
                    if (rail.getTarget() != null) {
                        rail.controlTarget(rail.doorCtrl);
                    }

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
                    if (rail.count >= TileEntityRailReception.SPAWN_DELAY * 20) {
                        rail.reset();
                        rail.spawn(rail.worldObj, x, y, z);
                    }
                }
            }
        }
    }

    public static void cart(boolean isAnti, EntityMinecart cart, TileEntityRailReception rail, EntityPlayer player, boolean playerDetectable) {
        if (rail == null) return; if (cart == null) return;

        World world = rail.worldObj; int x = rail.xCoord, y = rail.yCoord, z = rail.zCoord;

        double maxV;
        if (!playerDetectable) {
            maxV = 0.1;
            if (isRailPowered(world, x, y, z)) {
                if (isAnti) {
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
                    if (getRailDirection(world, x, y, z) == RailDirection.NS) {
                        if (cart.motionZ > maxV) { //cart.motionZ > maxV
                            if (cart.motionZ < maxV * 1.5) cart.motionZ = maxV * 1.5;
                        } else {
                            if (cart.motionZ >= 0) cart.motionZ = -0.005;
                            if (cart.motionZ > -maxV)
                                cart.motionZ = -Dynamics.LocoMotions.calcVelocityUp(Math.abs(cart.motionZ), 0.1, 1.0, 0.1, 0.02);
                        }
                    } else {
                        if (cart.motionX < -maxV) { //cart.motionX < -maxV
                            if (cart.motionX > -maxV * 1.5) cart.motionX = -maxV * 1.5;
                        } else {
                            if (cart.motionX <= 0) cart.motionX = 0.005;
                            if (cart.motionX < maxV)
                                cart.motionX = Dynamics.LocoMotions.calcVelocityUp(Math.abs(cart.motionX), 0.1, 1.0, 0.1, 0.02);
                        }
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
            if (rail.cartType.equals("loco")) {
                return;
            }

            if (rail.cartType.isEmpty() && (cart.motionX * cart.motionX + cart.motionZ * cart.motionZ == 0))
                registerCart(rail, cart);

            if (!world.isBlockIndirectlyGettingPowered(x, y, z)) {
                if (player != null) {
                    if ((cart.motionX * cart.motionX + cart.motionZ * cart.motionZ > 0) && !rail.enable) {
                        if ((Math.abs(cart.motionX) > maxV / 2) || (Math.abs(cart.motionZ) > maxV / 2)) {
                            cart.motionX = (Math.signum(cart.motionX) * Dynamics.LocoMotions.calcVelocityDown(Math.abs(cart.motionX), 0.1D, 1.0D, 1.0D, 1.0D, 0.05D, 0.02D));
                            cart.motionZ = (Math.signum(cart.motionZ) * Dynamics.LocoMotions.calcVelocityDown(Math.abs(cart.motionZ), 0.1D, 1.0D, 1.0D, 1.0D, 0.05D, 0.02D));
                        } else {
                            rail.enable = true;
                            rail.doorCtrl = true;

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

                            if (rail.getSender() != null)
                                isEnabled = rail.senderIsPowered();

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
                                rail.doorCtrl = false;
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
                            if (isAnti) {
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
                            } else {
                                if (getRailDirection(world, x, y, z) == RailDirection.NS) {
                                    if (cart.motionZ > maxV) {
                                        if (cart.motionZ < maxV * 1.5D) cart.motionZ = (maxV * 1.5D);
                                    } else {
                                        if (cart.motionZ >= 0.0D) cart.motionZ = -0.005D;
                                        if (cart.motionZ > -maxV) {
                                            cart.motionZ = -Dynamics.LocoMotions.calcVelocityUp(Math.abs(cart.motionZ), 0.1D, 1.0D, 0.1D, 0.02D);
                                        }
                                    }
                                } else {
                                    if (cart.motionX < -maxV) {
                                        if (cart.motionX > -maxV * 1.5D) cart.motionX = (-maxV * 1.5D);
                                    } else {
                                        if (cart.motionX <= 0.0D) cart.motionX = 0.005D;
                                        if (cart.motionX < maxV) {
                                            cart.motionX = Dynamics.LocoMotions.calcVelocityUp(Math.abs(cart.motionX), 0.1D, 1.0D, 0.1D, 0.02D);
                                        }
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
                if (isAnti) {
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
                } else {
                    if (cart.motionX * cart.motionX + cart.motionZ * cart.motionZ > 0) {
                        if (getRailDirection(world, x, y, z) == RailDirection.NS) {
                            if (cart.posZ - 0.5 > z) {
                                cart.killMinecart(new DamageSource("nsr"));
                            }
                        } else {
                            if (cart.posX - 0.5 < x) {
                                cart.killMinecart(new DamageSource("nsr"));
                            }
                        }
                    }
                }
            }
        }
    }

    public static void powered(boolean isAnti, EntityMinecart cart, TileEntityRailReception rail, boolean playerDetectable) {
        if (rail == null) return;

        World world = rail.worldObj; int x = rail.xCoord, y = rail.yCoord, z = rail.zCoord;

        if (!rail.cartType.isEmpty() && !world.isRemote && playerDetectable) {
            if (isAnti) {
                if (cart == null && (isRailPowered(world, x - 1, y, z) || isRailPowered(world, x, y, z + 1))) {
                    rail.spawn(world, x, y, z);
                    rail.reset();
                }
                if (cart != null && (isRailPowered(world, x + 1, y, z) || isRailPowered(world, x, y, z - 1))) {
                    cart.killMinecart(new DamageSource("nsr"));
                }
            } else {
                if (cart == null && (isRailPowered(world, x + 1, y, z) || isRailPowered(world, x, y, z - 1))) {
                    rail.spawn(world, x, y, z);
                    rail.reset();
                }
                if (cart != null && (isRailPowered(world, x - 1, y, z) || isRailPowered(world, x, y, z + 1))) {
                    cart.killMinecart(new DamageSource("nsr"));
                }
            }
        }
    }

}
