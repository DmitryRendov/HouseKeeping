package me.drendov.HouseKeeping;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Cmd
        implements CommandExecutor {
    private static HouseKeeping plugin = HouseKeeping.getInstance();
    private static Logger logger = plugin.getLogger();

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }

        if (cmd.getName().equalsIgnoreCase("housekeeping")) {
            if (sender.hasPermission("housekeeping.reload") || sender.isOp()) {
                try {
                    String playerName = "";
                    if (args.length > 0) {
                        if (!args[0].contains(":")) {
                            playerName = args[0];
                        }
                    } else {
                        plugin.showInfo(sender);
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                HouseKeeping.sendMessage(player, TextMode.Err, Messages.NoPermissionForCommand);
                return true;
            }
        }
        return false;
    }

}

