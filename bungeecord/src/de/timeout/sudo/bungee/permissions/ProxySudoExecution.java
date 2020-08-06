package de.timeout.sudo.bungee.permissions;

import de.timeout.sudo.bungee.Sudo;
import de.timeout.sudo.bungee.users.ProxyUser;
import de.timeout.sudo.permissions.SudoExecution;

import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Proxy implementation of Sudo-Execution
 * @author Timeout
 *
 */
public class ProxySudoExecution extends SudoExecution<ProxiedPlayer> {
	
	private static final Sudo main = Sudo.getInstance();

	public ProxySudoExecution(ProxyUser user, String command) {
		super(user, command, main.getConfig().getInt("sudo.maxAttempts"));
	}

	@Override
	public ProxiedPlayer getPlayer() {
		return main.getProxy().getPlayer(((ProxyUser) this.user).getUniqueID());
	}

}
