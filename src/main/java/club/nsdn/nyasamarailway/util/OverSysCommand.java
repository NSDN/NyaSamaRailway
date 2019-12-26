package club.nsdn.nyasamarailway.util;

import com.google.common.collect.Lists;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * Created by drzzm32 on 2019.12.25.
 */
public class OverSysCommand extends CommandBase {

        public OverSysCommand(){
            aliases = Lists.newArrayList("nyaOver");
        }

        private final List<String> aliases;

        @Override
        @Nonnull
        public String getName() {
            return "nyaOver";
        }

        @Override
        @Nonnull
        public String getUsage(@Nonnull ICommandSender sender) {
            return "nyaOver <sheet tag> <remote csv url> (O is OU, not zero; the encoding is UTF-8)";
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
                        String[] str = OverSheet.showSheetsInfo(server.getWorld(dim)).split("\n");
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

            String tag = args[0], url = args[1];
            EntityPlayer player = (EntityPlayer) sender;
            OverSheet sheet = OverSheet.fetchFromURL(url);
            if (sheet == null) {
                player.sendMessage(new TextComponentString(TextFormatting.RED + "[ERROR] OverSheet load error!"));
                return;
            }
            sheet.saveToWorld(server.getWorld(player.dimension), tag);
            player.sendMessage(new TextComponentString(TextFormatting.GRAY + "OverSheet loaded and saved."));
        }

        @Override
        public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
            if (sender instanceof EntityPlayer)
                return ((EntityPlayer) sender).isCreative();
            return false;
        }

        @Override
        @Nonnull
        public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
            return Collections.emptyList();
        }

}
