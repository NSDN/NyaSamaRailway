package club.nsdn.nyasamarailway.util;

import club.nsdn.nyasamaoptics.tileblock.screen.LEDPlate;
import club.nsdn.nyasamarailway.NyaSamaRailway;
import com.google.common.collect.Lists;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * Created by drzzm32 on 2020.2.21
 */
public class PublicInfoCore {

    public static class InfoSheet {

        private static final String NBT_TAG = "nsr.info";

        public static class SaveData extends WorldSavedData {

            public LinkedHashMap<String, String> sheets;

            public SaveData(String mapName) {
                super(mapName);
                sheets = new LinkedHashMap<>();
            }

            @Nonnull
            @Override
            public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound tagCompound) {
                if (sheets.isEmpty())
                    return tagCompound;

                for (Map.Entry<String, String> i : sheets.entrySet())
                    tagCompound.setString(i.getKey(), i.getValue());

                NyaSamaRailway.logger.info("InfoSheet saving...");

                return tagCompound;
            }

            @Override
            public void readFromNBT(@Nonnull NBTTagCompound tagCompound) {
                if (tagCompound.getKeySet().isEmpty())
                    return;

                NyaSamaRailway.logger.info("InfoSheet loading...");

                sheets.clear();
                for (String key : tagCompound.getKeySet())
                    sheets.put(key, tagCompound.getString(key));
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

        static String showSheetsInfo(World world) {
            StringBuilder builder = new StringBuilder();
            SaveData data = getData(world);

            if (data.sheets.isEmpty()) {
                builder.append("This world do not have any info.");
                return builder.toString();
            }

            builder.append("World [");
            builder.append(world.getWorldInfo().getWorldName());
            builder.append("] has ");
            builder.append(data.sheets.size());
            builder.append(" sheet(s):\n");
            for (Map.Entry<String, String> i : data.sheets.entrySet()) {
                builder.append(i.getKey());
                builder.append(": ");
                builder.append(i.getValue());
                builder.append("\n");
            }

            return builder.toString();
        }

        public static String get(World world, String key) {
            SaveData data = getData(world);
            if (!data.sheets.containsKey(key))
                return "null";
            return data.sheets.get(key);
        }

        static void put(World world, String key, String value) {
            SaveData data = getData(world);
            data.sheets.put(key, value);
            data.markDirty();
        }

    }

    public static class InfoSheetCommand extends CommandBase {

        public InfoSheetCommand(){
            aliases = Lists.newArrayList("nyaInfo");
        }

        private final List<String> aliases;

        @Override
        @Nonnull
        public String getName() {
            return "nyaInfo";
        }

        @Override
        @Nonnull
        public String getUsage(@Nonnull ICommandSender sender) {
            return "nyaInfo <key> <value>";
        }

        @Override
        @Nonnull
        public List<String> getAliases() {
            return aliases;
        }

        @Override
        public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) {
            if (args.length < 2) {
                sender.sendMessage(new TextComponentString(TextFormatting.AQUA + getUsage(sender)));
                if (sender instanceof EntityPlayer) {
                    int dim = ((EntityPlayer) sender).dimension;
                    if (args.length == 1) {
                        String[] str = InfoSheet.showSheetsInfo(server.getWorld(dim)).split("\n");
                        for (String s : str)
                            sender.sendMessage(new TextComponentString(TextFormatting.GRAY + s));
                    }
                }
                return;
            }

            if (!(sender instanceof EntityPlayer)) {
                sender.sendMessage(new TextComponentString(TextFormatting.RED + "[ERROR] This command can only be executed by player!"));
                return;
            }

            String key = args[0], value = args[1];
            EntityPlayer player = (EntityPlayer) sender;
            InfoSheet.put(server.getWorld(player.dimension), key, value);
            player.sendMessage(new TextComponentString(TextFormatting.GRAY + "Info added: " + key + " -> " + value));
        }

        @Override
        public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
            if (sender instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) sender;
                if (server.isDedicatedServer()) {
                    UserListOps listOps = server.getPlayerList().getOppedPlayers();
                    ArrayList<String> list = Lists.newArrayList(listOps.getKeys());
                    return list.contains(player.getName());
                } else {
                    return player.isCreative();
                }
            }

            return false;
        }

        @Override
        @Nonnull
        public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
            return Collections.emptyList();
        }

    }

    public static class PublicInfoCmd implements LEDPlate.ISpecialCommand {

        private int counter = 0;

        @Override
        public String runCmd(World world, BlockPos pos, String buffer, String[] args) {
            counter += 1;
            if (counter < 20) return buffer;
            counter = 0;

            String key = "global";
            if (args.length > 1) key = args[1];

            return InfoSheet.get(world, key);
        }

    }

}
