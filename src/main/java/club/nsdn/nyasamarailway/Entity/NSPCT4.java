package club.nsdn.nyasamarailway.Entity;

import club.nsdn.nyasamarailway.Items.ItemLoader;
import club.nsdn.nyasamarailway.TileEntities.Rail.RailMono;
import club.nsdn.nyasamarailway.TileEntities.Rail.RailMonoMagnet;
import club.nsdn.nyasamarailway.TileEntities.Rail.RailMonoMagnetBase;
import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.IMinecartCollisionHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartCollisionEvent;

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
