package club.nsdn.nyasamarailway.tileblock.rail;

import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;

/**
 * Created by drzzm32 on 2016.8.27.
 */
public class RailNoSleeperStraight extends RailBase {

    public RailNoSleeperStraight() {
        super(false);
        setBlockName("RailNoSleeperStraight");
        setIconLocation("rail_no_sleeper_straight");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Override
    protected void setBoundsByMeta(int meta) {
        if (meta >= 2 && meta <= 5)
        {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }
        else
        {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
        }
    }

}
