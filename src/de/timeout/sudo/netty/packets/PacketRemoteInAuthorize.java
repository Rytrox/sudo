package de.timeout.sudo.netty.packets;

import java.io.IOException;

import io.netty.buffer.ByteBuf;

/**
 * Packet which is sent by BungeeCord to send the result of Authorization
 * @author Timeout
 *
 */
public class PacketRemoteInAuthorize extends Packet<PacketRemoteInAuthorize> {
	
	private boolean success;

	/**
	 * Constructor for BungeeCord
	 * @author Timeout
	 *
	 * @param success if the authorization succeed
	 */
	public PacketRemoteInAuthorize(boolean success) {
		super(PacketRemoteInAuthorize.class);
		this.success = success;
	}
	
	/**
	 * Constructor for Decoders
	 * @author Timeout
	 *
	 */
	public PacketRemoteInAuthorize() {
		super(PacketRemoteInAuthorize.class);
	}

	@Override
	public void decode(ByteBuf input) throws IOException {
		// check readablility
		if(input.isReadable()) {
			// read success
			this.success = input.readBoolean();
		} else throw new IOException("Packet is unreadable");
	}

	@Override
	public void encode(ByteBuf output) throws IOException {
		// do super call
		super.encode(output);
		
		// write boolean into input
		output.writeBoolean(success);
	}
	
	/**
	 * Checks if the authorization with BungeeCord succeed
	 * @author Timeout
	 * 
	 * @return the authorization with bungeecord
	 */
	public boolean isSucceed() {
		return success;
	}
}
