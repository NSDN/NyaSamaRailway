package club.nsdn.nyasamarailway.api.signal;

import club.nsdn.nyasamarailway.api.cart.AbsLocoBase;
import club.nsdn.nyasamarailway.api.cart.AbsMotoCart;
import club.nsdn.nyasamarailway.api.cart.IExtendedInfoCart;
import club.nsdn.nyasamarailway.network.NetworkWrapper;
import club.nsdn.nyasamarailway.util.SoundUtil;
import club.nsdn.nyasamarailway.util.TrainController;
import club.nsdn.nyasamatelecom.api.device.SignalBoxGetter;
import club.nsdn.nyasamatelecom.api.device.SignalBoxSender;
import club.nsdn.nyasamatelecom.api.tileentity.ITriStateReceiver;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityActuator;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityReceiver;
import club.nsdn.nyasamatelecom.api.util.NSASM;
import club.nsdn.nyasamatelecom.api.util.Util;
import net.minecraft.entity.Entity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.thewdj.physics.Dynamics;
import org.thewdj.telecom.IPassive;

import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * Created by drzzm32 on 2019.2.10
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
                Util.say(getPlayer(), format, args);
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
    public void setDir(EnumFacing dir) {
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

    @Override
    public void setInvert(boolean invert) {
        this.invert = invert;
    }

    public EnumFacing direction;
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
        super.fromNBT(tagCompound);

        direction = EnumFacing.byName(
                tagCompound.getString("direction")
        );
        invert = tagCompound.getBoolean("invert");

        state = tagCompound.getInteger("state");
        prevState = tagCompound.getInteger("prevState");

        cartType = tagCompound.getString("cartType");
        extInfo = tagCompound.getString("extInfo");
        setDelay = tagCompound.getInteger("setDelay");
        doorCtrl = tagCompound.getBoolean("doorCtrl");
    }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
        if (direction == null) direction = EnumFacing.DOWN;
        tagCompound.setString("direction", direction.getName());
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

    protected boolean tryControlFirst(boolean state) {
        TileEntity railTarget = getTarget();
        if (railTarget == null) return false;

        if (railTarget instanceof TileEntitySignalLight) {
            ((TileEntitySignalLight) railTarget).isPowered = state;
            return true;
        }

        return false;
    }

    protected boolean tryControlSecond(boolean state) {
        TileEntity railTarget = getTarget();
        if (railTarget == null) return false;

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

    public static boolean configure(World world, BlockPos pos, EntityPlayer player) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity == null) return false;
        if (tileEntity instanceof TileEntityTrackSideReception) {
            TileEntityTrackSideReception reception = (TileEntityTrackSideReception) tileEntity;

            ItemStack stack = player.getHeldItemMainhand();
            if (!stack.isEmpty()) {
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
                            return pos.getX();
                        }

                        @Override
                        public double getY() {
                            return pos.getY();
                        }

                        @Override
                        public double getZ() {
                            return pos.getZ();
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

    @Override
    public void update() {
        tick(world, pos);
    }

    public void tick(World world, BlockPos pos) {
        if (world.isRemote) return;
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity == null) return;
        if (tileEntity instanceof TileEntityTrackSideReception) {
            TileEntityTrackSideReception reception = (TileEntityTrackSideReception) tileEntity;

            EntityMinecart cart = ITrackSide.getMinecart(reception, reception.direction);

            core(cart, reception);

            if (reception.hasChanged()) {
                reception.updateChanged();
                reception.refresh();
            }
        }

    }

    public abstract void spawn(World world, double x, double y, double z);

    public void spawn(World world, BlockPos pos) {
        spawn(world, pos.getX() + 0.5, pos.getY() + 0.0625, pos.getZ() + 0.5);
    }

    public static void setCartDefaultPosition(EntityMinecart cart, BlockPos pos) {
        cart.setVelocity(0, 0, 0);
        cart.motionX = cart.motionZ = 0.0D;
        cart.setPosition(pos.getX() + 0.5, pos.getY() + 0.0625, pos.getZ() + 0.5);
    }

    enum EnumSound {
        PAUSE,
        JETTY,
        READY,
        DELAY
    }

    public static void playSoundOnCart(EntityMinecart cart, EnumSound sound) {
        switch (sound) {
            case PAUSE:
                SoundUtil.instance().playSound(cart, SoundUtil.instance().RECEPTION_PAUSE);
                break;
            case JETTY:
                SoundUtil.instance().playSound(cart, SoundUtil.instance().RECEPTION_JETTY);
                break;
            case DELAY:
                SoundUtil.instance().playSound(cart, SoundUtil.instance().RECEPTION_DELAY);
                break;
            case READY:
                SoundUtil.instance().playSound(cart, SoundUtil.instance().RECEPTION_READY);
                break;
        }
    }

    protected void spawn() {
        EnumFacing railOffset = ITrackSide.getRailOffset(direction);
        BlockPos offset = new BlockPos(
                railOffset.getFrontOffsetX(),
                0,
                railOffset.getFrontOffsetZ()
        );
        BlockPos pos = getPos().add(offset);
        if (world.getBlockState(pos.up()).getBlock() instanceof BlockRailBase)
            spawn(world, pos.up());
        else if (world.getBlockState(pos).getBlock() instanceof BlockRailBase)
            spawn(world, pos);
    }

    protected void core(EntityMinecart cart, TileEntityTrackSideReception reception) {
        if (reception == null) return;

        boolean hasCart = (cart != null);
        World world = reception.world;
        EnumFacing railOffset = ITrackSide.getRailOffset(reception.direction);
        BlockPos offset = new BlockPos(
                railOffset.getFrontOffsetX(),
                0,
                railOffset.getFrontOffsetZ()
        );
        BlockPos pos = reception.getPos().add(offset);
        if (world.getBlockState(pos.up()).getBlock() instanceof BlockRailBase)
            pos = pos.up();

        if (reception.cartType.equals("loco")) {
            if (!hasCart) {
                reception.reset();
            } else {
                if (reception.getTarget() != null) {
                    reception.controlTarget(reception.doorCtrl);
                }

                LinkedList<EntityPlayer> players = new LinkedList<>();
                for (Entity entity : cart.getPassengers())
                    if (entity instanceof EntityPlayer)
                        players.add((EntityPlayer) entity);

                if (cart instanceof AbsLocoBase)
                    loco((AbsLocoBase) cart, reception, players);
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

                if (cart.getPassengers().isEmpty()) {
                    if (cart instanceof AbsMotoCart)
                        ((AbsMotoCart) cart).Velocity = 0.0D;
                    setCartDefaultPosition(cart, pos);

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

                if (!cart.getPassengers().isEmpty()) {
                    LinkedList<EntityPlayer> players = new LinkedList<>();
                    for (Entity entity : cart.getPassengers())
                        if (entity instanceof EntityPlayer)
                            players.add((EntityPlayer) entity);
                    cart(cart, reception, players);
                }
            }

            tri(cart, reception);
        }
    }

    protected void loco(AbsLocoBase loco, TileEntityTrackSideReception reception, LinkedList<EntityPlayer> players) {
        if (reception == null) return; if (loco == null) return;

        double maxV = 0.2;
        World world = reception.world;
        EnumFacing railOffset = ITrackSide.getRailOffset(reception.direction);
        BlockPos offset = new BlockPos(
                railOffset.getFrontOffsetX(),
                0,
                railOffset.getFrontOffsetZ()
        );
        BlockPos pos = reception.getPos().add(offset);
        if (world.getBlockState(pos.up()).getBlock() instanceof BlockRailBase)
            pos = pos.up();

        if (loco.Velocity > 0 && !reception.enable) {
            if (loco.Velocity > maxV) {
                // speed down
                loco.setEnginePower(0); loco.setEngineBrake(1);
            } else {
                // stop
                loco.setEnginePower(0);
                loco.setEngineBrake(1);
                reception.doorCtrl = true;

                reception.enable = true;
                setCartDefaultPosition(loco, pos);
                for (EntityPlayer player : players)
                    Util.say(player, "info.reception.pause", reception.setDelay);
                if (reception.setDelay == 10) playSoundOnCart(loco, EnumSound.PAUSE);
                else if (reception.setDelay == 20) playSoundOnCart(loco, EnumSound.JETTY);
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
                        for (EntityPlayer player : players)
                            Util.say(player, "info.reception.delay");
                        playSoundOnCart(loco, EnumSound.DELAY);
                    }
                }

                if (reception.delay == reception.setDelay * 15) {
                    reception.count = 0;
                    reception.doorCtrl = false;
                    for (EntityPlayer player : players)
                        Util.say(player, "info.reception.ready", reception.setDelay);
                    playSoundOnCart(loco, EnumSound.READY);
                }

                if (loco.Velocity > maxV) {
                    // keep speed down
                    loco.setEnginePower(0); loco.setEngineBrake(1);
                } else {
                    // keep stop
                    loco.setEnginePower(0); loco.setEngineBrake(1);
                    setCartDefaultPosition(loco, pos);
                }
            } else {
                // start, dir = neg, -x | +z
                int dir = 0;
                if (reception.direction.getFrontOffsetZ() != 0)
                    dir = (int) Math.signum(Math.sin(TrainController.calcYaw(loco) * Math.PI / 180.0));
                else if (reception.direction.getFrontOffsetX() != 0)
                    dir = (int) Math.signum(Math.cos(TrainController.calcYaw(loco) * Math.PI / 180.0));
                if ((reception.direction == EnumFacing.SOUTH || reception.direction == EnumFacing.WEST))
                    dir = -dir;
                if (reception.isInvert()) dir = -dir;
                loco.setEngineDir(dir);
                loco.setEnginePower(1); loco.setEngineBrake(10);
            }
        }
    }

    protected double getVelSq(EntityMinecart cart) {
        return cart.motionX * cart.motionX + cart.motionZ * cart.motionZ;
    }

    protected void cart(EntityMinecart cart, TileEntityTrackSideReception reception, LinkedList<EntityPlayer> players) {
        if (reception == null) return; if (cart == null) return;

        World world = reception.world;
        EnumFacing railOffset = ITrackSide.getRailOffset(reception.direction);
        BlockPos offset = new BlockPos(
                railOffset.getFrontOffsetX(),
                0,
                railOffset.getFrontOffsetZ()
        );
        BlockPos pos = reception.getPos().add(offset);
        if (world.getBlockState(pos.up()).getBlock() instanceof BlockRailBase)
            pos = pos.up();

        double maxV = 0.2;
        if (reception.cartType.equals("loco")) {
            return;
        }

        if (reception.cartType.isEmpty() && (getVelSq(cart) == 0))
            registerCart(reception, cart);

        if ((getVelSq(cart) > 0) && !reception.enable) {
            if ((Math.abs(cart.motionX) > maxV / 2) || (Math.abs(cart.motionZ) > maxV / 2)) {
                cart.motionX = (Math.signum(cart.motionX) * Dynamics.LocoMotions.calcVelocityDown(Math.abs(cart.motionX), 0.1D, 1.0D, 1.0D, 1.0D, 0.05D, 0.02D));
                cart.motionZ = (Math.signum(cart.motionZ) * Dynamics.LocoMotions.calcVelocityDown(Math.abs(cart.motionZ), 0.1D, 1.0D, 1.0D, 1.0D, 0.05D, 0.02D));
            } else {
                reception.enable = true;
                reception.doorCtrl = true;

                if (cart instanceof AbsMotoCart)
                    ((AbsMotoCart) cart).Velocity = 0.0D;
                setCartDefaultPosition(cart, pos);
                for (EntityPlayer player : players)
                    Util.say(player, "info.reception.pause", reception.setDelay);
                if (reception.setDelay == 10) playSoundOnCart(cart, EnumSound.PAUSE);
                else if (reception.setDelay == 20) playSoundOnCart(cart, EnumSound.JETTY);
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
                        for (EntityPlayer player : players)
                            Util.say(player, "info.reception.delay");
                        playSoundOnCart(cart, EnumSound.DELAY);
                    }
                }

                if (reception.delay == reception.setDelay * 15) {
                    reception.count = 0;
                    reception.doorCtrl = false;
                    for (EntityPlayer player : players)
                        Util.say(player, "info.reception.ready", reception.setDelay);
                    playSoundOnCart(cart, EnumSound.READY);
                }

                setCartDefaultPosition(cart, pos);
            } else {
                double dir = (reception.direction == EnumFacing.NORTH || reception.direction == EnumFacing.EAST) ? 1 : -1;
                if (reception.isInvert()) dir = -dir;
                if (reception.direction.getFrontOffsetZ() != 0) {
                    if (cart.motionZ * dir >= 0.0D) cart.motionZ = -dir * 0.005D;
                    if (Math.abs(cart.motionZ) < maxV)
                        cart.motionZ = -dir * Dynamics.LocoMotions.calcVelocityUp(Math.abs(cart.motionZ), 0.1D, 1.0D, 0.1D, 0.02D);
                } else if (reception.direction.getFrontOffsetX() != 0) {
                    if (cart.motionX * dir <= 0.0D) cart.motionX = dir * 0.005D;
                    if (Math.abs(cart.motionX) < maxV)
                        cart.motionX = dir * Dynamics.LocoMotions.calcVelocityUp(Math.abs(cart.motionX), 0.1D, 1.0D, 0.1D, 0.02D);
                }
            }
        }
    }

    protected void tri(EntityMinecart cart, TileEntityTrackSideReception reception) {
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
