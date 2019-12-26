package club.nsdn.nyasamarailway.util;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import cn.ac.nya.common.util.Utility;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by drzzm32 on 2019.12.25.
 */
public class OverSheet extends Utility.Data.IntDataSheet {

    private static final String NBT_TAG = "nsr.over";

    public static class SaveData extends WorldSavedData {

        public LinkedHashMap<String, OverSheet> sheets;

        public SaveData(String mapName) {
            super(mapName);
            sheets = new LinkedHashMap<>();
        }

        @Nonnull
        @Override
        public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound tagCompound) {
            if (sheets.isEmpty())
                return tagCompound;

            for (Map.Entry<String, OverSheet> i : sheets.entrySet()) {
                String str = i.getValue().toString();
                String[] lines = str.split("\n");
                int size = lines.length;
                if (size != i.getValue().size())
                    continue;

                NBTTagCompound tag = new NBTTagCompound();
                tag.setInteger("size", size);
                for (int j = 0; j < size; j++) {
                    String s = lines[j];
                    if (s.isEmpty()) break;
                    tag.setString("record_" + j, s);
                }
                tagCompound.setTag(i.getKey(), tag);
            }

            NyaSamaRailway.logger.info("OverSheet saving...");

            return tagCompound;
        }

        @Override
        public void readFromNBT(@Nonnull NBTTagCompound tagCompound) {
            if (tagCompound.getKeySet().isEmpty())
                return;

            NyaSamaRailway.logger.info("OverSheet loading...");

            sheets.clear();
            for (String key : tagCompound.getKeySet()) {
                if (tagCompound.getTag(key) instanceof NBTTagCompound) {
                    NBTTagCompound tag = (NBTTagCompound) tagCompound.getTag(key);
                    if (!tag.hasKey("size"))
                        continue;

                    String str = "", buf;
                    int size = tag.getInteger("size");
                    for (int i = 0; i < size; i++) {
                        buf = tag.getString("record_" + i);
                        if (buf.isEmpty()) break;
                        str = str.concat(buf);
                        if (i < size - 1)
                            str = str.concat("\n");
                    }
                    OverSheet sheet = new OverSheet();
                    sheet.fromString(str);
                    sheets.put(key, sheet);
                }
            }
        }

    }

    private static SaveData getData(World world) {
        MapStorage storage = world.getPerWorldStorage();
        SaveData data = (SaveData) storage.getOrLoadData(SaveData.class, NBT_TAG);
        if (data == null) {
            data = new SaveData(NBT_TAG);
            storage.setData(NBT_TAG, data);
        }
        return data;
    }

    static OverSheet fetchFromURL(String url) {
        OverSheet sheet = new OverSheet();
        String str = Utility.GET(url);
        if (str.isEmpty())
            return null;
        sheet.fromString(str);
        if (sheet.size() == 0)
            return null;
        return sheet;
    }

    public static OverSheet getOverSheet(World world, String tag) {
        SaveData data = getData(world);
        if (!data.sheets.containsKey(tag))
            return null;
        return data.sheets.get(tag);
    }

    static String showSheetsInfo(World world) {
        StringBuilder builder = new StringBuilder();
        SaveData data = getData(world);

        if (data.sheets.isEmpty()) {
            builder.append("This world do not have any sheet.");
            return builder.toString();
        }

        builder.append("World [");
        builder.append(world.getWorldInfo().getWorldName());
        builder.append("] has ");
        builder.append(data.sheets.size());
        builder.append(" sheet(s):\n");
        for (Map.Entry<String, OverSheet> i : data.sheets.entrySet()) {
            builder.append(i.getKey());
            builder.append(": ");
            OverSheet sheet = i.getValue();
            builder.append(sheet.size());
            if (sheet.size() > 0) {
                builder.append("x");
                builder.append(sheet.get(0).size());
            }
            builder.append("\n");
        }

        return builder.toString();
    }

    void saveToWorld(World world, String tag) {
        SaveData data = getData(world);
        data.sheets.put(tag, this);
        data.markDirty();
    }

}
