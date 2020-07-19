package de.timeout.sudo.bukkit.security;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.timeout.sudo.bukkit.Sudo;
import de.timeout.sudo.bukkit.netty.PasswordAttempt;
import de.timeout.sudo.netty.packets.PacketProxyInAuthorizeSudoer;
import de.timeout.sudo.users.Sudoer;
import de.timeout.sudo.users.User;

public class BukkitSudoHandler implements Listener {

	private static final Sudo main = Sudo.getInstance();
	
	private final Map<UUID, PasswordAttempt> passwordAwaiting = new HashMap<>(); 
	private final Set<UUID> blocked = new HashSet<>();
	
	@EventHandler
	public void onPasswordInput(AsyncPlayerChatEvent event) {
		// get User
		User user = main.getUserManager().getUser(event.getPlayer());
		// get Player
		Player player = event.getPlayer();
		// check if a password is awaiting
		if(passwordAwaiting.containsKey(user.getUniqueID())) {
			// break if user is blocked
			if(!blocked.contains(user.getUniqueID())) {
				// use bukkit way if bungeecord is disabled
				if(main.bungeecordEnabled()) {
					// send packet to client
					PacketProxyInAuthorizeSudoer auth = new PacketProxyInAuthorizeSudoer(user, event.getMessage());
					main.getNetty().sendPacket(auth);
				} else {
					String password = event.getMessage();
					// check for sudoer
					if(user instanceof Sudoer) {
						// get sudoer
						Sudoer sudoer = (Sudoer) user;
						// try to authorize
						if(sudoer.authorize(password)) {
							finishAuthorization(sudoer, false);
						} else wrongPassword(player);
					} else {
						// quit authorize
						passwordAwaiting.remove(user.getUniqueID());
						// send message
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &cUnable to aquire the command. Are you root?"));
					}
				}
			} else {
				// send message to user
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &cUnable to authorized. This user is blocked!"));
			}
				
			// cancel event
			event.setCancelled(true);
			event.setMessage("SUDO-REQUIRED-MESSAGE!");
		}
	}
	
	/**
	 * Method which will be executed when password is entered wrongly
	 * @author Timeout
	 * 
	 * @param attempt
	 * @return
	 */
	public boolean wrongPassword(Player player) {
		// get attempt
		PasswordAttempt attempt = passwordAwaiting.get(player.getUniqueId());
		// increase password
		attempt.addWrongAttempt();
		// return if amount is bigger
		boolean result = attempt.maxReached();
		// send message to player
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', 
				String.format("&8[&6Sudo&8] %s", !result ? "&cWrong password. Try again" : "&cToo many wrong attempts. Blocking user")));
		
		// block user if max is reached
		if(result) blockUser(player.getUniqueId());
		
		// return result
		return result;
	}
	
	/**
	 * Blocks a user internally
	 * @author Timeout
	 * 
	 * @param uuid the uuid of the user
	 */
	public void blockUser(@Nonnull UUID uuid) {
		// validate
		Validate.notNull(uuid, "UUID cannot be null");
		// remove from password awaiting
		passwordAwaiting.remove(uuid);
		// block user
		blocked.add(uuid);
	}
	
	@EventHandler
	public void onDeauthorize(PlayerQuitEvent event) {
		// get User
		User user = main.getUserManager().getUser(event.getPlayer());
		// if user is a sudoer
		if(user instanceof Sudoer) {
			// deauthorize
			((Sudoer) user).deauthorize();
			((Sudoer) user).disableRoot();
		}
	}
	
	/**
	 * Finishes the authorization and executes the command. <br>
	 * After execution it will remove the root access if keepRoot is false
	 * @author Timeout
	 * 
	 * @param player the player who executes the command
	 * @param keepRoot if the root access will be blocked after that command
	 * @throws IllegalArgumentException if the player is null
	 * @return
	 */
	public void finishAuthorization(@Nonnull Sudoer sudoer, boolean keepRoot) {
		// Validate
		Validate.notNull(sudoer, "Sudoer cannot be null");
		// get Player
		Player player = Bukkit.getPlayer(sudoer.getUniqueID());
		// remove user from awaiting
		player.performCommand(passwordAwaiting.remove(player.getUniqueId()).getCommand());
		// remove root access if boolean is false
		if(!keepRoot) sudoer.disableRoot();
	}
	
	/**
	 * Prepares Sudo to wait for a password input
	 * @author Timeout
	 * 
	 * @param user the user who performs the authorization
	 * @param command the command of the user 
	 * @throws IllegalArgumentException if the user is null or the command is empty
	 * @return true if a password will be listening. false if the user is blocked
	 */
	public boolean awaitAuthentification(@Nonnull User user, @Nonnull String command) {
		// Validate
		Validate.notNull(user, "User cannot be null");
		Validate.notEmpty(command, "Command can neither be null nor empty");
		
		// do nothing if player is blocked
		if(blocked.contains(user.getUniqueID())) {
			// put into password awaiting
			passwordAwaiting.put(user.getUniqueID(), new PasswordAttempt(command));
			
			return true;
		}
		return false;
	}
}
