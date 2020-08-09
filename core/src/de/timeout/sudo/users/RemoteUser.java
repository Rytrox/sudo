package de.timeout.sudo.users;

import de.timeout.sudo.security.Authorizable;

import org.jetbrains.annotations.NotNull;

/**
 * Interface for console-user of bukkit
 * @author Timeout
 *
 */
public interface RemoteUser extends User, Authorizable {

	/**
	 * Returns the server ip of the Bukkit-Server
	 * @return the server-ip of the Bukkit-Server. Cannot be null
	 */
	@NotNull
	public String getServerIP();
	
	/**
	 * Returns the server-port of the Bukkit-Server
	 * @return the server-port of the Bukkit-Server. Cannot be null
	 */
	public int getServerPort();
}