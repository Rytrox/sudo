package de.timeout.sudo.netty.packets;

import java.io.IOException;

import io.netty.buffer.ByteBuf;

/**
 * Packet sent by Remote-Server to request data from BungeeCord
 * @author Timeout
 *
 */
public class PacketProxyInLogin extends Packet<PacketProxyInLogin> {

	/**
	 * Constructor to create a new Packet <br>
	 * Works on both ways
	 * @author Timeout
	 *
	 */
	public PacketProxyInLogin() {
		super(PacketProxyInLogin.class);
	}

	@Override
	public void decode(ByteBuf input) throws IOException {
		/* EMPTY. PACKET HAS NO ATTRIBUTES */
	}

}
