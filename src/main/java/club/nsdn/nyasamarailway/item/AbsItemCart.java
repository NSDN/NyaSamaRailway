package club.nsdn.nyasamarailway.item;


import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.api.cart.IExtendedInfoCart;
import club.nsdn.nyasamarailway.util.ExtInfoCore;
import club.nsdn.nyasamatelecom.api.tool.NGTablet;
import club.nsdn.nyasamatelecom.api.util.NSASM;
import club.nsdn.nyasamatelecom.api.util.Util;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2019.2.10
 */
public abstract class AbsItemCart extends Item {

    public final Class<? extends EntityMinecart> cartClass;

    public AbsItemCart(Class<? extends EntityMinecart> cartClass, String name, String id) {
        super();
        this.cartClass = cartClass;
        setMaxStackSize(64);
        setUnlocalizedName(name);
        setRegistryName(NyaSamaRailway.MODID, id);
    }

    public boolean shouldSelfSpawn() {
        return false;
    }

    public void selfSpawn(World world, double x, double y, double z, String name, EntityPlayer player) {

    }

    public abstract EntityMinecart getCart(World world, double x, double y, double z);

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState state = world.getBlockState(pos);
        if (!BlockRailBase.isRailBlock(state)) {
            return EnumActionResult.FAIL;
        } else {
            ItemStack stack = player.getHeldItem(hand);
            if (!world.isRemote) {
                BlockRailBase.EnumRailDirection dir = state.getBlock() instanceof BlockRailBase ? ((BlockRailBase) state.getBlock()).getRailDirection(world, pos, state, (EntityMinecart) null) : BlockRailBase.EnumRailDirection.NORTH_SOUTH;
                double offsetY = 0.0D;
                if (dir.isAscending()) {
                    offsetY = 0.5D;
                }

                if (shouldSelfSpawn()) {
                    if (stack.hasDisplayName())
                        selfSpawn(world, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.0625D + offsetY, (double) pos.getZ() + 0.5D, stack.getDisplayName(), player);
                    else
                        selfSpawn(world, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.0625D + offsetY, (double) pos.getZ() + 0.5D, "", player);
                } else {
                    EntityMinecart cart = getCart(world, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.0625D + offsetY, (double)pos.getZ() + 0.5D);

                    if (cart instanceof IExtendedInfoCart) {
                        double x = (double)pos.getX() + 0.5D;
                        double y = (double)pos.getY() + 0.0625D + offsetY;
                        double z = (double)pos.getZ() + 0.5D;

                        ItemStack ngt;
                        for (int i = 0; i < 9; i++) {
                            ngt = player.inventory.mainInventory.get(i);
                            if (ngt == ItemStack.EMPTY) continue;
                            if (ngt.getItem() instanceof NGTablet) {
                                NBTTagList list = Util.getTagListFromNGT(ngt);

                                if (list != null) {
                                    String[][] code = NSASM.getCode(list);

                                    new ExtInfoCore(code) {
                                        @Override
                                        public World getWorld() {
                                            return world;
                                        }

                                        @Override
                                        public double getX() {
                                            return x;
                                        }

                                        @Override
                                        public double getY() {
                                            return y;
                                        }

                                        @Override
                                        public double getZ() {
                                            return z;
                                        }

                                        @Override
                                        public EntityPlayer getPlayer() {
                                            return player;
                                        }

                                        @Override
                                        public IExtendedInfoCart getCart() {
                                            return (IExtendedInfoCart) cart;
                                        }
                                    }.run();

                                    break;
                                }
                            }
                        }
                    }

                    if (stack.hasDisplayName()) {
                        cart.setCustomNameTag(stack.getDisplayName());
                    }

                    world.spawnEntity(cart);
                }
            }

            stack.shrink(1);
            return EnumActionResult.SUCCESS;
        }
    }

}
