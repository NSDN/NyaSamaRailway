package club.nsdn.nyasamarailway.item.tool;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamatelecom.api.tool.ToolBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.thewdj.linkage.api.IToolCrowbar;

/**
 * Created by drzzm32 on 2019.2.22
 */
public class Item74HC04 extends ToolBase implements IToolCrowbar {

    public Item74HC04() {
        super(ToolMaterial.IRON);
        setUnlocalizedName("Item74HC04");
        setRegistryName(NyaSamaRailway.MODID, "74hc04");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return true;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        IBlockState state = world.getBlockState(pos);

        if (state.getBlock().isAir(state, world, pos))
            return EnumActionResult.PASS;

        if (state.getBlock().rotateBlock(world, pos, facing)) {
            player.swingArm(hand);
            stack.damageItem(1, player);
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }

    /**
     * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise
     * the damage on the stack.
     */
    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        stack.damageItem(1, attacker);
        return true;
    }

    @Override
    public boolean canWhack(EntityPlayer player, EnumHand hand, ItemStack crowbar, BlockPos pos) {
        return true;
    }

    @Override
    public void onWhack(EntityPlayer player, EnumHand hand, ItemStack crowbar, BlockPos pos) {
        crowbar.damageItem(1, player);
        player.swingArm(hand);
    }

    @Override
    public boolean canLink(EntityPlayer player, EnumHand hand, ItemStack crowbar, EntityMinecart cart) {
        return player.isSneaking();
    }

    @Override
    public void onLink(EntityPlayer player, EnumHand hand, ItemStack crowbar, EntityMinecart cart) {
        crowbar.damageItem(1, player);
        player.swingArm(hand);
    }

    @Override
    public boolean canBoost(EntityPlayer player, EnumHand hand, ItemStack crowbar, EntityMinecart cart) {
        return !player.isSneaking();
    }

    @Override
    public void onBoost(EntityPlayer player, EnumHand hand, ItemStack crowbar, EntityMinecart cart) {
        player.swingArm(hand);
    }

}
