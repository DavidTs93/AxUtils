package me.Aldreda.AxUtils.Enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import me.Aldreda.AxUtils.Utils.Utils;

public enum Tags {
	SWORDS(Arrays.asList("WOODEN_SWORD","STONE_SWORD","IRON_SWORD","DIAMOND_SWORD","GOLDEN_SWORD","NETHERITE_SWORD"),EquipmentSlot.HAND),
	TRIDENT(Arrays.asList("TRIDENT"),EquipmentSlot.HAND),
	AXES(Arrays.asList("WOODEN_AXE","STONE_AXE","IRON_AXE","DIAMOND_AXE","GOLDEN_AXE","NETHERITE_AXE"),EquipmentSlot.HAND),
	PICKAXES(Arrays.asList("WOODEN_PICKAXE","STONE_PICKAXE","IRON_PICKAXE","DIAMOND_PICKAXE","GOLDEN_PICKAXE","NETHERITE_PICKAXE"),EquipmentSlot.HAND),
	SHOVELS(Arrays.asList("WOODEN_SHOVEL","STONE_SHOVEL","IRON_SHOVEL","DIAMOND_SHOVEL","GOLDEN_SHOVEL","NETHERITE_SHOVEL"),EquipmentSlot.HAND),
	HOES(Arrays.asList("WOODEN_HOE","STONE_HOE","IRON_HOE","DIAMOND_HOE","GOLDEN_HOE","NETHERITE_HOE"),EquipmentSlot.HAND),
	CHESTPLATES(Arrays.asList("LEATHER_CHESTPLATE","CHAINMAIL_CHESTPLATE","IRON_CHESTPLATE","DIAMOND_CHESTPLATE","GOLDEN_CHESTPLATE","NETHERITE_CHESTPLATE"),
			EquipmentSlot.CHEST),
	LEGGINGS(Arrays.asList("LEATHER_LEGGINGS","CHAINMAIL_LEGGINGS","IRON_LEGGINGS","DIAMOND_LEGGINGS","GOLDEN_LEGGINGS","NETHERITE_LEGGINGS"),EquipmentSlot.LEGS),
	BOOTS(Arrays.asList("LEATHER_BOOTS","CHAINMAIL_BOOTS","IRON_BOOTS","DIAMOND_BOOTS","GOLDEN_BOOTS","NETHERITE_BOOTS"),EquipmentSlot.FEET),
	HELMETS(Arrays.asList("LEATHER_HELMET","CHAINMAIL_HELMET","IRON_HELMET","DIAMOND_HELMET","GOLDEN_HELMET","NETHERITE_HELMET","TURTLE_HELMET"),EquipmentSlot.HEAD),
	SHIELD(Arrays.asList("SHIELD"),EquipmentSlot.OFF_HAND),
	EXTRATOOLS(Arrays.asList("CARROT_ON_A_STICK","WARPED_FUNGUS_ON_A_STICK","SHEARS","FLINT_AND_STEEL","FISHING_ROD"),null),
	EXTRAARMORY(Arrays.asList("ELYTRA"),EquipmentSlot.CHEST),
	BOWS(Arrays.asList("BOW","CROSSBOW"),EquipmentSlot.HAND),
	WOODEN(Arrays.asList("WOODEN_SWORD","WOODEN_AXE","WOODEN_PICKAXE","WOODEN_SHOVEL","WOODEN_HOE"),null),
	LEATHER(Arrays.asList("LEATHER_CHESTPLATE","LEATHER_LEGGINGS","LEATHER_BOOTS","LEATHER_HELMET"),null),
	STONE(Arrays.asList("STONE_SWORD","STONE_AXE","STONE_PICKAXE","STONE_SHOVEL","STONE_HOE"),null),
	GOLD(Arrays.asList("GOLDEN_SWORD","GOLDEN_AXE","GOLDEN_PICKAXE","GOLDEN_SHOVEL","GOLDEN_HOE","GOLDEN_CHESTPLATE","GOLDEN_LEGGINGS","GOLDEN_BOOTS",
			"GOLDEN_HELMET"),null),
	CHAINMAIL(Arrays.asList("CHAINMAIL_CHESTPLATE","CHAINMAIL_LEGGINGS","CHAINMAIL_BOOTS","CHAINMAIL_HELMET"),null),
	IRON(Arrays.asList("IRON_SWORD","IRON_AXE","IRON_PICKAXE","IRON_SHOVEL","IRON_HOE","IRON_CHESTPLATE","IRON_LEGGINGS","IRON_BOOTS","IRON_HELMET"),null),
	DIAMOND(Arrays.asList("DIAMOND_SWORD","DIAMOND_AXE","DIAMOND_PICKAXE","DIAMOND_SHOVEL","DIAMOND_HOE","DIAMOND_CHESTPLATE","DIAMOND_LEGGINGS","DIAMOND_BOOTS",
			"DIAMOND_HELMET"),null),
	NETHERITE(Arrays.asList("NETHERITE_SWORD","NETHERITE_AXE","NETHERITE_PICKAXE","NETHERITE_SHOVEL","NETHERITE_HOE","NETHERITE_CHESTPLATE","NETHERITE_LEGGINGS",
			"NETHERITE_BOOTS","NETHERITE_HELMET"),null);
	
	private final List<String> materials;
	public final EquipmentSlot slot;
	
	private Tags(List<String> materials, EquipmentSlot slot) {
		this.materials = materials;
		this.slot = slot;
	}
	
	public boolean contains(Material material) {
		if (material == null) return false;
		return materials.contains(material.name());
	}
	
	public static boolean isArmor(ItemStack item) {
		if (Utils.isNull(item)) return false;
		String type = item.getType().name();
		Boolean isBoots = BOOTS.materials.contains(type);
		Boolean isChestplate = CHESTPLATES.materials.contains(type);
		Boolean isHelmet = HELMETS.materials.contains(type);
		Boolean isLeggings = LEGGINGS.materials.contains(type);
		return (isBoots || isChestplate || isHelmet || isLeggings);
	}
	
	public static boolean isArmorExtra(ItemStack item) {
		if (Utils.isNull(item)) return false;
		String type = item.getType().name();
		return EXTRAARMORY.materials.contains(type) || SHIELD.materials.contains(type);
	}
	
	public static boolean isTool(ItemStack item) {
		if (Utils.isNull(item)) return false;
		String type = item.getType().name();
		Boolean isAxe = AXES.materials.contains(type);
		Boolean isHoe = HOES.materials.contains(type);
		Boolean isPickaxe = PICKAXES.materials.contains(type);
		Boolean isShovel = SHOVELS.materials.contains(type);
		return (isAxe || isHoe || isPickaxe || isShovel);
	}
	
	public static boolean isToolExtra(ItemStack item) {
		if (Utils.isNull(item)) return false;
		return EXTRATOOLS.materials.contains(item.getType().name());
	}
	
	public static boolean isWeapon(ItemStack item) {
		if (Utils.isNull(item)) return false;
		String type = item.getType().name();
		Boolean isWeapon = SWORDS.materials.contains(type) || TRIDENT.materials.contains(type);
		Boolean isBow = BOWS.materials.contains(type);
		return (isWeapon || isBow);
	}
	
	public static boolean isExtra(ItemStack item) {
		if (Utils.isNull(item)) return false;
		String type = item.getType().name();
		Boolean isWeapon = SHIELD.materials.contains(type);
		Boolean isTool = EXTRATOOLS.materials.contains(type);
		Boolean isArmor = EXTRAARMORY.materials.contains(type);
		return (isWeapon || isTool || isArmor);
	}
	
	public static List<Tags> get(ItemStack item) {
		if (Utils.isNull(item)) return null;
		return get(item.getType());
	}
	
	public static List<Tags> get(Material material) {
		if (material == null) return null;
		List<Tags> tags = new ArrayList<Tags>();
		for (Tags tag : values()) if (tag.materials.contains(material.name())) tags.add(tag);
		if (tags.isEmpty()) return null;
		return tags;
	}
	
	public static List<Material> getMaterials(Tags tag) {
		List<Material> materials = new ArrayList<Material>();
		for (String name : tag.materials) {
			Material material = Material.getMaterial(name);
			if (material != null) materials.add(material);
		}
		return materials;
	}
	
	public List<Material> getMaterials() {
		return getMaterials(this);
	}
}