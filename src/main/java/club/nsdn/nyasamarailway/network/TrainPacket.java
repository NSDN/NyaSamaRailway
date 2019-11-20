package club.nsdn.nyasamarailway.network;

import club.nsdn.nyasamarailway.item.tool.ItemNTP32Bit;
import club.nsdn.nyasamarailway.item.tool.ItemNTP8Bit;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.DimensionManager;

/**
 * Created by drzzm32 on 2019.2.10
 */
public class TrainPacket implements IMessage {

    public int P; public int R; public int Dir; public boolean Mode; public boolean MBlk;

    public int dimensionID;

    public double Velocity; public double nextVelocity;
    public double Yaw; public double prevYaw;

    public TrainPacket() {
        this.P = 0;
        this.R = 0;
        this.Dir = 0;

        this.dimensionID = -2;

        this.Velocity = 0;
        this.nextVelocity = 0;
        this.Yaw = 0;
        this.prevYaw = 0;

        this.Mode = false;
        this.MBlk = false;
    }

    public TrainPacket(int P, int R, int Dir) {
        this();

        this.P = P;
        this.R = R;
        this.Dir = Dir;
    }

    public void fromStack(ItemStack stack) {
        if (stack == null) return;
        if (stack.getItem() instanceof ItemNTP8Bit) {
            ItemNTP8Bit ntp8Bit = (ItemNTP8Bit) stack.getItem();
            this.P = ntp8Bit.power.get(stack);
            this.R = ntp8Bit.brake.get(stack);
            this.Dir = ntp8Bit.dir.get(stack);
            this.Mode = ntp8Bit.mode.get(stack);
            this.MBlk = ntp8Bit.mblk.get(stack);
        } else if (stack.getItem() instanceof ItemNTP32Bit) {
            ItemNTP32Bit ntp32Bit = (ItemNTP32Bit) stack.getItem();
            this.P = ntp32Bit.power.get(stack);
            this.R = ntp32Bit.brake.get(stack);
            this.Dir = ntp32Bit.dir.get(stack);
        }
    }

    public void toStack(ItemStack stack) {
        if (stack == null) return;
        if (stack.getItem() instanceof ItemNTP8Bit) {
            ItemNTP8Bit ntp8Bit = (ItemNTP8Bit) stack.getItem();
            ntp8Bit.power.set(stack, this.P);
            ntp8Bit.brake.set(stack, this.R);
            ntp8Bit.dir.set(stack, this.Dir);
            ntp8Bit.mode.set(stack, this.Mode);
            ntp8Bit.mblk.set(stack, this.MBlk);
        } else if (stack.getItem() instanceof ItemNTP32Bit) {
            ItemNTP32Bit ntp32Bit = (ItemNTP32Bit) stack.getItem();
            ntp32Bit.power.set(stack, this.P);
            ntp32Bit.brake.set(stack, this.R);
            ntp32Bit.dir.set(stack, this.Dir);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.P);
        buf.writeInt(this.R);
        buf.writeInt(this.Dir);
        buf.writeBoolean(this.Mode);
        buf.writeBoolean(this.MBlk);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.P = buf.readInt();
        this.R = buf.readInt();
        this.Dir = buf.readInt();
        this.Mode = buf.readBoolean();
        this.MBlk = buf.readBoolean();
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
            cart = (EntityMinecart) Minecraft.getMinecraft().world.getEntityByID(cartID);
        } catch (Exception e) {
            return null;
        }
        return cart;
    }

}
