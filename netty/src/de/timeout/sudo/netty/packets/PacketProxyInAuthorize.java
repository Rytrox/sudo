package de.timeout.sudo.netty.packets;

import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.jafama.FastMath;

/**
 * Packet which is sent by Spigot-Servers to authorize to their bungeecord
 * @author Timeout
 *
 */
public class PacketProxyInAuthorize implements Packet {
	
	private static final long serialVersionUID = 5678881027664400322L;
	
	private UUID proxyID;
	private int remotePort;

	/**
	 * Constructor for Spigot-Servers
	 * @author Timeout
	 *
	 * @param proxyID the uuid you are going to send
	 */
	public PacketProxyInAuthorize(@NotNull UUID proxyID, int port) {
		// Validate
		Validate.notNull(proxyID, "Proxy-UUID cannot be null");
		
		this.proxyID = proxyID;
		this.remotePort = FastMath.abs(port);
	}

	/**
	 * Get the UUID from configuration. Is null if the uuid could not be read
	 * @author Timeout
	 * 
	 * @return the proxy id which is located in the configuration file
	 */
	@Nullable
	public UUID getProxyID() {
		return proxyID;
	}

	/**
	 * Get spigot remote port
	 * @author Timeout
	 * 
	 * @return spigot remote port
	 */
	public int getRemotePort() {
		return remotePort;
	}
}
