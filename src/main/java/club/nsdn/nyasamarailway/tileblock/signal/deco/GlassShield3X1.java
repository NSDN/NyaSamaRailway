package club.nsdn.nyasamarailway.tileblock.signal.deco;

import net.minecraft.tileentity.TileEntity;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class GlassShield3X1 extends GlassShield {

    public static class TileEntityGlassShield3X1 extends TileEntityGlassShield {

        public TileEntityGlassShield3X1() {
            setInfo(4, 1, 3, 0.125);
        }

    }

    public GlassShield3X1() {
        super("GlassShield3X1", "glass_shield_3x1");
    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityGlassShield3X1();
    }

}
