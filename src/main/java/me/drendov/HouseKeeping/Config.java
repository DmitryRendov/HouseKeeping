package me.drendov.HouseKeeping;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

class Config {
    private HouseKeeping plugin;
    private FileConfiguration config;
    private static Integer daysWithholdFunds;
    private static boolean listAbsentOnStart;
    private static boolean preventPortalCreation;
    private static String safezoneWorld;
    private static List<String> safezoneBlockedCommands = new ArrayList<String>();
    private static List<String> ignoreWorlds = new ArrayList<String>();

    Config(HouseKeeping plugin) {
        this.plugin = plugin;
    }

    void load() {
        this.plugin.reloadConfig();
        this.config = this.plugin.getConfig();
        this.config.addDefault("debug", "false");
        this.config.addDefault("daysWithholdFunds", 30);
        this.config.addDefault("listAbsentOnStart", false);
        this.config.addDefault("safezone", "true");
        this.config.addDefault("safezone.world", "world");
        this.config.addDefault("safezone.prevent_portal_creation", true);
        this.config.addDefault("safezone.x1", -1024);
        this.config.addDefault("safezone.y1", -1024);
        this.config.addDefault("safezone.x2", 1024);
        this.config.addDefault("safezone.y2", 1024);
        List<String> defaultBlockedCommands = Arrays.asList("sethome", "home", "spawn");
        this.config.addDefault("blockedCommands", defaultBlockedCommands);
        this.initiateIgnoreWorlds();
        this.config.options().copyDefaults(true);
        this.plugin.saveConfig();

        daysWithholdFunds = this.config.getInt("daysWithholdFunds");
        listAbsentOnStart = this.config.getBoolean("listAbsentOnStart");
        safezoneWorld = this.config.getString("safezone.world");
        preventPortalCreation = this.config.getBoolean("safezone.prevent_portal_creation");
        safezoneBlockedCommands = this.config.getStringList("blockedCommands");
        ignoreWorlds = this.config.getStringList("ignoreWorlds");
    }

    private void initiateIgnoreWorlds() {
        List<String> ignoredWorlds = new ArrayList<>();
        for (World world : Bukkit.getWorlds()) {
            if (world.getEnvironment().equals(World.Environment.NETHER) || world.getEnvironment().equals(World.Environment.THE_END))
                ignoredWorlds.add(world.getName());
        }
        this.config.addDefault("ignoreWorlds", ignoredWorlds);
    }

    int getDaysWithholdFunds() {
        return daysWithholdFunds;
    }

    boolean getListAbsentOnStart() {
        return listAbsentOnStart;
    }

    boolean getRulePreventPortalCreation() {
        return preventPortalCreation;
    }

    String getSafezoneWorld() {
        return safezoneWorld;
    }

    List<String> getSafezoneBlockedCommands() { return safezoneBlockedCommands; }

    boolean isIgnoredWorld(String worldName) {
        return ignoreWorlds.contains(worldName);
    }

    HashMap<String, Location> getSafeZoneArea(Player player) {
        HashMap<String, Location> safeZoneArea = new HashMap<>();
        double worldXZFactor = player.getWorld().getEnvironment().equals(World.Environment.NETHER) ? 8.0 : 1.0;
        double worldYFactor = player.getWorld().getEnvironment().equals(World.Environment.NETHER) ? 2.0 : 1.0;
        safeZoneArea.put("loc1", new Location(player.getWorld(), this.config.getDouble("safezone.x1") / worldXZFactor, 0.0, this.config.getDouble("safezone.y1") / worldXZFactor));
        safeZoneArea.put("loc2", new Location(player.getWorld(), this.config.getDouble("safezone.x2") / worldXZFactor, 255.0 / worldYFactor, this.config.getDouble("safezone.y2") / worldXZFactor));
        return safeZoneArea;
    }
}
