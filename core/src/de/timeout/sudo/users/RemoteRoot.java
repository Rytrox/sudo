package de.timeout.sudo.users;

import org.jetbrains.annotations.NotNull;

/**
 * Interface for Bukkit-Servers which has Root-Access to BungeeCord
 * @author Timeout
 *
 */
public interface RemoteRoot extends Root {

	/**
	 * returns the ip of the bukkit-server
	 * @return the ip of the bukkit-server
	 */
	@NotNull
	public String getServerIP();
	
	/**
	 * returns the port of the bukkit-server
	 * @return the port of the bukkit-server
	 */
	public int getPort();
}
