package club.nsdn.nyasamarailway.tileblock.rail;

import club.nsdn.nyasamarailway.api.rail.AbsRail;
import club.nsdn.nyasamarailway.api.rail.AbsRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class Rail3rd extends MonoRailBase {

    public static class TileEntityRail3rd extends TileEntityMonoRailBase { }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityRail3rd();
    }

    public Rail3rd() {
        super("Rail3rd", "rail_3rd");
    }

    @Override
    public float getRailMaxSpeed(World world, EntityMinecart cart, BlockPos pos) {
        return 3.0F;
    }

    @Override
    public AbsRail.Rail getRail(World world, BlockPos pos, IBlockState state) {
        return new Rail(world, pos, state);
    }

    public class Rail extends AbsRail.Rail {

        @Override
        public Class<? extends AbsRailBase> getOuterClass() {
            return Rail3rd.class;
        }

        @Override
        public AbsRailBase.Rail getRail(World world, BlockPos pos, IBlockState state) {
            return new Rail(world, pos, state);
        }

        public Rail(World world, BlockPos pos, IBlockState state) {
            super(world, pos, state);
        }

    }

}
