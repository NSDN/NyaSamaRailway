package club.nsdn.nyasamatelecom.api.tool;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by drzzm32 on 2017.12.28.
 */
public class ToolBase extends Item {

    protected ToolMaterial toolMaterial;

    public ToolBase(ToolMaterial material) {
        super();
        this.toolMaterial = material;
        this.setMaxStackSize(1);
        this.setMaxDamage(material.getMaxUses());
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        return false;
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack itemStack, EntityPlayer player, EntityLivingBase entity) {
        return false;
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        return false;
    }

        @Override
    public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player) {
        return false;
    }

    @Override
    public void onUpdate(ItemStack itemStack, World world, Entity entity, int index, boolean inHand) {

    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        return itemStack;
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float px, float py, float pz) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public boolean isFull3D() {
        return true;
    }

    public String getToolMaterialName() {
        return this.toolMaterial.toString();
    }

    public void updateTileEntity(TileEntity tileEntity) {
        if (tileEntity == null) return;
        tileEntity.getWorldObj().markBlockForUpdate(
                tileEntity.xCoord,
                tileEntity.yCoord,
                tileEntity.zCoord
        );
    }

}
