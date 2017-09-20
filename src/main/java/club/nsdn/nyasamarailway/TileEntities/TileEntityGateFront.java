package club.nsdn.nyasamarailway.TileEntities;

import club.nsdn.nyasamarailway.Items.Item1N4148;
import club.nsdn.nyasamarailway.Items.ItemTicketBase;
import club.nsdn.nyasamarailway.Items.ItemTicketOnce;
import club.nsdn.nyasamarailway.TileEntities.Signals.TileEntityRailReceiver;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;
import java.util.Random;

/**
 * Created by drzzm32 on 2017.9.4.
 */
public class TileEntityGateFront extends TileEntityBase {

    public static class GateFront extends TileEntityRailReceiver {

        public static final int DELAY = 5;
        public int delay;

        public int over = -1;
        public boolean isEnabled = true;
        public ForgeDirection direction;

        public int setOver = 1;

        @Override
        @SideOnly(Side.CLIENT)
        public AxisAlignedBB getRenderBoundingBox()
        {
            return AxisAlignedBB
                    .getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1)
                    .expand(4, 4, 4);
        }

        public void fromNBT(NBTTagCompound tagCompound) {
            over = tagCompound.getInteger("over");
            isEnabled = tagCompound.getBoolean("isEnabled");
            direction = ForgeDirection.getOrientation(
                    tagCompound.getInteger("direction")
            );
            setOver = tagCompound.getInteger("setOver");
            super.fromNBT(tagCompound);
        }

        public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
            tagCompound.setInteger("over", over);
            tagCompound.setBoolean("isEnabled", isEnabled);
            if (direction == null) direction = ForgeDirection.UNKNOWN;
            tagCompound.setInteger("direction", direction.ordinal());
            tagCompound.setInteger("setOver", setOver);
            return super.toNBT(tagCompound);
        }

    }

    public TileEntityGateFront() {
        super("GateFront");
        setIconLocation("gate_front");
        setLightOpacity(0);
        setLightLevel(0.1F);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new GateFront();
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side)
    {
        return side == ForgeDirection.UP;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemStack) {
        int meta = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        if (world.getTileEntity(x, y, z) instanceof GateFront) {
            GateFront gateFront = (GateFront) world.getTileEntity(x, y, z);
            switch (meta) {
                case 0:
                    gateFront.direction = ForgeDirection.SOUTH;
                    break;
                case 1:
                    gateFront.direction = ForgeDirection.WEST;
                    break;
                case 2:
                    gateFront.direction = ForgeDirection.NORTH;
                    break;
                case 3:
                    gateFront.direction = ForgeDirection.EAST;
                    break;
                default:
                    break;
            }
            world.setBlockMetadataWithNotify(x, y, z, meta, 2);
        }
    }

    @Override
    protected void setBoundsByMeta(int meta) {
        float x1 = 0.25F, y1 = 0.0F, z1 = 0.5F, x2 = 0.75F, y2 = 1.5F, z2 = 1.0F;
        switch (meta & 3) {
            case 0:
                setBlockBounds(x1, y1, z1, x2, y2, z2);
                break;
            case 1:
                setBlockBounds(1.0F - z2, y1, x1, 1.0F - z1, y2, x2);
                break;
            case 2:
                setBlockBounds(1.0F - x2, y1, 1.0F - z2, 1.0F - x1, y2, 1.0F - z1);
                break;
            case 3:
                setBlockBounds(z1, y1, 1.0F - x2, z2, y2, 1.0F - x1);
                break;
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        float x1 = 0.25F, y1 = 0.0F, z1 = 0.5F, x2 = 0.75F, y2 = 1.0F, z2 = 1.0F;
        switch (meta & 3) {
            case 0:
                setBlockBounds(x1, y1, z1, x2, y2, z2);
                break;
            case 1:
                setBlockBounds(1.0F - z2, y1, x1, 1.0F - z1, y2, x2);
                break;
            case 2:
                setBlockBounds(1.0F - x2, y1, 1.0F - z2, 1.0F - x1, y2, 1.0F - z1);
                break;
            case 3:
                setBlockBounds(z1, y1, 1.0F - x2, z2, y2, 1.0F - x1);
                break;
        }
        return AxisAlignedBB.getBoundingBox(x + this.minX, y + this.minY, z + this.minZ, x + this.maxX, y + this.maxY, z + this.maxZ);
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        world.scheduleBlockUpdate(x, y, z, this, 1);
    }

    @Override
    public int tickRate(World world) {
        return 20;
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (!world.isRemote) {
            if (world.getTileEntity(x, y, z) instanceof GateFront) {
                GateFront gateFront = (GateFront) world.getTileEntity(x, y, z);
                boolean isEnable;

                if (gateFront.getSender() == null) {
                    isEnable = true;
                } else {
                    isEnable = gateFront.senderIsPowered();
                }

                if (isEnable != gateFront.isEnabled) {
                    gateFront.isEnabled = isEnable;
                    world.markBlockForUpdate(x, y, z);
                }

                doSniff(world, x, y, z, gateFront);

                world.scheduleBlockUpdate(x, y, z, this, 1);
            }
        }
    }

    public void doSniff(World world, int x, int y, int z, GateFront gateFront) {
        EntityPlayer player;
        if (gateFront.isEnabled) { //left
            switch (gateFront.direction) {
                case SOUTH:
                    player = findPlayer(world, x + 1, y , z);
                    if (player != null) {
                        if (getGateBase(world, x, y, z + 1) != null) {
                            getGateBase(world, x, y, z + 1).player = player.getDisplayName();
                        }
                    }
                    break;
                case WEST:
                    player = findPlayer(world, x, y , z + 1);
                    if (player != null) {
                        if (getGateBase(world, x - 1, y, z) != null) {
                            getGateBase(world, x - 1, y, z).player = player.getDisplayName();
                        }
                    }
                    break;
                case NORTH:
                    player = findPlayer(world, x - 1, y , z);
                    if (player != null) {
                        if (getGateBase(world, x, y, z - 1) != null) {
                            getGateBase(world, x, y, z - 1).player = player.getDisplayName();
                        }
                    }
                    break;
                case EAST:
                    player = findPlayer(world, x, y , z - 1);
                    if (player != null) {
                        if (getGateBase(world, x + 1, y, z) != null) {
                            getGateBase(world, x + 1, y, z).player = player.getDisplayName();
                        }
                    }
                    break;
                default:
                    break;
            }
        } else { //right
            switch (gateFront.direction) {
                case SOUTH:
                    player = findPlayer(world, x - 1, y , z);
                    if (getGateBase(world, x, y, z + 1) != null) {
                        boolean delayed = false, playerOK = false;
                        if (player != null) {
                            playerOK = getGateBase(world, x, y, z + 1).player.equals(player.getDisplayName());
                        }

                        if (getGateBase(world, x, y, z + 1).getDoorState() == TileEntityGateDoor.GateDoor.STATE_OPEN) {
                            gateFront.delay += 1;
                            if (gateFront.delay > GateFront.DELAY * 20) {
                                delayed = true;
                            }
                        } else {
                            gateFront.delay = 0;
                        }

                        if (delayed || playerOK) {
                            getGateBase(world, x, y, z + 1).player = "";
                            getGateBase(world, x, y, z + 1).closeDoor();
                            if (getGateFront(world, x, y, z + 2) != null) {
                                getGateFront(world, x, y, z + 2).over = -1;
                                world.markBlockForUpdate(x, y, z + 2);
                            }
                        }
                    }
                    break;
                case WEST:
                    player = findPlayer(world, x, y , z - 1);
                    if (getGateBase(world, x - 1, y, z) != null) {
                        boolean delayed = false, playerOK = false;
                        if (player != null) {
                            playerOK = getGateBase(world, x - 1, y, z).player.equals(player.getDisplayName());
                        }

                        if (getGateBase(world, x - 1, y, z).getDoorState() == TileEntityGateDoor.GateDoor.STATE_OPEN) {
                            gateFront.delay += 1;
                            if (gateFront.delay > GateFront.DELAY * 20) {
                                delayed = true;
                            }
                        } else {
                            gateFront.delay = 0;
                        }

                        if (delayed || playerOK) {
                            getGateBase(world, x - 1, y, z).player = "";
                            getGateBase(world, x - 1, y, z).closeDoor();
                            if (getGateFront(world, x - 2, y, z) != null) {
                                getGateFront(world, x - 2, y, z).over = -1;
                                world.markBlockForUpdate(x - 2, y, z);
                            }
                        }
                    }
                    break;
                case NORTH:
                    player = findPlayer(world, x + 1, y , z);
                    if (getGateBase(world, x, y, z - 1) != null) {
                        boolean delayed = false, playerOK = false;
                        if (player != null) {
                            playerOK = getGateBase(world, x, y, z - 1).player.equals(player.getDisplayName());
                        }

                        if (getGateBase(world, x, y, z - 1).getDoorState() == TileEntityGateDoor.GateDoor.STATE_OPEN) {
                            gateFront.delay += 1;
                            if (gateFront.delay > GateFront.DELAY * 20) {
                                delayed = true;
                            }
                        } else {
                            gateFront.delay = 0;
                        }

                        if (delayed || playerOK) {
                            getGateBase(world, x, y, z - 1).player = "";
                            getGateBase(world, x, y, z - 1).closeDoor();
                            if (getGateFront(world, x, y, z - 2) != null) {
                                getGateFront(world, x, y, z - 2).over = -1;
                                world.markBlockForUpdate(x, y, z - 2);
                            }
                        }
                    }
                    break;
                case EAST:
                    player = findPlayer(world, x, y , z + 1);
                    if (getGateBase(world, x + 1, y, z) != null) {
                        boolean delayed = false, playerOK = false;
                        if (player != null) {
                            playerOK = getGateBase(world, x + 1, y, z).player.equals(player.getDisplayName());
                        }

                        if (getGateBase(world, x + 1, y, z).getDoorState() == TileEntityGateDoor.GateDoor.STATE_OPEN) {
                            gateFront.delay += 1;
                            if (gateFront.delay > GateFront.DELAY * 20) {
                                delayed = true;
                            }
                        } else {
                            gateFront.delay = 0;
                        }

                        if (delayed || playerOK) {
                            getGateBase(world, x + 1, y, z).player = "";
                            getGateBase(world, x + 1, y, z).closeDoor();
                            if (getGateFront(world, x + 2, y, z) != null) {
                                getGateFront(world, x + 2, y, z).over = -1;
                                world.markBlockForUpdate(x + 2, y, z);
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public EntityPlayer findPlayer(World world, int x, int y, int z) {
        float bBoxSize = 0.125F;
        y += 1; //player's bounding box is higher than cart
        List bBox = world.getEntitiesWithinAABB(
                EntityPlayer.class,
                AxisAlignedBB.getBoundingBox((double) ((float) x + bBoxSize),
                        (double) y,
                        (double) ((float) z + bBoxSize),
                        (double) ((float) (x + 1) - bBoxSize),
                        (double) ((float) (y + 1) - bBoxSize),
                        (double) ((float) (z + 1) - bBoxSize))
        );

        if (!bBox.isEmpty()) {
            if (bBox.size() > 1) return null;
            return (EntityPlayer) bBox.get(0);
        }
        return null;
    }

    public TileEntityGateBase.GateBase getGateBase(World world, int x, int y, int z) {
        if (world.getTileEntity(x, y ,z) instanceof TileEntityGateBase.GateBase) {
            return (TileEntityGateBase.GateBase) world.getTileEntity(x, y, z);
        }
        return null;
    }

    public GateFront getGateFront(World world, int x, int y, int z) {
        if (world.getTileEntity(x, y ,z) instanceof GateFront) {
            return (GateFront) world.getTileEntity(x, y, z);
        }
        return null;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int v, float hitX, float hitY, float hitZ) {
        if (world.getTileEntity(x, y, z) instanceof GateFront) {
            GateFront gateFront = (GateFront) world.getTileEntity(x, y, z);
            if (player.getCurrentEquippedItem() != null && gateFront.isEnabled) {
                if (player.getCurrentEquippedItem().getItem() instanceof Item1N4148) {
                    if (!world.isRemote) {
                        switch (gateFront.direction) {
                            case SOUTH:
                                if (getGateBase(world, x, y, z + 1) != null) {
                                    if (getGateBase(world, x, y, z + 1).player.equals(player.getDisplayName())) {
                                        getGateBase(world, x, y, z + 1).openDoor();
                                        gateFront.over = -2;
                                        world.playSoundEffect(x + 0.5, y + 1.0, z + 0.5,
                                                "nyasamarailway:info.gate.beep",
                                                0.5F, 1.0F
                                        );
                                        world.markBlockForUpdate(x, y, z);
                                    }
                                }
                                break;
                            case WEST:
                                if (getGateBase(world, x - 1, y, z) != null) {
                                    if (getGateBase(world, x - 1, y, z).player.equals(player.getDisplayName())) {
                                        getGateBase(world, x - 1, y, z).openDoor();
                                        gateFront.over = -2;
                                        world.playSoundEffect(x + 0.5, y + 1.0, z + 0.5,
                                                "nyasamarailway:info.gate.beep",
                                                0.5F, 1.0F
                                        );
                                        world.markBlockForUpdate(x, y, z);
                                    }
                                }
                                break;
                            case NORTH:
                                if (getGateBase(world, x, y, z - 1) != null) {
                                    if (getGateBase(world, x, y, z - 1).player.equals(player.getDisplayName())) {
                                        getGateBase(world, x, y, z - 1).openDoor();
                                        gateFront.over = -2;
                                        world.playSoundEffect(x + 0.5, y + 1.0, z + 0.5,
                                                "nyasamarailway:info.gate.beep",
                                                0.5F, 1.0F
                                        );
                                        world.markBlockForUpdate(x, y, z);
                                    }
                                }
                                break;
                            case EAST:
                                if (getGateBase(world, x + 1, y, z) != null) {
                                    if (getGateBase(world, x + 1, y, z).player.equals(player.getDisplayName())) {
                                        getGateBase(world, x + 1, y, z).openDoor();
                                        gateFront.over = -2;
                                        world.playSoundEffect(x + 0.5, y + 1.0, z + 0.5,
                                                "nyasamarailway:info.gate.beep",
                                                0.5F, 1.0F
                                        );
                                        world.markBlockForUpdate(x, y, z);
                                    }
                                }
                                break;
                            default:
                                break;
                        }
                    }
                    return true;
                } else if (player.getCurrentEquippedItem().getItem() instanceof ItemTicketBase) {
                    if (!world.isRemote) {
                        ItemStack stack = player.getCurrentEquippedItem();
                        switch (gateFront.direction) {
                            case SOUTH:
                                if (getGateBase(world, x, y, z + 1) != null) {
                                    if (getGateBase(world, x, y, z + 1).player.equals(player.getDisplayName())) {
                                        if (stack.getItem() instanceof ItemTicketOnce) {
                                            if (gateFront.setOver != ItemTicketBase.getOver(stack))
                                                return true;
                                        }

                                        if (ItemTicketBase.getOver(stack) > 0) {
                                            if (ItemTicketBase.getState(stack)) {
                                                ItemTicketBase.decOver(stack);
                                                if (stack.getItem() instanceof ItemTicketOnce) {
                                                    ItemTicketBase.setOver(stack, 0);
                                                    player.destroyCurrentEquippedItem();
                                                }
                                                getGateBase(world, x, y, z + 1).openDoor();
                                                gateFront.over = ItemTicketBase.getOver(stack);
                                                ItemTicketBase.setState(stack, false);
                                            } else {
                                                getGateBase(world, x, y, z + 1).openDoor();
                                                gateFront.over = ItemTicketBase.getOver(stack);
                                                ItemTicketBase.setState(stack, true);
                                            }
                                            world.playSoundEffect(x + 0.5, y + 1.0, z + 0.5,
                                                    "nyasamarailway:info.gate.beep",
                                                    0.5F, 1.0F
                                            );
                                        }
                                        world.markBlockForUpdate(x, y, z);
                                    }
                                }
                                break;
                            case WEST:
                                if (getGateBase(world, x - 1, y, z) != null) {
                                    if (getGateBase(world, x - 1, y, z).player.equals(player.getDisplayName())) {
                                        if (stack.getItem() instanceof ItemTicketOnce) {
                                            if (gateFront.setOver != ItemTicketBase.getOver(stack))
                                                return true;
                                        }

                                        if (ItemTicketBase.getOver(stack) > 0) {
                                            if (ItemTicketBase.getState(stack)) {
                                                ItemTicketBase.decOver(stack);
                                                if (stack.getItem() instanceof ItemTicketOnce) {
                                                    ItemTicketBase.setOver(stack, 0);
                                                    player.destroyCurrentEquippedItem();
                                                }
                                                getGateBase(world, x - 1, y, z).openDoor();
                                                gateFront.over = ItemTicketBase.getOver(stack);
                                                ItemTicketBase.setState(stack, false);
                                            } else {
                                                getGateBase(world, x - 1, y, z).openDoor();
                                                gateFront.over = ItemTicketBase.getOver(stack);
                                                ItemTicketBase.setState(stack, true);
                                            }
                                            world.playSoundEffect(x + 0.5, y + 1.0, z + 0.5,
                                                    "nyasamarailway:info.gate.beep",
                                                    0.5F, 1.0F
                                            );
                                        }
                                        world.markBlockForUpdate(x, y, z);
                                    }
                                }
                                break;
                            case NORTH:
                                if (getGateBase(world, x, y, z - 1) != null) {
                                    if (getGateBase(world, x, y, z - 1).player.equals(player.getDisplayName())) {
                                        if (stack.getItem() instanceof ItemTicketOnce) {
                                            if (gateFront.setOver != ItemTicketBase.getOver(stack))
                                                return true;
                                        }

                                        if (ItemTicketBase.getOver(stack) > 0) {
                                            if (ItemTicketBase.getState(stack)) {
                                                ItemTicketBase.decOver(stack);
                                                if (stack.getItem() instanceof ItemTicketOnce) {
                                                    ItemTicketBase.setOver(stack, 0);
                                                    player.destroyCurrentEquippedItem();
                                                }
                                                getGateBase(world, x, y, z - 1).openDoor();
                                                gateFront.over = ItemTicketBase.getOver(stack);
                                                ItemTicketBase.setState(stack, false);
                                            } else {
                                                getGateBase(world, x, y, z - 1).openDoor();
                                                gateFront.over = ItemTicketBase.getOver(stack);
                                                ItemTicketBase.setState(stack, true);
                                            }
                                            world.playSoundEffect(x + 0.5, y + 1.0, z + 0.5,
                                                    "nyasamarailway:info.gate.beep",
                                                    0.5F, 1.0F
                                            );
                                        }
                                        world.markBlockForUpdate(x, y, z);
                                    }
                                }
                                break;
                            case EAST:
                                if (getGateBase(world, x + 1, y, z) != null) {
                                    if (getGateBase(world, x + 1, y, z).player.equals(player.getDisplayName())) {
                                        if (stack.getItem() instanceof ItemTicketOnce) {
                                            if (gateFront.setOver != ItemTicketBase.getOver(stack))
                                                return true;
                                        }

                                        if (ItemTicketBase.getOver(stack) > 0) {
                                            if (ItemTicketBase.getState(stack)) {
                                                ItemTicketBase.decOver(stack);
                                                if (stack.getItem() instanceof ItemTicketOnce) {
                                                    ItemTicketBase.setOver(stack, 0);
                                                    player.destroyCurrentEquippedItem();
                                                }
                                                getGateBase(world, x + 1, y, z).openDoor();
                                                gateFront.over = ItemTicketBase.getOver(stack);
                                                ItemTicketBase.setState(stack, false);
                                            } else {
                                                getGateBase(world, x + 1, y, z).openDoor();
                                                gateFront.over = ItemTicketBase.getOver(stack);
                                                ItemTicketBase.setState(stack, true);
                                            }
                                            world.playSoundEffect(x + 0.5, y + 1.0, z + 0.5,
                                                    "nyasamarailway:info.gate.beep",
                                                    0.5F, 1.0F
                                            );
                                        }
                                        world.markBlockForUpdate(x, y, z);
                                    }
                                }
                                break;
                            default:
                                break;
                        }
                    }
                    return true;
                }
            }
        }

        return false;
    }
}
