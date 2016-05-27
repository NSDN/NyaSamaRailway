package club.nsdn.nyasamarailway.Blocks;

/**
 * Created by drzzm32 on 2016.5.5.
 */

import club.nsdn.nyasamarailway.CreativeTab.CreativeTabLoader;
import net.minecraft.block.BlockRailPowered;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.world.World;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.thewdj.physics.Dynamics.LocoMotions;

public class BlockRailPoweredBase extends BlockRailPowered {

    public BlockRailPoweredBase(String name) {
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
        float maxV = getRailMaxSpeed(world, cart, x, y, z);
        if (world.getBlockMetadata(x, y, z) >= 8) {
            if (Math.abs(cart.motionX) < maxV && Math.abs(cart.motionZ) < maxV) {
                cart.motionX = Math.signum(cart.motionX) * LocoMotions.calcVelocityUp(Math.abs(cart.motionX), 0.1, 1.0, 1.0, 0.02);
                cart.motionZ = Math.signum(cart.motionZ) * LocoMotions.calcVelocityUp(Math.abs(cart.motionZ), 0.1, 1.0, 1.0, 0.02);
            }
        } else {
            cart.motionX = Math.signum(cart.motionX) * LocoMotions.calcVelocityDown(Math.abs(cart.motionX), 0.1, 1.0, 1.0, 1.0, 0.2, 0.02);
            cart.motionZ = Math.signum(cart.motionZ) * LocoMotions.calcVelocityDown(Math.abs(cart.motionZ), 0.1, 1.0, 1.0, 1.0, 0.2, 0.02);
        }
    }

}
