package club.nsdn.nyasamarailway.tileblock.signal.deco;

import net.minecraft.tileentity.TileEntity;

/**
 * Created by drzzm32 on 2017.9.6.
 */
public class GlassShieldHalf extends GlassShield {

    public static class TileEntityGlassShieldHalf extends TileEntityGlassShield {

        public TileEntityGlassShieldHalf() {
            setInfo(4, 1, 1.5, 0.125);
        }

    }

    public GlassShieldHalf() {
        super("GlassShieldHalf", "glass_shield_half");
    }

    @Override
    public TileEntity createNewTileEntity() {
        return new TileEntityGlassShieldHalf();
    }

}
