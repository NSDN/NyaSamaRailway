package club.nsdn.nyasamarailway.Items;

import club.nsdn.nyasamarailway.CreativeTab.CreativeTabLoader;
import club.nsdn.nyasamarailway.Util.GuiNGT;
import club.nsdn.nyasamarailway.Util.NSASM;
import club.nsdn.nyasamarailway.Util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import java.util.LinkedHashMap;

/**
 * Created by drzzm32 on 2016.10.25.
 */
public class ItemNGT extends ItemWritableBook {

    public ItemNGT() {
        super();
        setUnlocalizedName("ItemNGT");
        setMaxStackSize(1);
        setTextureName("nyasamarailway" + ":" + "item_ngt");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
        if (!Util.loadIf()) setCreativeTab(null);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        if (world.isRemote) {
            if (player.isSneaking()) {
                NBTTagList list = Util.getTagListFromNGT(itemStack);
                if (list == null) return itemStack;
                String[][] code = NSASM.getCode(list);
                new NSASM(code) {
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
                        return player.posY;
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
            } else {
                Minecraft.getMinecraft().displayGuiScreen(new GuiNGT(player, itemStack));
            }
        }
        return itemStack;
    }

}
