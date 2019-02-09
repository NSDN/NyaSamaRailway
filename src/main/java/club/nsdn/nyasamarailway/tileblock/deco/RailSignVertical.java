package club.nsdn.nyasamarailway.tileblock.deco;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.tileblock.TileBlock;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2018.1.11.
 */
public class RailSignVertical extends TileBlock {

    public static class TileEntityRailSignVertical extends TileEntityBase {

        public TileEntityRailSignVertical() {
            setInfo(4, 0.25, 1, 0.125);
        }

        @Nonnull
        @Override
        public AxisAlignedBB getRenderBoundingBox() {
            return super.getRenderBoundingBox().expand(4, 4, 4);
        }

    }

    public final String texture;
    @SideOnly(Side.CLIENT)
    public ResourceLocation location;

    public RailSignVertical(String name, String id, String texture) {
        super(name);
        setRegistryName(NyaSamaRailway.MODID, id);
        setLightOpacity(1);
        setLightLevel(0);
        this.texture = texture;
    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityRailSignVertical();
    }

    @Override
    public boolean isSideSolid(IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, EnumFacing facing) {
        return facing == EnumFacing.DOWN || facing == EnumFacing.UP;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack itemStack) {
        int val = MathHelper.floor((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        setDeviceMeta(world, pos, val);
    }

}
