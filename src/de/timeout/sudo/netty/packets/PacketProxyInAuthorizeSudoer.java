package de.timeout.sudo.netty.packets;

import java.io.IOException;
import java.util.UUID;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;
import io.netty.util.internal.ThreadLocalRandom;

public class PacketProxyInAuthorizeSudoer extends Packet<PacketProxyInAuthorizeSudoer> {
	
	private static final ThreadLocalRandom random = ThreadLocalRandom.current();
		
	private UUID uuid;
	private String password;
	
	/**
	 * Constructor for decoders
	 * @author Timeout
	 *
	 */
	public PacketProxyInAuthorizeSudoer() {
		super(PacketProxyInAuthorizeSudoer.class);
	}

	@Override
	public void decode(ByteBuf input) throws IOException {
		this.password = readString(input);
	}

	@Override
	public void encode(ByteBuf output) throws IOException {
		// Do super call!
		super.encode(output);
		// encode sudoer uuid
		writeString(output, uuid.toString());
		// encode password
		writeString(output, password);
	}
	
	/**
	 * Returns the sudoers uuid. Cannot be null
	 * @author Timeout
	 * 
	 * @return the sudoers uuid. Cannot be null
	 */
	@Nonnull
	public UUID getUniqueID() {
		return uuid;
	}

	/**
	 * Returns the encoded password. Cannot be null
	 * @author Timeout
	 * 
	 * @return the encoded password. Cannot be null
	 */
	@Nonnull
	public String getEncodedPassword() {
		return password;
	}
}
