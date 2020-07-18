package de.timeout.sudo.bukkit.listener;

import java.util.UUID;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

import de.timeout.sudo.bukkit.Sudo;

public class BukkitModeListener implements Listener {

	private static final Sudo main = Sudo.getInstance();
	
	/**
	 * Creates a new Listener and loads the user data of all users
	 * @author Timeout
	 *
	 */
	public BukkitModeListener() {
		// load all current onlineplayers
		Bukkit.getOnlinePlayers().forEach(player -> loadUser(player.getUniqueId()));
	}
	
	@EventHandler
	public void onLoadingUser(AsyncPlayerPreLoginEvent event) {
		// if event is not cancelled
		if(event.getLoginResult() == Result.ALLOWED) {
			// load user
			loadUser(event.getUniqueId());
		}
	}
	
	/**
	 * loads a user from file configuration
	 * @author Timeout
	 * 
	 * @param uuid the uuid of the user
	 * @throws IllegalArgumentException if the uuid is null
	 */
	private void loadUser(@Nonnull UUID uuid) {
		// Validate
		Validate.notNull(uuid, "UUID cannot be null");
		// load user
		main.getUserManager().loadUserFromFile(uuid);
	}
	
	@EventHandler
	public void onUserUnload(PlayerQuitEvent event) {
		// save in file
		main.getUserManager().unloadUserToFile(event.getPlayer());
	}
}
