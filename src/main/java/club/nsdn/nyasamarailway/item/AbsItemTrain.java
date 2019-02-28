package club.nsdn.nyasamarailway.item;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.api.cart.AbsTrainBase;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2019.2.27
 */
public abstract class AbsItemTrain extends Item {

    public final Class<? extends AbsTrainBase> trainClass;

    public AbsItemTrain(Class<? extends AbsTrainBase> trainClass, String name, String id) {
        super();
        this.trainClass = trainClass;
        setMaxStackSize(64);
        setUnlocalizedName(name);
        setRegistryName(NyaSamaRailway.MODID, id);
    }

    public abstract void doSpawn(World world, double x, double y, double z, EntityPlayer player, EnumFacing facing);

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState state = world.getBlockState(pos);
        if (!BlockRailBase.isRailBlock(state)) {
            return EnumActionResult.FAIL;
        } else {
            ItemStack stack = player.getHeldItem(hand);
            if (!world.isRemote && state.getBlock() instanceof BlockRailBase) {
                BlockRailBase.EnumRailDirection dir = ((BlockRailBase) state.getBlock()).getRailDirection(world, pos, state, (EntityMinecart) null);
                EnumFacing.Axis axis = dir == BlockRailBase.EnumRailDirection.NORTH_SOUTH ? EnumFacing.Axis.Z : EnumFacing.Axis.X;
                if (player.getHorizontalFacing().getAxis() != axis) {
                    player.sendMessage(new TextComponentString(TextFormatting.AQUA + "[NSR] Incorrect Facing"));
                    return EnumActionResult.FAIL;
                }
                doSpawn(world, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.0625D, (double) pos.getZ() + 0.5D, player, player.getHorizontalFacing());
            }

            stack.shrink(1);
            return EnumActionResult.SUCCESS;
        }
    }

}
