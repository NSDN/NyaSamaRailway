package club.nsdn.nyasamarailway.entity.cart;

import club.nsdn.nyasamarailway.api.cart.AbsMotoCart;
import club.nsdn.nyasamarailway.api.cart.IExtendedInfoCart;
import club.nsdn.nyasamarailway.network.TrainPacket;
import club.nsdn.nyasamarailway.util.HashMap;
import club.nsdn.nyasamarailway.util.TrainController;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2018.3.21.
 */
public class NSPCT9 extends AbsMotoCart implements IExtendedInfoCart {

    public String extenedInfo = "";
    private static final DataParameter<String> EXT = EntityDataManager.createKey(NSPCT9.class, DataSerializers.STRING);

    @Override
    protected void entityInit() {
        super.entityInit();

        dataManager.register(EXT, "");
    }

    public String getExtendedInfo() {
        return dataManager.get(EXT);
    }

    public void setExtendedInfo(String info) {
        this.extenedInfo = info;
        dataManager.set(EXT, info);
    }

    @Override
    public String getExtendedInfo(String key) {
        HashMap info = new HashMap();
        info.fromString(getExtendedInfo());
        if (info.containsKey(key))
            return info.get(key);
        return "";
    }

    @Override
    public void setExtendedInfo(String key, String data) {
        HashMap info = new HashMap();
        info.fromString(getExtendedInfo());
        info.remove(key);
        info.put(key, data);
        setExtendedInfo(info.toString());
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);

        setExtendedInfo(tagCompound.getString("ExtendedInfo"));
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);

        tagCompound.setString("ExtendedInfo", getExtendedInfo());
    }

    public NSPCT9(World world) {
        super(world);
        ignoreFrustumCheck = true;
    }

    public NSPCT9(World world, double x, double y, double z) {
        super(world, x, y, z);
        ignoreFrustumCheck = true;
    }

    @Override
    public float getMaxCartSpeedOnRail() {
        return 3.0F;
    }

    @Override
    public double getMountedYOffset() {
        return 0.0;
    }

    @Override
    public float getLinkageDistance(EntityMinecart cart) {
        return 1.0F;
    }

    @Override
    public float getOptimalDistance(EntityMinecart cart) {
        return 0.7F;
    }

    @Nonnull
    @Override
    public ItemStack getCartItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public void doMotion(TrainPacket packet, EntityMinecart cart) {
        TrainController.doMotionWithAir(packet, cart);
    }

}
