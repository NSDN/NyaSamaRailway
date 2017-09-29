package club.nsdn.nyasamarailway.Blocks;

/**
 * Created by drzzm32 on 2016.5.5.
 */

import net.minecraft.block.BlockRail;
import net.minecraft.world.World;
import net.minecraft.entity.item.EntityMinecart;
import club.nsdn.nyasamarailway.CreativeTab.CreativeTabLoader;

public class BlockRailBase extends BlockRail {

    public enum RailDirection {
        NONE,
        WE, //West-East
        NS //North-South
    }

    public RailDirection getRailDirection(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        if ((meta & 2) == 0 && (meta & 4) == 0) {
            return ((meta & 1) == 0) ? RailDirection.NS : RailDirection.WE;
        } else if ((meta & 2) > 0) {
            return RailDirection.WE;
        } else if ((meta & 4) > 0) {
            return RailDirection.NS;
        }
        return RailDirection.NONE;
    }

    public BlockRailBase(String name)
    {
        super();
        setBlockName(name);
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    protected void setTextureName(String name) {
        setBlockTextureName("nyasamarailway" + ":" + name);
    }

    @Override
    public float getRailMaxSpeed(World world, EntityMinecart cart, int x, int y, int z) {
        return 1.0F;
    }

    @Override
    public void onMinecartPass(World world, EntityMinecart cart, int x, int y, int z) {

    }

}
