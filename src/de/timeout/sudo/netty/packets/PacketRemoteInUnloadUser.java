package de.timeout.sudo.netty.packets;

import java.io.IOException;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;

import io.netty.buffer.ByteBuf;

/**
 * Packet which will be sent if a user leaves the proxy
 * @author Timeout
 *
 */
public class PacketRemoteInUnloadUser extends Packet<PacketRemoteInUnloadUser> {
	
	private UUID uuid;
	
	/**
	 * Constructor for decoders
	 * @author Timeout
	 *
	 */
	public PacketRemoteInUnloadUser() {
		super(PacketRemoteInUnloadUser.class);
	}
	
	/**
	 * Constructor for proxy to creat a new packet
	 * @author Timeout
	 *
	 * @param uuid the uuid of the user. Cannot be null
	 * @throws IllegalArgumentException if the uuid is null
	 */
	public PacketRemoteInUnloadUser(@Nonnull UUID uuid) {
		super(PacketRemoteInUnloadUser.class);
		// Validate
		Validate.notNull(uuid, "UUID cannot be null");
		this.uuid = uuid;
	}

	@Override
	public void decode(ByteBuf input) throws IOException {
		// load uuid
		uuid = UUID.fromString(readString(input));
	}

	@Override
	public void encode(ByteBuf output) throws IOException {
		// do super call
		super.encode(output);
		// write uuid
		writeString(output, uuid.toString());
	}
	
	/**
	 * Returns the uuid of the player. <br>
	 * Cannot be null
	 * @author Timeout
	 * 
	 * @return the uuid of the player
	 */
	@Nonnull
	public UUID getUniqueID() {
		return uuid;
	}

}
