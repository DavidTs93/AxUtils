package me.Aldreda.AxUtils.Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import com.google.common.collect.ImmutableList;

import me.Aldreda.AxUtils.Classes.Pair;

public class AxItem extends KeyedItem {
	protected static HashMap<String,AxItem> items = new HashMap<String,AxItem>();
	
	private List<String> keywords;
	/**
	 * PlayerInteractEvent will always be of Right Click action
	 */
	public final Consumer<Pair<AxItem,PlayerInteractEvent>> rightClick;
	/**
	 * PlayerInteractEvent will always be of Left Click action
	 */
	public final Consumer<Pair<AxItem,PlayerInteractEvent>> leftClick;
	
	public AxItem(ItemStack item, @Nullable String key, @Nullable String ... keywords) {
		this(item,key,null,null,keywords);
	}
	
	public AxItem(ItemStack item, @Nullable String key, @Nullable Consumer<Pair<AxItem,PlayerInteractEvent>> rightClick,
			@Nullable Consumer<Pair<AxItem,PlayerInteractEvent>> leftClick, String ... keywords) {
		super(item,key);
		List<String> keys = new ArrayList<String>();
		for (String keyword : keywords) if (!keys.contains(keyword)) keys.add(keyword.toLowerCase());
		this.keywords = ImmutableList.copyOf(keys);
		this.rightClick = rightClick;
		this.leftClick = leftClick;
	}
	
	/**
	 * IMPORTANT!!!
	 * Once an Item has been registered its registered form can no longer be changed!!!
	 */
	public AxItem register() {
		items.put(Objects.requireNonNull(items.containsKey(Objects.requireNonNull(key())) ? null :
			Objects.requireNonNull(key(),"Item key cannot be NULL!"),"The key: \"" + key() + "\" is already being used!"),this.getClass().cast(clone()));
		return this;
	}
	
	public boolean hasKeyword(String key) {
		return keywords.contains(key.toLowerCase());
	}
	
	public static AxItem getAxItem(ItemStack original) {
		try {
			AxItem item = getAxItem(original.getItemMeta().getPersistentDataContainer().get(ItemKey,PersistentDataType.STRING));
			if (original.getEnchantments().size() > 0) item.addEnchantments(Pair.fromMap(original.getEnchantments()));
			return item;
		} catch (Exception e) {}
		return null;
	}
	
	public static AxItem getAxItem(String key) {
		Material material = Material.getMaterial(key);
		if (material != null) return new AxItem(new ItemStack(material),key,"minecraft","vanilla");
		return items.get(key).clone();
	}
	
	@Override
	public AxItem clone() {
		try {
			AxItem item = this.getClass().cast(super.clone());
			item.keywords = ImmutableList.copyOf(this.keywords);
			return item;
		} catch (Exception e) {}
		return null;
	}
}