package me.Aldreda.AxUtils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.entity.EntityPotionEffectEvent.Cause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public class ReflectionUtils {
	public static String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	
	private static String removeUnnecessaryDots(String str) {
		return Arrays.stream(str.split("\\.")).filter(s -> !s.isEmpty()).collect(Collectors.joining("."));
	}
	
	public static Class<?> getClassNMS(String name, String subPackageNameNewNMS) {
		Class clazz = null;
		try {
			clazz = Class.forName(removeUnnecessaryDots("net.minecraft." + (Utils.getVersionInt() >= 17 ? subPackageNameNewNMS : "server." + version) + "." + name));
		} catch (Exception e) {}
		return clazz;
	}
	
	public static Class<?> getClassCraftBukkit(String name) {
		Class clazz = null;
		try {
			clazz = Class.forName(removeUnnecessaryDots("org.bukkit.craftbukkit." + version + "." + name));
		} catch (Exception e) {}
		return clazz;
	}

	/**
	 * @param item Bukkit ItemStack
	 * @return item's material's translatable name, example: Material.APPLE -> "item.minecraft.apple"
	 */
	public static String getItemTranslatableName(ItemStack item) {
		String name = null;
		try {
			Class<?> craftItemClass = getClassCraftBukkit("inventory.CraftItemStack");
			Class<?> nmsItemStackClass = getClassNMS("ItemStack","world.item");
			Method methodNMSCopy = craftItemClass.getMethod("asNMSCopy",ItemStack.class);
			Method methodGetItem = nmsItemStackClass.getMethod("getItem");
			Class<?> nmsItemClass = getClassNMS("Item","world.item");
			Method methodGetName = nmsItemClass.getMethod("getName");
			Object ItemStackNMS = methodNMSCopy.invoke(null,item);
			Object ItemNMS = methodGetItem.invoke(ItemStackNMS);
			name = (String) methodGetName.invoke(ItemNMS);
		} catch (Exception e) {}
		return name;
	}
	
	/**
	 * @param item Bukkit ItemStack
	 * @return CraftBukkit ItemStack
	 */
	public static Object ItemAsNMSCopy(ItemStack item) {
		try {
			Class<?> craftItemClass = getClassCraftBukkit("inventory.CraftItemStack");
			Method methodNMSCopy = craftItemClass.getMethod("asNMSCopy",ItemStack.class);
			return methodNMSCopy.invoke(null,item);
		} catch (Exception e) {}
		return null;
	}
	
	/**
	 * @param item CraftBukkit ItemStack
	 * @return Bukkit ItemStack
	 */
	public static ItemStack ItemAsBukkitCopy(Object item) {
		try {
			Class<?> craftItemClass = getClassCraftBukkit("inventory.CraftItemStack");
			Class<?> itemClass = getClassNMS("ItemStack","world.item");
			Method methodBukkitCopy = craftItemClass.getMethod("asBukkitCopy",itemClass);
			return (ItemStack) methodBukkitCopy.invoke(null,item);
		} catch (Exception e) {}
		return null;
	}
	
	public static ItemStack getTridentAsItemStack(Trident trident) {
		try {
			Class<?> craftClass = getClassCraftBukkit("inventory.CraftItemStack");
			Class<?> itemClass = getClassNMS("ItemStack","world.item");
			Class<?> tridentClass = getClassCraftBukkit("entity.CraftTrident");
			Method methodBukkitCopy = craftClass.getMethod("asBukkitCopy",itemClass);
			Method methodGetHandle = tridentClass.getDeclaredMethod("getHandle");
			Object castTrident = tridentClass.cast(trident);
			Object tridentEntity = methodGetHandle.invoke(castTrident);
			Field field = tridentEntity.getClass().getDeclaredField("trident");
			field.setAccessible(true);
			Object tridentHandle = field.get(tridentEntity);
			Object obj = methodBukkitCopy.invoke(tridentHandle.getClass(),tridentHandle);
			return (ItemStack) obj;
		} catch (Exception e) {}
		return null;
	}
	
	public static boolean[] addEffects(Player player, Cause cause, List<PotionEffect> effects) {
		if (effects == null || effects.isEmpty()) return null;
		boolean[] result = new boolean[effects.size()];
		Arrays.fill(result,false);
		try {
			Class<?> classCraftPlayer = getClassCraftBukkit("entity.CraftPlayer");
			Method methodGetHandle = classCraftPlayer.getDeclaredMethod("getHandle");
			Class<?> classEntityLiving = getClassNMS("EntityLiving","world.entity");
			Class<?> classMobEffect = getClassNMS("MobEffect","world.effect");
			Class<?> classMobEffectList = getClassNMS("MobEffectList","world.effect");
			Method methodFromId = classMobEffectList.getMethod("fromId",int.class);
			Object EntityLivingPlayer = methodGetHandle.invoke(player);
			Constructor<?> MobEffectConstructor = classMobEffect.getConstructor(classMobEffectList,int.class,int.class,
					boolean.class,boolean.class,boolean.class);
			Method methodAddEffect = classEntityLiving.getMethod("addEffect",classMobEffect,cause.getClass());
			for (int i = 0; i < effects.size(); i++) {
				PotionEffect effect = effects.get(i);
				try {
					Object MobEffect = MobEffectConstructor.newInstance(methodFromId.invoke(null,effect.getType().getId()),effect.getDuration() * 20,
							effect.getAmplifier(),effect.isAmbient(),effect.hasParticles(),effect.hasIcon());
					result[i] = (boolean) methodAddEffect.invoke(EntityLivingPlayer,MobEffect,cause);
				} catch (Exception e1) {}
			}
		} catch (Exception e) {}
		return result;
	}

	public static boolean[] addEffects(Player player, Cause cause, PotionEffect ... effects) {
		return addEffects(player,cause,Arrays.asList(effects));
	}
	
	public static boolean[] removeEffects(Player player, Cause cause, List<PotionEffectType> effects) {
		if (effects == null || effects.isEmpty()) return null;
		boolean[] result = new boolean[effects.size()];
		Arrays.fill(result,false);
		try {
			Class<?> classCraftPlayer = getClassCraftBukkit("entity.CraftPlayer");
			Method methodGetHandle = classCraftPlayer.getDeclaredMethod("getHandle");
			Class<?> classEntityLiving = getClassNMS("EntityLiving","world.entity");
			Class<?> classMobEffectList = getClassNMS("MobEffectList","world.effect");
			Method methodFromId = classMobEffectList.getMethod("fromId",int.class);
			Object EntityLivingPlayer = methodGetHandle.invoke(player);
			Method methodRemoveEffect = classEntityLiving.getMethod("removeEffect",classMobEffectList,cause.getClass());
			for (int i = 0; i < effects.size(); i++) {
				PotionEffectType effect = effects.get(i);
				try {
					result[i] = (boolean) methodRemoveEffect.invoke(EntityLivingPlayer,methodFromId.invoke(null,effect.getId()),cause);
				} catch (Exception e1) {}
			}
		} catch (Exception e) {}
		return result;
	}

	public static boolean[] removeEffects(Player player, Cause cause, PotionEffectType ... effects) {
		return removeEffects(player,cause,Arrays.asList(effects));
	}
}