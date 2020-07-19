package de.timeout.sudo.netty.packets;

import java.io.IOException;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.timeout.sudo.users.User;

import io.netty.buffer.ByteBuf;

/**
 * Packet to send user data 
 * @author Timeout
 *
 */
public class PacketRemoteInLoadUser extends Packet<PacketRemoteInLoadUser> {
	
	private UUID uuid;
	private String prefix;
	private String suffix;
	
	/**
	 * Constructor to create a new Packet. BungeeCord only!
	 * @author Timeout
	 *
	 * @param user the user you want to send
	 */
	public PacketRemoteInLoadUser(@NotNull User user) {
		super(PacketRemoteInLoadUser.class);
		
		Validate.notNull(user, "User cannot be null");
		
		this.uuid = user.getUniqueID();
		this.prefix = user.getPrefix();
		this.suffix = user.getSuffix();
	}
	
	/**
	 * Constructor for Decoders
	 * @author Timeout
	 *
	 */
	public PacketRemoteInLoadUser() {
		super(PacketRemoteInLoadUser.class);
	}

	@Override
	public void decode(ByteBuf input) throws IOException {
		// decode data
		this.uuid = readUUID(input);
		this.prefix = readString(input);
		this.suffix = readString(input);
	}

	@Override
	public void encode(ByteBuf output) throws IOException {
		// do super call
		super.encode(output);
		// write data into packet
		writeString(output, uuid.toString());
		writeString(output, prefix);
		writeString(output, suffix);
	}
	
	/**
	 * Returns the data of the user. Cannot be null
	 * @author Timeout
	 * 
	 * @return the user as Json-Object. Cannot be null
	 */
	@NotNull
	public UUID getUser() {
		return uuid;
	}
	
	/**
	 * Returns the untranslated prefix of the user.
	 * Is null if the prefix does not exist.
	 * @return the prefix or null
	 */
	@Nullable
	public String getPrefix() {
		return prefix;
	}
	
	/**
	 * Returns the untranslated suffix of the user.
	 * Is null if the suffix does not exist. 
	 * @return the suffix or null
	 */
	@Nullable
	public String getSuffix() {
		return suffix;
	}
}
