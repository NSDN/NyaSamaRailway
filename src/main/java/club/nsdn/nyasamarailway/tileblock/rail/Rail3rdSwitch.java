package club.nsdn.nyasamarailway.tileblock.rail;

import club.nsdn.nyasamarailway.api.rail.AbsRail;
import club.nsdn.nyasamarailway.api.rail.AbsRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class Rail3rdSwitch extends MonoRailSwitch {

    public static class TileEntityRail3rdSwitch extends TileEntityMonoRailSwitch { }

    public Rail3rdSwitch() {
        super("Rail3rdSwitch", "rail_3rd_switch");
    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityRail3rdSwitch();
    }

    @Override
    public AbsRail.Rail getRail(World world, BlockPos pos, IBlockState state) {
        return new Rail(world, pos, state);
    }

    public class Rail extends AbsRail.Rail {

        @Override
        public Class<? extends AbsRailBase> getOuterClass() {
            return Rail3rdSwitch.class;
        }

        @Override
        public AbsRailBase.Rail getRail(World world, BlockPos pos, IBlockState state) {
            return new Rail(world, pos, state);
        }

        @Override
        public void setState(World world, BlockPos pos, IBlockState state) {

        }

        public Rail(World world, BlockPos pos, IBlockState state) {
            super(world, pos, state);
        }

    }

}
