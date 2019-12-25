package club.nsdn.nyasamarailway.tileblock.func;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.item.ItemLoader;
import club.nsdn.nyasamarailway.item.misc.ItemNyaCoin;
import club.nsdn.nyasamarailway.item.misc.ItemTicketBase;
import club.nsdn.nyasamarailway.tileblock.TileBlock;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.12.25
 */
public class YakumoPC extends TileBlock {

    public static class TileEntityYakumoPC extends TileEntityBase {

        public boolean ledLeft = false, ledRight = false;
        public String buffer = "";
        public double offsetX = 0, offsetY = 0;

        public TileEntityYakumoPC() {
            setInfo(4, 1, 0.3125, 1);
        }

        @Nonnull
        @Override
        public AxisAlignedBB getRenderBoundingBox() {
            return super.getRenderBoundingBox().expand(4, 4, 4);
        }

        @Override
        public void fromNBT(NBTTagCompound tagCompound) {
            super.fromNBT(tagCompound);

            ledLeft = tagCompound.getBoolean("ledLeft");
            ledRight = tagCompound.getBoolean("ledRight");
            buffer = tagCompound.getString("buffer");
            offsetX = tagCompound.getDouble("offsetX");
            offsetY = tagCompound.getDouble("offsetY");
        }

        @Override
        public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
            tagCompound.setBoolean("ledLeft", ledLeft);
            tagCompound.setBoolean("ledRight", ledRight);
            tagCompound.setString("buffer", buffer);
            tagCompound.setDouble("offsetX", offsetX);
            tagCompound.setDouble("offsetY", offsetY);

            return super.toNBT(tagCompound);
        }

        public EnumFacing dirFromMeta(int meta) {
            switch (meta & 0x3) {
                case 0: return EnumFacing.NORTH;
                case 1: return EnumFacing.EAST;
                case 2: return EnumFacing.SOUTH;
                case 3: return EnumFacing.WEST;
            }
            return EnumFacing.DOWN;
        }

        private int __counter = 0, __delay = 0;
        @Override
        public void updateSignal(World world, BlockPos pos) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof TileEntityYakumoPC) {
                TileEntityYakumoPC pc = (TileEntityYakumoPC) tileEntity;
                int meta = pc.META;

                if (__counter < 20) __counter++;
                else {
                    __counter = 0;
                    pc.ledLeft = !pc.ledLeft;
                }

                if (pc.ledRight) {
                    if (__delay < 10) __delay++;
                    else {
                        __delay = 0;
                        pc.ledRight = false;
                    }
                } else __delay = 0;


                pc.refresh();
            }
        }

        public boolean busy() {
            return ledRight;
        }

        public void click() {
            ledRight = true;
        }

        public void throwItem(World world, BlockPos pos, ItemStack stack) {
            EnumFacing facing = dirFromMeta(META);
            EntityItem entityItem = new EntityItem(
                    world,
                    pos.getX() + 0.5 + facing.getFrontOffsetX(),
                    pos.getY() + 0.5 + facing.getFrontOffsetY(),
                    pos.getZ() + 0.5 + facing.getFrontOffsetZ(),
                    stack
            );
            world.spawnEntity(entityItem);
        }

    }

    public YakumoPC() {
        super("YakumoPC");
        setRegistryName(NyaSamaRailway.MODID, "yakumo_pc");
        setLightOpacity(0);
        setLightLevel(0.1F);
    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityYakumoPC();
    }

    @Override
    public boolean isSideSolid(IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, EnumFacing facing) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity == null) return false;
        if (tileEntity instanceof TileEntityBase) {
            int meta = ((TileEntityBase) tileEntity).META;
            EnumFacing dir = getDirFromMeta(meta);
            return dir != facing;
        }
        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack itemStack) {
        int val = MathHelper.floor((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        setDeviceMeta(world, pos, val);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)  {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity == null) return false;
        if (tileEntity instanceof TileEntityYakumoPC) {
            TileEntityYakumoPC pc = (TileEntityYakumoPC) tileEntity;
            ItemStack stack = player.getHeldItemMainhand();
            int meta = pc.META;

            if (!pc.busy()) {
                pc.buffer =
                        "face:" + facing.getName() + "\n" +
                        "hitX:" + String.format("%1.2f", hitX) + "\n" +
                        "hitY:" + String.format("%1.2f", hitY) + "\n" +
                        "hitZ:" + String.format("%1.2f", hitZ);

                pc.click();
            }

            return true;
        }
        return false;
    }


}
