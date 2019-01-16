package club.nsdn.nyasamatelecom.api.tool;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2018.12.13.
 */
public class ToolBase extends Item {

    public ToolMaterial toolMaterial;

    public ToolBase(ToolMaterial toolMaterial) {
        super();
        setMaxStackSize(1);
        setMaxDamage(toolMaterial.getMaxUses());
        this.toolMaterial = toolMaterial;
    }

    public boolean hitEntity(ItemStack itemStack, EntityLivingBase player, EntityLivingBase target) {
        itemStack.damageItem(2, target);
        return true;
    }

    public boolean onBlockDestroyed(ItemStack itemStack, World world, IBlockState state, BlockPos pos, EntityLivingBase player) {
        if (!world.isRemote && (double)state.getBlockHardness(world, pos) != 0.0D) {
            itemStack.damageItem(1, player);
        }
        return true;
    }

    @SideOnly(Side.CLIENT)
    public boolean isFull3D() {
        return true;
    }

    public int getItemEnchantability() {
        return this.toolMaterial.getEnchantability();
    }


    public boolean getIsRepairable(ItemStack thisStack, ItemStack itemStack) {
        ItemStack mat = this.toolMaterial.getRepairItemStack();
        return !mat.isEmpty() && OreDictionary.itemMatches(mat, itemStack, false) || super.getIsRepairable(thisStack, itemStack);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        return false;
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack itemStack, EntityPlayer player, EntityLivingBase entity, EnumHand hand) {
        return false;
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, EnumHand hand) {
        return EnumActionResult.PASS;
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack itemStack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return false;
    }

    @Override
    public void onUpdate(ItemStack itemStack, World world, Entity entity, int index, boolean inHand) {

    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
        return new ActionResult<>(EnumActionResult.PASS, player.getHeldItemMainhand());
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return EnumActionResult.PASS;
    }

    public void updateTileEntity(TileEntity tileEntity) {
        if (tileEntity == null) return;
        tileEntity.getWorld().notifyBlockUpdate(
            tileEntity.getPos(),
            tileEntity.getBlockType().getDefaultState(),
            tileEntity.getBlockType().getDefaultState(),
            2
        );
    }

}
