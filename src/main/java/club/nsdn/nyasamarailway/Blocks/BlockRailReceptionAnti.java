package club.nsdn.nyasamarailway.Blocks;

import club.nsdn.nyasamarailway.Entity.*;
import club.nsdn.nyasamarailway.Items.ItemLoader;
import club.nsdn.nyasamarailway.Items.ItemTrainController32Bit;
import club.nsdn.nyasamarailway.Items.ItemTrainController8Bit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailPowered;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import org.thewdj.physics.Dynamics;

/**
 * Created by drzzm32 on 2016.5.22.
 */

public class BlockRailReceptionAnti extends BlockRailPoweredBase implements IRailDirectional, ITileEntityProvider {

    public static class TileEntityRailReceptionAnti extends TileEntity {

        public String cartType = "";

        @Override
        public void writeToNBT(NBTTagCompound tagCompound) {
            super.writeToNBT(tagCompound);
            tagCompound.setString("cartType", cartType);
        }

        @Override
        public void readFromNBT(NBTTagCompound tagCompound) {
            super.readFromNBT(tagCompound);
            cartType = tagCompound.getString("cartType");
        }
    }

    public TileEntity createNewTileEntity(World world, int i) {
        return new TileEntityRailReceptionAnti();
    }

    public BlockRailReceptionAnti() {
        super("BlockRailReceptionAnti");
        setTextureName("rail_reception_anti");
    }

    public boolean isForward() {
        return false;
    }

    public boolean checkNearbySameRail(World world, int x, int y, int z) {
        return world.getBlock(x + 1, y, z) == this || world.getBlock(x - 1, y, z) == this ||
                world.getBlock(x, y, z + 1) == this || world.getBlock(x, y, z - 1) == this;
    }

    public void setRailVerticalOutput(World world, int x, int y, int z, boolean value) {
        int meta = world.getBlockMetadata(x, y, z);
        if (value) {
            if (getRailDirection(world, x, y, z) == RailDirection.NS) {
                world.setBlockMetadataWithNotify(x - 1, y, z, meta | 8, 3);
                world.setBlockMetadataWithNotify(x + 1, y, z, meta | 8, 3);
                world.setBlockMetadataWithNotify(x, y - 1, z, meta | 8, 3);
            } else {
                world.setBlockMetadataWithNotify(x, y, z - 1, meta | 8, 3);
                world.setBlockMetadataWithNotify(x, y, z + 1, meta | 8, 3);
                world.setBlockMetadataWithNotify(x, y - 1, z, meta | 8, 3);
            }
        } else {
            if (getRailDirection(world, x, y, z) == RailDirection.NS) {
                world.setBlockMetadataWithNotify(x - 1, y, z, meta & 7, 3);
                world.setBlockMetadataWithNotify(x + 1, y, z, meta & 7, 3);
                world.setBlockMetadataWithNotify(x, y - 1, z, meta & 7, 3);
            } else {
                world.setBlockMetadataWithNotify(x, y, z - 1, meta & 7, 3);
                world.setBlockMetadataWithNotify(x, y, z + 1, meta & 7, 3);
                world.setBlockMetadataWithNotify(x, y - 1, z, meta & 7, 3);
            }
        }

    }

    @Override
    public void onMinecartPass(World world, EntityMinecart cart, int x, int y, int z) {
        boolean playerDetectable = false;
        boolean hasPlayer = false;
        if (!checkNearbySameRail(world, x, y, z)) playerDetectable = true;
        if (cart.riddenByEntity instanceof EntityPlayer) {
            ItemStack stack = ((EntityPlayer) cart.riddenByEntity).getCurrentEquippedItem();
            if (stack != null) {
                if (stack.getItem() instanceof ItemTrainController8Bit ||
                        stack.getItem() instanceof ItemTrainController32Bit) {
                    return;
                }
            }
            hasPlayer = true;
        }

        double maxV;
        if (!playerDetectable) {
            maxV = 0.1;
            if (world.getBlockMetadata(x, y, z) >= 8) {
                if (getRailDirection(world, x, y, z) == RailDirection.NS) {
                    if (cart.motionZ < -maxV) { //cart.motionZ < -maxV
                        if (cart.motionZ > -maxV * 1.5) cart.motionZ = -maxV * 1.5;
                    } else {
                        if (cart.motionZ <= 0) cart.motionZ = 0.005;
                        if (cart.motionZ < maxV)
                            cart.motionZ = Dynamics.LocoMotions.calcVelocityUp(Math.abs(cart.motionZ), 0.1, 1.0, 0.1, 0.02);
                    }
                } else {
                    if (cart.motionX > maxV) { //cart.motionX > maxV
                        if (cart.motionX < maxV * 1.5) cart.motionX = maxV * 1.5;
                    } else {
                        if (cart.motionX >= 0) cart.motionX = -0.005;
                        if (cart.motionX > -maxV)
                            cart.motionX = -Dynamics.LocoMotions.calcVelocityUp(Math.abs(cart.motionX), 0.1, 1.0, 0.1, 0.02);
                    }
                }
            } else {
                if (Math.abs(cart.motionX) > maxV || Math.abs(cart.motionZ) > maxV) {
                    cart.motionX = Math.signum(cart.motionX) * Dynamics.LocoMotions.calcVelocityDown(Math.abs(cart.motionX), 0.1, 1.0, 1.0, 1.0, 0.05, 0.02);
                    cart.motionZ = Math.signum(cart.motionZ) * Dynamics.LocoMotions.calcVelocityDown(Math.abs(cart.motionZ), 0.1, 1.0, 1.0, 1.0, 0.05, 0.02);
                } else {
                    if (getRailDirection(world, x, y, z) == RailDirection.NS) {
                        cart.motionZ = 0;
                    } else {
                        cart.motionX = 0;
                    }
                }
            }
        } else {
            maxV = 0.2;
            TileEntityRailReceptionAnti rail = null;
            if (world.getTileEntity(x, y, z) instanceof TileEntityRailReceptionAnti) {
                rail = (TileEntityRailReceptionAnti) world.getTileEntity(x, y, z);
            }
            if (rail != null) {
                if (rail.cartType.isEmpty() && (cart.motionX * cart.motionX + cart.motionZ * cart.motionZ == 0)) rail.cartType = cart.getClass().getName();
            }
            if (!world.isBlockIndirectlyGettingPowered(x, y, z)) {
                if (hasPlayer) {
                    if (getRailDirection(world, x, y, z) == RailDirection.NS) {
                        if (cart.motionZ < -maxV) { //cart.motionZ < -maxV
                            if (cart.motionZ > -maxV * 1.5) cart.motionZ = -maxV * 1.5;
                        } else {
                            if (cart.motionZ <= 0) cart.motionZ = 0.005;
                            if (cart.motionZ < maxV)
                                cart.motionZ = Dynamics.LocoMotions.calcVelocityUp(Math.abs(cart.motionZ), 0.1, 1.0, 0.1, 0.02);
                        }
                    } else {
                        if (cart.motionX > maxV) { //cart.motionX > maxV
                            if (cart.motionX < maxV * 1.5) cart.motionX = maxV * 1.5;
                        } else {
                            if (cart.motionX >= 0) cart.motionX = -0.005;
                            if (cart.motionX > -maxV)
                                cart.motionX = -Dynamics.LocoMotions.calcVelocityUp(Math.abs(cart.motionX), 0.1, 1.0, 0.1, 0.02);
                        }
                    }
                }
            } else {
                if (cart.motionX * cart.motionX + cart.motionZ * cart.motionZ > 0) {
                    if (getRailDirection(world, x, y, z) == RailDirection.NS) {
                        if (cart.posZ - 0.5 < z) {
                            cart.setDead();
                            world.removeEntity(cart);
                        }
                    } else {
                        if (cart.posX - 0.5 > x) {
                            cart.setDead();
                            world.removeEntity(cart);
                        }
                    }
                }
            }
        }

    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        TileEntityRailReceptionAnti rail = null;
        if (world.getTileEntity(x, y, z) instanceof TileEntityRailReceptionAnti) {
            rail = (TileEntityRailReceptionAnti) world.getTileEntity(x, y, z);
        }
        if (rail != null) {
            if (player.isSneaking() && !world.isRemote) {
                rail.cartType = "";
                player.addChatComponentMessage(new ChatComponentTranslation("info.reception.cleared"));
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRailPowered(World world, int x, int y, int z, int meta, boolean hasCart) {
        boolean playerDetectable = false;
        if (!checkNearbySameRail(world, x, y, z)) playerDetectable = true;
        if (playerDetectable) {
            TileEntityRailReceptionAnti rail = null;
            if (world.getTileEntity(x, y, z) instanceof TileEntityRailReceptionAnti) {
                rail = (TileEntityRailReceptionAnti) world.getTileEntity(x, y, z);
            }
            if (rail != null) {
                if (!rail.cartType.isEmpty() && !hasCart && !world.isRemote) {
                    if (rail.cartType.equals(MinecartBase.class.getName())) {
                        MinecartBase cart = new MinecartBase(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);
                        world.spawnEntityInWorld(cart);
                    } else if (rail.cartType.equals(NSBT1.class.getName())) {
                        MinecartBase cart = new NSBT1(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);
                        world.spawnEntityInWorld(cart);
                    } else if (rail.cartType.equals(NSPCT1.class.getName())) {
                        MinecartBase cart = new NSPCT1(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);
                        world.spawnEntityInWorld(cart);
                    } else if (rail.cartType.equals(NSPCT2.class.getName())) {
                        MinecartBase cart = new NSPCT2(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);
                        world.spawnEntityInWorld(cart);
                    } else {
                        EntityMinecart cart = EntityMinecartEmpty.createMinecart(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5, -1);
                        world.spawnEntityInWorld(cart);
                    }
                }
            }
        }

    }

    @Override
    protected boolean func_150057_a(World world, int x, int y, int z, boolean bool, int r, int prevBaseMeta)
    {
        Block block = world.getBlock(x, y, z);

        if (block == this || block instanceof BlockRailSignalTransfer)
        {
            int meta = world.getBlockMetadata(x, y, z);
            int baseMeta = meta & 7;

            if (prevBaseMeta == 1 && (baseMeta == 0 || baseMeta == 4 || baseMeta == 5))
            {
                return false;
            }

            if (prevBaseMeta == 0 && (baseMeta == 1 || baseMeta == 2 || baseMeta == 3))
            {
                return false;
            }

            if ((meta & 8) != 0)
            {
                if (world.isBlockIndirectlyGettingPowered(x, y, z))
                {
                    return true;
                }

                return this.func_150058_a(world, x, y, z, meta, bool, r + 1);
            }
        }

        return false;
    }
}
