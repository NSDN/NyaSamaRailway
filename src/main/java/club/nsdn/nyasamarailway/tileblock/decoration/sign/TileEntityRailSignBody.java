package club.nsdn.nyasamarailway.tileblock.decoration.sign;

/**
 * Created by drzzm32 on 2016.5.22.
 */

import club.nsdn.nyasamarailway.tileblock.TileBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TileEntityRailSignBody extends TileBlock {

    public static class RailSignBody extends TileEntity {}

    public TileEntityRailSignBody() {
        super("RailSignBody");
        setIconLocation("rail_sign_body");
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new RailSignBody();
    }

    @Override
    protected void setBoundsByMeta(int meta) {
        switch (meta % 13) {
            case 1:
                setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 1.0F, 0.75F);
                break;
            case 2:
                setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 1.0F, 0.75F);
                break;
            case 3:
                setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 1.0F, 0.75F);
                break;
            case 4:
                setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 1.0F, 0.75F);
                break;
            case 5:
                setBlockBounds(0.25F, 0.25F, 0.0F, 0.75F, 0.75F, 1.0F);
                break;
            case 6:
                setBlockBounds( 0.0F, 0.25F, 0.25F, 1.0F, 0.75F, 0.75F);
                break;
            case 7:
                setBlockBounds(0.25F, 0.25F, 0.0F, 0.75F, 0.75F, 1.0F);
                break;
            case 8:
                setBlockBounds( 0.0F, 0.25F, 0.25F, 1.0F, 0.75F, 0.75F);
                break;
            case 9:
                setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 1.0F, 0.75F);
                break;
            case 10:
                setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 1.0F, 0.75F);
                break;
            case 11:
                setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 1.0F, 0.75F);
                break;
            case 12:
                setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 1.0F, 0.75F);
                break;
        }
    }

}
