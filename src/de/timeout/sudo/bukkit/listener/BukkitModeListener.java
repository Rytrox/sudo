package de.timeout.sudo.bukkit.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

import de.timeout.sudo.bukkit.Sudo;

public class BukkitModeListener implements Listener {

	private static final Sudo main = Sudo.getInstance();
	
	@EventHandler
	public void onLoadingUser(AsyncPlayerPreLoginEvent event) {
		// if event is not cancelled
		if(event.getLoginResult() == Result.ALLOWED) {
			// load user
			main.getGroupManager().loadUserFromFile(event.getUniqueId());
		}
	}
	
	@EventHandler
	public void onUserUnload(PlayerQuitEvent event) {
		// save in file
		main.getGroupManager().unloadUserToFile(event.getPlayer());
	}
}
