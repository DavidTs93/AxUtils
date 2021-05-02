package me.Aldreda.AxUtils.Listeners;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ComplexRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.Aldreda.AxUtils.AxUtils;
import me.Aldreda.AxUtils.Classes.Listener;
import me.Aldreda.AxUtils.Utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

public class DisableDefaultFeaturesListener extends Listener {
	
	public DisableDefaultFeaturesListener() {
		register(AxUtils.getInstance());
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void disableCraftingRepairEvent(PrepareItemCraftEvent event) {
		if (!(event.getRecipe() instanceof ComplexRecipe)) return;
		String namespace = ((ComplexRecipe) event.getRecipe()).getKey().getNamespace();
		String key = ((ComplexRecipe) event.getRecipe()).getKey().getKey();
		if (namespace.equalsIgnoreCase("minecraft") && key.equalsIgnoreCase("repair_item")) event.getInventory().setResult(null);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void disableGrindstoneRepairEvent(InventoryClickEvent event) {
		if (event.isCancelled() || event.getInventory().getType() != InventoryType.GRINDSTONE) return;
		new BukkitRunnable() {
			public void run() {
				ItemStack item1 = event.getInventory().getItem(0);
				ItemStack item2 = event.getInventory().getItem(1);
				ItemStack result = event.getInventory().getItem(2);
				if ((Utils.isNull(item1) && Utils.isNull(item2)) || Utils.isNull(result)) return;
				if (!Utils.isNull(item1) && !Utils.isNull(item2)) event.getInventory().setItem(2,null);
			}
		}.runTask(AxUtils.getInstance());
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void disableAnvilRepairDoubleEvent(PrepareAnvilEvent event) {
		if (event.getViewers().isEmpty()) return;
		ItemStack item1 = event.getInventory().getItem(0);
		ItemStack item2 = event.getInventory().getItem(1);
		ItemStack result = event.getInventory().getItem(2);
		if (Utils.isNull(item1) || Utils.isNull(item2) || Utils.isNull(result)) return;
		if (item1.getType() == item2.getType() && item1.getType() == result.getType()) {
			event.setResult(null);
			event.getInventory().setItem(2,null);
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void disableRenaming(PrepareAnvilEvent event) {
		if (event.getViewers().isEmpty()) return;
		new BukkitRunnable() {
			public void run() {
				event.getInventory().setRepairCost(0);
				/*Player player = (Player) event.getView().getPlayer();
				ItemStack result = event.getInventory().getItem(2);
				if (Utils.isNull(result)) return;
				ItemStack item1 = event.getInventory().getItem(0);
				if (Utils.isNull(item1) || item1.getType() == Material.NAME_TAG) return;
				result = ReflectionUtils.setNameItem(result,ReflectionUtils.getNameItem(item1));
				if (Utils.sameItem(item1,result)) result = null;
				event.getInventory().setItem(2,result);
				player.updateInventory();*/
			}
		}.runTask(AxUtils.getInstance());
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void disableRenaming(InventoryClickEvent event) {
		if (event.getInventory().getType() != InventoryType.ANVIL || event.getRawSlot() != 2) return;
		ItemStack result = event.getInventory().getItem(2);
		if (Utils.isNull(result)) return;
		ItemStack item1 = event.getInventory().getItem(0);
		if (Utils.isNull(item1) || item1.getType() == Material.NAME_TAG) return;
		if (item1.getItemMeta().displayName() == null && result.getItemMeta().displayName() != null) event.setCancelled(true);
		else Utils.broadcast(Component.text(((AnvilInventory) event.getView().getTopInventory()).getRenameText()).decoration(TextDecoration.ITALIC,false));
		//else if (item1.getItemMeta().displayName() == null) return;
		/*else if (!Utils.chatColorsStrip(item1.getItemMeta().displayName()).equals(Utils.chatColorsStrip(result.getItemMeta().displayName())) ||
				(item1.getItemMeta().getDisplayName().contains("§o") && !result.getItemMeta().getDisplayName().contains("§o")))
						event.setCancelled(true);*/
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void disableGrindstoneXPEvent(InventoryClickEvent event) {
		if (event.isCancelled() || event.getInventory().getType() != InventoryType.GRINDSTONE || event.getRawSlot() != 2) return;
		event.setCancelled(true);
		if (!Utils.isNull(event.getInventory().getItem(0)) && !Utils.isNull(event.getInventory().getItem(1))) return;
		new BukkitRunnable() {
			public void run() {
				Player player = (Player) event.getWhoClicked();
				Inventory inv = event.getInventory();
				ItemStack result = inv.getItem(2);
				if (event.isShiftClick()) {
					if (player.getInventory().firstEmpty() != -1) Utils.givePlayer(player,result,false);
				} else if(event.getHotbarButton() != -1) {
					if (Utils.isNull(Utils.getFromSlot(player,event.getHotbarButton()))) Utils.setItemSlot(player,result,event.getHotbarButton());
				} else player.setItemOnCursor(result);
				inv.setItem(0,null);
				inv.setItem(1,null);
				inv.setItem(2,null);
				player.playSound(player.getLocation(),Sound.BLOCK_GRINDSTONE_USE,1,1);
			}
		}.runTask(AxUtils.getInstance());
	}
}