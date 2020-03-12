package de.timeout.sudo.bungee;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.timeout.libs.config.ColoredLogger;
import de.timeout.libs.config.ConfigCreator;
import de.timeout.sudo.bungee.permissions.ProxyGroupManager;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class Sudo extends Plugin {
	
	private static final ConfigurationProvider PROVIDER = ConfigurationProvider.getProvider(YamlConfiguration.class);
	private static final ColoredLogger LOG = new ColoredLogger("&8[&6Sudo&8] ");
	private static final String CONFIG_YML = "config.yml";
	private static final String GROUPS_YML = "groups.yml";

	private static Sudo instance;
	
	private ProxyGroupManager groupManager;
	
	private Configuration config;
	private Configuration groups;

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
	 * Returns the colored logger of the plugin
	 * @author Timeout
	 * 
	 * @return the colored logger of the plugin
	 */
	@Nonnull
	public static ColoredLogger log() {
		return LOG;
	}
	
	
	/**
	 * Returns the group manager. <br>
	 * Returns null if the GroupManager is not loaded. Be sure to call {@link Sudo#initializeManager()} first.
	 * 
	 * @return the groupmanager or null
	 */
	@Nullable
	public ProxyGroupManager getGroupManager() {
		return groupManager;
	}
	
	@Override
	public void onEnable() {
		// initialize instance
		instance = this;
		// load configurations
		loadConfigurations();
		// reload configs
		reloadConfig();
		reloadGroupConfig();
		// initialize manager
		initializeManager();
	}

	private void initializeManager() {
		groupManager = new ProxyGroupManager();
	}

	@Override
	public void onDisable() {
		
	}
	
	private void loadConfigurations() {
		// load ConfigCreator
		ConfigCreator creator = new ConfigCreator(getDataFolder(), "assets/sudo/bungee");
		// create configs
		try {
			creator.loadRessource(CONFIG_YML);
			creator.loadRessource(GROUPS_YML);
		} catch (IOException e) {
			LOG.log(Level.WARNING, "&cCannot load configurations from Plugin.");
		}
	}
	
	/**
	 * Loads the configuration file from data folder
	 */
	public void reloadConfig() {
		try {
			config = PROVIDER.load(new File(getDataFolder(), CONFIG_YML));
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
			PROVIDER.save(config, new File(getDataFolder(), CONFIG_YML));
		} catch (IOException e) {
			getProxy().getConsole().sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &cUnable to write config.yml. IO-Exception: " + e.toString())));
		}
	}
	
	public void reloadGroupConfig() {
		try {
			PROVIDER.load(new File(getDataFolder(), GROUPS_YML));
		} catch (IOException e) {
			getProxy().getConsole().sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &cUnable to read groups.yml. IO-Exception: " + e.toString())));
		}
	}
	
	@Nullable
	public Configuration getGroupConfig() {
		return groups;
	}
	
	public void saveGroupConfig() {
		try {
			PROVIDER.save(config, new File(getDataFolder(), GROUPS_YML));
		} catch (IOException e) {
			getProxy().getConsole().sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &cUnable to save groups.yml. IO-Exception: " + e.toString())));
		}
	}
}
