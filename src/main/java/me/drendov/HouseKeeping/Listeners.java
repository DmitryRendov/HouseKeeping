package me.drendov.HouseKeeping;

import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.world.PortalCreateEvent;

public class Listeners
        implements Listener {
    private static HouseKeeping plugin = HouseKeeping.getInstance();
    private static Logger logger = plugin.getLogger();

    @EventHandler
    public void onPlayerCommandPreProcess(final PlayerCommandPreprocessEvent event) {

        Player player = event.getPlayer();

        if (player.isOp() || player.hasPermission("housekeeping.bypass"))
            return;

        if (HouseKeeping.getInstance().config.isIgnoredWorld(player.getWorld().getName()))
            return;

        GameMode mode = player.getGameMode();
        if (mode != GameMode.CREATIVE && mode != GameMode.SPECTATOR) {
            HashMap getSafeZoneArea = HouseKeeping.getInstance().config.getSafeZoneArea(player);
            Location loc1 = (Location) getSafeZoneArea.get("loc1");
            Location loc2 = (Location) getSafeZoneArea.get("loc2");
            if (!this.isInRect(player, loc1, loc2)) {
                for (final String command : HouseKeeping.getInstance().config.getSafezoneBlockedCommands()) {
                    if (event.getMessage().toLowerCase().equals("/" + command) || event.getMessage().toLowerCase().startsWith("/" + command + " ")) {
                        event.setCancelled(true);
                        HouseKeeping.sendMessage(player, TextMode.Err, Messages.DenyCommandMsg);
                    }
                }
            }
        }

    }

    // TODO: Check performance via load testing.
    // Refactor to use scheduler instead
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!player.isFlying()) return;
        if (!player.getWorld().getName().equals(HouseKeeping.getInstance().config.getSafezoneWorld())) return;
        if (player.isOp() || player.hasPermission("housekeeping.bypass") || player.hasPermission("housekeeping.bypass.fly"))
            return;

        GameMode mode = player.getGameMode();
        if (mode != GameMode.CREATIVE && mode != GameMode.SPECTATOR) {
            HashMap getSafeZoneArea = HouseKeeping.getInstance().config.getSafeZoneArea(player);
            Location loc1 = (Location) getSafeZoneArea.get("loc1");
            Location loc2 = (Location) getSafeZoneArea.get("loc2");
            if (!this.isInRect(player, loc1, loc2)) {
                player.setFlying(false);
                HouseKeeping.sendMessage(player, TextMode.Err, Messages.CantFlyHere);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if (!player.isFlying()) return;
        if (!player.getWorld().getName().equals(HouseKeeping.getInstance().config.getSafezoneWorld())) return;
        if (player.isOp() || player.hasPermission("housekeeping.bypass") || player.hasPermission("housekeeping.bypass.fly"))
            return;

        GameMode mode = player.getGameMode();
        if (mode != GameMode.CREATIVE && mode != GameMode.SPECTATOR) {
            HashMap getSafeZoneArea = HouseKeeping.getInstance().config.getSafeZoneArea(player);
            Location loc1 = (Location) getSafeZoneArea.get("loc1");
            Location loc2 = (Location) getSafeZoneArea.get("loc2");
            if (!this.isInRect(player, loc1, loc2)) {
                HouseKeeping.sendMessage(player, TextMode.Err, Messages.CantFlyHere);
                event.setCancelled(true);
                player.setAllowFlight(false);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPortalLight(final PortalCreateEvent event) {

        Player player = null;
        if (event.getEntity() instanceof Player) {
            player = (Player) event.getEntity();
        }
        if (player == null) {
            event.setCancelled(true);
            return;
        }
        if (player.isOp() || player.hasPermission("housekeeping.bypass") || player.hasPermission("housekeeping.bypass.portal"))
            return;

        HashMap getSafeZoneArea = HouseKeeping.getInstance().config.getSafeZoneArea(player);
        Location loc1 = (Location) getSafeZoneArea.get("loc1");
        Location loc2 = (Location) getSafeZoneArea.get("loc2");
        if (this.isInRect(player, loc1, loc2)) {
            if (event.getReason() == PortalCreateEvent.CreateReason.FIRE) {
                if (HouseKeeping.getInstance().config.getRulePreventPortalCreation()) {
                    HouseKeeping.sendMessage(player, TextMode.Err, Messages.DenyNetherPortalMsg);
                }
                event.setCancelled(HouseKeeping.getInstance().config.getRulePreventPortalCreation());
            }
        }
    }


    private boolean isInRect(Player player, Location loc1, Location loc2) {
        double[] dim = new double[2];

        dim[0] = loc1.getX();
        dim[1] = loc2.getX();
        Arrays.sort(dim);
        if (player.getLocation().getX() > dim[1] || player.getLocation().getX() < dim[0])
            return false;

        dim[0] = loc1.getZ();
        dim[1] = loc2.getZ();
        Arrays.sort(dim);
        return !(player.getLocation().getZ() > dim[1]) && !(player.getLocation().getZ() < dim[0]);
    }
}

