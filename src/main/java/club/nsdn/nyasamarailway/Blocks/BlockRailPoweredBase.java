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

    public int getRailChargeDistance() { return 32; }

    public enum RailDirection {
        NONE,
        WE, //West-East
        NS //North-South
    }

    public RailDirection getRailDirection(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        if ((meta & 2) == 0 && (meta & 4) == 0) {
            return ((meta & 1) == 0) ? RailDirection.NS : RailDirection.WE;
        } else if ((meta & 2) > 0 && (meta & 4) == 0) {
            return RailDirection.WE;
        } else if ((meta & 2) == 0 && (meta & 4) > 0) {
            return RailDirection.NS;
        }
        return RailDirection.NONE;
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

    @Override
    protected boolean func_150058_a(World p_150058_1_, int p_150058_2_, int p_150058_3_, int p_150058_4_, int p_150058_5_, boolean p_150058_6_, int p_150058_7_) {
        if(p_150058_7_ >= getRailChargeDistance()) {
            return false;
        } else {
            int var8 = p_150058_5_ & 7;
            boolean var9 = true;
            switch(var8) {
                case 0:
                    if(p_150058_6_) {
                        ++p_150058_4_;
                    } else {
                        --p_150058_4_;
                    }
                    break;
                case 1:
                    if(p_150058_6_) {
                        --p_150058_2_;
                    } else {
                        ++p_150058_2_;
                    }
                    break;
                case 2:
                    if(p_150058_6_) {
                        --p_150058_2_;
                    } else {
                        ++p_150058_2_;
                        ++p_150058_3_;
                        var9 = false;
                    }

                    var8 = 1;
                    break;
                case 3:
                    if(p_150058_6_) {
                        --p_150058_2_;
                        ++p_150058_3_;
                        var9 = false;
                    } else {
                        ++p_150058_2_;
                    }

                    var8 = 1;
                    break;
                case 4:
                    if(p_150058_6_) {
                        ++p_150058_4_;
                    } else {
                        --p_150058_4_;
                        ++p_150058_3_;
                        var9 = false;
                    }

                    var8 = 0;
                    break;
                case 5:
                    if(p_150058_6_) {
                        ++p_150058_4_;
                        ++p_150058_3_;
                        var9 = false;
                    } else {
                        --p_150058_4_;
                    }

                    var8 = 0;
            }

            return this.func_150057_a(p_150058_1_, p_150058_2_, p_150058_3_, p_150058_4_, p_150058_6_, p_150058_7_, var8)?true:var9 && this.func_150057_a(p_150058_1_, p_150058_2_, p_150058_3_ - 1, p_150058_4_, p_150058_6_, p_150058_7_, var8);
        }
    }

}
