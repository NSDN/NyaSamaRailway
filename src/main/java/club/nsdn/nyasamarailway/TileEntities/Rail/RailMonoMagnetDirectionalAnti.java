package club.nsdn.nyasamarailway.TileEntities.Rail;

import club.nsdn.nyasamarailway.Blocks.IRailDirectional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import org.thewdj.physics.Dynamics;

/**
 * Created by drzzm32 on 2017.1.13.
 */
public class RailMonoMagnetDirectionalAnti extends RailMonoMagnetBase implements IRailDirectional {

    public static class TileEntityRail extends TileEntity implements RailMonoMagnetPowerable {

        @Override
        @SideOnly(Side.CLIENT)
        public AxisAlignedBB getRenderBoundingBox()
        {
            return AxisAlignedBB
                    .getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1)
                    .expand(4, 4, 4);
        }

        @Override
        public void writeToNBT(NBTTagCompound tagCompound) {
            super.writeToNBT(tagCompound);
        }

        @Override
        public void readFromNBT(NBTTagCompound tagCompound) {
            super.readFromNBT(tagCompound);
        }

        @Override
        public Packet getDescriptionPacket() {
            NBTTagCompound tagCompound = new NBTTagCompound();
            return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, tagCompound);
        }

        @Override
        public void onDataPacket(NetworkManager manager, S35PacketUpdateTileEntity packet) {
            NBTTagCompound tagCompound = packet.func_148857_g();
        }

    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityRail();
    }

    public RailMonoMagnetDirectionalAnti() {
        super(true, "RailMonoMagnetDirectionalAnti", "rail_mono_magnet_directional_anti");
    }

    public boolean isForward() {
        return false;
    }

    public void onMinecartPass(World world, EntityMinecart cart, int x, int y, int z) {
        double maxV = 0.4D;
        if (getRailDirection(world, x, y, z) == RailDirection.NS) {
            if (cart.motionZ <= 0.0D) cart.motionZ = 0.005D;
            if (cart.motionZ < maxV) {
                cart.motionZ = Dynamics.LocoMotions.calcVelocityUp(Math.abs(cart.motionZ), 0.1D, 1.0D, 0.1D, 0.02D);
            }
        } else {
            if (cart.motionX > -maxV) {
                cart.motionX = -Dynamics.LocoMotions.calcVelocityUp(Math.abs(cart.motionX), 0.1D, 1.0D, 0.1D, 0.02D);
            }
        }
    }

}
