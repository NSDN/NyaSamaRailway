package club.nsdn.nyasamaoptics.tileblock.screen;

import club.nsdn.nyasamaoptics.NyaSamaOptics;
import club.nsdn.nyasamaoptics.block.BlockLoader;
import club.nsdn.nyasamaoptics.creativetab.CreativeTabLoader;
import club.nsdn.nyasamaoptics.util.LEDPlateCore;
import club.nsdn.nyasamatelecom.api.device.DeviceBase;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityReceiver;
import club.nsdn.nyasamatelecom.api.util.NSASM;
import club.nsdn.nyasamatelecom.api.util.Util;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.1.30.
 */
public class LEDPlate extends DeviceBase {

    public static final int ALIGN_CENTER = 0, ALIGN_LEFT = 1, ALIGN_RIGHT = 2;

    public static class TileEntityLEDPlate extends TileEntityReceiver {

        public String content;
        public int color;
        public int back;
        public double scale;
        public int align;

        public boolean isEnabled;
        public boolean prevIsEnabled;

        public TileEntityLEDPlate() {
            super();
            content = "DUMMY";
            color = 0xEE1111;
            back = 0x000000;
            scale = 1.0;
            align = ALIGN_CENTER;
            setInfo(12, 1, 0.125, 1);
        }

        @Override
        public boolean shouldRenderInPass(int pass) {
            return true;
        }

        @Override
        @SideOnly(Side.CLIENT)
        @Nonnull
        public AxisAlignedBB getRenderBoundingBox()
        {
            return INFINITE_EXTENT_AABB;
        }

        @Override
        public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
            tagCompound.setString("content", content);
            tagCompound.setInteger("color", color);
            tagCompound.setInteger("back", back);
            tagCompound.setDouble("scale", scale);
            tagCompound.setInteger("align", align);
            tagCompound.setBoolean("isEnabled", isEnabled);
            return super.toNBT(tagCompound);
        }

        @Override
        public void fromNBT(NBTTagCompound tagCompound) {
            super.fromNBT(tagCompound);
            content = tagCompound.getString("content");
            color = tagCompound.getInteger("color");
            back = tagCompound.getInteger("back");
            scale = tagCompound.getDouble("scale");
            align = tagCompound.getInteger("align");
            isEnabled = tagCompound.getBoolean("isEnabled");
        }

        public static void updateThis(TileEntityLEDPlate tileEntityLEDPlate) {
            tileEntityLEDPlate.refresh();
        }

        @Override
        public void updateSignal(World world, BlockPos pos) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity == null) return;
            if (tileEntity instanceof TileEntityLEDPlate) {
                TileEntityLEDPlate light = (TileEntityLEDPlate) tileEntity;

                if (light.getSender() != null) {
                    light.isEnabled = light.senderIsPowered();
                } else {
                    light.isEnabled = true;
                }

                BlockLoader.light.lightCtl(world, pos, light.isEnabled);

                if (light.isEnabled != light.prevIsEnabled) {
                    light.prevIsEnabled = light.isEnabled;
                    light.refresh();
                }
            }
        }

    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityLEDPlate();
    }

    public LEDPlate() {
        super(Material.GLASS, "LEDPlate");
        setRegistryName(NyaSamaOptics.MODID, "led_plate");
        setLightLevel(0);
        setCreativeTab(CreativeTabLoader.tabNyaSamaOptics);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)  {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityLEDPlate) {
            TileEntityLEDPlate text = (TileEntityLEDPlate) tileEntity;
            if (!world.isRemote) {
                ItemStack stack = player.getHeldItemMainhand();
                if (!stack.isEmpty()) {

                    NBTTagList list = Util.getTagListFromNGT(stack);
                    if (list == null) return false;
                    String[][] code = NSASM.getCode(list);
                    new LEDPlateCore(code) {
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
                        public TileEntityLEDPlate getTile() {
                            return text;
                        }
                    }.run();

                }
            }
            return true;
        }

        return false;
    }

    @Override
    public void breakBlock(World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity != null) {
            if (tileEntity instanceof TileEntityReceiver) {
                ((TileEntityReceiver) tileEntity).onDestroy();
            }
        }
        super.breakBlock(world, pos, state);
    }

}
