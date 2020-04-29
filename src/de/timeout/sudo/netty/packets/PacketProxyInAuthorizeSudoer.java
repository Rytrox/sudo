package de.timeout.sudo.netty.packets;

import java.io.IOException;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;

import de.timeout.sudo.groups.User;
import de.timeout.sudo.utils.PasswordCryptor;

import io.netty.buffer.ByteBuf;

public class PacketProxyInAuthorizeSudoer extends Packet<PacketProxyInAuthorizeSudoer> {
			
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
	
	/**
	 * Creates a new Packet which authorizes the Packet
	 * @author Timeout
	 *
	 * @param user the user you want to authorize
	 * @param password the password uncrypted
	 */
	public PacketProxyInAuthorizeSudoer(@Nonnull User user, @Nonnull String password) {
		super(PacketProxyInAuthorizeSudoer.class);
		// Validate
		Validate.notNull(user, "User cannot be null");
		Validate.notEmpty(password, "Password can neither be null nor empty");
		
		this.uuid = user.getUniqueID();
		this.password = PasswordCryptor.encode(password);
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
