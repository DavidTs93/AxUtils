package me.Aldreda.AxUtils.Utils;

import java.util.Objects;

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
import net.kyori.adventure.text.format.TextDecoration;

public abstract class ListenerInventoryPages extends ListenerInventory {
	protected int currentPage = 1;
	protected static ItemStack close = Utils.makeItem(Material.BARRIER,Component.translatable("spectatorMenu.close",
			NamedTextColor.RED).decoration(TextDecoration.ITALIC,false),ItemFlag.values());
	protected static ItemStack next = Utils.makeItem(Material.ARROW,Component.translatable("spectatorMenu.next_page",
			NamedTextColor.GREEN).decoration(TextDecoration.ITALIC,false),ItemFlag.values());
	protected static ItemStack previous = Utils.makeItem(Material.ARROW,Component.translatable("spectatorMenu.previous_page",
			NamedTextColor.GOLD).decoration(TextDecoration.ITALIC,false),ItemFlag.values());
	
	/**
	 * @param lines Number of lines NOT including the bottom (Close,Next,Previous)
	 */
	public ListenerInventoryPages(Player owner, Player player, int lines, Component name, JavaPlugin plugin, Object ... objs) {
		super(Utils.makeInventory(owner,Objects.requireNonNull(lines > 5 || lines < 1 ? null : lines + 1),name));
		first(objs);
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
		ItemStack slotItem = event.getView().getItem(slot);
		if (isEmpty(slotItem)) return;
		if (slot == inventory.getSize() - 5 && Utils.sameItem(slotItem,close)) event.getView().close();
		else if (slot == inventory.getSize() - 1 && Utils.sameItem(slotItem,next)) setPage(currentPage + 1);
		else if (slot == inventory.getSize() - 9 && Utils.sameItem(slotItem,previous)) setPage(currentPage - 1);
		else otherSlot(event,slot,slotItem);
	}
	
	protected void reset() {
		for (int i = 0; i < inventory.getSize(); i++) inventory.setItem(i,null);
	}
	
	public void setPage(int page) {
		if (page < 1 || (page == 1 && maxPage() == 0) || page > maxPage()) return;
		currentPage = page;
		reset();
		setPageContents(page);
		inventory.setItem(inventory.getSize() - 5,close);
		if (page < maxPage()) inventory.setItem(inventory.getSize() - 1,next);
		if (page > 1) inventory.setItem(inventory.getSize() - 9,previous);
	}
	
	protected void first(Object ... objs) {
	}
	
	protected boolean isEmpty(ItemStack item) {
		return Utils.isNull(item);
	}
	
	protected boolean firstSlotCheck(int slot, ClickType click) {
		return false;
	}
	
	protected boolean secondSlotCheck(int slot, ClickType click) {
		if (click == ClickType.CREATIVE) return true;
		return false;
	}
	
	protected abstract void setPageContents(int page);
	public abstract int maxPage();
	protected abstract void otherSlot(InventoryClickEvent event, int slot, ItemStack slotItem);
}