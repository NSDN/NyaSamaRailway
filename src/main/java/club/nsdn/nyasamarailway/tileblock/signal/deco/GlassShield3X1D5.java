package club.nsdn.nyasamarailway.tileblock.signal.deco;

import net.minecraft.tileentity.TileEntity;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class GlassShield3X1D5 extends GlassShield {

    public static class TileEntityGlassShield3X1D5 extends TileEntityGlassShield {

        public TileEntityGlassShield3X1D5() {
            setInfo(4, 1, 3, 0.125);
        }

        @Override
        protected void updateBounds() {
            double x1 = -0.5;
            if ((META & 0x8) != 0) x1 = 0.875F;
            setBoundsByXYZ(
                    x1, 0, 0.5 - this.SIZE.z / 2,
                    0.5 + this.SIZE.x / 2, this.SIZE.y, 0.5 + this.SIZE.z / 2
            );
        }

    }

    public GlassShield3X1D5() {
        super("GlassShield3X1D5", "glass_shield_3x1d5");
    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityGlassShield3X1D5();
    }

}
