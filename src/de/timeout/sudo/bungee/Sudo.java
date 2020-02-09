package de.timeout.sudo.bungee;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nullable;

import de.timeout.sudo.bungee.groups.GroupManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class Sudo extends Plugin {

	private static Sudo instance;
	
	private GroupManager groupManager;
	
	private Configuration config;

	/**
	 * Returns the Main-Instance of the Plugin. <br>
	 * Returns null if the plugin is not loaded. <b> Be sure it is loaded before calling this method
	 * 
	 * @return the instance or null if the plugin is not loaded
	 */
	@Nullable
	public static Sudo getInstance() {
		return instance;
	}
	
	/**
	 * Returns the group manager. <br>
	 * Returns null if the GroupManager is not loaded. Be sure to call {@link Sudo#initializeManager()} first.
	 * 
	 * @return the groupmanager or null
	 */
	@Nullable
	public GroupManager getGroupManager() {
		return groupManager;
	}
	
	@Override
	public void onEnable() {
		// initialize instance
		instance = this;
		// initialize manager
		initializeManager();
	}

	private void initializeManager() {
		groupManager = new GroupManager();
	}

	@Override
	public void onDisable() {
		
	}
	
	/**
	 * Loads the configuration file from data folder
	 */
	public void reloadConfig() {
		try {
			config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
		} catch (IOException e) {
			getProxy().getConsole().sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &cUnable to load config.yml from datafolder. IO-Exception: " + e)));
		}
	}
	
	/**
	 * Returns the loaded configuration. <br>
	 * Be sure to call {@link Sudo#reloadConfig()} before using this method
	 * 
	 * @return the configuration or null if the configuration is not loaded yet
	 */
	@Nullable
	public Configuration getConfig() {
		return config;
	}
	
	public void saveConfig() {
		try {
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(getDataFolder(), "config.yml"));
		} catch (IOException e) {
			getProxy().getConsole().sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &cUnable to write config.yml. IO-Exception: " + e)));
		}
	}
}
