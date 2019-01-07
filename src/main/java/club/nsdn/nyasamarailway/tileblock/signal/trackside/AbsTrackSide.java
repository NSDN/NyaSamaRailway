package club.nsdn.nyasamarailway.tileblock.signal.trackside;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.tileblock.signal.ITrackSide;
import club.nsdn.nyasamatelecom.api.device.SignalBox;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

/**
 * Created by drzzm32 on 2019.1.5.
 */
public abstract class AbsTrackSide extends SignalBox {

    public AbsTrackSide(String name, String icon) {
        super(NyaSamaRailway.MODID, name, icon);
    }

    @Override
    public abstract TileEntity createNewTileEntity(World world, int meta);

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack) {
        int rot = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        if (player.rotationPitch > 0.0F) {
            world.setBlockMetadataWithNotify(x, y, z, rot, 2);
        } else {
            world.setBlockMetadataWithNotify(x, y, z, rot | 0x4, 2);
        }

        ITrackSide.getDirByMeta(world.getTileEntity(x, y, z));
    }

    @Override
    protected void setBoundsByMeta(int meta) {
        float x = 0.25F, y = 0.3125F, z = 1.0F;
        float x1 = 0.5F - x / 2, y1 = 0.0F, z1 = 0.5F - z / 2, x2 = 0.5F + x / 2, y2 = y, z2 = 0.5F + z / 2;
        switch (meta & 7) {
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

            case 4:
                setBlockBounds(x1, 1.0F - y2, z1, x2, 1.0F - y1, z2);
                break;
            case 5:
                setBlockBounds(1.0F - z2, 1.0F - y2, x1, 1.0F - z1, 1.0F - y1, x2);
                break;
            case 6:
                setBlockBounds(1.0F - x2, 1.0F - y2, 1.0F - z2, 1.0F - x1, 1.0F - y1, 1.0F - z1);
                break;
            case 7:
                setBlockBounds(z1, 1.0F - y2, 1.0F - x2, z2, 1.0F - y1, 1.0F - x1);
                break;
        }
    }

    @Override
    public abstract void updateTick(World world, int x, int y, int z, Random random);

    @Override
    public abstract boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ);

}
