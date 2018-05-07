package club.nsdn.nyasamarailway.block.rail;

import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2018.5.6.
 */
public interface IRailReception {
    boolean checkNearbySameRail(World world, int x, int y, int z);
    boolean timeExceed(World world, int x, int y, int z);
    void setRailState(World world, int x, int y, int z, boolean state);
}
