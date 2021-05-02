package me.Aldreda.AxUtils.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import me.Aldreda.AxUtils.Enums.EquipMethod;

public final class ArmorEquipEvent extends PlayerEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private boolean cancel = false;
	private final EquipMethod method;
	private final EquipmentSlot slot;
	private final ItemStack oldArmor;
	private ItemStack newArmor;
	private final int hotbar;
	
	public ArmorEquipEvent(Player player, EquipMethod method, EquipmentSlot slot, ItemStack oldArmor, ItemStack newArmor) {
		this(player,method,slot,oldArmor,newArmor,-1);
	}

	/**
	 * @param player the player
	 * @param method EquipMethod
	 * @param oldArmor old armor before the change
	 * @param newArmor new armor after the change
	 */
	public ArmorEquipEvent(Player player, EquipMethod method, EquipmentSlot slot, ItemStack oldArmor, ItemStack newArmor, int hotbar) {
		super(player);
		this.method = method;
		this.slot = slot;
		this.oldArmor = oldArmor;
		this.newArmor = newArmor;
		this.hotbar = hotbar;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	@Override
	public final HandlerList getHandlers() {
		return handlers;
	}
	
	public final void setCancelled(final boolean cancel) {
		this.cancel = cancel;
	}
	
	public final boolean isCancelled() {
		return cancel;
	}
	
	public EquipMethod getEquipMethod() {
		return method;
	}
	
	public EquipmentSlot getSlot() {
		return slot;
	}
	
	public ItemStack getOldArmor() {
		return oldArmor == null ? null : oldArmor.clone();
	}
	
	public ItemStack getNewArmor() {
		return newArmor;
	}
	
	public ItemStack setNewArmor(ItemStack newArmor) {
		return this.newArmor = newArmor;
	}
	
	public int getHotbarSlot() {
		return hotbar;
	}
	
	public boolean hasHotbarSlot() {
		return hotbar > 0;
	}
}