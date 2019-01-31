package club.nsdn.nyasamaoptics.tileblock.screen;

import club.nsdn.nyasamatelecom.api.tileentity.TileEntityReceiver;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

/**
 * Created by drzzm32 on 2019.1.30.
 */
public class TileEntityPlatformPlate extends TileEntityReceiver {

    public static final int ALIGN_CENTER = 0, ALIGN_LEFT = 1, ALIGN_RIGHT = 2;

    public String content;
    public int color;
    public double scale;
    public int align;

    public boolean isEnabled;
    public boolean prevIsEnabled;

    public TileEntityPlatformPlate() {
        super();
        content = "DUMMY";
        color = 0x323232;
        scale = 1.0;
        align = ALIGN_CENTER;
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @Nonnull
    public AxisAlignedBB getRenderBoundingBox()
    {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound tagCompound) {
        tagCompound.setString("content", content);
        tagCompound.setInteger("color", color);
        tagCompound.setDouble("scale", scale);
        tagCompound.setInteger("align", align);
        tagCompound.setBoolean("isEnabled", isEnabled);
        return super.toNBT(tagCompound);
    }

    @Override
    public void fromNBT(NBTTagCompound tagCompound) {
        super.fromNBT(tagCompound);
        content = tagCompound.getString("content");
        color = tagCompound.getInteger("color");
        scale = tagCompound.getDouble("scale");
        align = tagCompound.getInteger("align");
        isEnabled = tagCompound.getBoolean("isEnabled");
    }

    public static void updateThis(TileEntityPlatformPlate tilePlate) {
        tilePlate.refresh();
    }

}
