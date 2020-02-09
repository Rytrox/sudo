package de.timeout.sudo.bukkit;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import de.timeout.libs.config.UTFConfig;
import net.md_5.bungee.api.ChatColor;

/**
 * Represents the Main-Class of the Bukkit plugin
 * @author Timeout
 *
 */
public class Sudo extends JavaPlugin {

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
	}

	@Override
	public void reloadConfig() {
		this.config = new UTFConfig(new File(getDataFolder(), "config.yml"));
	}

	@Override
	public void saveConfig() {
		try {
			this.getConfig().save(new File(getDataFolder(), "config.yml"));
		} catch (IOException e) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &cUnable to save config.yml. IO-Exception: " + e));
		}
	}
	
	
}
