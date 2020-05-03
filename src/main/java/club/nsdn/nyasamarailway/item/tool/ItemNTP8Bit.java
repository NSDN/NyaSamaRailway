package club.nsdn.nyasamarailway.item.tool;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import club.nsdn.nyasamarailway.api.cart.IMobileBlocking;
import club.nsdn.nyasamarailway.api.item.IController;
import club.nsdn.nyasamarailway.creativetab.CreativeTabLoader;
import club.nsdn.nyasamarailway.api.cart.IHighSpeedCart;
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

/**
 * Created by drzzm32 on 2019.2.10
 */
public class ItemNTP8Bit extends ToolBase implements IController {

    public ItemNBTHelper<Integer> power = new ItemNBTHelper<>(Integer.class, "power");
    public ItemNBTHelper<Integer> brake = new ItemNBTHelper<>(Integer.class, "brake");
    public ItemNBTHelper<Integer> dir = new ItemNBTHelper<>(Integer.class, "dir");
    public ItemNBTHelper<Integer> cart = new ItemNBTHelper<>(Integer.class, "cart");

    public ItemNBTHelper<Boolean> mode = new ItemNBTHelper<>(Boolean.class, "mode");
    public ItemNBTHelper<Boolean> mblk = new ItemNBTHelper<>(Boolean.class, "mblk");

    public ItemNTP8Bit() {
        super(ToolMaterial.IRON);
        setUnlocalizedName("ItemNTP8Bit");
        setRegistryName(NyaSamaRailway.MODID, "ntp_8");
        setCreativeTab(CreativeTabLoader.tabNyaSamaRailway);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    @Override
    public void onUpdate(ItemStack itemStack, World world, Entity entity, int index, boolean inHand) {
        boolean inOffHand = false;
        if (entity instanceof EntityPlayer)
            inOffHand = ((EntityPlayer) entity).getHeldItemOffhand() == itemStack;
        if (!world.isRemote && (inHand || inOffHand)) {
            if (entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;

                TrainPacket packet = new TrainPacket();
                packet.P = this.power.get(itemStack);
                packet.R = this.brake.get(itemStack);
                packet.Dir = this.dir.get(itemStack);
                packet.Mode = this.mode.get(itemStack);
                packet.MBlk = this.mblk.get(itemStack);
                packet.dimensionID = player.dimension;

                EntityMinecart cart = packet.getCartInServer(this.cart.get(itemStack));
                if (cart != null) {
                    if (cart instanceof IHighSpeedCart) {
                        ((IHighSpeedCart) cart).setHighSpeedMode(packet.Mode);
                    }
                    if (cart instanceof IMobileBlocking) {
                        ((IMobileBlocking) cart).setBlockingState(packet.MBlk);
                    }
                    if (cart instanceof AbsLocoBase) {
                        ((AbsLocoBase) cart).setTrainPacket(packet);
                        return;
                    }
                    packet.Velocity = Dynamics.vel(cart.motionX, cart.motionZ);
                    TrainController.doMotion(packet, cart);
                }
            }
        }
    }

    @Override
    public void onCreated(ItemStack itemStack, World world, EntityPlayer player) {
        power.set(itemStack, 0); brake.set(itemStack, 5);
        dir.set(itemStack, 0); cart.set(itemStack, -1);
        if (player instanceof EntityPlayerMP) {
            TrainPacket packet = new TrainPacket();
            packet.fromStack(itemStack);
            NetworkWrapper.instance.sendTo(packet, (EntityPlayerMP) player);
        }
    }

    public void addCart(ItemStack itemStack, EntityPlayer player, Entity entity) {
        int id = entity.getEntityId();
        power.set(itemStack, 0); brake.set(itemStack, 5);
        dir.set(itemStack, 0); cart.set(itemStack, id);
        if (player instanceof EntityPlayerMP) {
            TrainPacket packet = new TrainPacket();
            packet.fromStack(itemStack);
            NetworkWrapper.instance.sendTo(packet, (EntityPlayerMP) player);
        }
        Util.say(player, "info.ntp.controlled");
    }

    public void clearCart(ItemStack itemStack, EntityPlayer player) {
        power.set(itemStack, 0); brake.set(itemStack, 5);
        dir.set(itemStack, 0); cart.set(itemStack, -1);
        if (player instanceof EntityPlayerMP) {
            TrainPacket packet = new TrainPacket();
            packet.fromStack(itemStack);
            NetworkWrapper.instance.sendTo(packet, (EntityPlayerMP) player);
        }
        Util.say(player, "info.ntp.cleared");
    }

}
