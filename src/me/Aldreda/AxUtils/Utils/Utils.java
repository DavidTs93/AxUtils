package me.Aldreda.AxUtils.Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.SkeletonHorse;
import org.bukkit.entity.Wither;
import org.bukkit.entity.Zoglin;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.ZombieHorse;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import me.Aldreda.AxUtils.AxUtils;
import me.Aldreda.AxUtils.Classes.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.ChatColor;

public class Utils {
	private static final Pattern pattern = Pattern.compile("&#[a-fA-F0-9]{6}");
	private static final Pattern unicode = Pattern.compile("\\\\u\\+[a-fA-F0-9]{4}");
	private static List<Material> interactable = null;
	
	public static String javaVersion() {
		String javaVersion = "";
		Iterator<Entry<Object,Object>> systemProperties = System.getProperties().entrySet().iterator();
		while (systemProperties.hasNext() && javaVersion.isEmpty()) {
			Entry<Object,Object> property = (Entry<Object,Object>) systemProperties.next();
			if (property.getKey().toString().equalsIgnoreCase("java.version")) javaVersion = property.getValue().toString();
		}
		return javaVersion;
	}
	
	/**
	 * @return Strips the string from colors and converts to color code using {@code&}.
	 * 1.16+ HEX colors can be used via {@code&#??????}.
	 */
	public static String chatColors(String str) {
		str = chatColorsStrip(str);
		Matcher match = unicode.matcher(str);
		while (match.find()) {
			String code = str.substring(match.start(),match.end());
			str = str.replace(code,Character.toString((char) Integer.parseInt(code.replace("\\u+",""),16)));
			match = unicode.matcher(str);
		}
		match = pattern.matcher(str);
		while (match.find()) {
			String color = str.substring(match.start(),match.end());
			str = str.replace(color,ChatColor.of(color.replace("&","")) + "");
			match = pattern.matcher(str);
		}
		return ChatColor.translateAlternateColorCodes('&',str);
	}
	
	public static List<String> chatColors(List<String> list) {
		List<String> newList = new ArrayList<String>();
		for (String str : list) if (str != null)
			if (str.trim().isEmpty()) newList.add("");
			else newList.add(chatColors(str));
		return newList;
	}
	
	public static void chatColors(CommandSender sender, String str) {
		sender.sendMessage(chatColors(str));
	}
	
	public static void chatColorsLogPlugin(String str) {
		Bukkit.getLogger().info(chatColorsPlugin(str));
	}
	
	public static void chatLogPlugin(String str) {
		Bukkit.getLogger().info(chatColorsPlugin("") + str);
	}
	
	public static String chatColorsPlugin(String str) {
		return chatColors("&d[" + AxUtils.pluginNameColors + "&d]&r " + str);
	}

	public static void chatColorsPlugin(CommandSender sender, String str) {
		sender.sendMessage(chatColorsPlugin(str));
	}

	public static String chatColorsUsage(String str) {
		return chatColors("&cUsage: &r/" + AxUtils.pluginNameColors + "&r " + str);
	}

	public static void chatColorsUsage(CommandSender sender, String str) {
		sender.sendMessage(chatColorsUsage(str));
	}
	
	/**
	 * Revert color codes using {@code&}
	 */
	public static String chatColorsToString(String str) {
		return chatColorsToString(str,"&");
	}
	
	public static String chatColorsToString(String str, String colorCode) {
		Pattern unicode = Pattern.compile("§[xX](§[a-fA-F0-9]){6}");
		Matcher match = unicode.matcher(str);
		while (match.find()) {
			String code = str.substring(match.start(),match.end());
			str = str.replace(code,"§" + code.replaceAll("§[xX]","#").replace("§",""));
			match = unicode.matcher(str);
		}
		return str.replace("§",colorCode);
	}
	
	public static List<String> chatColorsToString(List<String> list) {
		return chatColorsToString(list,"&");
	}
	
	public static List<String> chatColorsToString(List<String> list, String colorCode) {
		List<String> newList = new ArrayList<String>();
		for (String str : list) if (str != null) {
			if (str.trim().isEmpty()) newList.add("");
			else newList.add(chatColorsToString(str,colorCode));
		}
		return newList;
	}
	
	public static void chatColorsActionBar(Player player, Component ... components) {
		Component comp = Component.empty();
		for (Component component : components) comp = comp.append(component);
		player.sendActionBar(comp);
	}
	
	public static String splitCapitalize(String str, String splitReg) {
		return splitCapitalize(str,splitReg,"&");
	}
	
	public static String splitCapitalize(String str, String splitReg, String colorCode) {
		if (str == null || str.trim().isEmpty()) return "";
		String[] splitName = null;
		if (splitReg == null || splitReg.trim().isEmpty()) {
			splitName = new String [] {str};
		}
		else {
			splitName = str.split(splitReg);
		}
		String newStr = "";
		for (String sub : splitName) {
			boolean found = false;
			int i;
			for (i = 0; i < sub.length() - 1; i++) {
				try {
					if (sub.substring(i - 1,i).equalsIgnoreCase(colorCode)) continue;
				} catch (Exception e) {}
				if (sub.substring(i,i+1).matches("[a-zA-Z]+")) {
					found = true;
					break;
				}
			}
			if (found) {
				newStr += sub.substring(0,i) + sub.substring(i,i+1).toUpperCase() + sub.substring(i+1).toLowerCase() + " ";
			}
		}
		Pattern pattern = Pattern.compile(" " + colorCode + "[a-zA-Z0-9]{1}Of ");
		Matcher match = pattern.matcher(newStr);
		while (match.find()) {
			String code = newStr.substring(match.start(),match.end());
			newStr = newStr.replace(code,code.replace("Of ","of "));
			match = pattern.matcher(newStr);
		}
		pattern = Pattern.compile(" " + colorCode + "[a-zA-Z0-9]{1}The ");
		match = pattern.matcher(newStr);
		while (match.find()) {
			String code = newStr.substring(match.start(),match.end());
			newStr = newStr.replace(code,code.replace("The ","the "));
			match = pattern.matcher(newStr);
		}
		newStr = newStr.replace(" Of "," of ");
		newStr = newStr.replace(" The "," the ");
		return newStr.trim();
	}
	
	public static String chatColorsStrip(String str) {
		return ChatColor.stripColor(str);
	}
	
	public static String encode(String str, String regSplit, String regJoin) {
		return String.join(regJoin,str.split(regSplit));
	}
	
	public static ItemStack getFromSlot(Player player, int slot) {
		ItemStack item = null;
		if (slot == -106) item = player.getInventory().getItemInOffHand();
		else if (slot < 0) return item;
		else if (slot == 100) item = player.getInventory().getBoots();
		else if (slot == 101) item = player.getInventory().getLeggings();
		else if (slot == 102) item = player.getInventory().getChestplate();
		else if (slot == 103) item = player.getInventory().getHelmet();
		else item = player.getInventory().getItem(slot);
		return item;
	}
	
	public static int getSlot(Player player, EquipmentSlot slot) {
		if (slot == null) return -1;
		if (slot == EquipmentSlot.HEAD) return 103;
		if (slot == EquipmentSlot.CHEST) return 102;
		if (slot == EquipmentSlot.LEGS) return 101;
		if (slot == EquipmentSlot.FEET) return 100;
		if (slot == EquipmentSlot.OFF_HAND) return -106;
		return player.getInventory().getHeldItemSlot();
	}
	
	public static void setItemSlot(Player player, ItemStack item, int slot) {
		if (slot == -106) player.getInventory().setItemInOffHand(item);
		else if (slot < 0) return;
		else if (slot == 100) player.getInventory().setBoots(item);
		else if (slot == 101) player.getInventory().setLeggings(item);
		else if (slot == 102) player.getInventory().setChestplate(item);
		else if (slot == 103) player.getInventory().setHelmet(item);
		else player.getInventory().setItem(slot,item);
	}
	
	/**
	 * @return if the items are the identical besides the amount
	 */
	public static boolean sameItem(ItemStack item1, ItemStack item2) {
		if (item1 == null || item2 == null) return item1 == item2;
		if (item1 == item2 || item1.equals(item2)) return true;
		ItemStack cmp1 = asQuantity(item1,1);
		ItemStack cmp2 = asQuantity(item2,1);
		return cmp1 == cmp2 || cmp1.equals(cmp2);
	}
	
	/**
	 * @return if the items are the identical besides the amount and the display name
	 */
	public static boolean similarItem(ItemStack item1, ItemStack item2) {
		if (item1 == null || item2 == null) return item1 == item2;
		if (item1 == item2 || item1.equals(item2)) return true;
		ItemStack cmp1 = asQuantity(item1,1);
		ItemStack cmp2 = asQuantity(item2,1);
		if (cmp1 == cmp2 || cmp1.equals(cmp2)) return true;
		ItemMeta meta1 = cmp1.getItemMeta();
		ItemMeta meta2 = cmp2.getItemMeta();
		meta1.displayName(null);
		meta2.displayName(null);
		cmp1.setItemMeta(meta1);
		cmp2.setItemMeta(meta2);
		return cmp1 == cmp2 || cmp1.equals(cmp2);
	}
	
	/**
	 * Pick up items properly from custom set results, example: Anvil, Smithing Table
	 */
	public static void uniqueCraftingHandle(InventoryClickEvent event, int reduce, float pitch) {
		if (!(event.getWhoClicked() instanceof Player) || isNull(event.getInventory().getItem(0)) || isNull(event.getInventory().getItem(1)) ||
				event.getInventory().getItem(1).getAmount() < reduce || (!event.isShiftClick() && !event.isLeftClick() &&
				!event.isRightClick() && event.getHotbarButton() <= -1)) return;
		if (event.getRawSlot() != 2) return;
		Player player = (Player) event.getWhoClicked();
		Inventory inv = event.getInventory();
		ItemStack result = inv.getItem(2);
		/*String item1 = event.getInventory().getItem(0).getItemMeta().getDisplayName();
		if (!Utils.chatColorsStrip(item1).equals(Utils.chatColorsStrip(result.getItemMeta().getDisplayName())) ||
				(item1.contains("§o") && !result.getItemMeta().getDisplayName().contains("§o"))) return;*/
		if (event.isShiftClick()) {
			if (player.getInventory().firstEmpty() == -1) {
				event.setCancelled(true);
				return;
			}
			givePlayer(player,result,false);
		} else if(event.getHotbarButton() != -1) {
			if (!isNull(getFromSlot(player,event.getHotbarButton()))) {
				event.setCancelled(true);
				return;
			}
			setItemSlot(player,result,event.getHotbarButton());
		} else player.setItemOnCursor(result);
		inv.setItem(0,null);
		if (inv.getItem(1).getAmount() > reduce) inv.getItem(1).setAmount(inv.getItem(1).getAmount() - reduce);
		else inv.setItem(1,null);
		inv.setItem(2,null);
		player.updateInventory();
		if (inv.getType() == InventoryType.ANVIL) player.playSound(player.getLocation(),Sound.BLOCK_ANVIL_USE,1,pitch);
		else if (inv.getType() == InventoryType.SMITHING) player.playSound(player.getLocation(),Sound.BLOCK_SMITHING_TABLE_USE,1,pitch);
		return;
	}
	
	/**
	 * @return stored Enchantments in an Enchanted Book
	 */
	public static Map<Enchantment,Integer> getStoredEnchants(ItemStack item) {
		if (isNull(item)) return null;
		if (item.getType() != Material.ENCHANTED_BOOK) return null;
		Map<Enchantment,Integer> enchants = ((EnchantmentStorageMeta) item.getItemMeta()).getStoredEnchants();
		return enchants;
	}
	
	/**
	 * @return number as Roman numerals
	 */
	public static String toRoman(int num) {
		int[] values = {1000,900,500,400,100,90,50,40,10,9,5,4,1};
		String[] romanLiterals = {"M","CM","D","CD","C","XC","L","XL","X","IX","V","IV","I"};
		StringBuilder roman = new StringBuilder();
		for(int i = 0 ; i < values.length; i++)
			while (num >= values[i]) {
				num -= values[i];
				roman.append(romanLiterals[i]);
			}
		return roman.toString();
	}
	
	public static NamespacedKey namespacedKey(String name) {
		return new NamespacedKey(AxUtils.getInstance(),name);
	}
	
	@SuppressWarnings("deprecation")
	public static NamespacedKey namespacedKey(String prefix, String name) {
		return new NamespacedKey(prefix,name);
	}

	public static NamespacedKey toNamespacedKey(String str) {
		String[] splitKey = str.split(":");
		if (splitKey.length == 2 && !splitKey[0].isEmpty() && !splitKey[1].isEmpty()) return namespacedKey(splitKey[0],splitKey[1]);
		return null;
	}
	
	public static boolean isNull(ItemStack item) {
		return item == null || item.getType().isAir();
	}
	
	public static JString JString(String str) {
		return new JString(str);
	}

	@SuppressWarnings("unused")
	private static class JString implements java.io.Serializable {
		private static final long serialVersionUID = 1L;
		String value;
		public JString(String value) {
			super();
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		public void setValue(String value) {
			this.value = value;
		}
		
		@Override
		public String toString(){
			return this.value;
		}
	}
	
	public static class PairInt extends Pair<Integer,Integer> {
		public PairInt(int first, int second) {
			super(first,second);
		}
		
		public PairInt add(PairInt add) {
			return add(add.first(),add.second());
		}
		
		public PairInt add(int first, int second) {
			return new PairInt(this.first() + first,this.second() + second);
		}
	}
	
	/**
	 * Give an item to a player.
	 * If their inventory is full, drops the item at the given location.
	 */
	public static Item givePlayer(Player player, ItemStack item, Location drop, boolean glow) {
		if (player == null || isNull(item)) return null;
		if (!player.isDead() && player.getInventory().addItem(item).isEmpty()) return null;
		if (drop != null) {
			Item droppedItem = dropItem(drop,item);
			droppedItem.setGlowing(glow);
			return droppedItem;
		}
		return null;
	}
	
	/**
	 * Drop an item naturally to the world at a given location
	 * @return the dropped item
	 */
	public static Item dropItem(Location drop, ItemStack item) {
		if (drop == null || item == null) return null;
		return drop.getWorld().dropItemNaturally(drop,item);
	}
	
	public static Item givePlayer(Player player, ItemStack item, boolean glow) {
		if (player == null) return null;
		return givePlayer(player,item,player.getLocation(),glow);
	}
	
	/**
	 * @return the Minecraft version the server is running on
	 */
	public static String getVersion() {
		return Bukkit.getServer().getVersion().split("\\(MC:")[1].split("\\)")[0].trim().split(" ")[0].trim();
	}
	
	/**
	 * @return the main number of the server's version (1)
	 */
	public static int getVersionMain() {
		return Integer.parseInt(getVersion().split("\\.")[0]);
	}

	/**
	 * @return the number of the server's version (14,15,16,etc.)
	 */
	public static int getVersionInt() {
		return Integer.parseInt(getVersion().split("\\.")[1]);
	}
	
	/**
	 * @param digitsAfterDot >= 0
	 * @return the number rounded to specified digits after the dot
	 */
	public static double roundAfterDot(double num, int digitsAfterDot) {
		if (digitsAfterDot < 0) return num;
		if (digitsAfterDot == 0) return (double) Math.round(num);
		String format = "0.";
		for (int i = 0; i < digitsAfterDot; i++) format += "0";
		return Double.parseDouble((new DecimalFormat(format)).format(num));
	}
	
	/**
	 * @return serialized version
	 */
	public static String ObjectToBase64(Object obj) {
		if (obj == null) return null;
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
			dataOutput.writeInt(1);
			dataOutput.writeObject(obj);
			dataOutput.close();
			return Base64Coder.encodeLines(outputStream.toByteArray()).replace("\n","").replace("\r","");
        } catch (Exception e) {}
		return null;
    }
	
	/**
	 * @return deserialized version
	 */
	public static Object ObjectFromBase64(String data) {
		if (data == null) return null;
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			dataInput.readInt();
			Object obj = dataInput.readObject();
			dataInput.close();
			return obj;
		} catch (Exception e) {}
		return null;
    }
	
	public static Object Null() {
		return null;
	}
	
	/**
	 * @return online player from name, null if not found
	 */
	public static Player getOnlinePlayer(String name) {
		Player player = null;
		try {
			for (Player onlinePlayer : Bukkit.getOnlinePlayers()) if (onlinePlayer.getName().equalsIgnoreCase(name)) {
				player = onlinePlayer;
				break;
			}
		} catch (Exception e) {}
		return player;
	}
	
	public static boolean isUndead(LivingEntity entity) {
		return entity instanceof Zombie || entity instanceof ZombieHorse || entity instanceof Skeleton || entity instanceof SkeletonHorse ||
				entity instanceof Zoglin || entity instanceof Phantom || entity instanceof Wither;
	}
	
	public static List<Component> ListStringToListComponent(List<String> strs) {
		if (strs == null) return null;
		List<Component> list = new ArrayList<Component>();
		for (String str : strs) list.add(Component.text(str).decoration(TextDecoration.ITALIC,false));
		return list;
	}
	
	public static ItemStack cloneChange(ItemStack base, Component name, List<Component> lore, int model, boolean removeFlags, ItemFlag ... flags) {
		if (base == null) return null;
		ItemStack item = base.clone();
		ItemMeta meta = item.getItemMeta();
		if (name == null) meta.displayName(null);
		else if (!name.equals(Component.empty())) meta.displayName(name);
		if (removeFlags) for (ItemFlag flag : ItemFlag.values()) meta.removeItemFlags(flag);
		for (ItemFlag flag : flags) if (flag != null) meta.addItemFlags(flag);
		if (model > 0) meta.setCustomModelData(model);
		else if (model == 0) meta.setCustomModelData(null);
		if (lore == null) meta.lore(null);
		else if (!lore.isEmpty()) meta.lore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack makeItem(Material material, Component name, ItemFlag ... itemflag) {
		return makeItem(material,name,null,0,itemflag);
	}
	
	public static ItemStack makeItem(Material material, Component name, int model, ItemFlag ... itemflag) {
		return makeItem(material,name,null,model,itemflag);
	}
	
	public static ItemStack makeItem(Material material, Component name, List<Component> lore, ItemFlag ... itemflag) {
		return makeItem(material,name,lore,0,itemflag);
	}
	
	public static ItemStack makeItem(Material material, Component name, List<Component> lore, int model, ItemFlag ... itemflag) {
		if (material == null) return null;
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		if (name != null) meta.displayName(name);
		if (lore != null) meta.lore(lore);
		for (ItemFlag flag : itemflag) meta.addItemFlags(flag);
		if (model > 0) meta.setCustomModelData(model);
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack makePotion(Component name, int model, List<Component> lore, boolean hide, Color color, PotionData base, PotionEffect ... effects) {
		return makePotion(Material.POTION,name,model,lore,hide,color,base,effects);
	}
	
	public static ItemStack makePotionSplash(Component name, int model, List<Component> lore, boolean hide, Color color, PotionData base, PotionEffect ... effects) {
		return makePotion(Material.SPLASH_POTION,name,model,lore,hide,color,base,effects);
	}
	
	private static ItemStack makePotion(Material material, Component name, int model, List<Component> lore, boolean hide, Color color, PotionData base,
			PotionEffect ... effects) {
		ItemStack item = hide ? makeItem(material,name,lore,model,ItemFlag.HIDE_POTION_EFFECTS) : makeItem(material,name,lore,model);
		PotionMeta meta = (PotionMeta) item.getItemMeta();
		if (base != null) meta.setBasePotionData(base);
		for (PotionEffect effect : effects) meta.addCustomEffect(effect,true);
		if (color != null) meta.setColor(color);
		item.setItemMeta(meta);
		return item;
	}
	
	public static void broadcast(Component component) {
		Bukkit.getServer().sendMessage(component);
	}
	
	public static ItemStack asQuantity(ItemStack item, int amount) {
		return asQuantity(item,amount,false);
	}
	
	public static ItemStack asQuantity(ItemStack item, int amount, boolean allowIllegal) {
		if (isNull(item)) return item;
		ItemStack clone = item.clone();
		int newAmount = Math.max(1,amount);
		if (!allowIllegal) newAmount = Math.min(newAmount,clone.getMaxStackSize());
		clone.setAmount(newAmount);
		return clone;
	}
	
	public static boolean isInteractable(Material material) {
		if (interactable == null) createInteractable();
		return interactable.contains(material);
	}
	
	public static boolean isInteract(Material material, Player player) {
		if (isInteractable(material)) return !player.isSneaking();
		return false;
	}
	
	private static void createInteractable() {
		interactable = new ArrayList<Material>();
		String[] initialInteractable = {"MINECART","CHEST_MINECART","FURNACE_MINECART","HOPPER_MINECART","CHEST","ENDER_CHEST","TRAPPED_CHEST",
				"NOTE_BLOCK","CRAFTING_TABLE","FURNACE","BLAST_FURNACE","LEVER","ENCHANTING_TABLE","BEACON","DAYLIGHT_DETECTOR","HOPPER","DROPPER","REPEATER",
				"COMPARATOR","COMPOSTER","CAKE","BREWING_STAND","LOOM","BARREL","SMOKER","CARTOGRAPHY_TABLE","SMITHING_TABLE","GRINDSTONE",
				"LECTERN","STONECUTTER","DISPENSER","BELL","ITEM_FRAME","FLOWER_POT"};
		addInteractable(initialInteractable);
		addInteractable(Tag.ANVIL.getValues());
		addInteractable(Tag.BUTTONS.getValues());
		addInteractable(Tag.FENCE_GATES.getValues());
		addInteractable(Tag.TRAPDOORS.getValues());
		addInteractable(Tag.SHULKER_BOXES.getValues());
		addInteractable(Tag.DOORS.getValues());
		addInteractable(Tag.BEDS.getValues());
	}
	
	public static void addInteractable(Material ... materials) {
		if (materials == null || materials.length == 0) return;
		for (Material material : materials) if (material != null) interactable.add(material);
	}
	
	public static void addInteractable(String ... materials) {
		if (materials == null || materials.length == 0) return;
		for (String material : materials) if (material != null) addInteractable(Material.getMaterial(material));
	}
	
	public static void addInteractable(Collection<Material> materials) {
		if (materials == null || materials.isEmpty()) return;
		for (Material material : materials) if (material != null) addInteractable(material);
	}
	
	public static <V> List<V> joinLists(List<? extends V> list1, List<? extends V> list2) {
		if (list1 == null) list1 = new ArrayList<V>();
		if (list2 == null) list2 = new ArrayList<V>();
		List<V> list = new ArrayList<V>();
		for (V obj : list1) list.add(obj);
		for (V obj : list2) list.add(obj);
		return list;
	}
	
	public static Inventory makeInventory(InventoryHolder owner, int lines, Component name) {
		if (name == null) return Bukkit.createInventory(owner,lines * 9);
		return Bukkit.createInventory(owner,lines * 9,name);
	}
	
	public static long newSessionID() {
		long id = System.currentTimeMillis() - (long)1e11;
		for (int i = 0; i < ThreadLocalRandom.current().nextInt(5,100); i++) id -= ThreadLocalRandom.current().nextLong((long) 1e10);
		return id;
	}
	
	public static boolean isPlayerNPC(Player player) {
		if (AxUtils.getCitizensManager() == null) return false;
		return AxUtils.getCitizensManager().isNPC(player);
	}
}