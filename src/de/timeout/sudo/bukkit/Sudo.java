package de.timeout.sudo.bukkit;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import de.timeout.libs.config.ColoredLogger;
import de.timeout.libs.config.ConfigCreator;
import de.timeout.libs.config.UTFConfig;
import de.timeout.sudo.bukkit.listener.ModifyWorldListener;
import de.timeout.sudo.bukkit.listener.VanillaPermissionOverrider;
import de.timeout.sudo.bukkit.permissions.BukkitGroupManager;
import de.timeout.sudo.netty.bukkit.BukkitSocket;
import de.timeout.sudo.permissions.GroupConfigurable;

/**
 * Represents the Main-Class of the Bukkit plugin
 * @author Timeout
 *
 */
public class Sudo extends JavaPlugin implements GroupConfigurable<UTFConfig> {
	
	private static final ColoredLogger LOG = new ColoredLogger("&8[&6Sudo&8] ");
	private static final String CONFIG_YML = "config.yml";
	private static final String GROUPS_YML = "groups.yml";
	
	private final UTFConfig spigot = new UTFConfig(new File(getDataFolder().getParentFile().getParentFile(), "spigot.yml"));
	
	private UTFConfig config;
	private UTFConfig groups;
	
	private BukkitSocket netty;
	private BukkitGroupManager groupManager;

	/**
	 * This method returns the instance of the plugin
	 * @return the instance of the plugin
	 */
	@Nonnull
	public static Sudo getInstance() {
		return JavaPlugin.getPlugin(Sudo.class);
	}
	
	/**
	 * Returns the ColoredLogger of this plugin. Cannot be null
	 * @author Timeout
	 * 
	 * @return the colored logger. Cannot be null
	 */
	@Nonnull
	public static ColoredLogger log() {
		return LOG;
	}
	
	@Override
	public UTFConfig getConfig() {
		return config;
	}

	@Override
	public void onDisable() {
		// disable connection to bungeecord server
		this.netty.close();
	}

	@Override
	public void onEnable() {
		// create configurations
		createConfiguration();
		reloadConfig();
		reloadGroupConfig();
		registerManager();
		startSocketClient();
	}
	
	public BukkitGroupManager getGroupManager() {
		return groupManager;
	}
	
	private void startSocketClient() {
		// create server
		netty = new BukkitSocket(
				getConfig().getString("bungeecord.host", "localhost"),
				getConfig().getInt("bungeecord.port", 10020));
		// start server
		Thread clientThread = new Thread(netty);
		clientThread.setName("Sudo-SocketClient Thread");
		clientThread.start();
	}
	
	private void createConfiguration() {
		// create ConfigCreator
		ConfigCreator creator = new ConfigCreator(getDataFolder(), "assets/rytrox/sudo/bukkit");
		// create config.yml
		try {
			creator.loadRessource(CONFIG_YML);
			creator.loadRessource(GROUPS_YML);
		} catch (IOException e) {
			// log error
			LOG.log(Level.WARNING, "Unable to create configurations.", e);
		}
	}
	
	private void registerManager() {
		// register group mananger
		this.groupManager = new BukkitGroupManager(!bungeecordEnabled());
		// register modifyworld
		Bukkit.getPluginManager().registerEvents(new ModifyWorldListener(), this);
		// register overrider
		Bukkit.getPluginManager().registerEvents(new VanillaPermissionOverrider(), this);
	}

	/**
	 * Checks if bungeecord is enabled
	 * @author Timeout
	 * 
	 * @return true if bungeecord is enabled. Else false
	 */
	public boolean bungeecordEnabled() {
		// load spigot.yml
		return spigot.getBoolean("settings.bungeecord");
	}
	
	/**
	 * Returns the current netty-server. <br>
	 * Returns null if the plugin performs Bukkit-Mode
	 * @author Timeout
	 * 
	 * @return the current netty-server
	 */
	@Nullable
	public BukkitSocket getNetty() {
		return netty;
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
			LOG.log(Level.WARNING, "&cUnable to save config.yml.", e);
		}
	}

	@Override
	public void reloadGroupConfig() {
		this.groups = new UTFConfig(new File(getDataFolder(), GROUPS_YML));
	}

	@Override
	public UTFConfig getGroupConfig() {
		return groups;
	}

	@Override
	public void saveGroupConfig() {
		try {
			this.groups.save(new File(getDataFolder(), GROUPS_YML));
		} catch (IOException e) {
			LOG.log(Level.WARNING, "&cCannot save groups.yml", e);
		}
	}
}
