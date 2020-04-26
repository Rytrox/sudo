package de.timeout.sudo.bukkit.security;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import de.timeout.sudo.groups.User;

public class BukkitSudoHandler implements Listener {

	private final Map<User, String> passwordAwaiting = new HashMap<>(); 
	private final Set<User> blocked = new HashSet<>();
	
	@EventHandler
	public void onPasswordInput(AsyncPlayerChatEvent event) {
		// send packet to client
	}
	
	public void awaitAuthentification(@Nonnull User user, @Nonnull String command) {
		
	}
}
