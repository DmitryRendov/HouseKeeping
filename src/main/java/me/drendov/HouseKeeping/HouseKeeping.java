package me.drendov.HouseKeeping;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class HouseKeeping extends JavaPlugin {
    private static HouseKeeping instance;

    //for logging to the console
    private static Logger logger;

    public final Config config = new Config(this);
    protected final static String pluginFolderPath = "plugins" + File.separator + "HouseKeeping";
    final static String configFilePath = pluginFolderPath + File.separator + "config.yml";
    final static String messagesFilePath = pluginFolderPath + File.separator + "messages.yml";

    private String version;
    private String[] messages;
    public final String msgBorder = new String(new char[10]).replace("\0", ChatColor.GREEN + "-" + ChatColor.DARK_GREEN + "-");

    @Override
    public void onEnable() {
        instance = this;
        logger = instance.getLogger();

        //load up all the messages from messages.yml
        this.loadMessages();
        logger.info("Customizable messages loaded.");

        this.config.load();

        // register for events
        PluginManager pluginManager = this.getServer().getPluginManager();

        if (pluginManager.getPlugin("Vault") == null) {
            logger.info("Missing dependency 'Vault'. Cannot continue.");
            getPluginLoader().disablePlugin(this);
            return;
        }

        if (pluginManager.getPlugin("Essentials") == null) {
            logger.info("Missing dependency 'EssentialsX'. Cannot continue.");
            getPluginLoader().disablePlugin(this);
            return;
        }

        // player events
        pluginManager.registerEvents((Listener) new Listeners(), (Plugin) this);
        Objects.requireNonNull(this.getCommand("housekeeping")).setExecutor(new Cmd());

        try
        {
            int pluginId = 12268;
            Metrics metrics = new Metrics(this, pluginId);
        }
        catch (Throwable ignored){}
        PluginDescriptionFile pdfFile = this.getDescription();
        this.version = pdfFile.getVersion();
        logger.info("HouseKeeping v" + this.version + " enabled.");
    }

    @Override
    public void onDisable() {
        logger.info("HouseKeeping disabled.");
    }

    public static HouseKeeping getInstance() {
        return instance;
    }

    void showInfo(CommandSender sender) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }
        HouseKeeping.sendMessage(player, TextMode.Instr, ChatColor.WHITE + "----- " + ChatColor.GREEN + "HouseKeeping v" + this.version + ChatColor.WHITE + " -----");
        HouseKeeping.sendMessage(player, TextMode.Instr, "/housekeeping reload " + ChatColor.WHITE + " - " + HouseKeeping.getInstance().getMessage(Messages.ReloadMsg) );
    }

    //sends a color-coded message to a player
    public static void sendMessage(Player player, ChatColor color, Messages messageID, String... args) {
        sendMessage(player, color, messageID, 0, args);
    }

    //sends a color-coded message to a player
    public static void sendMessage(Player player, ChatColor color, Messages messageID, long delayInTicks, String... args) {
        String message = HouseKeeping.getInstance().getMessage(messageID, args);
        sendMessage(player, color, message, delayInTicks);
    }

    //sends a color-coded message to a player
    public static void sendMessage(Player player, ChatColor color, String message) {
        if (message == null || message.length() == 0) return;

        if (player == null) {
            logger.info(color + message);
        } else {
            player.sendMessage(ChatColor.RED + "[HouseKeeping] " + color + message);
        }
    }

    public static void sendMessage(Player player, ChatColor color, String message, long delayInTicks) {
        SendPlayerMessageTask task = new SendPlayerMessageTask(player, color, message);

        //Only schedule if there should be a delay. Otherwise, send the message right now, else the message will appear out of order.
        if (delayInTicks > 0) {
            HouseKeeping.getInstance().getServer().getScheduler().runTaskLater(HouseKeeping.getInstance(), task, delayInTicks);
        } else {
            task.run();
        }
    }

    private void loadMessages() {
        Messages[] messageIDs = Messages.values();
        this.messages = new String[Messages.values().length];

        HashMap<String, CustomizableMessage> defaults = new HashMap<String, CustomizableMessage>();
        // initialize defaults
        this.addDefault(defaults, Messages.Reloaded, "Config reloaded.", null);
        this.addDefault(defaults, Messages.ReloadMsg, "Reload the plugin config from the disc.", null);
        this.addDefault(defaults, Messages.NoPermissionForCommand, "You don't have permission to do that.", null);

        // load the config file
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(messagesFilePath));

        // for each message ID
        for (Messages messageID : messageIDs) {
            //get default for this message
            CustomizableMessage messageData = defaults.get(messageID.name());

            // if default is missing, log an error and use some fake data for now so that the plugin can run
            if (messageData == null) {
                logger.info("Missing message for " + messageID.name() + ".  Please contact the developer.");
                messageData = new CustomizableMessage(messageID, "Missing message!  ID: " + messageID.name() + ".  Please contact a server admin.", null);
            }

            // read the message from the file, use default if necessary
            this.messages[messageID.ordinal()] = config.getString("Messages." + messageID.name() + ".Text", messageData.text);
            config.set("Messages." + messageID.name() + ".Text", this.messages[messageID.ordinal()]);

            this.messages[messageID.ordinal()] = this.messages[messageID.ordinal()].replace('$', (char) 0x00A7);

            if (messageData.notes != null) {
                messageData.notes = config.getString("Messages." + messageID.name() + ".Notes", messageData.notes);
                config.set("Messages." + messageID.name() + ".Notes", messageData.notes);
            }
        }

        //save any changes
        try {
            config.options().header("Use a YAML editor like NotepadPlusPlus to edit this file.  \nAfter editing, back up your changes before reloading the server in case you made a syntax error.  \nUse dollar signs ($) for formatting codes, which are documented here: http://minecraft.gamepedia.com/Formatting_codes");
            config.save(HouseKeeping.messagesFilePath);
        } catch (IOException exception) {
            logger.info("Unable to write to the configuration file at \"" + HouseKeeping.messagesFilePath + "\"");
        }

        defaults.clear();
        System.gc();
    }

    private void addDefault(HashMap<String, CustomizableMessage> defaults,
                            Messages id, String text, String notes) {
        CustomizableMessage message = new CustomizableMessage(id, text, notes);
        defaults.put(id.name(), message);
    }

    synchronized public String getMessage(Messages messageID, String... args) {
        String message = messages[messageID.ordinal()];

        for (int i = 0; i < args.length; i++) {
            String param = args[i];
            message = message.replace("{" + i + "}", param);
        }

        return message;
    }


}
