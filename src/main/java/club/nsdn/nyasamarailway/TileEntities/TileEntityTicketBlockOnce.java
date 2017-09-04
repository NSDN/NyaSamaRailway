package club.nsdn.nyasamarailway.TileEntities;

import club.nsdn.nyasamarailway.Items.ItemLoader;
import club.nsdn.nyasamarailway.Items.ItemTicketBase;
import club.nsdn.nyasamarailway.Items.ItemTicketOnce;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by drzzm32 on 2017.9.4.
 */
public class TileEntityTicketBlockOnce extends TileEntityBase {

    public static class TicketBlock extends TileEntity {

    }

    public TileEntityTicketBlockOnce() {
        super("TicketBlockOnce");
        setIconLocation("block_ticket_once");
        setLightOpacity(0);
        setLightLevel(0.1F);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TicketBlock();
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side)
    {
        return side == ForgeDirection.UP;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack) {
        int meta = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        world.setBlockMetadataWithNotify(x, y, z, meta, 2);
    }

    @Override
    protected void setBoundsByMeta(int meta) {
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int v, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            if (player.getCurrentEquippedItem() == null) {
                ItemStack itemStack = new ItemStack(ItemLoader.itemTicketOnce);
                ItemTicketBase.setOver(itemStack, 1);
                player.setCurrentItemOrArmor(0, itemStack);
                return true;
            }
        }
        return false;
    }


}
