package club.nsdn.nyasamaoptics.tileblock.screen;

import club.nsdn.nyasamaoptics.NyaSamaOptics;
import club.nsdn.nyasamaoptics.block.BlockLoader;
import club.nsdn.nyasamaoptics.creativetab.CreativeTabLoader;
import club.nsdn.nyasamaoptics.util.StationLampCore;
import club.nsdn.nyasamatelecom.api.device.DeviceBase;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityReceiver;
import club.nsdn.nyasamatelecom.api.util.NSASM;
import club.nsdn.nyasamatelecom.api.util.Util;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.1.30.
 */
public class StationLamp extends DeviceBase {

    public static final int ALIGN_CENTER = 0, ALIGN_LEFT = 1, ALIGN_RIGHT = 2;

    public static class TileEntityStationLamp extends TileEntityReceiver {

        public String content;
        public int color;
        public int back;
        public double scale;
        public String logo;

        public boolean isEnabled;
        public boolean prevIsEnabled;

        public TileEntityStationLamp() {
            super();
            content = "DUMMY";
            color = 0xEE1111;
            back = 0x000000;
            scale = 1.0;
            logo = "null";
            setInfo(4, 1, 1.375, 1);
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
            tagCompound.setString("logo", logo);
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
            logo = tagCompound.getString("logo");
            isEnabled = tagCompound.getBoolean("isEnabled");
        }

        public static void updateThis(TileEntityStationLamp tileEntityStationLamp) {
            tileEntityStationLamp.refresh();
        }

        @Override
        public void updateSignal(World world, BlockPos pos) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity == null) return;
            if (tileEntity instanceof TileEntityStationLamp) {
                TileEntityStationLamp lamp = (TileEntityStationLamp) tileEntity;

                if (lamp.getSender() != null) {
                    lamp.isEnabled = lamp.senderIsPowered();
                } else {
                    lamp.isEnabled = true;
                }

                BlockLoader.light.lightCtl(world, pos, lamp.isEnabled);

                if (lamp.isEnabled != lamp.prevIsEnabled) {
                    lamp.prevIsEnabled = lamp.isEnabled;
                    lamp.refresh();
                }
            }
        }

    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityStationLamp();
    }

    public StationLamp() {
        super(Material.GLASS, "StationLamp");
        setRegistryName(NyaSamaOptics.MODID, "station_lamp");
        setLightLevel(0);
        setCreativeTab(CreativeTabLoader.tabNyaSamaOptics);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack itemStack) {
        int val = MathHelper.floor((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        setDeviceMeta(world, pos, val);
    }

    @Override
    public boolean isSideSolid(IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, EnumFacing facing) {
        return facing == EnumFacing.DOWN;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)  {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityStationLamp) {
            TileEntityStationLamp text = (TileEntityStationLamp) tileEntity;
            if (!world.isRemote) {
                ItemStack stack = player.getHeldItemMainhand();
                if (!stack.isEmpty()) {

                    NBTTagList list = Util.getTagListFromNGT(stack);
                    if (list == null) return false;
                    String[][] code = NSASM.getCode(list);
                    new StationLampCore(code) {
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
                        public TileEntityStationLamp getTile() {
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
