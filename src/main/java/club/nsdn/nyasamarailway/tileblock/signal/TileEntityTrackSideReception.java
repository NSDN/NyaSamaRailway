package club.nsdn.nyasamarailway.tileblock.signal;

import club.nsdn.nyasamarailway.entity.IExtendedInfoCart;
import club.nsdn.nyasamarailway.entity.LocoBase;
import club.nsdn.nyasamarailway.util.RailReceptionCore;
import club.nsdn.nyasamarailway.util.TrainController;
import club.nsdn.nyasamatelecom.api.tileentity.ITriStateReceiver;
import club.nsdn.nyasamatelecom.api.util.NSASM;
import club.nsdn.nyasamatelecom.api.util.Util;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.thewdj.physics.Dynamics;

import java.util.List;

/**
 * Created by drzzm32 on 2019.1.4.
 */
public abstract class TileEntityTrackSideReception extends TileEntityRailReception implements ITriStateReceiver, ITrackSide {

    @Override
    public boolean getSGNState() {
        return this.doorCtrl;
    }

    @Override
    public boolean getTXDState() {
        return getTarget() != null;
    }

    @Override
    public boolean getRXDState() {
        return getSender() != null;
    }

    public ForgeDirection direction;

    public static final int STATE_POS = 1;
    public static final int STATE_ZERO = 0;
    public static final int STATE_NEG = -1;

    public int state;
    public int prevState;

    @Override
    public void setStatePos() {
        state = state == STATE_NEG ? STATE_ZERO : STATE_POS;
    }

    @Override
    public void setStateNeg() {
        state = state == STATE_POS ? STATE_ZERO : STATE_NEG;
    }

    @Override
    public void fromNBT(NBTTagCompound tagCompound) {
        direction = ForgeDirection.getOrientation(
                tagCompound.getInteger("direction")
        );
        state = tagCompound.getInteger("state");
        prevState = tagCompound.getInteger("prevState");
        super.fromNBT(tagCompound);
    }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
        if (direction == null) direction = ForgeDirection.UNKNOWN;
        tagCompound.setInteger("direction", direction.ordinal());
        tagCompound.setInteger("state", state);
        tagCompound.setInteger("prevState", prevState);
        return super.toNBT(tagCompound);
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

    public static void tick(World world, int x, int y, int z) {
        if (world.isRemote) return;
        if (world.getTileEntity(x, y, z) == null) return;
        if (world.getTileEntity(x, y, z) instanceof TileEntityTrackSideReception) {
            TileEntityTrackSideReception reception = (TileEntityTrackSideReception) world.getTileEntity(x, y, z);

            EntityMinecart cart = ITrackSide.getMinecart(reception, reception.direction);

            core(cart, reception);

            world.scheduleBlockUpdate(x, y, z, reception.blockType, 1);
        }

    }

    public abstract void spawn(World world, int x, int y, int z);

    protected static void core(EntityMinecart cart, TileEntityTrackSideReception reception) {
        if (reception == null) return;

        boolean hasCart = cart != null;
        int x = reception.xCoord, y = reception.yCoord, z = reception.zCoord;

        if (reception.cartType.equals("loco")) {
            if (!hasCart) {
                reception.reset();
            } else {
                if (reception.getTarget() != null) {
                    reception.controlTarget(reception.doorCtrl);
                }

                if (cart instanceof LocoBase)
                    loco((LocoBase) cart, reception);
            }
        } else {
            if (!hasCart) {
                reception.count += 1;
                if (reception.count >= TileEntityRailReception.SPAWN_DELAY * 20) {
                    reception.reset();
                    reception.spawn(reception.worldObj, x, y, z);
                }
            } else {
                if (reception.getTarget() != null) {
                    reception.controlTarget(reception.doorCtrl);
                }

                if (cart.riddenByEntity == null) {
                    cart.motionX = 0.0D; cart.motionZ = 0.0D;
                    cart.setPosition(x + 0.5, y + 0.5, z + 0.5);

                    reception.reset();

                    reception.prev = true;
                } else if (reception.prev) {
                    reception.prev = false;
                    //reception.delay = DELAY_TIME * 15 - 1;
                }

                EntityPlayer player = null;
                if (cart.riddenByEntity instanceof EntityPlayer)
                    player = (EntityPlayer) cart.riddenByEntity;
                cart(cart, reception, player);

                tri(cart, reception);
            }
        }
    }

    protected static void loco(LocoBase loco, TileEntityTrackSideReception reception) {
        if (reception == null) return; if (loco == null) return;

        double maxV = 0.2;
        int x = reception.xCoord, y = reception.yCoord, z = reception.zCoord;
        World world = loco.worldObj;

        if (loco.Velocity > 0 && !reception.enable) {
            if (loco.Velocity > maxV) {
                // speed down
                loco.setEnginePower(0); loco.setEngineBrake(1);
            } else {
                // stop
                loco.setEnginePower(0); loco.setEngineBrake(1);
                reception.doorCtrl = true;

                reception.enable = true;
                loco.setPosition(x + 0.5, y + 0.5, z + 0.5);
                if (reception.setDelay == 10) world.playSoundAtEntity(loco, "nyasamarailway:info.reception.pause", 0.5F, 1.0F);
            }
        } else {
            if (reception.delay < reception.setDelay * 20 && reception.enable) {
                boolean isEnabled = false;

                if (reception.getSender() != null)
                    isEnabled = reception.senderIsPowered();

                if (!isEnabled) reception.delay += 1;
                else {
                    reception.count += 1;

                    if (reception.delay + reception.count == reception.setDelay * 15) {
                        reception.delay = reception.setDelay * 15 - 1;
                        reception.count += 1;
                        world.playSoundAtEntity(loco, "nyasamarailway:info.reception.delay", 0.5F, 1.0F);
                    }
                }

                if (reception.delay == reception.setDelay * 15) {
                    reception.count = 0;
                    reception.doorCtrl = false;
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
                if (reception.direction == ForgeDirection.NORTH || reception.direction == ForgeDirection.EAST)
                    loco.setEngineDir(1);
                else if (reception.direction == ForgeDirection.SOUTH || reception.direction == ForgeDirection.WEST)
                    loco.setEngineDir(-1);
                loco.setEnginePower(1); loco.setEngineBrake(10);
            }
        }
    }

    protected static void cart(EntityMinecart cart, TileEntityTrackSideReception reception, EntityPlayer player) {
        if (reception == null) return; if (cart == null) return;

        World world = reception.worldObj;
        int x = reception.xCoord, y = reception.yCoord, z = reception.zCoord;

        double maxV = 0.2;
        if (reception.cartType.equals("loco")) {
            return;
        }

        if (reception.cartType.isEmpty() && (cart.motionX * cart.motionX + cart.motionZ * cart.motionZ == 0))
            registerCart(reception, cart);

        if (player != null) {
            if ((cart.motionX * cart.motionX + cart.motionZ * cart.motionZ > 0) && !reception.enable) {
                if ((Math.abs(cart.motionX) > maxV / 2) || (Math.abs(cart.motionZ) > maxV / 2)) {
                    cart.motionX = (Math.signum(cart.motionX) * Dynamics.LocoMotions.calcVelocityDown(Math.abs(cart.motionX), 0.1D, 1.0D, 1.0D, 1.0D, 0.05D, 0.02D));
                    cart.motionZ = (Math.signum(cart.motionZ) * Dynamics.LocoMotions.calcVelocityDown(Math.abs(cart.motionZ), 0.1D, 1.0D, 1.0D, 1.0D, 0.05D, 0.02D));
                } else {
                    reception.enable = true;
                    reception.doorCtrl = true;

                    cart.motionX = 0.0D; cart.motionZ = 0.0D;
                    cart.setPosition(x + 0.5, y + 0.5, z + 0.5);
                    if (reception.setDelay == 10) {
                        player.addChatComponentMessage(
                            new ChatComponentTranslation("info.reception.pause", reception.setDelay)
                        );
                        world.playSoundAtEntity(cart, "nyasamarailway:info.reception.pause", 0.5F, 1.0F);
                    }
                }
            } else {
                if (reception.delay < reception.setDelay * 20 && reception.enable) {
                    boolean isEnabled = false;

                    if (reception.getSender() != null)
                        isEnabled = reception.senderIsPowered();

                    if (!isEnabled) reception.delay += 1;
                    else {
                        reception.count += 1;

                        if (reception.delay + reception.count == reception.setDelay * 15) {
                            reception.delay = reception.setDelay * 15 - 1;
                            reception.count += 1;
                            player.addChatComponentMessage(
                                new ChatComponentTranslation("info.reception.delay")
                            );
                            world.playSoundAtEntity(cart, "nyasamarailway:info.reception.delay", 0.5F, 1.0F);
                        }
                    }

                    if (reception.delay == reception.setDelay * 15) {
                        reception.count = 0;
                        reception.doorCtrl = false;
                        player.addChatComponentMessage(
                            new ChatComponentTranslation("info.reception.ready")
                        );
                        world.playSoundAtEntity(cart, "nyasamarailway:info.reception.ready", 0.5F, 1.0F);
                    }

                    cart.motionX = 0.0D;
                    cart.motionZ = 0.0D;
                    cart.setPosition(x + 0.5, y + 0.5, z + 0.5);
                } else {
                    double dir = (reception.direction == ForgeDirection.NORTH || reception.direction == ForgeDirection.EAST) ? 1 : -1;
                    if (reception.direction == ForgeDirection.NORTH || reception.direction == ForgeDirection.SOUTH) {
                        if (cart.motionZ * dir >= 0.0D) cart.motionZ = -dir * 0.005D;
                        if (Math.abs(cart.motionZ) < maxV)
                            cart.motionZ = -dir * Dynamics.LocoMotions.calcVelocityUp(Math.abs(cart.motionZ), 0.1D, 1.0D, 0.1D, 0.02D);
                    } else if (reception.direction == ForgeDirection.WEST || reception.direction == ForgeDirection.EAST) {
                        if (cart.motionX * dir <= 0.0D) cart.motionX = dir * 0.005D;
                        if (Math.abs(cart.motionX) < maxV)
                            cart.motionX = dir * Dynamics.LocoMotions.calcVelocityUp(Math.abs(cart.motionX), 0.1D, 1.0D, 0.1D, 0.02D);
                    }
                }
            }
        } else {
            cart.motionX = 0.0D; cart.motionZ = 0.0D;
            cart.setPosition(x + 0.5, y + 0.5, z + 0.5);

            reception.reset();
        }
    }

    protected static void tri(EntityMinecart cart, TileEntityTrackSideReception reception) {
        if (reception == null) return;

        World world = reception.worldObj;
        int x = reception.xCoord, y = reception.yCoord, z = reception.zCoord;

        if (!reception.cartType.isEmpty()) {
            switch (reception.state) {
                case STATE_POS:
                    if (cart == null) {
                        reception.spawn(world, x, y, z); reception.reset();
                    }
                    break;
                case STATE_NEG:
                    if (cart != null) {
                        cart.killMinecart(new DamageSource("nsr"));
                    }
                case STATE_ZERO:
                    break;
            }
        }
    }

}
