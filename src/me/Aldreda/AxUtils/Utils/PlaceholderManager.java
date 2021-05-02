package me.Aldreda.AxUtils.Utils;

import java.util.List;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;

public class PlaceholderManager {
	
	public String translate(Player player, String str) {
		if (str == null) return null;
		return PlaceholderAPI.setPlaceholders(player,str);
	}
	
	public List<String> translate(Player player, List<String> str) {
		if (str == null) return null;
		return PlaceholderAPI.setPlaceholders(player,str);
	}
}