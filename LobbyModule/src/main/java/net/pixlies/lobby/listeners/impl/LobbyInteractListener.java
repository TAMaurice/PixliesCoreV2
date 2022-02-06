package net.pixlies.lobby.listeners.impl;

import io.papermc.paper.event.entity.EntityDamageItemEvent;
import net.pixlies.lobby.Lobby;
import net.pixlies.lobby.managers.LobbyManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class LobbyInteractListener implements Listener {

    private static final Lobby instance = Lobby.getInstance();
    private final LobbyManager manager = instance.getLobbyManager();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (manager.isInBuildMode(player.getUniqueId())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (manager.isInBuildMode(player.getUniqueId())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (manager.isInBuildMode(player.getUniqueId())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
        Player player = event.getPlayer();
        if (manager.isInBuildMode(player.getUniqueId())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onHurtSad(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!(event.getDamager() instanceof Player damager)) return;
        if (manager.isInBuildMode(player.getUniqueId())) return;
        if (manager.isInBuildMode(damager.getUniqueId())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onHurtSad(EntityDamageItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (manager.isInBuildMode(player.getUniqueId())) return;
        event.setCancelled(true);
    }

}