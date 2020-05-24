package club.nsdn.nyasamarailway.ext;

import club.nsdn.nyasamarailway.api.cart.AbsTrainBase;
import club.nsdn.nyasamarailway.api.cart.CartPart;
import club.nsdn.nyasamarailway.api.item.IController;
import club.nsdn.nyasamarailway.api.signal.TileEntityGlassShield;
import club.nsdn.nyasamarailway.block.BlockPlatform;
import club.nsdn.nyasamarailway.entity.train.NSRM1;
import club.nsdn.nyasamarailway.item.tool.Item1N4148;
import club.nsdn.nyasamarailway.item.tool.Item74HC04;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;

public class AbsMetro extends AbsTrainBase {

    public AbsMetro(World world) {
        super(world);
    }

    public AbsMetro(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(DOOR_STATE_L, false);
        dataManager.register(DOOR_STATE_R, false);

        initEntity();
    }

    @Override
    protected void readEntityFromNBT(@Nonnull NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);
        setDoorStateLeft(tagCompound.getBoolean("doorStateLeft"));
        setDoorStateRight(tagCompound.getBoolean("doorStateRight"));

        fromNBT(tagCompound);
    }

    @Override
    protected void writeEntityToNBT(@Nonnull NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setBoolean("doorStateLeft", getDoorStateLeft());
        tagCompound.setBoolean("doorStateRight", getDoorStateRight());

        toNBT(tagCompound);
    }

    @Override
    public boolean shouldRiderSit() {
        return shouldSit();
    }

    @Override
    public double getMountedYOffset() {
        return passengerYOffset();
    }

    @Override
    public void updatePassenger(@Nonnull Entity entity) {
        passengerUpdate(entity);
    }

    public void initEntity() { }
    public void fromNBT(@Nonnull NBTTagCompound tag) { }
    public void toNBT(@Nonnull NBTTagCompound tag) { }
    public boolean shouldSit() { return false; }
    public double passengerYOffset() { return 0; }
    public void passengerUpdate(@Nonnull Entity passenger) { }

    public boolean isWideDoor() { return false; }

    /* ---------------------------------------------------------------- */

    private static final DataParameter<Boolean> DOOR_STATE_L = EntityDataManager.createKey(NSRM1.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> DOOR_STATE_R = EntityDataManager.createKey(NSRM1.class, DataSerializers.BOOLEAN);

    private boolean prevDoorStateLeft = false, prevDoorStateRight = false;
    public int doorProgressLeft = 0, doorProgressRight = 0;

    public final int STEP = 4;

    public boolean getDoorStateLeft() {
        return dataManager.get(DOOR_STATE_L);
    }

    public boolean getDoorStateRight() {
        return dataManager.get(DOOR_STATE_R);
    }

    public void setDoorStateLeft(boolean value) {
        dataManager.set(DOOR_STATE_L, value);
    }

    public void setDoorStateRight(boolean value) {
        dataManager.set(DOOR_STATE_R, value);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public double getRenderFixOffset() {
        return -0.25;
    }

    @Override
    public int getMaxPassengerSize() {
        return 12;
    }

    @Override
    public boolean processInitialInteract(@Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
        if (player.isSneaking()) {
            return false;
        } else if (!canFitPassenger(player)) {
            return true;
        } else {
            ItemStack stack = player.getHeldItemMainhand();
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof Item74HC04) {
                    setDoorStateLeft(!getDoorStateLeft());
                    return true;
                }
                if (stack.getItem() instanceof Item1N4148) {
                    setDoorStateRight(!getDoorStateRight());
                    return true;
                }
                if (stack.getItem() instanceof IController)
                    return true;
            }
            if (!this.world.isRemote) {
                player.startRiding(this);
            }

            return true;
        }
    }

    public BlockPos calcPlatform(BlockPos pos) {
        EnumFacing facing = this.getHorizontalFacing(); // Engine is the front
        if (getDoorStateLeft())
            pos = pos.offset(facing.rotateYCCW(), 2);
        else if (getDoorStateRight())
            pos = pos.offset(facing.rotateY(), 2);
        else {
            if (world.getBlockState(pos.down().offset(facing.rotateYCCW())).getBlock() instanceof BlockPlatform)
                pos = pos.offset(facing.rotateYCCW(), 2);
            else if (world.getBlockState(pos.down().offset(facing.rotateY())).getBlock() instanceof BlockPlatform)
                pos = pos.offset(facing.rotateY(), 2);
            else
                return null;
        }
        return pos;
    }

    @Override
    protected void removePassenger(Entity entity) {
        BlockPos pos = entity.getPosition();

        ArrayList<Double> list = new ArrayList<>();
        LinkedHashMap<Double, BlockPos> map = new LinkedHashMap<>();
        double v = pos.distanceSq(this.getPosition());
        list.add(v); map.put(v, this.getPosition());
        for (CartPart p : getMultiPart()) {
            v = pos.distanceSq(p.getPosition());
            list.add(v); map.put(v, p.getPosition());
        }
        list.sort(Comparator.naturalOrder());
        pos = map.get(list.get(0));

        pos = calcPlatform(pos);
        if (pos == null) {
            super.removePassenger(entity);
            return;
        }

        super.removePassenger(entity);
        entity.setPositionAndUpdate(
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5
        );
    }

    public boolean canFit(BlockPos pos, Entity entity, boolean res) {
        EnumFacing facing = this.getHorizontalFacing(); // Engine is the front

        if (getDoorStateLeft() || getDoorStateRight()) {
            if (getDoorStateLeft())
                pos = pos.offset(facing.rotateYCCW());
            else if (getDoorStateRight())
                pos = pos.offset(facing.rotateY());

            Vec3i vec = entity.getPosition().subtract(pos);
            if (facing.getAxis() == EnumFacing.Axis.X) {
                return Math.abs(vec.getZ()) <= 2 && Math.abs(vec.getX()) <= 1 && res;
            } else if (facing.getAxis() == EnumFacing.Axis.Z) {
                return Math.abs(vec.getX()) <= 2 && Math.abs(vec.getZ()) <= 1 && res;
            }
        }

        pos = this.getPosition();
        if (getShield(pos.offset(facing.rotateYCCW()), facing) != null) return false;
        if (getShield(pos.offset(facing.rotateY()), facing) != null) return false;

        return res;
    }

    @Override
    protected boolean canFitPassenger(Entity entity) {
        boolean res = super.canFitPassenger(entity);

        res |= canFit(this.getPosition(), entity, res);
        for (CartPart p : getMultiPart())
            res |= canFit(p.getPosition(), entity, res);

        return res;
    }

    public TileEntityGlassShield getShield(BlockPos pos, EnumFacing facing) {
        TileEntity te = world.getTileEntity(isWideDoor() ? pos.offset(facing) : pos);
        if (te instanceof TileEntityGlassShield)
            return (TileEntityGlassShield) te;
        return null;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (!world.isRemote) {
            EnumFacing facing = getHorizontalFacing(); // Engine is the front
            BlockPos pos = getPosition();
            TileEntityGlassShield shield = getShield(pos.offset(facing.rotateYCCW()), facing);
            if (shield != null) {
                if (shield.state == TileEntityGlassShield.STATE_OPENING)
                    setDoorStateLeft(true);
                else if (shield.state == TileEntityGlassShield.STATE_CLOSING)
                    setDoorStateLeft(false);
            }

            shield = getShield(pos.offset(facing.rotateY()), facing);
            if (shield != null) {
                if (shield.state == TileEntityGlassShield.STATE_OPENING)
                    setDoorStateRight(true);
                else if (shield.state == TileEntityGlassShield.STATE_CLOSING)
                    setDoorStateRight(false);
            }
        }

        if (world.isRemote) {
            if (prevDoorStateLeft != getDoorStateLeft()) {
                if (getDoorStateLeft()) {
                    doorProgressLeft += STEP;
                    if (doorProgressLeft == 100) {
                        prevDoorStateLeft = getDoorStateLeft();
                    }
                } else {
                    doorProgressLeft -= STEP;
                    if (doorProgressLeft == 0) {
                        prevDoorStateLeft = getDoorStateLeft();
                    }
                }
            } else {
                if (prevDoorStateLeft) {
                    doorProgressLeft = doorProgressLeft < 100 ? doorProgressLeft + STEP : 100;
                } else {
                    doorProgressLeft = doorProgressLeft > 0 ? doorProgressLeft - STEP : 0;
                }
            }

            if (prevDoorStateRight != getDoorStateRight()) {
                if (getDoorStateRight()) {
                    doorProgressRight += STEP;
                    if (doorProgressRight == 100) {
                        prevDoorStateRight = getDoorStateRight();
                    }
                } else {
                    doorProgressRight -= STEP;
                    if (doorProgressRight == 0) {
                        prevDoorStateRight = getDoorStateRight();
                    }
                }
            } else {
                if (prevDoorStateRight) {
                    doorProgressRight = doorProgressRight < 100 ? doorProgressRight + STEP : 100;
                } else {
                    doorProgressRight = doorProgressRight > 0 ? doorProgressRight - STEP : 0;
                }
            }
        }
    }

}
