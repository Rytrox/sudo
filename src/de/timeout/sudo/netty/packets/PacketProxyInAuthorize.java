package de.timeout.sudo.netty.packets;

import java.io.IOException;
import java.util.UUID;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.Validate;

import net.jafama.FastMath;

import io.netty.buffer.ByteBuf;

/**
 * Packet which is sent by Spigot-Servers to authorize to their bungeecord
 * @author Timeout
 *
 */
public class PacketProxyInAuthorize extends Packet<PacketProxyInAuthorize> {
	
	private UUID proxyID;
	private int remotePort;

	/**
	 * Constructor for Spigot-Servers
	 * @author Timeout
	 *
	 * @param proxyID the uuid you are going to send
	 */
	public PacketProxyInAuthorize(@Nonnull UUID proxyID, @Nonnegative int port) {
		super(PacketProxyInAuthorize.class);
		// Validate
		Validate.notNull(proxyID, "Proxy-UUID cannot be null");
		
		this.proxyID = proxyID;
		this.remotePort = FastMath.abs(port);
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
		// read port
		remotePort = input.readInt();
	}

	@Override
	public void encode(ByteBuf output) throws IOException {
		// call supermethod
		super.encode(output);
		// write uuid into packet
		writeString(output, proxyID.toString());
		// write port
		output.writeInt(remotePort);
	}

	/**
	 * Get the UUID from configuration
	 * @author Timeout
	 * 
	 * @return the proxy id which is located in the configuration file
	 */
	@Nonnull
	public UUID getProxyID() {
		return proxyID;
	}

	/**
	 * Get spigot remote port
	 * @author Timeout
	 * 
	 * @return spigot remote port
	 */
	@Nonnegative
	public int getRemotePort() {
		return remotePort;
	}
}
