package club.nsdn.nyasamarailway.tileblock.functional;

/**
 * Created by drzzm32 on 2016.5.10.
 */

import club.nsdn.nyasamarailway.tileblock.TileBlock;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class TileEntityStationSign extends TileBlock {

    public static class StationSign extends TileEntity {
        public String StationNameCN = " ";
        public String StationNameEN = " ";
        public String LeftStations = " ";
        public String RightStations = " ";

        @Override
        public boolean shouldRenderInPass(int pass) {
            return true;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public AxisAlignedBB getRenderBoundingBox()
        {
            return AxisAlignedBB
                    .getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1)
                    .expand(2, 2, 2);
        }

        @Override
        public void writeToNBT(NBTTagCompound tagCompound) {
            super.writeToNBT(tagCompound);
            tagCompound.setString("StationNameCN", StationNameCN);
            tagCompound.setString("StationNameEN", StationNameEN);
            tagCompound.setString("LeftStations", LeftStations);
            tagCompound.setString("RightStations", RightStations);
        }

        @Override
        public void readFromNBT(NBTTagCompound tagCompound) {
            super.readFromNBT(tagCompound);
            StationNameCN = tagCompound.getString("StationNameCN");
            StationNameEN = tagCompound.getString("StationNameEN");
            LeftStations = tagCompound.getString("LeftStations");
            RightStations = tagCompound.getString("RightStations");
        }

        @Override
        public Packet getDescriptionPacket() {
            NBTTagCompound tagCompound = new NBTTagCompound();
            tagCompound.setString("StationNameCN", StationNameCN);
            tagCompound.setString("StationNameEN", StationNameEN);
            tagCompound.setString("LeftStations", LeftStations);
            tagCompound.setString("RightStations", RightStations);
            return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, tagCompound);
        }

        @Override
        public void onDataPacket(NetworkManager manager, S35PacketUpdateTileEntity packet) {
            NBTTagCompound tagCompound = packet.func_148857_g();
            StationNameCN = tagCompound.getString("StationNameCN");
            StationNameEN = tagCompound.getString("StationNameEN");
            LeftStations = tagCompound.getString("LeftStations");
            RightStations = tagCompound.getString("RightStations");
        }

    }

    public TileEntityStationSign() {
        super("StationSign");
        setIconLocation("station_sign");
        setLightOpacity(0);
        //setCreativeTab(null);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new StationSign();
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
    }

    @Override
    protected void setBoundsByMeta(int meta) {
        float x1 = 0.0F, y1 = 0.0F, z1 = 0.375F, x2 = 1.0F, y2 = 2.0F, z2 = 0.625F;
        switch (meta % 13) {
            case 1:
                setBlockBounds(x1, y1, z1, x2, y2, z2);
                break;
            case 2:
                setBlockBounds(1.0F - z2, y1, x1, 1.0F - z1, y2, x2);
                break;
            case 3:
                setBlockBounds(1.0F - x2, y1, 1.0F - z2, 1.0F - x1, y2, 1.0F - z1);
                break;
            case 4:
                setBlockBounds(z1, y1, 1.0F - x2, z2, y2, 1.0F - x1);
                break;
        }
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack) {
        int l = MathHelper.floor_double((double) (player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        if (l == 0) {
            world.setBlockMetadataWithNotify(x, y, z, 1, 2);
        }

        if (l == 1) {
            world.setBlockMetadataWithNotify(x, y, z, 2, 2);
        }

        if (l == 2) {
            world.setBlockMetadataWithNotify(x, y, z, 3, 2);
        }

        if (l == 3) {
            world.setBlockMetadataWithNotify(x, y, z, 4, 2);
        }

    }

}
