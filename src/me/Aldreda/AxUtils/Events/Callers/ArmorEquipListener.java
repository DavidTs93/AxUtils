package me.Aldreda.AxUtils.Events.Callers;

import me.Aldreda.AxUtils.AxUtils;
import me.Aldreda.AxUtils.Classes.Listener;
import me.Aldreda.AxUtils.Enums.EquipMethod;
import me.Aldreda.AxUtils.Events.ArmorEquipEvent;
import me.Aldreda.AxUtils.Utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ArmorEquipListener extends Listener {
	
	public ArmorEquipListener() {
		register(AxUtils.getInstance());
	}
	
	@SuppressWarnings("unused")
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void inventoryClick(InventoryClickEvent event) {
		if (event.isCancelled() || event.getAction() == InventoryAction.NOTHING || event.getClick() == ClickType.MIDDLE ||
				(event.getInventory().getType() != InventoryType.CRAFTING && event.getInventory().getType() != InventoryType.CREATIVE) || !(event.getWhoClicked() instanceof Player)) return;
		if (event.getSlotType() != SlotType.ARMOR && event.getSlotType() != SlotType.QUICKBAR && event.getSlotType() != SlotType.CONTAINER) return;
		if (event.getClick() == ClickType.CREATIVE && event.getSlotType() != SlotType.ARMOR) return;
		Player player = (Player) event.getWhoClicked();
		ItemStack current = event.getCurrentItem();
		int slot = event.getSlot();
		if (slot == 40) slot = -106;
		else if (slot >= 36) slot += 64;
		EquipmentSlot equipSlot;
		if (slot == 100) equipSlot = EquipmentSlot.FEET;
		else if (slot == 101) equipSlot = EquipmentSlot.LEGS;
		else if (slot == 102) equipSlot = EquipmentSlot.CHEST;
		else if (slot == 103) equipSlot = EquipmentSlot.HEAD;
		else equipSlot = current.getType().getEquipmentSlot();
		if (equipSlot == null) return;
		boolean shift = event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT;
		boolean hotbar = event.getClick() == ClickType.NUMBER_KEY;
		boolean offhand = event.getClick() == ClickType.SWAP_OFFHAND;
		boolean drop = event.getClick() == ClickType.DROP || event.getClick() == ClickType.CONTROL_DROP;
		if (hotbar && slot < 100) return;
		ItemStack oldArmor;
		ItemStack newArmor;
		EquipMethod method;
		if (shift) {
			oldArmor = slot >= 100 ? Utils.getFromSlot(player,slot) : null;
			newArmor = slot >= 100 ? null : Utils.getFromSlot(player,slot);
			method = EquipMethod.SHIFT_CLICK;
		} else if (hotbar) {
			oldArmor = Utils.getFromSlot(player,slot);
			newArmor = Utils.getFromSlot(player,event.getHotbarButton());
			method = EquipMethod.HOTBAR_SWAP;
		} else if (offhand) {
			oldArmor = Utils.getFromSlot(player,slot);
			newArmor = Utils.getFromSlot(player,-106);
			method = EquipMethod.OFFHAND_SWAP;
		} else if (drop) {
			oldArmor = Utils.getFromSlot(player,slot);
			newArmor = null;
			method = EquipMethod.DROP;
		} else {
			oldArmor = Utils.getFromSlot(player,slot);
			newArmor = event.getCursor();
			method = event.getClick() == ClickType.CREATIVE ? EquipMethod.CREATIVE : EquipMethod.PICK_DROP;
		}
		ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(player,method,equipSlot,oldArmor,newArmor);
		Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
		if (armorEquipEvent.isCancelled()) event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void playerInteractEvent(PlayerInteractEvent event) {
		if (!event.hasItem() || event.useItemInHand().equals(Result.DENY)) return;
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;
		Player player = event.getPlayer();
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && Utils.isInteract(event.getClickedBlock().getType(),player)) return;
		ItemStack item = event.getItem();
		if (nonArmorHelmet(item.getType())) return;
		EquipmentSlot method = item.getType().getEquipmentSlot();
		if (method == null) return;
		boolean helmet = Utils.isNull(player.getInventory().getHelmet()) && method == EquipmentSlot.HEAD;
		boolean chestplate = Utils.isNull(player.getInventory().getChestplate()) && method == EquipmentSlot.CHEST;
		boolean leggings = Utils.isNull(player.getInventory().getLeggings()) && method == EquipmentSlot.LEGS;
		boolean boots = Utils.isNull(player.getInventory().getBoots()) && method == EquipmentSlot.FEET;
		if (!helmet && !chestplate && !leggings && !boots) return;
		ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(event.getPlayer(),EquipMethod.RIGHT_CLICK,method,null,item);
		Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
		if (armorEquipEvent.isCancelled()) event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void inventoryDrag(InventoryDragEvent event) {
		if (event.isCancelled()) return;
		if (event.getRawSlots().isEmpty()) return;
		int slot = event.getRawSlots().stream().findFirst().orElse(0);
		EquipmentSlot method = event.getOldCursor().getType().getEquipmentSlot();
		if (method == null) return;
		if (slot != getSlot(method)) return;
		ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent((Player) event.getWhoClicked(),EquipMethod.DRAG,method,null,event.getOldCursor());
		Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
		if (armorEquipEvent.isCancelled()) {
			event.setResult(Result.DENY);
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void itemBreakEvent(PlayerItemBreakEvent event) {
		EquipmentSlot method = event.getBrokenItem().getType().getEquipmentSlot();
		if (method == null) return;
		Player player = event.getPlayer();
		Bukkit.getServer().getPluginManager().callEvent(new ArmorEquipEvent(player,EquipMethod.BROKE,method,event.getBrokenItem(),null));
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void playerDeathEvent(PlayerDeathEvent event) {
		Player player = event.getEntity();
		if(event.getKeepInventory()) return;
		ItemStack helmet = player.getInventory().getHelmet();
		ItemStack chestplate = player.getInventory().getChestplate();
		ItemStack leggings = player.getInventory().getLeggings();
		ItemStack boots = player.getInventory().getBoots();
		if (!Utils.isNull(helmet)) Bukkit.getServer().getPluginManager().callEvent(new ArmorEquipEvent(player,EquipMethod.DEATH,EquipmentSlot.HEAD,helmet,null));
		if (!Utils.isNull(chestplate))
			Bukkit.getServer().getPluginManager().callEvent(new ArmorEquipEvent(player,EquipMethod.DEATH,EquipmentSlot.CHEST,chestplate,null));
		if (!Utils.isNull(leggings)) Bukkit.getServer().getPluginManager().callEvent(new ArmorEquipEvent(player,EquipMethod.DEATH,EquipmentSlot.LEGS,leggings,null));
		if (!Utils.isNull(boots)) Bukkit.getServer().getPluginManager().callEvent(new ArmorEquipEvent(player,EquipMethod.DEATH,EquipmentSlot.FEET,boots,null));
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onDispenseArmorEvent(BlockDispenseArmorEvent event) {
		if (event.isCancelled()) return;
		if (Utils.isNull(event.getItem())) return;
		if (!(event.getTargetEntity() instanceof Player)) return;
		Player player = (Player) event.getTargetEntity();
		ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(player,EquipMethod.DISPENSER,event.getItem().getType().getEquipmentSlot(),
				event.getItem(),null);
		Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
		if (armorEquipEvent.isCancelled()) event.setCancelled(true);
	}
	
	/*public static EquipmentSlot getEquipSlot(Material material) {
		if (material == null) return null;
		return material.getEquipmentSlot();
		if (nonArmorHelmet(material) || Tags.HELMETS.contains(material)) return EquipmentSlot.HEAD;
		if (Tags.CHESTPLATES.contains(material) || Tags.EXTRAARMORY.contains(material)) return EquipmentSlot.CHEST;
		if (Tags.LEGGINGS.contains(material)) return EquipmentSlot.LEGS;
		if (Tags.BOOTS.contains(material)) return EquipmentSlot.FEET;
		return null;
	}*/
	
	public static Boolean nonArmorHelmet(Material material) {
		if (material == null) return null;
		return material.getEquipmentSlot() == EquipmentSlot.HEAD && !material.name().endsWith("_HELMET");
	}
	
	private int getSlot(EquipmentSlot method) {
		if (method == EquipmentSlot.HEAD) return 5;
		if (method == EquipmentSlot.CHEST) return 6;
		if (method == EquipmentSlot.LEGS) return 7;
		if (method == EquipmentSlot.FEET) return 8;
		return -1;
	}
}