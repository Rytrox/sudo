package de.timeout.sudo.bungee;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.timeout.libs.config.ColoredLogger;
import de.timeout.libs.config.ConfigCreator;
import de.timeout.sudo.bungee.permissions.ProxyGroupManager;
import de.timeout.sudo.netty.bungeecord.BungeeSocketServer;
import de.timeout.sudo.permissions.GroupConfigurable;

import net.jafama.FastMath;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class Sudo extends Plugin implements GroupConfigurable<Configuration> {
	
	private static final ConfigurationProvider PROVIDER = ConfigurationProvider.getProvider(YamlConfiguration.class);
	private static final ColoredLogger LOG = new ColoredLogger("&8[&6Sudo&8] ");
	
	private static final String CONFIG_YML = "config.yml";
	private static final String GROUPS_YML = "groups.yml";
	private static final String LOAD_ERROR = "&cUnable to read %s";
	private static final String SAVE_ERROR = "&cUnable to save %s";

	private static Sudo instance;
	
	private ProxyGroupManager groupManager;
	private BungeeSocketServer netty;
	
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
		LOG.log(Level.INFO, "&7Load &6Sudo &eVersion 0.0.1-SNAPSHOT");
		// initialize instance
		instance = this;
		// load configurations
		loadConfigurations();
		// reload configs
		reloadConfig();
		reloadGroupConfig();
		// start netty server
		startSocketServer();
		// initialize manager
		initializeManager();
	}

	private void initializeManager() {
		// initialize group manager
		groupManager = new ProxyGroupManager();
		// register listener
		this.getProxy().getPluginManager().registerListener(this, groupManager);
	}

	@Override
	public void onDisable() {
		// disable server
		netty.close();
	}
	
	/**
	 * Starts the Socket-Server
	 * @author Timeout
	 *
	 */
	private void startSocketServer() {
		// create netty server
		netty = new BungeeSocketServer(FastMath.abs(getConfig().getInt("netty.port", 10020)));
		// start netty server
		Thread serverThread = new Thread(netty);
		serverThread.setName("SudoServer-Thread");
		serverThread.start();
	}
	
	private void loadConfigurations() {
		// load ConfigCreator
		ConfigCreator creator = new ConfigCreator(getDataFolder(), "assets/rytrox/sudo/bungee");
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
			LOG.log(Level.WARNING, String.format(LOAD_ERROR, CONFIG_YML));
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
			LOG.log(Level.WARNING, String.format(SAVE_ERROR, CONFIG_YML), e);
		}
	}
	
	@Override
	public void reloadGroupConfig() {
		try {
			groups = PROVIDER.load(new File(getDataFolder(), GROUPS_YML));
		} catch (IOException e) {
			LOG.log(Level.WARNING, String.format(LOAD_ERROR, GROUPS_YML), e);
		}
	}
	
	@Override
	public Configuration getGroupConfig() {
		return groups;
	}
	
	@Override
	public void saveGroupConfig() {
		try {
			PROVIDER.save(config, new File(getDataFolder(), GROUPS_YML));
		} catch (IOException e) {
			LOG.log(Level.WARNING, String.format(SAVE_ERROR, GROUPS_YML), e);
		}
	}
	
	/**
	 * Returns the Netty-Server. Cannot be null
	 * @author Timeout
	 * 
	 * @return the Netty-Server
	 */
	@Nonnull
	public BungeeSocketServer getNettyServer() {
		// load server if the server is null
		if(netty == null) startSocketServer();
		return netty;
	}
}
