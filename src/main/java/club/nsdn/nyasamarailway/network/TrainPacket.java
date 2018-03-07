package club.nsdn.nyasamarailway.network;

import club.nsdn.nyasamarailway.item.tool.ItemNTP32Bit;
import club.nsdn.nyasamarailway.item.tool.ItemNTP8Bit;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.DimensionManager;

/**
 * Created by drzzm32 on 2016.5.16.
 */

public class TrainPacket implements IMessage {

    public static class PacketCtSHandler implements IMessageHandler<TrainPacket, IMessage> {
        @Override
        public IMessage onMessage(TrainPacket packet, MessageContext context) {
            EntityPlayerMP serverPlayer = context.getServerHandler().playerEntity;

            ItemStack stack = serverPlayer.getCurrentEquippedItem();
            if (stack != null) {
                if (stack.getItem() instanceof ItemNTP8Bit) {
                    ItemNTP8Bit ntp8Bit = (ItemNTP8Bit) stack.getItem();
                    ntp8Bit.power.set(stack, packet.P);
                    ntp8Bit.brake.set(stack, packet.R);
                    ntp8Bit.dir.set(stack, packet.Dir);
                } else if (stack.getItem() instanceof ItemNTP32Bit) {
                    ItemNTP32Bit ntp32Bit = (ItemNTP32Bit) stack.getItem();
                    ntp32Bit.power.set(stack, packet.P);
                    ntp32Bit.brake.set(stack, packet.R);
                    ntp32Bit.dir.set(stack, packet.Dir);
                }
            }

            return null;
        }
    }

    public static class PacketStCHandler implements IMessageHandler<TrainPacket, IMessage> {
        @Override
        public IMessage onMessage(TrainPacket packet, MessageContext context) {
            return null;
        }
    }

    public int P; public int R; public int Dir;

    public boolean highSpeed; public int dimensionID;

    public double Velocity; public double nextVelocity;
    public double Yaw; public double prevYaw;

    public TrainPacket() {
        this.P = 0;
        this.R = 0;
        this.Dir = 0;

        this.highSpeed = false;
        this.dimensionID = -2;

        this.Velocity = 0;
        this.nextVelocity = 0;
        this.Yaw = 0;
        this.prevYaw = 0;
    }

    public TrainPacket(int P, int R, int Dir) {
        this();

        this.P = P;
        this.R = R;
        this.Dir = Dir;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.P);
        buf.writeInt(this.R);
        buf.writeInt(this.Dir);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.P = buf.readInt();
        this.R = buf.readInt();
        this.Dir = buf.readInt();
    }

    public EntityMinecart getCartInServer(int cartID) {
        if (cartID <= 0) return null;
        if (this.dimensionID < -1) return null;
        EntityMinecart cart;
        try {
            cart = (EntityMinecart) DimensionManager.getWorld(this.dimensionID).getEntityByID(cartID);
        } catch (Exception e) {
            return null;
        }
        return cart;
    }

    @SideOnly(Side.CLIENT)
    public EntityMinecart getCartInClient(int cartID) {
        if (cartID <= 0) return null;
        EntityMinecart cart;
        try {
            cart = (EntityMinecart) Minecraft.getMinecraft().theWorld.getEntityByID(cartID);
        } catch (Exception e) {
            return null;
        }
        return cart;
    }

}
