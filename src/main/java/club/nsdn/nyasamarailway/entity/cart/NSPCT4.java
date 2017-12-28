package club.nsdn.nyasamarailway.entity.cart;

import club.nsdn.nyasamarailway.entity.MinecartBase;
import club.nsdn.nyasamarailway.item.ItemLoader;
import club.nsdn.nyasamarailway.tileblock.rail.mono.RailMonoMagnetBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2016.12.1.
 */
public class NSPCT4 extends MinecartBase {

    public double shiftY = -1.0;

    public NSPCT4(World world) {
        super(world);
    }

    public NSPCT4(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public boolean canMakePlayerTurn() {
        return false;
    }

    @Override
    public float getMaxCartSpeedOnRail() {
        return 2.0F;
    }

    @Override
    public double getMountedYOffset() {
        return 0.3 + shiftY;
    }

    @Override
    public void killMinecart(DamageSource source)
    {
        this.setDead();
        ItemStack itemstack = new ItemStack(ItemLoader.itemNSPCT4, 1);
        itemstack.setStackDisplayName(itemstack.getDisplayName());
        this.entityDropItem(itemstack, 0.0F);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.posY);
        int z = MathHelper.floor_double(this.posZ);
        if (worldObj.getBlock(x, y, z) instanceof RailMonoMagnetBase) {
            if (shiftY > -1.0) shiftY -= 0.05;
        } else {
            if (worldObj.getBlock(x + 1, y, z) instanceof RailMonoMagnetBase) return;
            if (worldObj.getBlock(x - 1, y, z) instanceof RailMonoMagnetBase) return;
            if (worldObj.getBlock(x, y, z + 1) instanceof RailMonoMagnetBase) return;
            if (worldObj.getBlock(x, y, z - 1) instanceof RailMonoMagnetBase) return;

            if (shiftY < 0) shiftY += 0.05;
        }
    }
}
