package club.nsdn.nyasamarailway.Blocks;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by drzzm32 on 2017.6.16.
 */
public class BlockRailBlocking extends BlockRailDetectorBase implements ITileEntityProvider {

    public static LinkedHashMap<UUID, TileEntityRailBlocking> tmpRails;

    public static class TileEntityRailBlocking extends TileEntity {

        public String linkedX, linkedY, linkedZ;

        public TileEntityRailBlocking getLinkedRail() {
            if (linkedX.equals("null") || linkedY.equals("null") || linkedZ.equals("null")) return null;

            TileEntity tileEntity;
            try {
                tileEntity = worldObj.getTileEntity(
                        Integer.parseInt(linkedX), Integer.parseInt(linkedY), Integer.parseInt(linkedZ)
                );
            } catch (Exception e) {
                return null;
            }
            if (tileEntity == null) return null;
            if (!(tileEntity instanceof TileEntityRailBlocking)) return null;

            return (TileEntityRailBlocking) tileEntity;
        }

        public void setLinkedRail(TileEntityRailBlocking rail) {
            if (rail == null) {
                linkedX = "null";
                linkedY = "null";
                linkedZ = "null";
            } else {
                linkedX = String.valueOf(rail.xCoord);
                linkedY = String.valueOf(rail.yCoord);
                linkedZ = String.valueOf(rail.zCoord);
            }
        }

        public TileEntityRailBlocking() {
            linkedX = "null";
            linkedY = "null";
            linkedZ = "null";
        }

        @Override
        public void writeToNBT(NBTTagCompound tagCompound) {
            super.writeToNBT(tagCompound);
            if (getLinkedRail() == null) {
                tagCompound.setString("linkedRailX", "null");
                tagCompound.setString("linkedRailY", "null");
                tagCompound.setString("linkedRailZ", "null");
            }
            tagCompound.setString("linkedRailX", linkedX);
            tagCompound.setString("linkedRailY", linkedY);
            tagCompound.setString("linkedRailZ", linkedZ);
        }

        @Override
        public void readFromNBT(NBTTagCompound tagCompound) {
            super.readFromNBT(tagCompound);
            linkedX = tagCompound.getString("linkedRailX");
            linkedY = tagCompound.getString("linkedRailY");
            linkedZ = tagCompound.getString("linkedRailZ");
        }

        @Override
        public Packet getDescriptionPacket() {
            NBTTagCompound tagCompound = new NBTTagCompound();
            if (getLinkedRail() == null) {
                tagCompound.setString("linkedRailX", "null");
                tagCompound.setString("linkedRailY", "null");
                tagCompound.setString("linkedRailZ", "null");
            }
            tagCompound.setString("linkedRailX", linkedX);
            tagCompound.setString("linkedRailY", linkedY);
            tagCompound.setString("linkedRailZ", linkedZ);
            return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, tagCompound);
        }

        @Override
        public void onDataPacket(NetworkManager manager, S35PacketUpdateTileEntity packet) {
            NBTTagCompound tagCompound = packet.func_148857_g();
            linkedX = tagCompound.getString("linkedRailX");
            linkedY = tagCompound.getString("linkedRailY");
            linkedZ = tagCompound.getString("linkedRailZ");
        }
    }

    public TileEntity createNewTileEntity(World world, int i) {
        return new TileEntityRailBlocking();
    }

    public BlockRailBlocking() {
        super("BlockRailBlocking");
        setTextureName("rail_blocking");

        tmpRails = new LinkedHashMap<UUID, TileEntityRailBlocking>();
    }

    public void setOutputSignal(World world, int x, int y, int z, boolean state) {
        if (world.getBlock(x, y, z) != this) return;
        int meta = world.getBlockMetadata(x, y, z);
        if (state) {
            if ((meta & 8) != 0) return;
            world.setBlockMetadataWithNotify(x, y, z, meta | 8, 3);
            world.notifyBlocksOfNeighborChange(x, y, z, this);
            world.notifyBlocksOfNeighborChange(x, y - 1, z, this);
            world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
        } else {
            if ((meta & 8) == 0) return;
            world.setBlockMetadataWithNotify(x, y, z, meta & 7, 3);
            world.notifyBlocksOfNeighborChange(x, y, z, this);
            world.notifyBlocksOfNeighborChange(x, y - 1, z, this);
            world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
        }
        world.func_147453_f(x, y, z, world.getBlock(x, y, z));
    }

    public void setOutputSignal(TileEntityRailBlocking rail, boolean state) {
        setOutputSignal(rail.getWorldObj(), rail.xCoord, rail.yCoord, rail.zCoord, state);
    }

    public boolean railHasPowered(World world, int x, int y, int z) {
        return world.getBlock(x, y, z) == this && (world.getBlockMetadata(x, y, z) & 8) != 0;
    }

    public boolean nearbyRailPowered(World world, int x, int y, int z) {
        return railHasPowered(world, x + 1, y, z) || railHasPowered(world, x - 1, y, z) ||
                railHasPowered(world, x, y, z + 1) || railHasPowered(world, x, y, z - 1);
    }

    public boolean railHasCart(World world, int x, int y, int z) {
        float bBoxSize = 0.125F;
        List bBox = world.getEntitiesWithinAABB(
                EntityMinecart.class,
                AxisAlignedBB.getBoundingBox(
                        (double) ((float) x + bBoxSize),
                        (double) y,
                        (double) ((float) z + bBoxSize),
                        (double) ((float) (x + 1) - bBoxSize),
                        (double) ((float) (y + 1) - bBoxSize),
                        (double) ((float) (z + 1) - bBoxSize)
                )
        );
        return !bBox.isEmpty();
    }

    public boolean nearbyRailHasCart(World world, int x, int y, int z) {
        return (world.getBlock(x + 1, y, z) == this && railHasCart(world, x + 1, y, z)) ||
                (world.getBlock(x - 1, y, z) == this && railHasCart(world, x - 1, y, z)) ||
                (world.getBlock(x, y, z + 1) == this && railHasCart(world, x, y, z + 1)) ||
                (world.getBlock(x, y, z - 1) == this && railHasCart(world, x, y, z - 1));
    }

    public TileEntityRailBlocking[] getNearbyRail(World world, int x, int y, int z) {
        return new TileEntityRailBlocking[]{
                world.getTileEntity(x + 1, y, z) instanceof TileEntityRailBlocking ? (TileEntityRailBlocking) world.getTileEntity(x + 1, y, z) : null,
                world.getTileEntity(x - 1, y, z) instanceof TileEntityRailBlocking ? (TileEntityRailBlocking) world.getTileEntity(x - 1, y, z) : null,
                world.getTileEntity(x, y, z + 1) instanceof TileEntityRailBlocking ? (TileEntityRailBlocking) world.getTileEntity(x, y, z + 1) : null,
                world.getTileEntity(x, y, z - 1) instanceof TileEntityRailBlocking ? (TileEntityRailBlocking) world.getTileEntity(x, y, z - 1) : null,
        };
    }

    @Override
    public void setRailOutput(World world, int x, int y, int z, int meta) {
        TileEntityRailBlocking thisRail = null;
        if (world.getTileEntity(x, y, z) instanceof TileEntityRailBlocking)
            thisRail = (TileEntityRailBlocking) world.getTileEntity(x, y, z);

        if (thisRail != null) {
            if (thisRail.getLinkedRail() != null) {
                if (nearbyRailPowered(world, x, y, z)) {
                    setOutputSignal(thisRail, true);
                    setOutputSignal(thisRail.getLinkedRail(), true);
                }
            } else {
                if (!nearbyRailHasCart(world, x, y, z)) {
                    TileEntityRailBlocking[] rails = getNearbyRail(world, x, y, z);
                    for (TileEntityRailBlocking rail : rails) {
                        if (rail != null) {
                            setOutputSignal(rail, false);
                            if (rail.getLinkedRail() != null)
                                setOutputSignal(rail.getLinkedRail(), false);
                        }
                    }
                }

                if (railHasCart(world, x, y, z) && !railHasPowered(world, x, y, z)) {
                    world.setBlockMetadataWithNotify(x, y, z, meta | 8, 3);
                    world.notifyBlocksOfNeighborChange(x, y, z, this);
                    world.notifyBlocksOfNeighborChange(x, y - 1, z, this);
                    world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
                }

                if (!railHasCart(world, x, y, z) && railHasPowered(world, x, y, z)) {
                    world.setBlockMetadataWithNotify(x, y, z, meta & 7, 3);
                    world.notifyBlocksOfNeighborChange(x, y, z, this);
                    world.notifyBlocksOfNeighborChange(x, y - 1, z, this);
                    world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
                }

                if (railHasCart(world, x, y, z)) {
                    world.scheduleBlockUpdate(x, y, z, this, 20);
                }

                world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        TileEntityRailBlocking thisRail = null;
        if (world.getTileEntity(x, y, z) instanceof TileEntityRailBlocking) {
            thisRail = (TileEntityRailBlocking) world.getTileEntity(x, y, z);
        }
        if (thisRail != null) {
            UUID uuid = player.getUniqueID();
            if (player.isSneaking() && !world.isRemote) {
                if (tmpRails.containsKey(uuid)) {
                    if (tmpRails.get(uuid) == thisRail) {
                        thisRail.setLinkedRail(null);
                        player.addChatComponentMessage(new ChatComponentTranslation("info.blocking.abort"));
                    } else {
                        if (thisRail.getLinkedRail() == tmpRails.get(uuid)) {
                            thisRail.getLinkedRail().setLinkedRail(null);
                            thisRail.setLinkedRail(null);
                            player.addChatComponentMessage(new ChatComponentTranslation("info.blocking.disconnected"));
                        } else {
                            thisRail.setLinkedRail(tmpRails.get(uuid));
                            thisRail.getLinkedRail().setLinkedRail(thisRail);
                            player.addChatComponentMessage(new ChatComponentTranslation("info.blocking.connected"));
                        }
                    }
                    tmpRails.remove(uuid);
                } else {
                    tmpRails.put(uuid, thisRail);
                    player.addChatComponentMessage(new ChatComponentTranslation("info.blocking.begin"));
                }
                return true;
            }
        }
        return false;
    }

}
