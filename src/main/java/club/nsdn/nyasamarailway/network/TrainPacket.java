package club.nsdn.nyasamarailway.network;

import club.nsdn.nyasamarailway.event.ToolHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.DimensionManager;

import java.util.*;

/**
 * Created by drzzm32 on 2016.5.16.
 */

public class TrainPacket implements IMessage {

    public static class PacketCtSHandler implements IMessageHandler<TrainPacket, IMessage> {
        @Override
        public IMessage onMessage(TrainPacket packet, MessageContext context) {
            EntityPlayerMP serverPlayer = context.getServerHandler().playerEntity;

            if (packet.isUnits) {
                if (ToolHandler.controller32Bit == null) {
                    ToolHandler.controller32Bit = new TrainPacket();
                }
                ToolHandler.controller32Bit.isUnits = true;
                ToolHandler.controller32Bit.dimensionID = serverPlayer.dimension;
                ToolHandler.controller32Bit.playerID = serverPlayer.getEntityId();
                ToolHandler.controller32Bit.P = packet.P;
                ToolHandler.controller32Bit.R = packet.R;
                ToolHandler.controller32Bit.Dir = packet.Dir;
            } else {
                if (ToolHandler.controller8Bit == null) {
                    ToolHandler.controller8Bit = new TrainPacket();
                }
                ToolHandler.controller8Bit.isUnits = false;
                ToolHandler.controller8Bit.dimensionID = serverPlayer.dimension;
                ToolHandler.controller8Bit.playerID = serverPlayer.getEntityId();
                ToolHandler.controller8Bit.P = packet.P;
                ToolHandler.controller8Bit.R = packet.R;
                ToolHandler.controller8Bit.Dir = packet.Dir;
            }

            return null;
        }
    }

    public static class PacketStCHandler implements IMessageHandler<TrainPacket, IMessage> {
        @Override
        public IMessage onMessage(TrainPacket packet, MessageContext context) {
            if (packet.isUnits) {

                if (ToolHandler.controller32Bit == null) {
                    ToolHandler.controller32Bit = new TrainPacket();
                }
                ToolHandler.controller32Bit.isUnits = true;
                if (packet.cartID > 0) {
                    if (ToolHandler.controller32Bit.trainUnits.isEmpty()) {
                        ToolHandler.controller32Bit.trainUnits.add(packet.cartID);
                        ToolHandler.controller32Bit.dimensionID = packet.dimensionID;
                        ToolHandler.controller32Bit.cartID = packet.cartID;
                        ToolHandler.controller32Bit.P = packet.P;
                        ToolHandler.controller32Bit.R = packet.R;
                        ToolHandler.controller32Bit.Dir = packet.Dir;
                    } else {
                        if (!ToolHandler.controller32Bit.trainUnits.contains(packet.cartID))
                            ToolHandler.controller32Bit.trainUnits.add(packet.cartID);
                    }
                } else {
                    ToolHandler.controller32Bit.trainUnits.clear();
                }

            } else {
                ToolHandler.controller8Bit = new TrainPacket();
                ToolHandler.controller8Bit.isUnits = false;
                if (packet.cartID > 0) {
                    ToolHandler.controller8Bit.dimensionID = packet.dimensionID;
                    ToolHandler.controller8Bit.cartID = packet.cartID;
                    ToolHandler.controller8Bit.P = packet.P;
                    ToolHandler.controller8Bit.R = packet.R;
                    ToolHandler.controller8Bit.Dir = packet.Dir;
                }
            }

            return null;
        }
    }

    public LinkedList<Integer> trainUnits;
    public boolean isUnits;

    public int dimensionID;
    public int playerID;
    public int cartID;
    public int P;
    public int R;
    public int Dir;
    public double Velocity;
    public double nextVelocity;
    public double Yaw;
    public double prevYaw;

    public TrainPacket() {
        this.trainUnits = new LinkedList<Integer>();
        this.isUnits = false;
        this.dimensionID = -2;
        this.playerID = -1;
        this.cartID = -1;
        this.P = 0;
        this.R = 0;
        this.Dir = 0;
        this.Velocity = 0;
        this.nextVelocity = 0;
        this.Yaw = 0;
        this.prevYaw = 0;
    }

    public TrainPacket(int playerID, int cartID, int P, int R, int Dir) {
        this.trainUnits = new LinkedList<Integer>();
        this.isUnits = false;
        this.dimensionID = -2;
        this.playerID = playerID;
        this.cartID = cartID;
        this.P = P;
        this.R = R;
        this.Dir = Dir;
        this.Velocity = 0;
        this.nextVelocity = 0;
        this.Yaw = 0;
        this.prevYaw = 0;
    }

    public TrainPacket(int cartID, int P, int R, int Dir) {
        this.trainUnits = new LinkedList<Integer>();
        this.isUnits = false;
        this.dimensionID = -2;
        this.cartID = cartID;
        this.P = P;
        this.R = R;
        this.Dir = Dir;
        this.Velocity = 0;
        this.nextVelocity = 0;
        this.Yaw = 0;
        this.prevYaw = 0;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.isUnits);
        buf.writeInt(this.dimensionID);
        buf.writeInt(this.cartID);
        buf.writeInt(this.P);
        buf.writeInt(this.R);
        buf.writeInt(this.Dir);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.isUnits = buf.readBoolean();
        this.dimensionID = buf.readInt();
        this.cartID = buf.readInt();
        this.P = buf.readInt();
        this.R = buf.readInt();
        this.Dir = buf.readInt();
    }

    public EntityPlayerMP getPlayerInServer() {
        if (this.playerID <= 0) return null;
        if (this.dimensionID < -1) return null;
        EntityPlayerMP player;
        try {
            player = (EntityPlayerMP) DimensionManager.getWorld(this.dimensionID).getEntityByID(this.playerID);
        } catch (Exception e) {
            return null;
        }
        return player;
    }

    public EntityMinecart getCartInServer() {
        if (this.cartID <= 0) return null;
        if (this.dimensionID < -1) return null;
        EntityMinecart cart;
        try {
            cart = (EntityMinecart) DimensionManager.getWorld(this.dimensionID).getEntityByID(this.cartID);
        } catch (Exception e) {
            return null;
        }
        return cart;
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
    public EntityMinecart getCartInClient() {
        if (this.cartID <= 0) return null;
        EntityMinecart cart;
        try {
            cart = (EntityMinecart) Minecraft.getMinecraft().theWorld.getEntityByID(this.cartID);
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
