package club.nsdn.nyasamatelecom.api.tool;

import club.nsdn.nyasamatelecom.api.tool.util.GuiNGTablet;
import club.nsdn.nyasamatelecom.api.util.NSASM;
import club.nsdn.nyasamatelecom.api.util.Util;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;

/**
 * Created by drzzm32 on 2018.12.13.
 */
public class NGTablet extends ItemWritableBook {

    private final String modid;
    private final SimpleNetworkWrapper wrapper;

    public NGTablet(String modid, SimpleNetworkWrapper wrapper, String name, String id) {
        super();
        this.modid = modid.toLowerCase();
        this.wrapper = wrapper;

        setMaxStackSize(1);
        setUnlocalizedName(name);
        setRegistryName(modid, id);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
        if (!world.isRemote) {
            runNGT(player.getHeldItemMainhand(), world, player);
        } else {
            showGUI(player.getHeldItemMainhand(), world, player);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItemMainhand());
    }

    public SimpleNetworkWrapper getNetworkWrapper() {
        return null;
    }

    public void runNGT(ItemStack itemStack, World world, EntityPlayer player) {
        if (player.isSneaking()) {
            NBTTagList list = Util.getTagListFromNGT(itemStack);
            if (list == null) return;
            String[][] code = NSASM.getCode(list);
            new NSASM(code) {
                @Override
                public SimpleNetworkWrapper getWrapper() {
                    return getNetworkWrapper();
                }

                @Override
                public World getWorld() {
                    return world;
                }

                @Override
                public double getX() {
                    return player.posX;
                }

                @Override
                public double getY() {
                    return player.posY + 1.5;
                }

                @Override
                public double getZ() {
                    return player.posZ;
                }

                @Override
                public EntityPlayer getPlayer() {
                    return player;
                }

                @Override
                public void loadFunc(LinkedHashMap<String, Operator> funcList) {
                    funcList.put("rnd", ((dst, src) -> {
                        if (src != null) return Result.ERR;
                        if (dst == null) return Result.ERR;
                        if (dst.readOnly) return Result.ERR;

                        dst.type = RegType.INT;
                        dst.data = Math.round(Math.random() * 255);
                        return Result.OK;
                    }));
                }
            }.run();
        }
    }

    @SideOnly(Side.CLIENT)
    public void showGUI(ItemStack itemStack, World world, EntityPlayer player) {
        if (!player.isSneaking()) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiNGTablet(this.modid, "textures/item/ngt_gui.png", wrapper, itemStack));
        }
    }

}
