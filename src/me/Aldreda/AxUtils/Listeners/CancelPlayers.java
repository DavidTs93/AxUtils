package me.Aldreda.AxUtils.Listeners;

import me.Aldreda.AxUtils.AxUtils;
import me.Aldreda.AxUtils.Classes.Listener;
import me.Aldreda.AxUtils.Utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class CancelPlayers extends Listener {
	private static final Set<Player> players = new HashSet<Player>();
	private static MoveListener move = null;
	
	public CancelPlayers(JavaPlugin plugin) {
		if (plugin == AxUtils.getInstance()) register(plugin);
	}
	
	public static void addPlayer(@NotNull Player player) {
		if (player == null || Utils.isPlayerNPC(player)) return;
		players.add(player);
		check();
	}
	
	public static void removePlayer(@NotNull Player player) {
		if (player == null) return;
		players.remove(player);
		check();
	}
	
	public static boolean isPlayerCancelled(@NotNull Player player) {
		return player != null && players.contains(player);
	}
	
	private static void check() {
		if (players.isEmpty()) {
			if (move != null) {
				move.unregister();
				move = null;
			}
		} else if (move == null) move = new MoveListener();
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void unregisterOnLeaveEvent(PlayerQuitEvent event) {
		removePlayer(event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onInteract(PlayerInteractEvent event) {
		if (players.contains(event.getPlayer())) event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onSwap(PlayerSwapHandItemsEvent event) {
		if (players.contains(event.getPlayer())) event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onDrop(PlayerDropItemEvent event) {
		if (players.contains(event.getPlayer())) event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onClick(InventoryClickEvent event) {
		try {
			if (players.contains((Player) event.getWhoClicked())) event.setCancelled(true);
		} catch (Exception e) {}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onCommand(PlayerCommandPreprocessEvent event) {
		if (players.contains(event.getPlayer())) event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onHotbar(PlayerItemHeldEvent event) {
		if (players.contains(event.getPlayer())) event.setCancelled(true);
	}
	
	private static class MoveListener extends Listener {
		
		private MoveListener() {
			register(AxUtils.getInstance());
		}
		
		@Override
		public void unregister() {
			super.unregister();
		}
		
		@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
		public void onMove(PlayerMoveEvent event) {
			if (players.contains(event.getPlayer())) event.setCancelled(true);
		}
	}
}