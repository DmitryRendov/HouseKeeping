package me.drendov.HouseKeeping;

import org.bukkit.configuration.file.FileConfiguration;

class Config {
    private HouseKeeping plugin;
    private FileConfiguration config;
    private static Integer daysWithholdFunds;

    Config(HouseKeeping plugin) {
        this.plugin = plugin;
    }

    void load() {
        this.plugin.reloadConfig();
        this.config = this.plugin.getConfig();
        this.config.addDefault("debug", "true");
        this.config.addDefault("daysWithholdFunds", 30);
        this.config.options().copyDefaults(true);
        this.plugin.saveConfig();
        daysWithholdFunds = this.config.getInt("daysWithholdFunds");
    }

    public int getDaysWithholdFunds() {
        return daysWithholdFunds;
    }
}

