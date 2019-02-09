package club.nsdn.nyasamarailway.tileblock.signal.deco;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.item.tool.Item1N4148;
import club.nsdn.nyasamarailway.item.misc.ItemTicketBase;
import club.nsdn.nyasamarailway.item.misc.ItemTicketOnce;
import club.nsdn.nyasamarailway.tileblock.TileBlock;
import club.nsdn.nyasamarailway.tileblock.func.GateBase;
import club.nsdn.nyasamarailway.tileblock.func.GateDoor;
import club.nsdn.nyasamarailway.util.SoundUtil;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityReceiver;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by drzzm32 on 2017.9.4.
 */
public class GateFront extends TileBlock {

    public static class TileEntityGateFront extends TileEntityReceiver {

        public static final int DELAY = 5;
        public int delay;

        public int over = -1;
        public boolean isEnabled = true;
        public EnumFacing direction;

        public int setOver = 1;

        public TileEntityGateFront() {
            setInfo(4, 0.5, 1.5, 0.5);
        }

        @Override
        protected void updateBounds() {
            setBoundsByXYZ(
                    0.5 - this.SIZE.x / 2, 0, 0.75 - this.SIZE.z / 2,
                    0.5 + this.SIZE.x / 2, this.SIZE.y, 0.75 + this.SIZE.z / 2
            );
        }

        @Nonnull
        @Override
        public AxisAlignedBB getRenderBoundingBox() {
            return super.getRenderBoundingBox().expand(4, 4, 4);
        }

        @Override
        public void fromNBT(NBTTagCompound tagCompound) {
            super.fromNBT(tagCompound);

            over = tagCompound.getInteger("over");
            isEnabled = tagCompound.getBoolean("isEnabled");
            direction = EnumFacing.byName(
                    tagCompound.getString("direction")
            );
            setOver = tagCompound.getInteger("setOver");
        }

        @Override
        public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
            tagCompound.setInteger("over", over);
            tagCompound.setBoolean("isEnabled", isEnabled);
            if (direction == null) direction = EnumFacing.DOWN;
            tagCompound.setString("direction", direction.getName());
            tagCompound.setInteger("setOver", setOver);

            return super.toNBT(tagCompound);
        }

        @Override
        public void updateSignal(World world, BlockPos pos) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof TileEntityGateFront) {
                TileEntityGateFront gateFront = (TileEntityGateFront) tileEntity;

                boolean isEnable;

                if (gateFront.getSender() == null) {
                    isEnable = true;
                } else {
                    isEnable = gateFront.senderIsPowered();
                }

                if (isEnable != gateFront.isEnabled) {
                    gateFront.isEnabled = isEnable;
                    gateFront.refresh();
                }

                doSniff(world, pos, gateFront);
            }
        }

        public void doSniff(World world, BlockPos pos, TileEntityGateFront gateFront) {
            EntityPlayer player;

            if (gateFront.direction == null)
                gateFront.direction = EnumFacing.DOWN;
            EnumFacing dir = gateFront.direction;

            BlockPos vec = new BlockPos(dir.getDirectionVec());
            if (gateFront.isEnabled) {
                player = findPlayer(world, pos.add(vec.rotate(Rotation.COUNTERCLOCKWISE_90)));
                if (player != null) {
                    GateBase.TileEntityGateBase gateBase = getGateBase(world, pos.offset(dir));
                    if (gateBase != null) {
                        gateBase.player = player.getDisplayNameString();
                    }
                }
            } else {
                player = findPlayer(world, pos.add(vec.rotate(Rotation.CLOCKWISE_90)));
                GateBase.TileEntityGateBase gateBase = getGateBase(world, pos.offset(dir));
                if (gateBase != null) {
                    boolean delayed = false, playerOK = false;
                    if (player != null) {
                        playerOK = gateBase.player.equals(player.getDisplayNameString());
                    }
                    if (gateBase.getDoorState() == GateDoor.TileEntityGateDoor.STATE_OPEN) {
                        gateFront.delay += 1;
                        if (gateFront.delay > TileEntityGateFront.DELAY * 20) {
                            delayed = true;
                        }
                    } else {
                        gateFront.delay = 0;
                    }

                    if (delayed || playerOK) {
                        gateBase.player = "";
                        gateBase.closeDoor();
                        GateFront.TileEntityGateFront gateFront1 = getGateFront(world, pos.offset(dir, 2));
                        if (gateFront1 != null) {
                            gateFront1.over = -1;
                            gateFront1.refresh();
                        }
                    }
                }
            }
        }

        public EntityPlayer findPlayer(World world, BlockPos pos) {
            float bBoxSize = 0.125F;
            int x = pos.getX(), y = pos.getY(), z = pos.getZ();
            y += 1; //player's bounding box is higher than cart
            List bBox = world.getEntitiesWithinAABB(
                    EntityPlayer.class,
                    new AxisAlignedBB((double) ((float) x + bBoxSize),
                            (double) y,
                            (double) ((float) z + bBoxSize),
                            (double) ((float) (x + 1) - bBoxSize),
                            (double) ((float) (y + 1) - bBoxSize),
                            (double) ((float) (z + 1) - bBoxSize))
            );
            if (!bBox.isEmpty()) {
                if (bBox.size() > 1) return null;
                return (EntityPlayer) bBox.get(0);
            }
            return null;
        }

        public static GateBase.TileEntityGateBase getGateBase(World world, BlockPos pos) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof GateBase.TileEntityGateBase) {
                return (GateBase.TileEntityGateBase) tileEntity;
            }
            return null;
        }

        public static TileEntityGateFront getGateFront(World world, BlockPos pos) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof TileEntityGateFront) {
                return (TileEntityGateFront) tileEntity;
            }
            return null;
        }

    }

    public GateFront() {
        super("GateFront");
        setRegistryName(NyaSamaRailway.MODID, "gate_front");
        setLightOpacity(0);
        setLightLevel(0.1F);
    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityGateFront();
    }

    @Override
    public boolean isSideSolid(IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, EnumFacing facing) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity == null) return false;
        if (tileEntity instanceof TileEntityBase) {
            int meta = ((TileEntityBase) tileEntity).META;
            EnumFacing dir = getDirFromMeta(meta);
            return dir.getOpposite() == facing;
        }
        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack itemStack) {
        int val = MathHelper.floor((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        setDeviceMeta(world, pos, val);
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityGateFront)
            ((TileEntityGateFront) tileEntity).direction = getDirFromMeta(val).getOpposite();
    }

    @Override
    @Nonnull
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, @Nonnull World world, @Nonnull BlockPos pos) {
        return super.getSelectedBoundingBox(state, world, pos).contract(0, 0.5, 0);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)  {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity == null) return false;
        if (tileEntity instanceof TileEntityGateFront) {
            TileEntityGateFront gateFront = (TileEntityGateFront) tileEntity;
            EnumFacing dir = gateFront.direction;
            GateBase.TileEntityGateBase gateBase = TileEntityGateFront.getGateBase(world, pos.offset(dir));

            ItemStack stack = player.getHeldItemMainhand();
            if (!stack.isEmpty() && gateFront.isEnabled) {
                Item item = stack.getItem();

                if (item instanceof Item1N4148) {
                    if (!world.isRemote) {
                        if (gateBase != null) {
                            if (gateBase.player.equals(player.getDisplayNameString())) {
                                gateBase.openDoor();
                                gateFront.over = -2;
                                SoundUtil.instance().playSoundTop(world, pos, SoundUtil.instance().GATE_BEEP);
                                gateFront.refresh();
                            }
                        }
                    }
                    return true;
                } else if (item instanceof ItemTicketBase) {
                    if (!world.isRemote) {

                        if (gateBase != null) {
                            if (gateBase.player.equals(player.getDisplayNameString())) {
                                if (item instanceof ItemTicketOnce) {
                                    if (gateFront.setOver != ItemTicketBase.getOver(stack))
                                        return true;
                                }

                                if (ItemTicketBase.getOver(stack) > 0) {
                                    if (ItemTicketBase.getState(stack)) {
                                        ItemTicketBase.decOver(stack);
                                        if (stack.getItem() instanceof ItemTicketOnce) {
                                            ItemTicketBase.setOver(stack, 0);
                                            player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
                                        }
                                        gateBase.openDoor();
                                        gateFront.over = ItemTicketBase.getOver(stack);
                                        ItemTicketBase.setState(stack, false);
                                    } else {
                                        gateBase.openDoor();
                                        gateFront.over = ItemTicketBase.getOver(stack);
                                        ItemTicketBase.setState(stack, true);
                                    }
                                    SoundUtil.instance().playSoundTop(world, pos, SoundUtil.instance().GATE_BEEP);
                                }

                                gateFront.refresh();
                            }
                        }
                    }
                    return true;
                }
            }
        }

        return false;
    }
}
