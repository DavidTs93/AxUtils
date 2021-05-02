package me.Aldreda.AxUtils.Classes;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Listener class with functions to register and unregister.
 * Extend this class with your listener. Don't forget to register the listener via {@link #register}! 
 */
public abstract class Listener implements org.bukkit.event.Listener {
	protected boolean isRegistered = false;
	
	protected void register(JavaPlugin instance) {
		Bukkit.getServer().getPluginManager().registerEvents(this,instance);
		isRegistered = true;
	}
	
	protected void unregister() {
		HandlerList.unregisterAll(this);
		isRegistered = false;
	}
}