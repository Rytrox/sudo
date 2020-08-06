package de.timeout.sudo.bukkit.permissions;

import de.timeout.sudo.bukkit.Sudo;
import de.timeout.sudo.permissions.SudoExecution;
import de.timeout.sudo.users.AuthorizableUser;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Bukkit-Implementation of SudoExecution
 * 
 * @author Timeout
 *
 */
public class BukkitSudoExecution extends SudoExecution<Player> {
	
	private static final Sudo main = Sudo.getInstance();

	public BukkitSudoExecution(AuthorizableUser user, String command) {
		super(user, command, main.getConfig().getInt("sudo.maxAttempts"));
	}

	@Override
	public Player getPlayer() {
		return Bukkit.getPlayer(this.user.getUniqueID());
	}

}
