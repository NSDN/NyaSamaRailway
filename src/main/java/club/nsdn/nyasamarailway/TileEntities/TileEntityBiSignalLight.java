package club.nsdn.nyasamarailway.TileEntities;

import club.nsdn.nyasamarailway.TileEntities.Signals.TileEntityRailPassiveReceiver;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

/**
 * Created by drzzm32 on 2017.10.5.
 */
public class TileEntityBiSignalLight extends TileEntityBase {

    private static final int LIGHT_OFF = 0;
    private static final int LIGHT_R = 1;
    private static final int LIGHT_Y = 2;
    private static final int LIGHT_G = 3;

    public static class BiSignalLight extends TileEntitySignalLight.SignalLight {
    }

    public TileEntityBiSignalLight() {
        super("BiSignalLight");
        setIconLocation("bi_signal_light");
        setLightOpacity(0);
        setLightLevel(0.75F);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new BiSignalLight();
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return side == ForgeDirection.DOWN;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack) {
        int meta = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        world.setBlockMetadataWithNotify(x, y, z, meta, 2);
    }

    @Override
    protected void setBoundsByMeta(int meta) {
        float x1 = 0.34375F, y1 = 0.0F, z1 = 0.375F, x2 = 0.65625F, y2 = 0.6875F, z2 = 0.625F;

        switch (meta & 3) {
            case 0:
                setBlockBounds(x1, y1, z1, x2, y2, z2);
                break;
            case 1:
                setBlockBounds(1.0F - z2, y1, x1, 1.0F - z1, y2, x2);
                break;
            case 2:
                setBlockBounds(1.0F - x2, y1, 1.0F - z2, 1.0F - x1, y2, 1.0F - z1);
                break;
            case 3:
                setBlockBounds(z1, y1, 1.0F - x2, z2, y2, 1.0F - x1);
                break;
        }
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        world.scheduleBlockUpdate(x, y, z, this, 1);
    }

    @Override
    public void onBlockPreDestroy(World world, int x, int y, int z, int meta) {
        super.onBlockPreDestroy(world, x, y, z, meta);
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity != null) {
            if (tileEntity instanceof TileEntityRailPassiveReceiver) {
                ((TileEntityRailPassiveReceiver) tileEntity).onDestroy();
            }
        }
    }

    @Override
    public int tickRate(World world) {
        return 10;
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (!world.isRemote) {
            updateLight(world, x, y, z);
        }
    }

    public void updateLight(World world, int x ,int y, int z) {
        if (world.getTileEntity(x, y, z) == null) return;
        if (world.getTileEntity(x, y, z) instanceof BiSignalLight) {
            BiSignalLight biSignalLight = (BiSignalLight) world.getTileEntity(x, y, z);
            boolean isEnable;
            if (biSignalLight.getSender() == null) {
                isEnable = biSignalLight.isPowered;
            } else {
                isEnable = biSignalLight.senderIsPowered();
            }
            int meta = world.getBlockMetadata(x, y, z);
            int old = meta;

            meta = meta & 0x3;
            if (biSignalLight.isBlinking) {
                if (biSignalLight.delay > 10) {
                    if (biSignalLight.delay < 20) {
                        biSignalLight.delay += 1;
                    } else {
                        biSignalLight.delay = 0;
                    }
                    meta = setLightState(isEnable, meta, biSignalLight.lightType);
                } else {
                    biSignalLight.delay += 1;
                }
            } else {
                meta = setLightState(isEnable, meta, biSignalLight.lightType);
            }

            if (old != meta || !biSignalLight.prevLightType.equals(biSignalLight.lightType)) {
                biSignalLight.prevLightType = biSignalLight.lightType;
                if (((meta >> 2) & 0x3) == 0) {
                    setLightLevel(0.0F);
                } else {
                    setLightLevel(0.75F);
                }
                world.setBlockMetadataWithNotify(x, y, z, meta, 3);
                world.markBlockForUpdate(x, y, z);
            }

            world.scheduleBlockUpdate(x, y, z, this, 1);
        }
    }

    public int setLightState(boolean isEnable, int meta, String type) {
        String lightType = type.toLowerCase();
        if (lightType.equals("red&off"))
            return isEnable ? meta | (LIGHT_R << 2) : meta;
        else if (lightType.equals("yellow&off"))
            return isEnable ? meta | (LIGHT_Y << 2) : meta;
        else if (lightType.equals("green&off"))
            return isEnable ? meta | (LIGHT_G << 2) : meta;
        else if (lightType.equals("red&yellow"))
            return isEnable ? meta | (LIGHT_R << 2) : meta | (LIGHT_Y << 2);
        else if (lightType.equals("red&green"))
            return isEnable ? meta | (LIGHT_R << 2) : meta | (LIGHT_G << 2);
        else if (lightType.equals("yellow&green"))
            return isEnable ? meta | (LIGHT_Y << 2) : meta | (LIGHT_G << 2);
        else if (lightType.equals("white&blue"))
            return isEnable ? meta | (2 << 2) : meta | (3 << 2);
        else if (lightType.equals("yellow&purple"))
            return isEnable ? meta | (2 << 2) : meta | (3 << 2);
        else
            return meta;
    }

}
