package club.nsdn.nyasamarailway.tileblock.func;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.item.misc.ItemTicketBase;
import club.nsdn.nyasamarailway.item.misc.ItemTicketOnce;
import club.nsdn.nyasamarailway.item.tool.Item1N4148;
import club.nsdn.nyasamarailway.tileblock.TileBlock;
import club.nsdn.nyasamarailway.util.SoundUtil;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityReceiver;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by drzzm32 on 2019.12.24
 */
public class GateTiny extends TileBlock {

    public static class TileEntityGateTiny extends TileEntityReceiver {

        public static final int RST_DELAY = 5;

        public EnumFacing direction;
        public boolean isEnabled = true;

        public boolean doorState = false;

        public int over = -1;
        public int setOver = 1;

        private String nowName = "";
        private int rstCounter = RST_DELAY;

        public TileEntityGateTiny() {
            super();
            setInfo(4, 1, 1.5, 0.5);
        }

        @Override
        protected void updateBounds() {
            setBoundsByXYZ(
                    0.5 - this.SIZE.x / 2, 0, 0.25 - this.SIZE.z / 2,
                    0.5 + this.SIZE.x / 2, this.SIZE.y, 0.25 + this.SIZE.z / 2
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

            direction = EnumFacing.byName(tagCompound.getString("direction"));
            isEnabled = tagCompound.getBoolean("isEnabled");



            over = tagCompound.getInteger("over");
            setOver = tagCompound.getInteger("setOver");
        }

        @Override
        public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
            if (direction == null) direction = EnumFacing.DOWN;
            tagCompound.setString("direction", direction.getName());
            tagCompound.setBoolean("isEnabled", isEnabled);



            tagCompound.setInteger("over", over);
            tagCompound.setInteger("setOver", setOver);

            return super.toNBT(tagCompound);
        }

        @Override
        public void updateSignal(World world, BlockPos pos) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof TileEntityGateTiny) {
                TileEntityGateTiny gate = (TileEntityGateTiny) tileEntity;

                boolean isEnable;

                if (gate.getSender() == null) {
                    isEnable = true;
                } else {
                    isEnable = gate.senderIsPowered();
                }

                if (isEnable != gate.isEnabled) {
                    gate.isEnabled = isEnable;
                    gate.refresh();
                }

                doSniff(world, pos, gate);

                if (gate.rstCounter < RST_DELAY * 20)
                    gate.rstCounter += 1;
                else if (gate.rstCounter == RST_DELAY * 20) {
                    gate.rstCounter += 1;
                    gate.reset();
                }
            }
        }

        public void doSniff(World world, BlockPos pos, TileEntityGateTiny gate) {
            EntityPlayer player;

            if (gate.direction == null)
                gate.direction = EnumFacing.DOWN;
            EnumFacing dir = gate.direction;

            BlockPos vec = new BlockPos(dir.getDirectionVec());
            if (gate.isEnabled) {
                player = findPlayer(world, pos.add(vec));
                if (player != null) {
                    gate.nowName = player.getDisplayNameString();
                }
                player = findPlayer(world, pos.add(vec.rotate(Rotation.CLOCKWISE_180)));
                if (player != null) {
                    if (gate.nowName.equals(player.getDisplayNameString()))
                        gate.openDoor();
                }
            }
        }

        public void reset() {
            doorState = false;
            nowName = "";
            over = -1;
            refresh();
        }

        public void openDoor() {
            doorState = true;
            rstCounter = 0;
        }

        public void closeDoor() {
            doorState = false;
            rstCounter = 0;
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

    }

    public GateTiny() {
        super("GateTiny");
        setRegistryName(NyaSamaRailway.MODID, "gate_tiny");
        setLightOpacity(0);
        setLightLevel(0.1F);
    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityGateTiny();
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
        if (tileEntity instanceof TileEntityGateTiny)
            ((TileEntityGateTiny) tileEntity).direction = getDirFromMeta(val).getOpposite();
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
        if (tileEntity instanceof TileEntityGateTiny) {
            TileEntityGateTiny gate = (TileEntityGateTiny) tileEntity;

            ItemStack stack = player.getHeldItemMainhand();
            if (!stack.isEmpty() && gate.isEnabled) {
                Item item = stack.getItem();

                if (item instanceof Item1N4148) {
                    if (!world.isRemote) {
                        if (gate.nowName.equals(player.getDisplayNameString())) {
                            gate.openDoor();
                            gate.over = -2;
                            SoundUtil.instance().playSoundTop(world, pos, SoundUtil.instance().GATE_BEEP);
                            gate.refresh();
                        }
                    }
                    return true;
                } else if (item instanceof ItemTicketBase) {
                    if (!world.isRemote) {

                        if (gate.nowName.equals(player.getDisplayNameString())) {
                            if (item instanceof ItemTicketOnce) {
                                if (gate.setOver != ItemTicketBase.getOver(stack)) {
                                    gate.closeDoor();
                                    gate.over = -1;
                                    return true;
                                }
                            }

                            if (ItemTicketBase.getOver(stack) > 0) {
                                if (ItemTicketBase.getState(stack)) {
                                    ItemTicketBase.decOver(stack);
                                    if (stack.getItem() instanceof ItemTicketOnce) {
                                        ItemTicketBase.setOver(stack, 0);
                                        player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
                                    }
                                    gate.openDoor();
                                    gate.over = ItemTicketBase.getOver(stack);
                                    ItemTicketBase.setState(stack, false);
                                } else {
                                    gate.openDoor();
                                    gate.over = ItemTicketBase.getOver(stack);
                                    ItemTicketBase.setState(stack, true);
                                }
                                SoundUtil.instance().playSoundTop(world, pos, SoundUtil.instance().GATE_BEEP);
                            }

                            gate.refresh();
                        }
                    }
                    return true;
                }
            }
        }

        return false;
    }

}
