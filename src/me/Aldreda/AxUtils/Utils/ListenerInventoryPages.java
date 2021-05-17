package me.Aldreda.AxUtils.Utils;

import me.Aldreda.AxUtils.AxUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public abstract class ListenerInventoryPages extends ListenerInventory {
	protected int currentPage = 1;
	protected static ItemStack close = Utils.makeItem(Material.BARRIER,Component.translatable("spectatorMenu.close",
			NamedTextColor.RED).decoration(TextDecoration.ITALIC,false),ItemFlag.values());
	protected static ItemStack next = Utils.makeItem(Material.ARROW,Component.translatable("spectatorMenu.next_page",
			NamedTextColor.GREEN).decoration(TextDecoration.ITALIC,false),ItemFlag.values());
	protected static ItemStack previous = Utils.makeItem(Material.ARROW,Component.translatable("spectatorMenu.previous_page",
			NamedTextColor.GOLD).decoration(TextDecoration.ITALIC,false),ItemFlag.values());
	protected int closeSlot = inventory.getSize() - 5;
	protected int nextSlot = inventory.getSize() - 1;
	protected int previousSlot = inventory.getSize() - 9;
	protected int size;
	protected Player player;
	protected boolean alwaysSetNext = false;
	protected boolean alwaysSetPrevious = false;
	
	/**
	 * @param lines Number of lines NOT including the bottom (Close,Next,Previous)
	 */
	public ListenerInventoryPages(InventoryHolder owner, Player player, int lines, Component name, JavaPlugin plugin, Object ... objs) {
		super(Utils.makeInventory(owner,Objects.requireNonNull(lines > 5 || lines < 1 ? null : lines + 1),name));
		size = (lines + 1) * 9;
		this.player = player;
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
		if (cancelCheck(slot,click)) event.setCancelled(true);
		if (!click.isRightClick() && !click.isLeftClick()) return;
		if (secondSlotCheck(slot,click)) return;
		ItemStack slotItem = event.getView().getItem(slot);
		if (isEmpty(slotItem)) return;
		if (slot == closeSlot) event.getView().close();
		else if (slot == nextSlot) setPage(currentPage + 1);
		else if (slot == previousSlot) setPage(currentPage - 1);
		else otherSlot(event,slot,slotItem);
	}
	
	protected void reset() {
		for (int i = 0; i < inventory.getSize(); i++) inventory.setItem(i,null);
	}
	
	public void setPage(int page) {
		if (page < 1 || (page == 1 && maxPage() == 0) || page > maxPage()) return;
		beforeSetPage(page);
		currentPage = page;
		reset();
		setPageContents(page);
		inventory.setItem(closeSlot,close(page));
		if (alwaysSetNext || page < maxPage()) inventory.setItem(nextSlot,next(page));
		if (alwaysSetPrevious || page > 1) inventory.setItem(previousSlot,previous(page));
		cancelCloseUnregister = true;
		player.openInventory(inventory);
		new BukkitRunnable() {
			public void run() {
				cancelCloseUnregister = false;
			}
		}.runTask(AxUtils.getInstance());
	}
	
	protected void beforeSetPage(int page) {
	}
	
	protected void first(Object ... objs) {
	}
	
	protected ItemStack close(int page) {
		return close;
	}
	
	protected ItemStack next(int page) {
		return next;
	}
	
	protected ItemStack previous(int page) {
		return previous;
	}
	
	protected boolean isEmpty(ItemStack item) {
		return Utils.isNull(item);
	}
	
	protected boolean cancelCheck(int slot, ClickType click) {
		return true;
	}
	
	protected boolean firstSlotCheck(int slot, ClickType click) {
		return false;
	}
	
	protected boolean secondSlotCheck(int slot, ClickType click) {
		return click.isCreativeAction();
	}
	
	protected abstract void setPageContents(int page);
	public abstract int maxPage();
	protected abstract void otherSlot(InventoryClickEvent event, int slot, ItemStack slotItem);
}