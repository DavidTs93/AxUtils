package me.Aldreda.AxUtils.Items;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import me.Aldreda.AxUtils.AxUtils;
import me.Aldreda.AxUtils.Classes.Listener;
import me.Aldreda.AxUtils.Events.ArmorEquipEvent;
import me.Aldreda.AxUtils.Events.ItemRestrictedEvent;
import me.Aldreda.AxUtils.Utils.Utils;

public class Restrictions {
	public static Restriction Unequippable = new Unequippable();
	public static Restriction Unplaceable = new Unplaceable();
	
	public static abstract class Restriction extends Listener {
		private final NamespacedKey key;
		public final RestrictionType type;
		
		private Restriction(String key, RestrictionType type) {
			this.key = Utils.namespacedKey(key);
			this.type = type;
			register(AxUtils.getInstance());
		}
		
		public ItemStack add(ItemStack item) {
			if (Utils.isNull(item)) return item;
			ItemMeta meta = item.getItemMeta();
			meta.getPersistentDataContainer().set(key,PersistentDataType.STRING,"");
			item.setItemMeta(meta);
			return item;
		}
		
		public ItemStack remove(ItemStack item) {
			if (Utils.isNull(item)) return item;
			ItemMeta meta = item.getItemMeta();
			if (is(meta)) meta.getPersistentDataContainer().remove(key);
			item.setItemMeta(meta);
			return item;
		}
		
		public boolean is(ItemStack item) {
			if (Utils.isNull(item)) return false;
			ItemMeta meta = item.getItemMeta();
			return is(meta);
		}
		
		private boolean is(ItemMeta meta) {
			return meta.getPersistentDataContainer().has(key,PersistentDataType.STRING);
		}
		
		protected void restrictionEvent(Cancellable event, ItemStack item, HumanEntity player) {
			ItemRestrictedEvent restrictionEvent = new ItemRestrictedEvent(type,item);
			restrictionEvent.callEvent();
			if (restrictionEvent.isCancelled()) return;
			event.setCancelled(true);
			if (restrictionEvent.getCancelMSG() != null) player.sendMessage(restrictionEvent.getCancelMSG());
		}
	}
	
	private static class Unequippable extends Restriction {
		public Unequippable() {
			super("unequippable",RestrictionType.Unequippable);
		}
		
		@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
		public void onEquip(ArmorEquipEvent event) {
			if (event.isCancelled() || !is(event.getNewArmor())) return;
			restrictionEvent(event,event.getNewArmor(),event.getPlayer());
		}
	}
	
	private static class Unplaceable extends Restriction {
		public Unplaceable() {
			super("unplaceable",RestrictionType.Unplaceable);
		}
		
		@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
		public void onPlace(BlockPlaceEvent event) {
			if (event.isCancelled() || !is(event.getItemInHand())) return;
			restrictionEvent(event,event.getItemInHand(),event.getPlayer());
		}
	}
	
	public enum RestrictionType {
		Unequippable,
		Unplaceable;
	}
}