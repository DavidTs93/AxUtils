package me.Aldreda.AxUtils.Listeners;

import me.Aldreda.AxUtils.AxUtils;
import me.Aldreda.AxUtils.Classes.Listener;
import me.Aldreda.AxUtils.Classes.Pair;
import me.Aldreda.AxUtils.Utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class CancelPlayers extends Listener {
	private static final Set<Player> players = new HashSet<Player>();
	private static final Set<Player> rotatable = new HashSet<Player>();
	private static final Set<Player> noDamage = new HashSet<Player>();
	private static MoveListener move = null;
	
	public CancelPlayers(JavaPlugin plugin) {
		if (plugin == AxUtils.getInstance()) register(plugin);
	}
	
	public static void addPlayer(@NotNull Player player) {
		addPlayer(player,false,false);
	}
	
	public static void addPlayer(@NotNull Player player, boolean allowRotation, boolean disableDamage) {
		if (player == null || Utils.isPlayerNPC(player) || players.contains(player)) return;
		players.add(player);
		if (allowRotation) rotatable.add(player);
		if (disableDamage) noDamage.add(player);
		check();
	}
	
	public static Pair<Boolean,Boolean> getPlayer(@NotNull Player player) {
		if (player == null || Utils.isPlayerNPC(player)) return null;
		if (players.contains(player)) return Pair.of(rotatable.contains(player),noDamage.contains(player));
		return null;
	}
	
	public static void removePlayer(@NotNull Player player) {
		if (player == null) return;
		players.remove(player);
		rotatable.remove(player);
		noDamage.remove(player);
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
	
	@EventHandler(priority = EventPriority.LOWEST)
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
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onDamage(EntityDamageEvent event) {
		if ((event.getEntity() instanceof Player) && noDamage.contains((Player) event.getEntity())) {
			event.setCancelled(true);
			event.setDamage(0);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onAirChange(EntityAirChangeEvent event) {
		if ((event.getEntity() instanceof Player) && noDamage.contains((Player) event.getEntity())) event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onTarget(EntityTargetEvent event) {
		if (event.getTarget() != null && (event.getTarget() instanceof Player) && noDamage.contains((Player) event.getTarget())) event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPotion(EntityPotionEffectEvent event) {
		if ((event.getEntity() instanceof Player) && noDamage.contains((Player) event.getEntity())) event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onItemDamage(PlayerItemDamageEvent event) {
		if (noDamage.contains(event.getPlayer())) event.setCancelled(true);
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
			if (!players.contains(event.getPlayer())) return;
			if (!event.hasChangedBlock() && event.hasChangedOrientation() && rotatable.contains(players)) return;
			event.setCancelled(true);
		}
	}
}