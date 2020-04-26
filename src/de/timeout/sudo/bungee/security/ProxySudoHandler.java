package de.timeout.sudo.bungee.security;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;

import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.groups.User;
import de.timeout.sudo.security.Sudoer;

import net.jafama.FastMath;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ProxySudoHandler implements Listener {
	
	private static final Sudo main = Sudo.getInstance();
	
	private final Map<Sudoer, Entry<String, AtomicInteger>> passwordAwaiting = new HashMap<>();
	private final Set<Sudoer> lockedUsers = new HashSet<>();
	
	private final int maxAttempts = FastMath.max(FastMath.abs(main.getConfig().getInt("sudo.maxAttempts", 3)), 1);
	private final boolean lock = main.getConfig().getBoolean("sudo.lock", true);
	
	@EventHandler
	public void onUserPasswordInput(ChatEvent event) {
		// check if executor is a player
		if(event.getSender() instanceof ProxiedPlayer) {
			// get proxiedplayer
			ProxiedPlayer p = (ProxiedPlayer) event.getSender();
			// get proxiedplayers profile
			User user = main.getGroupManager().getUser(p);
			// check if user is sudoer
			if(user instanceof Sudoer) {
				// get sudoer
				Sudoer sudoer = (Sudoer) user;
				// get awaited command
				Entry<String, AtomicInteger> commandAndAttempts = passwordAwaiting.get(sudoer);
				// check if the proxy is awaiting a password of the sudoer
				if(commandAndAttempts != null) {
					// get command and attempts
					String command = commandAndAttempts.getKey();
					AtomicInteger attempts = commandAndAttempts.getValue();
						
					// perform command if the user is authorized
					if(!(sudoer.authorize(event.getMessage()))) {
						// abort if the count has reached the end
						if(attempts.incrementAndGet() >= maxAttempts) {
							// send message
							p.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&',
									String.format("&8[&6Sudo&8] &c%d failed attempts to record the password", maxAttempts))));
							// lock user if lock is enabled
							if(lock) lockedUsers.add(sudoer);
						} else return;
					} else main.getProxy().getPluginManager().dispatchCommand(p, command);
				}
				// remove from map
				passwordAwaiting.remove(sudoer);
			} else p.sendMessage(new TextComponent(
					ChatColor.translateAlternateColorCodes('&', 
					String.join("&8[&6Sudo&8] &c%s is not in the sudoers file. This incident will be reported", p.getName()))));
		}
	}
	
	/**
	 * Prepares the plugin for a new authorization process. <br>
	 * Does nothing if the user is already authorized
	 * @author Timeout
	 * 
	 * @param user the user who wants to authorize
	 * @param command the command without sudo-process
	 * @throws IllegalStateException if the user is already in the authorization process.
	 */
	public void awaitAuthorization(@Nonnull Sudoer user, @Nonnull String command) {
		// Validate
		Validate.notNull(user, "User cannot be null");
		Validate.notEmpty(command, "Command cannot be empty");
		
		// do nothing if the user is already in authorization process
		if(!passwordAwaiting.containsKey(user)) {
			// add to passwordAwaiting if the user is neither blocked nor authorized
			if(!lockedUsers.contains(user) || !user.isAuthorized()) {
				passwordAwaiting.put(user, new AbstractMap.SimpleEntry<>(command, new AtomicInteger()));
			}
		} else throw new IllegalStateException("User cannot use commands while password insertion");
	}
}
