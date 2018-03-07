package club.nsdn.nyasamarailway.item.tool;

import club.nsdn.nyasamarailway.entity.LocoBase;
import club.nsdn.nyasamarailway.extmod.Traincraft;
import club.nsdn.nyasamarailway.network.TrainPacket;
import club.nsdn.nyasamarailway.util.ItemNBTHelper;
import club.nsdn.nyasamarailway.util.TrainController;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

import java.util.ArrayList;

/**
 * Created by drzzm32 on 2016.5.9.
 */
public class ItemNTP32Bit extends ItemToolBase {

    public ItemNBTHelper<Integer> power = new ItemNBTHelper<>(Integer.class, "power");
    public ItemNBTHelper<Integer> brake = new ItemNBTHelper<>(Integer.class, "brake");
    public ItemNBTHelper<Integer> dir = new ItemNBTHelper<>(Integer.class, "dir");
    public ItemNBTHelper<int[]> carts = new ItemNBTHelper<>(int[].class, "carts");

    public ItemNTP32Bit() {
        super(ToolMaterial.IRON);
        setUnlocalizedName("ItemNTP32Bit");
        setTexName("ntp-32");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        player.setItemInUse(itemStack, this.getMaxItemUseDuration(itemStack));
        return itemStack;
    }

    @Override
    public void onUpdate(ItemStack itemStack, World world, Entity entity, int index, boolean inHand) {
        if (!world.isRemote && inHand) {
            if (entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;

                TrainPacket packet = new TrainPacket();
                packet.P = this.power.get(itemStack);
                packet.R = this.brake.get(itemStack);
                packet.Dir = this.dir.get(itemStack);
                packet.dimensionID = player.dimension;

                int[] carts = this.carts.get(itemStack);
                if (carts.length == 1 && carts[0] == -1)
                    return;
                EntityMinecart cart;
                for (int i : carts) {
                    cart = packet.getCartInServer(i);
                    if (cart != null) {
                        if (cart instanceof LocoBase) continue;
                        if (Traincraft.getInstance() != null) {
                            if (Traincraft.instance.isLocomotive(cart)) {
                                Traincraft.instance.Locomotive_setIsLocoTurnedOn(cart, true);
                            }
                        }
                        TrainController.doMotion(packet, cart);
                    }
                }
            }
        }
    }

    @Override
    public void onCreated(ItemStack itemStack, World world, EntityPlayer player) {
        int[] array = new int[] { -1 };
        power.set(itemStack, 0); brake.set(itemStack, 5);
        dir.set(itemStack, 0); carts.set(itemStack, array);
    }

    public void addCart(ItemStack itemStack, EntityPlayer player, Entity entity) {
        if (entity instanceof LocoBase) {
            player.addChatComponentMessage(new ChatComponentTranslation("info.ntp.error"));
            return;
        }

        int[] array = carts.get(itemStack);
        ArrayList<Integer> list = new ArrayList<>();
        for (int i : array) list.add(i);

        int id = entity.getEntityId();
        if (!list.contains(id)) {
            list.add(id);
            player.addChatComponentMessage(new ChatComponentTranslation("info.ntp.controlled"));
        }

        array = new int[list.size()];
        for (int i = 0; i < list.size(); i++)
            array[i] = list.get(i);
        carts.set(itemStack, array);
    }

    public void clearCart(ItemStack itemStack, EntityPlayer player) {
        int[] array = new int[] { -1 };
        power.set(itemStack, 0); brake.set(itemStack, 5);
        dir.set(itemStack, 0); carts.set(itemStack, array);
        player.addChatComponentMessage(new ChatComponentTranslation("info.ntp.cleared"));
    }

}
