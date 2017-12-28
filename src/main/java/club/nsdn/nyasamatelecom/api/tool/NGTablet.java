package club.nsdn.nyasamatelecom.api.tool;

import club.nsdn.nyasamatelecom.api.tool.util.GuiNGTablet;
import club.nsdn.nyasamatelecom.api.util.NSASM;
import club.nsdn.nyasamatelecom.api.util.Util;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import java.util.LinkedHashMap;

/**
 * Created by drzzm32 on 2017.12.28.
 */
public class NGTablet extends ItemWritableBook {

    private final String modid;
    private final SimpleNetworkWrapper wrapper;

    public NGTablet(String modid, SimpleNetworkWrapper wrapper, String name, String icon) {
        super();
        this.modid = modid.toLowerCase();
        this.wrapper = wrapper;

        setUnlocalizedName(name);
        setMaxStackSize(1);
        setTextureName(this.modid + ":" + icon);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            runNGT(itemStack, world, player);
        } else {
            showGUI(itemStack, world, player);
        }

        return itemStack;
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
            Minecraft.getMinecraft().displayGuiScreen(new GuiNGTablet(this.modid, "textures/items/ngt-gui.png", wrapper, itemStack));
        }
    }

}
