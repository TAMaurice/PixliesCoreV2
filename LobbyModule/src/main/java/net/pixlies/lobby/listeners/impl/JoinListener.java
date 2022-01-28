package net.pixlies.lobby.listeners.impl;

import net.pixlies.lobby.Lobby;
import net.pixlies.lobby.config.Config;
import net.pixlies.lobby.utils.JoinItems;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Objects;

public class JoinListener implements Listener {

    private static final Lobby instance = Lobby.getInstance();
    private final Config config = Lobby.getInstance().getConfig();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        player.setBedSpawnLocation(null, true);
        player.setExp(0);
        player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue());
        player.setFoodLevel(20);
        player.setInvulnerable(true);
        player.setFreezeTicks(0);
        player.setGameMode(GameMode.ADVENTURE);
        // TODO Player teleport to spawn
        // Join messages can be done in the normal core.
        player.setAllowFlight(player.hasPermission("pixlies.lobby.flight"));
        JoinItems.give(player);

    }

}
