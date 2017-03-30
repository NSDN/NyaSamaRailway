package club.nsdn.nyasamarailway.TileEntities.Rail;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2017.1.13.
 */
public class RailMonoMagnetSignalTransfer extends RailMonoMagnetPowered {

    public static class TileEntityRail extends TileEntity implements RailMonoMagnetPowerable {

        @Override
        public boolean shouldRenderInPass(int pass) {
            return true;
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

    public RailMonoMagnetSignalTransfer() {
        super("RailMonoMagnetSignalTransfer", "rail_mono_magnet_signal_transfer");
    }

    @Override
    public void onMinecartPass(World world, EntityMinecart cart, int x, int y, int z) {
    }

}
