package de.timeout.sudo.bungee;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.timeout.libs.config.ColoredLogger;
import de.timeout.libs.config.ConfigCreator;
import de.timeout.sudo.bungee.commands.CustomizeCommand;
import de.timeout.sudo.bungee.commands.GroupCommand;
import de.timeout.sudo.bungee.commands.SudoCommand;
import de.timeout.sudo.bungee.groups.ProxyGroupManager;
import de.timeout.sudo.bungee.netty.BungeeSocketServer;
import de.timeout.sudo.bungee.users.ProxyUserManager;
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
	private ProxyUserManager userManager;
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
	@NotNull
	public static ColoredLogger log() {
		return LOG;
	}
	
	
	/**
	 * Returns the group manager. Cannot be null
	 * @author Timeout
	 * 
	 * @return the group manager. Cannot be null
	 */
	@NotNull
	public ProxyGroupManager getGroupManager() {
		return groupManager;
	}
	
	/**
	 * Returns the user manager. Cannot be null
	 * @author Timeout
	 * 
	 * @return the user manager. Cannot be null
	 */
	@NotNull
	public ProxyUserManager getUserManager() {
		return userManager;
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
		// initialize command
		registerCommands();
	}

	private void initializeManager() {
		// initialize group manager
		groupManager = new ProxyGroupManager();
		
		// initialize sudoer manager
		try {
			userManager = new ProxyUserManager();
			this.getProxy().getPluginManager().registerListener(instance, userManager);
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Unable to start root-user", e);
		}
	}
	
	private void registerCommands() {
//		this.getProxy().getPluginManager().registerCommand(instance, new SudoCommand());
//		this.getProxy().getPluginManager().registerCommand(instance, new CustomizeCommand());
//		this.getProxy().getPluginManager().registerCommand(instance, new GroupCommand());
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
		// create configs
		try {
			ConfigCreator.loadRessource("/assets/timeout/sudo/bungee/sudoers.out", new File(getDataFolder(), "sudoers.out"));
		} catch (IOException e) {
			LOG.log(Level.WARNING, "&cCannot load configurations from Plugin.", e);
		}
	}
	
	/**
	 * Loads the configuration file from data folder
	 */
	public void reloadConfig() {
		try {
			config = PROVIDER.load(ConfigCreator.loadRessource("/assets/timeout/sudo/bungee/config.yml", new File(getDataFolder(), CONFIG_YML)));
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
			groups = PROVIDER.load(ConfigCreator.loadRessource("/assets/timeout/sudo/bungee/groups.yml", new File(getDataFolder(), GROUPS_YML)));
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
	@NotNull
	public BungeeSocketServer getNettyServer() {
		// load server if the server is null
		if(netty == null) startSocketServer();
		return netty;
	}
}
