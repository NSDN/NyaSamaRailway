package club.nsdn.nyasamarailway.tileblock.signal.deco;

import net.minecraft.tileentity.TileEntity;

/**
 * Created by drzzm32 on 2020.2.20
 */
public class GlassShield2X1D5 extends GlassShield {

    public static class TileEntityGlassShield2X1D5 extends TileEntityGlassShield {

        public TileEntityGlassShield2X1D5() {
            setInfo(4, 1, 2, 0.125);
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

    public GlassShield2X1D5() {
        super("GlassShield2X1D5", "glass_shield_2x1d5");
    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityGlassShield2X1D5();
    }

}
