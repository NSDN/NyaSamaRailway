package club.nsdn.nyasamarailway.entity.loco;

import club.nsdn.nyasamarailway.api.cart.IBogie;
import club.nsdn.nyasamarailway.api.cart.nsc.AbsNSCxBM;
import club.nsdn.nyasamarailway.network.TrainPacket;
import club.nsdn.nyasamarailway.util.TrainController;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Created by drzzm32 on 2019.3.21
 */
public class NSBT6M extends AbsNSCxBM implements IBogie {

    protected UUID baseCart = UUID.randomUUID();
    protected boolean isLong = false;

    @Override
    protected void readEntityFromNBT(NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);
        this.baseCart = tagCompound.getUniqueId("baseCart");
        this.isLong = tagCompound.getBoolean("isLong");
    }

    @Override
    protected void writeEntityToNBT(@Nonnull NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setUniqueId("baseCart", this.baseCart);
        tagCompound.setBoolean("isLong", this.isLong);
    }

    @Override
    public UUID getBaseCartID() {
        return baseCart;
    }

    @Override
    public Entity getBaseCart() {
        if (world instanceof WorldServer)
            return ((WorldServer) world).getEntityFromUuid(baseCart);
        return null;
    }

    @Override
    public void setBaseCart(Entity baseCart) {
        this.baseCart = baseCart.getUniqueID();
    }

    public void setLong(boolean state) { this.isLong = state; }

    public NSBT6M(World world) {
        super(world);
        ignoreFrustumCheck = true;
        setSize(1.0F, 0.5F);
    }

    public NSBT6M(World world, double x, double y, double z) {
        super(world, x, y, z);
        ignoreFrustumCheck = true;
        setSize(1.0F, 0.5F);
    }

    @Override
    public float getMaxCartSpeedOnRail() {
        return 3.0F;
    }

    @Override
    public double getMountedYOffset() {
        return 0;
    }

    @Override
    public float getLinkageDistance(EntityMinecart cart) {
        if (cart instanceof IBogie) {
            IBogie bogie = (IBogie) cart;
            if (bogie.getBaseCartID().equals(this.getBaseCartID()))
                return isLong ? 7.0F : 4.0F;
        }
        return 3.0F;
    }

    @Override
    public float getOptimalDistance(EntityMinecart cart) {
        if (cart instanceof IBogie) {
            IBogie bogie = (IBogie) cart;
            if (bogie.getBaseCartID().equals(this.getBaseCartID()))
                return isLong ? 5.0F : 2.0F;
        }
        return 1.5F;
    }

    @Nonnull
    @Override
    public ItemStack getCartItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        return MinecraftForge.EVENT_BUS.post(new MinecartInteractEvent(this, player, hand));
    }

    @Override
    public void doMotion(TrainPacket packet, EntityMinecart cart) {
        TrainController.doMotionWithAir(packet, cart);
    }

    @Override
    public int getMaxPassengerSize() {
        return 1;
    }

    @Override // Called by rider
    public void updatePassenger(Entity entity) {

    }

}
