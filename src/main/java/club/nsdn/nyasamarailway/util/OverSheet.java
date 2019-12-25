package club.nsdn.nyasamarailway.util;

import club.nsdn.nyasamarailway.NyaSamaRailway;
import cn.ac.nya.common.util.Utility;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

/**
 * Created by drzzm32 on 2019.12.25.
 */
public class OverSheet extends Utility.Data.IntDataSheet {

    static class SaveData extends WorldSavedData {

        ArrayList<String> records;

        SaveData() {
            super("nsr.over");
            records = new ArrayList<>();
        }

        @Nonnull
        @Override
        public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound tagCompound) {
            if (records.isEmpty())
                return tagCompound;

            int size = records.size();
            tagCompound.setInteger("size", size);
            for (int i = 0; i < size; i++) {
                String s = records.get(i);
                if (s == null) s = "";
                tagCompound.setString("record_" + i, s);
            }
            return tagCompound;
        }

        @Override
        public void readFromNBT(@Nonnull NBTTagCompound tagCompound) {
            if (!tagCompound.hasKey("size"))
                return;

            int size = tagCompound.getInteger("size");
            records.clear();
            for (int i = 0; i < size; i++) {
                if (!tagCompound.hasKey("record_" + i))
                    break;
                records.add(tagCompound.getString("record_" + i));
            }
            if (records.size() != size) {
                NyaSamaRailway.logger.info("OverSheet load error, at " + mapName);
                records.clear();
            }
        }

        @Override
        public String toString() {
            String str = ""; int size = records.size();
            for (int i = 0; i < size; i++) {
                str = str.concat(records.get(i));
                if (i < size - 1)
                    str = str.concat("\n");
            }
            return str;
        }

        static SaveData fromOverSheet(OverSheet sheet) {
            SaveData data = new SaveData();
            if (sheet.size() == 0)
                return null;
            String str = sheet.toString();
            String[] lines = str.split("\n");
            if (lines.length != sheet.size())
                return null;
            data.records.clear();
            data.records.addAll(Arrays.asList(lines));
            return data;
        }

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

    private static OverSheet fetchFromNBT(World world, String tag) {
        WorldSavedData data = world.loadData(SaveData.class, tag);
        if (data == null)
            return null;
        OverSheet sheet = new OverSheet();
        String str = data.toString();
        if (str.isEmpty())
            return null;
        sheet.fromString(str);
        if (sheet.size() == 0)
            return null;
        return sheet;
    }

    private static LinkedHashMap<World, LinkedHashMap<String, OverSheet>> sheets = new LinkedHashMap<>();
    public static OverSheet getOverSheet(World world, String tag) {
        LinkedHashMap<String, OverSheet> subSheets;
        OverSheet sheet;

        if (!sheets.containsKey(world)) {
            sheet = fetchFromNBT(world, tag);
            if (sheet == null)
                return null;
            subSheets = new LinkedHashMap<>();
            subSheets.put(tag, sheet);
            sheets.put(world, subSheets);
        } else {
            subSheets = sheets.get(world);
            if (!subSheets.containsKey(tag)) {
                sheet = fetchFromNBT(world, tag);
                if (sheet == null)
                    return null;
                subSheets.put(tag, sheet);
            } else
                sheet = subSheets.get(tag);
        }

        return sheet;
    }

    void saveToWorld(World world, String tag) {
        SaveData data = SaveData.fromOverSheet(this);
        if (data == null) {
            NyaSamaRailway.logger.info("OverSheet save error, at " + world.getWorldInfo().getWorldName());
            return;
        }
        world.setData(tag, data);
        sheets.remove(world);
    }

}
