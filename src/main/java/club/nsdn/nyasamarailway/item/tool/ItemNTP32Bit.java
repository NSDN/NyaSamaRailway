package club.nsdn.nyasamarailway.item.tool;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.api.cart.AbsLocoBase;
import club.nsdn.nyasamarailway.network.NetworkWrapper;
import club.nsdn.nyasamarailway.network.TrainPacket;
import club.nsdn.nyasamarailway.util.ItemNBTHelper;
import club.nsdn.nyasamarailway.util.TrainController;
import club.nsdn.nyasamatelecom.api.tool.ToolBase;
import club.nsdn.nyasamatelecom.api.util.Util;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.thewdj.physics.Dynamics;

import javax.annotation.Nonnull;
import java.util.ArrayList;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class ItemNTP32Bit extends ToolBase {

    public ItemNBTHelper<Integer> power = new ItemNBTHelper<>(Integer.class, "power");
    public ItemNBTHelper<Integer> brake = new ItemNBTHelper<>(Integer.class, "brake");
    public ItemNBTHelper<Integer> dir = new ItemNBTHelper<>(Integer.class, "dir");
    public ItemNBTHelper<int[]> carts = new ItemNBTHelper<>(int[].class, "carts");

    public ItemNTP32Bit() {
        super(ToolMaterial.IRON);
        setUnlocalizedName("ItemNTP32Bit");
        setRegistryName(NyaSamaRailway.MODID, "ntp_32");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
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
                        if (cart instanceof AbsLocoBase) continue;
                        packet.Velocity = Dynamics.vel(cart.motionX, cart.motionZ);
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
        if (player instanceof EntityPlayerMP) {
            TrainPacket packet = new TrainPacket();
            packet.fromStack(itemStack);
            NetworkWrapper.instance.sendTo(packet, (EntityPlayerMP) player);
        }
    }

    public void addCart(ItemStack itemStack, EntityPlayer player, Entity entity) {
        if (entity instanceof AbsLocoBase) {
            Util.say(player, "info.ntp.error");
            return;
        }

        int[] array = carts.get(itemStack);
        ArrayList<Integer> list = new ArrayList<>();
        for (int i : array) list.add(i);

        int id = entity.getEntityId();
        if (!list.contains(id)) {
            list.add(id);
            Util.say(player, "info.ntp.controlled");
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
        if (player instanceof EntityPlayerMP) {
            TrainPacket packet = new TrainPacket();
            packet.fromStack(itemStack);
            NetworkWrapper.instance.sendTo(packet, (EntityPlayerMP) player);
        }
        Util.say(player, "info.ntp.cleared");
    }

}
