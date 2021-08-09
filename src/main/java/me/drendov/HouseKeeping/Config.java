package me.drendov.HouseKeeping;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

class Config {
    private HouseKeeping plugin;
    private FileConfiguration config;
    private static Integer daysWithholdFunds;
    public static List<String> safezoneBlockedCommands = new ArrayList<String>();

    Config(HouseKeeping plugin) {
        this.plugin = plugin;
    }

    void load() {
        this.plugin.reloadConfig();
        this.config = this.plugin.getConfig();
        this.config.addDefault("debug", "true");
        this.config.addDefault("daysWithholdFunds", 30);
        this.config.addDefault("safezone", "true");
        this.config.addDefault("safezone.x1", -100);
        this.config.addDefault("safezone.y1", -100);
        this.config.addDefault("safezone.x2", 100);
        this.config.addDefault("safezone.y2", 100);
        List<String> blockedCommands = Arrays.asList("sethome", "home", "ehome", "homes", "ehomes", "tpa", "tphere", "tpyes", "call", "ecall", "etpa", "tpask", "etpask", "spawn", "warp", "warps", "ewarp", "warps", "ewarps");
        this.config.addDefault("blockedCommands", blockedCommands);
        this.config.options().copyDefaults(true);
        this.plugin.saveConfig();
        daysWithholdFunds = this.config.getInt("daysWithholdFunds");
        safezoneBlockedCommands = this.config.getStringList("blockedCommands");
    }

    public int getDaysWithholdFunds() {
        return daysWithholdFunds;
    }

    public List<String> getSafezoneBlockedCommands() {
        return safezoneBlockedCommands;
    }

    public HashMap<String, Location> getSafeZoneArea(Player player) {
        HashMap<String, Location> safeZoneArea = new HashMap<>();
        safeZoneArea.put("loc1", new Location(player.getWorld(), this.config.getDouble("safezone.x1"), 0.0, this.config.getDouble("safezone.y1" )));
        safeZoneArea.put("loc2", new Location(player.getWorld(), this.config.getDouble("safezone.x2"), 255.0, this.config.getDouble("safezone.y2")));
        return safeZoneArea;
    }
}
