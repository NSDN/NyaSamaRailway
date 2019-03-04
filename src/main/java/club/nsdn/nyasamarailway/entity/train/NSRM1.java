package club.nsdn.nyasamarailway.entity.train;

import club.nsdn.nyasamarailway.api.cart.AbsTrainBase;
import club.nsdn.nyasamarailway.api.cart.CartUtil;
import club.nsdn.nyasamarailway.api.signal.TileEntityGlassShield;
import club.nsdn.nyasamarailway.block.BlockPlatform;
import club.nsdn.nyasamarailway.item.tool.Item1N4148;
import club.nsdn.nyasamarailway.item.tool.Item74HC04;
import club.nsdn.nyasamarailway.item.tool.ItemNTP32Bit;
import club.nsdn.nyasamarailway.item.tool.ItemNTP8Bit;
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

/**
 * Created by drzzm32 on 2019.2.27
 */
public class NSRM1 extends AbsTrainBase {

    private static final DataParameter<Boolean> DOOR_STATE_L = EntityDataManager.createKey(NSRM1.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> DOOR_STATE_R = EntityDataManager.createKey(NSRM1.class, DataSerializers.BOOLEAN);

    private boolean prevDoorStateLeft = false, prevDoorStateRight = false;
    public int doorProgressLeft = 0, doorProgressRight = 0;

    public final int STEP = 4;

    public NSRM1(World world) {
        super(world);
    }

    public NSRM1(World world, double x, double y, double z) {
        super(world, x, y, z);
    }


    @Override
    protected void entityInit() {
        super.entityInit();

        dataManager.register(DOOR_STATE_L, false);
        dataManager.register(DOOR_STATE_R, false);
    }

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
    protected void readEntityFromNBT(@Nonnull NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);

        setDoorStateLeft(tagCompound.getBoolean("doorStateLeft"));
        setDoorStateRight(tagCompound.getBoolean("doorStateRight"));
    }

    @Override
    protected void writeEntityToNBT(@Nonnull NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);

        tagCompound.setBoolean("doorStateLeft", getDoorStateLeft());
        tagCompound.setBoolean("doorStateRight", getDoorStateRight());
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }

    @Override
    public double getMountedYOffset() {
        return 0.25;
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

    @Override // Called by rider
    public void updatePassenger(@Nonnull Entity entity) {
        CartUtil.updatePassenger12(this, entity);
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
                if (stack.getItem() instanceof ItemNTP8Bit || stack.getItem() instanceof ItemNTP32Bit) {
                    return true;
                }
            }
            if (!this.world.isRemote) {
                player.startRiding(this);
            }

            return true;
        }
    }

    public boolean getStateLeft(boolean invert) {
        if (invert)
            return getDoorStateRight();
        return getDoorStateLeft();
    }

    public boolean getStateRight(boolean invert) {
        if (invert)
            return getDoorStateLeft();
        return getDoorStateRight();
    }

    @Override
    protected void removePassenger(Entity entity) {
        BlockPos pos = this.getPosition();
        EnumFacing facing = EnumFacing.fromAngle(180 - this.rotationYaw).rotateYCCW(); // Engine is the front
        boolean invert = facing.getAxis() == EnumFacing.Axis.X;
        if (getStateLeft(invert))
            pos = pos.offset(facing.rotateYCCW(), 2);
        else if (getStateRight(invert))
            pos = pos.offset(facing.rotateY(), 2);
        else {
            if (world.getBlockState(pos.down().offset(facing.rotateYCCW())).getBlock() instanceof BlockPlatform)
                pos = pos.offset(facing.rotateYCCW(), 2);
            else if (world.getBlockState(pos.down().offset(facing.rotateY())).getBlock() instanceof BlockPlatform)
                pos = pos.offset(facing.rotateY(), 2);
            else {
                super.removePassenger(entity);
                return;
            }
        }

        super.removePassenger(entity);
        entity.setPositionAndUpdate(
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5
        );
    }

    @Override
    protected boolean canFitPassenger(Entity entity) {
        boolean res = super.canFitPassenger(entity);
        BlockPos pos = this.getPosition();
        EnumFacing facing = EnumFacing.fromAngle(180 - this.rotationYaw).rotateYCCW(); // Engine is the front

        if (getDoorStateLeft() || getDoorStateRight()) {
            boolean invert = facing.getAxis() == EnumFacing.Axis.X;
            if (getStateLeft(invert))
                pos = pos.offset(facing.rotateYCCW());
            else if (getStateRight(invert))
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

    public TileEntityGlassShield getShield(BlockPos pos, EnumFacing facing) {
        TileEntity te = world.getTileEntity(pos.offset(facing));
        if (te instanceof TileEntityGlassShield)
            return (TileEntityGlassShield) te;
        return null;
    }

    public void setDoorState(boolean invert, boolean value) {
        if (invert) setDoorStateRight(value);
        else setDoorStateLeft(value);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (!world.isRemote) {
            EnumFacing facing = EnumFacing.fromAngle(180 - this.rotationYaw).rotateYCCW(); // Engine is the front
            boolean invert = facing.getAxis() == EnumFacing.Axis.X;
            BlockPos pos = getPosition();
            TileEntityGlassShield shield = getShield(pos.offset(facing.rotateYCCW()), facing);
            if (shield != null) {
                if (shield.state == TileEntityGlassShield.STATE_OPENING)
                    setDoorState(invert, true);
                else if (shield.state == TileEntityGlassShield.STATE_CLOSING)
                    setDoorState(invert, false);
            }

            invert = !invert;
            shield = getShield(pos.offset(facing.rotateY()), facing);
            if (shield != null) {
                if (shield.state == TileEntityGlassShield.STATE_OPENING)
                    setDoorState(invert, true);
                else if (shield.state == TileEntityGlassShield.STATE_CLOSING)
                    setDoorState(invert, false);
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
