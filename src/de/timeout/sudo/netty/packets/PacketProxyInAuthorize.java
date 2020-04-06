package de.timeout.sudo.netty.packets;

import java.io.IOException;
import java.util.UUID;

import io.netty.buffer.ByteBuf;

/**
 * Packet which is sent by Spigot-Servers to authorize to their bungeecord
 * @author Timeout
 *
 */
public class PacketProxyInAuthorize extends Packet<PacketProxyInAuthorize> {
	
	private UUID proxyID;

	/**
	 * Constructor for Spigot-Servers
	 * @author Timeout
	 *
	 * @param proxyID the uuid you are going to send
	 */
	public PacketProxyInAuthorize(UUID proxyID) {
		super(PacketProxyInAuthorize.class);
		this.proxyID = proxyID;
	}
	
	/**
	 * Constructor for decoders
	 * @author Timeout
	 *
	 */
	public PacketProxyInAuthorize() {
		super(PacketProxyInAuthorize.class);
	}
	
	@Override
	public void decode(ByteBuf input) throws IOException {
		// read string
		proxyID = UUID.fromString(readString(input));
	}

	@Override
	public void encode(ByteBuf output) throws IOException {
		// call supermethod
		super.encode(output);
		// write uuid into packet
		writeString(output, proxyID.toString());
	}

	/**
	 * Get the UUID from configuration
	 * @author Timeout
	 * 
	 * @return the proxy id which is located in the configuration file
	 */
	public UUID getProxyID() {
		return proxyID;
	}

}
