package club.nsdn.nyasamaoptics.tileblock.holo;

import club.nsdn.nyasamaoptics.block.BlockLoader;
import club.nsdn.nyasamaoptics.util.font.FontLoader;
import club.nsdn.nyasamaoptics.util.font.TextModel;
import club.nsdn.nyasamatelecom.api.tileentity.TileEntityReceiver;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.HashSet;

/**
 * Created by drzzm32 on 2019.1.30.
 */
public class TileEntityHoloText extends TileEntityReceiver {

    @SideOnly(Side.CLIENT)
    public TextModel model;

    public String content;
    public int color;
    public int thick;
    public double scale;
    public int align;
    public int font;

    public boolean isEnabled;
    public boolean prevIsEnabled;

    public int hash = -1;

    public TileEntityHoloText() {
        super();
        content = "O";
        color = 0xFFFFFF;
        thick = 4;
        scale = 1.0;
        font = FontLoader.FONT_SONG;
    }

    @SideOnly(Side.CLIENT)
    public void createModel() {
        HashSet<Object> hashSet = new HashSet<Object>();
        hashSet.add(content);
        hashSet.add(String.valueOf(thick));
        hashSet.add(String.valueOf(scale));
        hashSet.add(String.valueOf(font));

        if (hash != hashSet.hashCode()) {
            model = FontLoader.getModel(font, align, content, thick);
            hash = hashSet.hashCode();
        }
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
        tagCompound.setInteger("thick", thick);
        tagCompound.setDouble("scale", scale);
        tagCompound.setInteger("align", align);
        tagCompound.setInteger("font", font);
        tagCompound.setBoolean("isEnabled", isEnabled);
        return super.toNBT(tagCompound);
    }

    @Override
    public void fromNBT(NBTTagCompound tagCompound) {
        super.fromNBT(tagCompound);
        content = tagCompound.getString("content");
        color = tagCompound.getInteger("color");
        thick = tagCompound.getInteger("thick");
        scale = tagCompound.getDouble("scale");
        align = tagCompound.getInteger("align");
        font = tagCompound.getInteger("font");
        isEnabled = tagCompound.getBoolean("isEnabled");
    }

    public static void updateThis(TileEntityHoloText holoText) {
        holoText.refresh();
    }

    @Override
    public void updateSignal(World world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity == null) return;
        if (tileEntity instanceof TileEntityHoloText) {
            TileEntityHoloText holoText = (TileEntityHoloText) tileEntity;

            if (holoText.getSender() != null) {
                holoText.isEnabled = holoText.senderIsPowered();
            } else {
                holoText.isEnabled = true;
            }

            BlockLoader.light.lightCtl(world, pos, holoText.isEnabled);

            if (holoText.isEnabled != holoText.prevIsEnabled) {
                holoText.prevIsEnabled = holoText.isEnabled;
                holoText.refresh();
            }
        }
    }

}
