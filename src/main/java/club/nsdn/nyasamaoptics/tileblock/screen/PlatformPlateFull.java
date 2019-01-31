package club.nsdn.nyasamaoptics.tileblock.screen;

import club.nsdn.nyasamaoptics.NyaSamaOptics;
import club.nsdn.nyasamaoptics.block.BlockLoader;
import club.nsdn.nyasamaoptics.creativetab.CreativeTabLoader;
import club.nsdn.nyasamaoptics.util.PlatformPlateCore;
import club.nsdn.nyasamatelecom.api.device.DeviceBase;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityReceiver;
import club.nsdn.nyasamatelecom.api.util.NSASM;
import club.nsdn.nyasamatelecom.api.util.Util;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.1.30.
 */
public class PlatformPlateFull extends DeviceBase {

    public static final int ALIGN_CENTER = 0, ALIGN_LEFT = 1, ALIGN_RIGHT = 2;

    public static class TileEntityPlatformPlateFull extends TileEntityPlatformPlate {

        public TileEntityPlatformPlateFull() {
            setInfo(12, 1, 1, 0.625);
        }

        @Override
        public void updateSignal(World world, BlockPos pos) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity == null) return;
            if (tileEntity instanceof TileEntityPlatformPlateFull) {
                TileEntityPlatformPlateFull light = (TileEntityPlatformPlateFull) tileEntity;

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
        return new TileEntityPlatformPlateFull();
    }


    public PlatformPlateFull() {
        super(Material.GLASS, "PlatformPlateFull");
        setRegistryName(NyaSamaOptics.MODID, "platform_plate_full");
        setLightLevel(0);
        setCreativeTab(CreativeTabLoader.tabNyaSamaOptics);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)  {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityPlatformPlateFull) {
            TileEntityPlatformPlateFull text = (TileEntityPlatformPlateFull) tileEntity;
            if (!world.isRemote) {
                ItemStack stack = player.getHeldItemMainhand();
                if (!stack.isEmpty()) {

                    NBTTagList list = Util.getTagListFromNGT(stack);
                    if (list == null) return false;
                    String[][] code = NSASM.getCode(list);
                    new PlatformPlateCore(code) {
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
                        public TileEntityPlatformPlate getTile() {
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
