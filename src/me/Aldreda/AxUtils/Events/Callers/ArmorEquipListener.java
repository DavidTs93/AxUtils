package me.Aldreda.AxUtils.Events.Callers;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import me.Aldreda.AxUtils.AxUtils;
import me.Aldreda.AxUtils.Classes.Listener;
import me.Aldreda.AxUtils.Enums.EquipMethod;
import me.Aldreda.AxUtils.Events.ArmorEquipEvent;
import me.Aldreda.AxUtils.Utils.Utils;

public class ArmorEquipListener extends Listener {
	
	public ArmorEquipListener() {
		register(AxUtils.getInstance());
	}
	
	@SuppressWarnings("unused")
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void inventoryClick(InventoryClickEvent event) {
		if (event.isCancelled()) return;
		if(event.getAction() == InventoryAction.NOTHING) return;
		List<InventoryType> invs = Arrays.asList(InventoryType.CRAFTING,InventoryType.CREATIVE,InventoryType.PLAYER);
		if (!invs.contains(event.getInventory().getType()) || !(event.getWhoClicked() instanceof Player)) return;
		if (event.getSlotType() != SlotType.ARMOR && event.getSlotType() != SlotType.QUICKBAR && event.getSlotType() != SlotType.CONTAINER) return;
		Player player = (Player) event.getWhoClicked();
		ItemStack cursor = event.getCursor();
		ItemStack current = event.getCurrentItem();
		int slot = event.getRawSlot();
		boolean shift = event.getClick().equals(ClickType.SHIFT_LEFT) || event.getClick().equals(ClickType.SHIFT_RIGHT);
		EquipmentSlot equipSlot = (shift ? current : cursor).getType().getEquipmentSlot();
		if (!shift && equipSlot != null && slot != getSlot(equipSlot)) return;
		ArmorEquipEvent armorEquipEvent = null;
		boolean numberKey = event.getAction().equals(InventoryAction.HOTBAR_SWAP);
		if (shift) {
			equipSlot = current.getType().getEquipmentSlot();
			if (equipSlot == null) return;
			boolean equiped = slot == getSlot(equipSlot);
			boolean equipment = Utils.isNull(player.getInventory().getItem(equipSlot));
			if (equiped ? equipment : !equipment) return;
			armorEquipEvent = new ArmorEquipEvent(player,EquipMethod.SHIFT_CLICK,equipSlot,!equiped ? null : current,!equiped ? current : null);
		} else {
			if (numberKey) {
				int hotbar;
				try {
					hotbar = event.getHotbarButton();
					if (hotbar == -1) throw new Exception();
				} catch (Exception e) {
					hotbar = -106;
				}
				ItemStack hotbarItem = Utils.getFromSlot(player,hotbar);
				if (Utils.isNull(hotbarItem)) equipSlot = current.getType().getEquipmentSlot();
				else equipSlot = hotbarItem.getType().getEquipmentSlot();
				if (slot != getSlot(equipSlot)) return;
				armorEquipEvent = new ArmorEquipEvent(player,EquipMethod.HOTBAR_SWAP,equipSlot,current,hotbarItem,hotbar);
			} else {
				if (Utils.isNull(cursor)) equipSlot = current.getType().getEquipmentSlot();
				else equipSlot = cursor.getType().getEquipmentSlot();
				if (slot != getSlot(equipSlot)) return;
				armorEquipEvent = new ArmorEquipEvent(player,EquipMethod.PICK_DROP,equipSlot,current,cursor);
			}
		}
		if (armorEquipEvent == null) return;
		Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
		if (armorEquipEvent.isCancelled()) event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerInteractEvent(PlayerInteractEvent event) {
		if (!event.hasItem() || event.useItemInHand().equals(Result.DENY)) return;
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;
		ItemStack item = event.getItem();
		if (nonArmorHelmet(item.getType())) return;
		Player player = event.getPlayer();
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && Utils.isInteract(event.getClickedBlock().getType(),player)) return;
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
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
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

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void itemBreakEvent(PlayerItemBreakEvent event) {
		EquipmentSlot method = event.getBrokenItem().getType().getEquipmentSlot();
		if (method == null) return;
		Player player = event.getPlayer();
		Bukkit.getServer().getPluginManager().callEvent(new ArmorEquipEvent(player,EquipMethod.BROKE,method,event.getBrokenItem(),null));
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
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
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
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