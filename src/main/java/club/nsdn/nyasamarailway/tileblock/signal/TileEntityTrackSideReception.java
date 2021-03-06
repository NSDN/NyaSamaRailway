package club.nsdn.nyasamarailway.tileblock.signal;

import club.nsdn.nyasamarailway.entity.IExtendedInfoCart;
import club.nsdn.nyasamarailway.entity.LocoBase;
import club.nsdn.nyasamarailway.extmod.Railcraft;
import club.nsdn.nyasamarailway.network.NetworkWrapper;
import club.nsdn.nyasamarailway.util.TrainController;
import club.nsdn.nyasamatelecom.api.device.SignalBoxGetter;
import club.nsdn.nyasamatelecom.api.device.SignalBoxSender;
import club.nsdn.nyasamatelecom.api.tileentity.ITriStateReceiver;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityActuator;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityReceiver;
import club.nsdn.nyasamatelecom.api.util.NSASM;
import club.nsdn.nyasamatelecom.api.util.Util;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.thewdj.physics.Dynamics;
import org.thewdj.telecom.IPassive;

import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * Created by drzzm32 on 2019.1.4.
 */
public abstract class TileEntityTrackSideReception extends TileEntityActuator implements ITriStateReceiver, ITrackSide {

    public static abstract class ReceptionCore extends NSASM {

        public ReceptionCore(String[][] code) {
            super(code);
        }

        @Override
        public SimpleNetworkWrapper getWrapper() {
            return NetworkWrapper.instance;
        }

        private void prt(String format, Object... args) {
            if (getPlayer() != null)
                getPlayer().addChatComponentMessage(new ChatComponentTranslation(format, args));
        }

        public void setDelay(TileEntityTrackSideReception rail, int value) {
            if (rail == null) return;
            rail.setDelay = value <= 0 ? 1 : value;
            prt("info.reception.set", rail.setDelay);
        }

        @Override
        public void loadFunc(LinkedHashMap<String, Operator> funcList) {
            funcList.put("set", ((dst, src) -> {
                if (src != null) return Result.ERR;
                if (dst == null) return Result.ERR;

                if (dst.type == RegType.INT) {
                    setDelay(getReception(), (int) dst.data);
                    return Result.OK;
                }
                return Result.ERR;
            }));

            funcList.put("inv", ((dst, src) -> {
                if (src != null) return Result.ERR;
                if (dst != null) return Result.ERR;

                getReception().invert = !getReception().invert;

                return Result.OK;
            }));
        }

        public abstract TileEntityTrackSideReception getReception();

    }

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

    protected boolean prevSGN, prevTXD, prevRXD, prevInv;
    protected boolean hasChanged() {
        return prevSGN != getSGNState() || prevTXD != getTXDState() || prevRXD != getRXDState() || prevInv != isInvert();
    }
    protected void updateChanged() {
        prevSGN = getSGNState(); prevTXD = getTXDState(); prevRXD = getRXDState(); prevInv = isInvert();
    }

    @Override
    public void setDir(ForgeDirection dir) {
        direction = dir;
    }

    @Override
    public boolean hasInvert() {
        return true;
    }

    @Override
    public boolean isInvert() {
        return invert;
    }

    public ForgeDirection direction;
    public boolean invert = false;

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
        direction = ForgeDirection.getOrientation(
                tagCompound.getInteger("direction")
        );
        invert = tagCompound.getBoolean("invert");

        state = tagCompound.getInteger("state");
        prevState = tagCompound.getInteger("prevState");

        cartType = tagCompound.getString("cartType");
        extInfo = tagCompound.getString("extInfo");
        setDelay = tagCompound.getInteger("setDelay");
        doorCtrl = tagCompound.getBoolean("doorCtrl");
        super.fromNBT(tagCompound);
    }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
        if (direction == null) direction = ForgeDirection.UNKNOWN;
        tagCompound.setInteger("direction", direction.ordinal());
        tagCompound.setBoolean("invert", invert);

        tagCompound.setInteger("state", state);
        tagCompound.setInteger("prevState", prevState);

        tagCompound.setString("cartType", cartType);
        tagCompound.setString("extInfo", extInfo);
        tagCompound.setInteger("setDelay", setDelay);
        tagCompound.setBoolean("doorCtrl", doorCtrl);
        return super.toNBT(tagCompound);
    }

    @Override
    public void controlTarget(boolean state) {
        if (!tryControlFirst(state))
            if (!tryControlSecond(state))
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

    private boolean tryControlFirst(boolean state) {
        TileEntity railTarget = getTarget();

        if (railTarget == null) return false;
        if (Railcraft.getInstance() == null) return false;
        if (!Railcraft.getInstance().verifySwitch(railTarget)) return false;

        Railcraft.getInstance().setSwitch(railTarget, state);
        return true;
    }

    private boolean tryControlSecond(boolean state) {
        TileEntity railTarget = getTarget();
        if (railTarget == null) return false;

        if (railTarget instanceof TileEntitySignalLight) {
            ((TileEntitySignalLight) railTarget).isPowered = state;
            return true;
        }
        return false;
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

    private static void registerCart(TileEntityTrackSideReception rail, EntityMinecart cart) {
        rail.cartType = cart.getClass().getName();
        if (cart instanceof IExtendedInfoCart) rail.extInfo = ((IExtendedInfoCart) cart).getExtendedInfo();
    }

    protected void reset() {
        count = 0;
        delay = 0;
        doorCtrl = false;
        enable = false;
    }

    public static boolean configure(World world, int x, int y, int z, EntityPlayer player) {
        if (world.getTileEntity(x, y, z) == null) return false;
        if (world.getTileEntity(x, y, z) instanceof TileEntityTrackSideReception) {
            TileEntityTrackSideReception reception = (TileEntityTrackSideReception) world.getTileEntity(x, y, z);

            ItemStack stack = player.getCurrentEquippedItem();
            if (stack != null) {
                NBTTagList list = Util.getTagListFromNGT(stack);
                if (list == null) return false;

                if (!world.isRemote) {
                    String[][] code = NSASM.getCode(list);
                    new ReceptionCore(code) {
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
                        public TileEntityTrackSideReception getReception() {
                            return reception;
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

            if (reception.hasChanged()) {
                reception.updateChanged();
                world.markBlockForUpdate(x, y, z);
            }

            world.scheduleBlockUpdate(x, y, z, reception.getBlockType(), 1);
        }

    }

    public abstract void spawn(World world, double x, double y, double z);

    protected void spawn() {
        ForgeDirection railPos = direction.getRotation(ITrackSide.getAxis());
        int x = xCoord + railPos.offsetX, y = yCoord, z = zCoord + railPos.offsetZ;
        if (getWorldObj().getBlock(x, y + 1, z) instanceof BlockRailBase)
            spawn(getWorldObj(), x + 0.5, y + 0.5 + 1, z + 0.5);
        else if (getWorldObj().getBlock(x, y, z) instanceof BlockRailBase)
            spawn(getWorldObj(), x + 0.5, y + 0.5, z + 0.5);
    }

    protected static void core(EntityMinecart cart, TileEntityTrackSideReception reception) {
        if (reception == null) return;

        boolean hasCart = (cart != null);
        ForgeDirection railPos = reception.direction.getRotation(ITrackSide.getAxis());
        int x = reception.xCoord + railPos.offsetX, y = reception.yCoord, z = reception.zCoord + railPos.offsetZ;

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
                if (reception.count >= SPAWN_DELAY * 20) {
                    reception.reset();
                    reception.spawn();
                }
            } else {
                if (reception.getTarget() != null) {
                    reception.controlTarget(reception.doorCtrl);
                }

                if (cart.riddenByEntity == null) {
                    cart.motionX = 0.0D; cart.motionZ = 0.0D;
                    cart.setPosition(x + 0.5, y + 0.5, z + 0.5);

                    reception.reset();

                    if (ITrackSide.hasMultiMinecart(reception, reception.direction)) {
                        LinkedList<EntityMinecart> carts = ITrackSide.getMinecarts(reception, reception.direction);
                        for (int i = 1; i < carts.size(); i++)
                            carts.get(i).killMinecart(new DamageSource("nsr"));
                    }

                    reception.prev = true;
                } else if (reception.prev) {
                    reception.prev = false;
                    //reception.delay = DELAY_TIME * 15 - 1;
                }

                EntityPlayer player = null;
                if (cart.riddenByEntity instanceof EntityPlayer)
                    player = (EntityPlayer) cart.riddenByEntity;
                cart(cart, reception, player);
            }

            tri(cart, reception);
        }
    }

    protected static void loco(LocoBase loco, TileEntityTrackSideReception reception) {
        if (reception == null) return; if (loco == null) return;

        double maxV = 0.2;
        World world = loco.worldObj;
        ForgeDirection railPos = reception.direction.getRotation(ITrackSide.getAxis());
        int x = reception.xCoord + railPos.offsetX, y = reception.yCoord, z = reception.zCoord + railPos.offsetZ;
        if (world.getBlock(x, y + 1, z) instanceof BlockRailBase)
            y += 1;

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
                int dir = 0;
                if (reception.direction.offsetZ != 0)
                    dir = (int) Math.signum(Math.sin(TrainController.calcYaw(loco) * Math.PI / 180.0));
                else if (reception.direction.offsetX != 0)
                    dir = (int) Math.signum(Math.cos(TrainController.calcYaw(loco) * Math.PI / 180.0));
                if ((reception.direction == ForgeDirection.SOUTH || reception.direction == ForgeDirection.WEST))
                    dir = -dir;
                if (reception.isInvert()) dir = -dir;
                loco.setEngineDir(dir);
                loco.setEnginePower(1); loco.setEngineBrake(10);
            }
        }
    }

    protected static void cart(EntityMinecart cart, TileEntityTrackSideReception reception, EntityPlayer player) {
        if (reception == null) return; if (cart == null) return;

        World world = reception.worldObj;
        ForgeDirection railPos = reception.direction.getRotation(ITrackSide.getAxis());
        int x = reception.xCoord + railPos.offsetX, y = reception.yCoord, z = reception.zCoord + railPos.offsetZ;
        if (world.getBlock(x, y + 1, z) instanceof BlockRailBase)
            y += 1;

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
                    if (reception.isInvert()) dir = -dir;
                    if (reception.direction.offsetZ != 0) {
                        if (cart.motionZ * dir >= 0.0D) cart.motionZ = -dir * 0.005D;
                        if (Math.abs(cart.motionZ) < maxV)
                            cart.motionZ = -dir * Dynamics.LocoMotions.calcVelocityUp(Math.abs(cart.motionZ), 0.1D, 1.0D, 0.1D, 0.02D);
                    } else if (reception.direction.offsetX != 0) {
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

        if (!reception.cartType.isEmpty() && !reception.cartType.equals("loco")) {
            switch (reception.state) {
                case STATE_POS:
                    if (cart == null) {
                        reception.spawn(); reception.reset();
                    }
                    break;
                case STATE_NEG:
                    if (cart != null) {
                        cart.killMinecart(new DamageSource("nsr"));
                    }
                    break;
                case STATE_ZERO:
                    break;
            }
            reception.state = STATE_ZERO;
        }
    }

}
