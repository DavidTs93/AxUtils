package me.Aldreda.AxUtils;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import me.Aldreda.AxUtils.Events.Callers.EventCallers;
import me.Aldreda.AxUtils.Listeners.CancelPlayers;
import me.Aldreda.AxUtils.Listeners.DisableDefaultFeaturesListener;
import me.Aldreda.AxUtils.Utils.CitizensManager;
import me.Aldreda.AxUtils.Utils.PlaceholderManager;
import me.Aldreda.AxUtils.Utils.Utils;
import me.Aldreda.AxUtils.Utils.WorldGuardManager;
import me.DMan16.AxEconomy.AxEconomy;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Iterator;

public class AxUtils extends JavaPlugin {
	private static AxUtils instance = null;
	public static final String pluginName = "Aldreda";
	public static final String pluginNameColors = "&6&lAldreda";
	private static MySQL SQL = null;
	private static AxEconomy economy = null;
	private static WorldGuardManager WorldGuardManager = null;
	private static PlaceholderManager PAPIManager = null;
	private static CitizensManager CitizensManager = null;
	private static ProtocolManager ProtocolManager;

	public void onLoad() {
		if (getServer().getPluginManager().getPlugin("WorldGuard") != null) WorldGuardManager = new WorldGuardManager();
	}
	
	public void onEnable() {
		saveDefaultConfig();
		instance = this;
		try {
			SQL = new MySQL(getConfig().getString("mysql.host"),getConfig().getInt("mysql.port"),getConfig().getString("mysql.database"),
					getConfig().getString("mysql.username"),getConfig().getString("mysql.password"));
		} catch (SQLException e) {
			this.getLogger().severe("MySQL error: ");
			e.printStackTrace();
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		Utils.chatColorsLogPlugin("&aConnected to MySQL database");
		firstOfAll();
		Utils.chatColorsLogPlugin("&aLoaded, running on version: &f" + Utils.getVersion() + "&a, Java version: &f" + Utils.javaVersion());
		if (WorldGuardManager != null) Utils.chatColorsLogPlugin("&aHooked to &fWorldGuard");
		if (PAPIManager != null) Utils.chatColorsLogPlugin("&aHooked to &fPlaceholderAPI");
		if (CitizensManager != null) Utils.chatColorsLogPlugin("&aHooked to &fCitizens");
		if (ProtocolManager != null) Utils.chatColorsLogPlugin("&aHooked to &fProtocolLib");
	}

	public void onDisable() {
		Bukkit.getScheduler().cancelTasks(this);
		Utils.chatColorsLogPlugin(pluginNameColors + " &adisabed");
	}
	
	private void firstOfAll() {
		disableNetheriteUpgrade();
		new EventCallers();
		new DisableDefaultFeaturesListener();
		new CancelPlayers(instance);
		if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) PAPIManager = new PlaceholderManager();
		if (getServer().getPluginManager().getPlugin("Citizens") != null) CitizensManager = new CitizensManager();
		if (getServer().getPluginManager().getPlugin("ProtocolLib") != null) ProtocolManager = ProtocolLibrary.getProtocolManager();
	}

	private void disableNetheriteUpgrade() {
		Iterator<Recipe> recipes = Bukkit.getServer().recipeIterator();
		while (recipes.hasNext()) {
			Recipe recipe = recipes.next();
			if (recipe instanceof SmithingRecipe) {
				String namespace = ((SmithingRecipe) recipe).getKey().getNamespace().toLowerCase();
				String key = ((SmithingRecipe) recipe).getKey().getKey().toLowerCase();
				if (namespace.equals("minecraft") && key.startsWith("netherite_") && key.endsWith("_smithing")) recipes.remove();
			}
		}
	}
	
	public static final AxUtils getInstance() {
		return instance;
	}
	
	public static final MySQL getMySQL() {
		return SQL;
	}
	
	public static final void AxEconomyReady() {
		if (Bukkit.getServer().getPluginManager().getPlugin("AxEconomy") != null) try {
			economy = (AxEconomy) Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class).getProvider();
			Utils.chatColorsLogPlugin("&aHooked to &fAxEconomy!");
		} catch (Exception e) {}
	}
	
	public static final AxEconomy getEconomy() {
		return economy;
	}
	
	public static final WorldGuardManager getWorldGuardManager() {
		return WorldGuardManager;
	}
	
	public static final PlaceholderManager getPAPIManager() {
		return PAPIManager;
	}
	
	public static final CitizensManager getCitizensManager() {
		return CitizensManager;
	}
	
	public static final ProtocolManager getProtocolManager() {
		return ProtocolManager;
	}
}