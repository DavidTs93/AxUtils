package me.Aldreda.AxUtils.Utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public abstract class ListenerInventoryPages extends ListenerInventory {
	protected int currentPage = 1;
	protected ItemStack close = Utils.makeItem(Material.BARRIER,Component.translatable("spectatorMenu.close",NamedTextColor.RED),ItemFlag.values());
	protected ItemStack next = Utils.makeItem(Material.ARROW,Component.translatable("spectatorMenu.next_page",NamedTextColor.GREEN),ItemFlag.values());
	protected ItemStack previous = Utils.makeItem(Material.BARRIER,Component.translatable("spectatorMenu.previous_page",NamedTextColor.GOLD),ItemFlag.values());
	
	public ListenerInventoryPages(Player owner, Player player, int lines, Component name, JavaPlugin plugin) {
		super(Utils.makeInventory(owner,lines,name));
		setPage(1);
		register(plugin);
		player.openInventory(inventory);
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		if (!event.getView().getTopInventory().equals(inventory)) return;
		int slot = event.getRawSlot();
		ClickType click = event.getClick();
		if (firstSlotCheck(slot,click)) return;
		event.setCancelled(true);
		if (!click.isRightClick() && !click.isLeftClick()) return;
		if (secondSlotCheck(slot,click)) return;
		ItemStack slotItem = event.getInventory().getItem(slot);
		if (isEmpty(slotItem)) return;
		if (slot == inventory.getSize() - 5 && Utils.sameItem(slotItem,close)) event.getView().close();
		else if (slot == inventory.getSize() - 1 && Utils.sameItem(slotItem,next)) setPage(currentPage + 1);
		else if (slot == inventory.getSize() - 9 && Utils.sameItem(slotItem,previous)) setPage(currentPage - 1);
		else otherSlot(event,slot,slotItem);
	}
	
	public void reset() {
		for (int i = 0; i < inventory.getSize(); i++) inventory.setItem(i,null);
	}
	
	public void setPage(int page) {
		if (page < 1 || (page == 1 && maxPage() == 0) || page > maxPage()) return;
		reset();
		setPageContents(page);
		inventory.setItem(inventory.getSize() - 5,close);
		if (page < maxPage() - 1) inventory.setItem(inventory.getSize() - 1,next);
		if (page > 1) inventory.setItem(inventory.getSize() - 9,previous);
	}
	
	public boolean isEmpty(ItemStack item) {
		return Utils.isNull(item);
	}
	
	public boolean firstSlotCheck(int slot, ClickType click) {
		return false;
	}
	
	public boolean secondSlotCheck(int slot, ClickType click) {
		if (click == ClickType.CREATIVE) return true;
		return false;
	}
	
	public abstract void setPageContents(int page);
	public abstract int maxPage();
	public abstract void otherSlot(InventoryClickEvent event, int slot, ItemStack slotItem);
}