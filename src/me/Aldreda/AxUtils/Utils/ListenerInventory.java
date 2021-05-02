package me.Aldreda.AxUtils.Utils;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import me.Aldreda.AxUtils.Classes.Listener;

public abstract class ListenerInventory extends Listener {
	protected final Inventory inventory;
	protected boolean cancelCloseUnregister = false;
	
	public ListenerInventory(Inventory inv) {
		this.inventory = inv;
	}
	
	@EventHandler(ignoreCancelled = true)
	public void unregisterOnClose(InventoryCloseEvent event) {
		if (event.getView().getTopInventory().equals(inventory) && !cancelCloseUnregister) unregister();
	}
	
	@EventHandler(ignoreCancelled = true)
	public void unregisterOnLeaveEvent(PlayerQuitEvent event) {
		if ((inventory.getHolder() instanceof OfflinePlayer) && event.getPlayer().getUniqueId().equals(((OfflinePlayer) inventory.getHolder()).getUniqueId()))
			unregister();
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onInventoryDrag(InventoryDragEvent event) {
		if (event.getView().getTopInventory().equals(inventory)) for (int slot : event.getRawSlots()) if (slot < inventory.getSize()) {
			event.setCancelled(true);
			return;
		}
	}
}