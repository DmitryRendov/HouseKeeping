package me.drendov.HouseKeeping;

import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Cmd implements CommandExecutor {
    private static HouseKeeping plugin = HouseKeeping.getInstance();
    private static Logger logger = plugin.getLogger();

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }

        if (cmd.getName().equalsIgnoreCase("housekeeping")) {

            if (args.length == 0) {
                if (sender.isOp() || sender.hasPermission("housekeeping.admin"))
                    plugin.showInfo(sender);
                return true;
            }

            if (args[0].equalsIgnoreCase("absent")) {
                if (!sender.hasPermission("housekeeping.absent") && !sender.isOp()) {
                    HouseKeeping.sendMessage(player, TextMode.Err, Messages.NoPermissionForCommand);
                    return true;
                }
                HouseKeeping.checkAbsence();
                return true;
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("housekeeping.reload") && !sender.isOp()) {
                    HouseKeeping.sendMessage(player, TextMode.Err, Messages.NoPermissionForCommand);
                    return true;
                }
                plugin.reloadConfig();
                HouseKeeping.sendMessage(player, TextMode.Info, Messages.Reloaded);
                return true;
            } else {
                // Command not found
                plugin.showInfo(sender);
                return true;
            }
        }
        return false;
    }
}
