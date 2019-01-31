package club.nsdn.nyasamaoptics.tileblock.light;

import club.nsdn.nyasamaoptics.NyaSamaOptics;
import club.nsdn.nyasamaoptics.block.BlockLoader;
import club.nsdn.nyasamaoptics.creativetab.CreativeTabLoader;
import club.nsdn.nyasamaoptics.util.RGBLightCore;
import club.nsdn.nyasamatelecom.api.device.DeviceBase;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityReceiver;
import club.nsdn.nyasamatelecom.api.util.NSASM;
import club.nsdn.nyasamatelecom.api.util.Util;
import cn.ac.nya.forgeobj.WavefrontObject;
import net.minecraft.block.Block;
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
public class RGBLight extends DeviceBase {

    public static class TileEntityRGBLight extends TileEntityReceiver {

        public int color;
        public boolean isEnabled;
        public boolean prevIsEnabled;

        public TileEntityRGBLight() {
            super();
            color = 0xFFFFFF;
            setInfo(12, 1, 1, 1);
        }

        public TileEntityRGBLight(double x, double y, double z) {
            super();
            color = 0xFFFFFF;
            setInfo(12, x, y, z);
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
            tagCompound.setInteger("color", color);
            tagCompound.setBoolean("isEnabled", isEnabled);
            return super.toNBT(tagCompound);
        }

        @Override
        public void fromNBT(NBTTagCompound tagCompound) {
            super.fromNBT(tagCompound);
            color = tagCompound.getInteger("color");
            isEnabled = tagCompound.getBoolean("isEnabled");
        }

        public EnumFacing getDirFromMeta(int meta) {
            if (meta < 4) return EnumFacing.UP;
            else if (meta > 7) return EnumFacing.DOWN;
            else {
                switch (meta) {
                    case 4: return EnumFacing.NORTH;
                    case 5: return EnumFacing.EAST;
                    case 6: return EnumFacing.SOUTH;
                    case 7: return EnumFacing.WEST;
                }
            }
            return EnumFacing.DOWN;
        }

        @Override
        public void updateSignal(World world, BlockPos pos) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity == null) return;
            if (tileEntity instanceof TileEntityRGBLight) {
                TileEntityRGBLight light = (TileEntityRGBLight) tileEntity;

                if (light.getSender() != null) {
                    light.isEnabled = light.senderIsPowered();
                } else {
                    light.isEnabled = true;
                }

                int meta = light.META;
                Block block = world.getBlockState(pos).getBlock();
                if (block instanceof RGBLight) {
                    if (((RGBLight) block).isFloodLight)
                        BlockLoader.lineLight.lightCtl(world, pos, getDirFromMeta(meta), 16, light.isEnabled);
                    else
                        BlockLoader.light.lightCtl(world, pos, light.isEnabled);
                }

                if (light.isEnabled != light.prevIsEnabled) {
                    light.prevIsEnabled = light.isEnabled;
                    light.refresh();
                }
            }
        }
    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityRGBLight(x, y, z);
    }

    public boolean isFloodLight;
    public float x, y, z;
    public String resource;
    @SideOnly(Side.CLIENT)
    public WavefrontObject modelShell;
    @SideOnly(Side.CLIENT)
    public WavefrontObject modelLight;

    public RGBLight(String name, String id, float x, float y, float z) {
        super(Material.GLASS, name);
        setRegistryName(NyaSamaOptics.MODID, id);
        setLightLevel(0);
        setCreativeTab(CreativeTabLoader.tabNyaSamaOptics);
        this.isFloodLight = false;
        this.x = x; this.y = y; this.z = z;
        this.resource = id;
    }

    public RGBLight(String name, String id, float x, float y, float z, boolean isFloodLight) {
        super(Material.GLASS, name);
        setRegistryName(NyaSamaOptics.MODID, id);
        setLightLevel(0);
        setCreativeTab(CreativeTabLoader.tabNyaSamaOptics);
        this.isFloodLight = isFloodLight;
        this.x = x; this.y = y; this.z = z;
        this.resource = id;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)  {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityRGBLight) {
            TileEntityRGBLight light = (TileEntityRGBLight) tileEntity;
            if (!world.isRemote) {
                ItemStack stack = player.getHeldItemMainhand();
                if (!stack.isEmpty()) {

                    NBTTagList list = Util.getTagListFromNGT(stack);
                    if (list == null) return false;
                    String[][] code = NSASM.getCode(list);
                    new RGBLightCore(code) {
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
                        public TileEntityRGBLight getLight() {
                            return light;
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
