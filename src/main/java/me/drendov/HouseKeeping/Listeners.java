package me.drendov.HouseKeeping;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.logging.Logger;

public class Listeners
        implements Listener {
    private static HouseKeeping plugin = HouseKeeping.getInstance();
    private static Logger logger = plugin.getLogger();

    @EventHandler
    public void inventoryOpenListener(InventoryOpenEvent event) {
        String player = event.getPlayer().getName();

        HouseKeeping.sendMessage(null, TextMode.Instr,
                "User " + event.getPlayer().getName());
    }
}

