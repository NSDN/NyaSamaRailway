package club.nsdn.nyasamarailway.Items;

import club.nsdn.nyasamarailway.Blocks.*;
import club.nsdn.nyasamarailway.TileEntities.TileEntityStationSign;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by drzzm32 on 2016.6.7.
 */
public class ItemStationSign extends ItemToolBase {

    public ItemStationSign() {
        super(ToolMaterial.IRON);
        setUnlocalizedName("ItemStationSign");
        setTexName("station_sign");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        player.setItemInUse(itemStack, this.getMaxItemUseDuration(itemStack));
        return itemStack;
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        Block block = world.getBlock(x, y, z);

        if (block == null)
            return false;

        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity == null)
            return false;
        if (tileEntity instanceof TileEntitySign) {
            TileEntityStationSign.StationSign sign = new TileEntityStationSign.StationSign();
            sign.StationNameCN = ((TileEntitySign) tileEntity).signText[0];
            sign.StationNameEN = ((TileEntitySign) tileEntity).signText[1];
            sign.LeftStations = ((TileEntitySign) tileEntity).signText[2];
            sign.RightStations = ((TileEntitySign) tileEntity).signText[3];

            int l = MathHelper.floor_double((double) (player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
            if (l == 0) {
                world.setBlock(x, y, z, BlockLoader.blockStationSign, 1, 2);
            }

            if (l == 1) {
                world.setBlock(x, y, z, BlockLoader.blockStationSign, 2, 2);
            }

            if (l == 2) {
                world.setBlock(x, y, z, BlockLoader.blockStationSign, 3, 2);
            }

            if (l == 3) {
                world.setBlock(x, y, z, BlockLoader.blockStationSign, 4, 2);
            }

            world.markBlockForUpdate(x, y, z);

            tileEntity = world.getTileEntity(x, y, z);
            if (tileEntity == null)
                return false;
            if (tileEntity instanceof TileEntityStationSign.StationSign) {
                ((TileEntityStationSign.StationSign) tileEntity).StationNameCN = sign.StationNameCN;
                ((TileEntityStationSign.StationSign) tileEntity).StationNameEN = sign.StationNameEN;
                ((TileEntityStationSign.StationSign) tileEntity).LeftStations = sign.LeftStations;
                ((TileEntityStationSign.StationSign) tileEntity).RightStations = sign.RightStations;

                return !world.isRemote;
            }
        }

        return false;
    }
}
