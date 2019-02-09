package club.nsdn.nyasamarailway.tileblock.deco;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.tileblock.TileBlock;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2016.5.22.
 */
public class RailSignHead extends TileBlock {

    public static class TileEntityRailSignHead extends TileEntityBase {

        public TileEntityRailSignHead() {
            setInfo(4, 1, 1, 0.5);
        }

        @Nonnull
        @Override
        public AxisAlignedBB getRenderBoundingBox() {
            return super.getRenderBoundingBox().expand(4, 4, 4);
        }

    }

    public final String fore, back;
    @SideOnly(Side.CLIENT)
    public ResourceLocation texFore;
    @SideOnly(Side.CLIENT)
    public ResourceLocation texBack;


    public RailSignHead(String name, String id, String fore, String back) {
        super(name);
        setRegistryName(NyaSamaRailway.MODID, id);
        setLightOpacity(1);
        setLightLevel(0);
        this.fore = fore;
        this.back = back;
    }

    public RailSignHead(String name, String id, String texture) {
        this(name, id, texture, null);
    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityRailSignHead();
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
