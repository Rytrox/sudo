package de.timeout.sudo.bukkit;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonObject;

import de.timeout.libs.config.ColoredLogger;
import de.timeout.libs.config.ConfigCreator;
import de.timeout.libs.config.UTFConfig;
import de.timeout.sudo.bukkit.messenger.PluginMessageFuture;
import de.timeout.sudo.bukkit.permissions.BukkitGroupManager;
import de.timeout.sudo.netty.bukkit.BukkitSocket;
import de.timeout.sudo.permissions.GroupConfigurable;

import net.md_5.bungee.api.ChatColor;

/**
 * Represents the Main-Class of the Bukkit plugin
 * @author Timeout
 *
 */
public class Sudo extends JavaPlugin implements GroupConfigurable<UTFConfig> {
	
	private static final ColoredLogger LOG = new ColoredLogger("&8[&6Sudo&8] ");
	private static final String CONFIG_YML = "config.yml";
	private static final String GROUPS_YML = "groups.yml";

	private static Sudo instance;
	
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
		return instance;
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
		// initialize instance
		instance = this;
		// create configurations
		createConfiguration();
		reloadConfig();
		reloadGroupConfig();
		startSocketClient();
		
//		// load groupsmanager
//		groupManager = new BukkitGroupManager(!bungeecordEnabled());
		
		
		// DEBUG:
//		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("PlayerCount");
		out.writeUTF("remote");
//		
//		this.getServer().sendPluginMessage(instance, "BungeeCord", out.toByteArray());
		Bukkit.getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new TestFuture("BungeeCord", out));
	}
	
	public BukkitGroupManager getGroupManager() {
		return groupManager;
	}
	
	/**
	 * Checks in server's spigot.yml if bungeecord is enabled. <br>
	 * Returns false if the spigot.yml cannot be found
	 * 
	 * @author Timeout
	 * 
	 * @return if bungeecord is enabled or false for not found
	 */
	private boolean bungeecordEnabled() {
		// get spigot.yml
		UTFConfig spigot = new UTFConfig(new File(getDataFolder().getParentFile().getParentFile(), "spigot.yml"));
		// check if bungeecord is enabled. Return false for not found
		return spigot.getBoolean("settings.bungeecord", false);
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
	
	private static class TestFuture extends PluginMessageFuture {

		public TestFuture(String channel, ByteArrayDataOutput out) {
			super(channel, out);
		}

		@Override
		protected JsonObject readData(ByteArrayDataInput input) {
			// create jsonobject
			JsonObject object = new JsonObject();
			object.addProperty("name", input.readUTF());
			object.addProperty("count", input.readInt());
			return object;
		}
		
	}
}
