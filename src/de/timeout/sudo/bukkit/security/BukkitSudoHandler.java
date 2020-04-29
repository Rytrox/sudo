package de.timeout.sudo.bukkit.security;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.timeout.sudo.bukkit.Sudo;
import de.timeout.sudo.netty.packets.PacketProxyInAuthorizeSudoer;
import de.timeout.sudo.users.Sudoer;
import de.timeout.sudo.users.User;

public class BukkitSudoHandler implements Listener {

	private static final Sudo main = Sudo.getInstance();
	
	private final Map<UUID, String> passwordAwaiting = new HashMap<>(); 
	private final Set<UUID> blocked = new HashSet<>();
	
	@EventHandler
	public void onPasswordInput(AsyncPlayerChatEvent event) {
		// get User
		User user = main.getGroupManager().getUser(event.getPlayer());
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
					// BUKKIT-WAY
				}
			} else {
				// send message to user
				event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&6Sudo&8] &cUnable to authorized. This user is blocked!"));
			}
				
			// cancel event
			event.setCancelled(true);
			event.setMessage("SUDO-REQUIRED-MESSAGE!");
		}
	}
	
	@EventHandler
	public void onDeauthorize(PlayerQuitEvent event) {
		// get User
		User user = main.getGroupManager().getUser(event.getPlayer());
		// if user is a sudoer
		if(user instanceof Sudoer) {
			// deauthorize
			((Sudoer) user).deauthorize();
		}
	}
	
	/**
	 * Finishes the authorization and executes the command
	 * @author Timeout
	 * 
	 * @param player the player who executes the command
	 */
	public void finishAuthorization(Player player) {
		// remove user from awaiting
		player.performCommand(passwordAwaiting.remove(player.getUniqueId()));
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
			passwordAwaiting.put(user.getUniqueID(), command);
			
			return true;
		}
		return false;
	}
}
