package de.timeout.sudo.bukkit;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import de.timeout.libs.config.ConfigCreator;
import de.timeout.libs.config.UTFConfig;
import de.timeout.sudo.bukkit.connectors.ProxyMessageHandler;
import net.md_5.bungee.api.ChatColor;

/**
 * Represents the Main-Class of the Bukkit plugin
 * @author Timeout
 *
 */
public class Sudo extends JavaPlugin {
	
	private static final String CONFIG_YML = "config.yml";

	private static Sudo instance;
	
	private UTFConfig config;

	/**
	 * This method returns the instance of the plugin
	 * @return the instance of the plugin
	 */
	@Nonnull
	public static Sudo getInstance() {
		return instance;
	}
	
	@Override
	public UTFConfig getConfig() {
		return config;
	}

	@Override
	public void onDisable() {

	}

	@Override
	public void onEnable() {
		// initialize instance
		instance = this;
		// create configurations
		createConfiguration();
		reloadConfig();
		
		// check default settings
		// if bungeecord is enabled
		if(config.getBoolean("bungeecord", true)) {
			// log message in console
			log("Bungeecord is &aenabled&7. Wait for Bungeecord Settings...");
			// register plugin-message channel
			Bukkit.getMessenger().registerIncomingPluginChannel(instance, "sudo", new ProxyMessageHandler());
			Bukkit.getMessenger().unregisterOutgoingPluginChannel(instance, "sudo");
		}
	}
	
	private void createConfiguration() {
		// create ConfigCreator
		ConfigCreator creator = new ConfigCreator(getDataFolder(), "assets/sudo/bukkit");
		// create config.yml
		try {
			creator.loadRessource(CONFIG_YML);
		} catch (IOException e) {
			// log error
			error("Unable to create configurations.", e);
		}
	}

	@Override
	public void reloadConfig() {
		this.config = new UTFConfig(new File(getDataFolder(), CONFIG_YML));
	}

	@Override
	public void saveConfig() {
		try {
			this.getConfig().save(new File(getDataFolder(), CONFIG_YML));
		} catch (IOException e) {
			// log error
			error("Unable to save config.yml.", e);
		}
	}
	
	/**
	 * Prints an error in Console
	 * Does nothing when message or exception is null
	 * 
	 * @param message the error message
	 * @param e the exception itself
	 */
	public void error(String message, Throwable e) {
		// validate
		if(message != null && e != null) 
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(Locale.ENGLISH, "&8[&6Sudo&8] &c%s %s:%s", message, e.getClass().getName(), e.getMessage())));
	}
	
	public void log(String message) {
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(Locale.ENGLISH, "&8[&6Sudo&8] &7%s", (message != null ? message : ""))));
	}
}
