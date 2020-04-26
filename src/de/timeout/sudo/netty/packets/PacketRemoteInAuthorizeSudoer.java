package de.timeout.sudo.netty.packets;

import java.io.IOException;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.Validate;

import io.netty.buffer.ByteBuf;

public class PacketRemoteInAuthorizeSudoer extends Packet<PacketRemoteInAuthorizeSudoer> {

	private UUID sudoer;
	private AuthorizationResult authorized;
	
	/**
	 * Constructor for decoders
	 * @author Timeout
	 *
	 */
	public PacketRemoteInAuthorizeSudoer() {
		super(PacketRemoteInAuthorizeSudoer.class);
	}
	
	public PacketRemoteInAuthorizeSudoer(@Nonnull UUID uuid, @Nonnull AuthorizationResult result) {
		super(PacketRemoteInAuthorizeSudoer.class);
		
		// Validate
		Validate.notNull(uuid, "UUID cannot be null");
		Validate.notNull(result, "Result cannot be null");
		
		this.sudoer = uuid;
		this.authorized = result;
	}

	@Override
	public void decode(ByteBuf input) throws IOException {
		// read sudoer
		sudoer = UUID.fromString(readString(input));
		// read success
		authorized = AuthorizationResult.getResultByID(input.readInt());
	}

	@Override
	public void encode(ByteBuf output) throws IOException {
		// Do super call!
		super.encode(output);
		// encode user
		writeString(output, sudoer.toString());
		// encode result
		output.writeInt(authorized.getID());
	}
	
	/**
	 * Returns if the sudoer could be authorized
	 * @author Timeout
	 * 
	 * @return true if the user could be authorized. false otherwise
	 */
	@Nonnull
	public AuthorizationResult getAuthResult() {
		return authorized; 
	}
	
	/**
	 * Returns the uuid of the sudoer. Cannot be null
	 * @author Timeout
	 * 
	 * @return the uuid of the sudoer. Cannot be null
	 */
	@Nonnull
	public UUID getUniqueID() {
		return sudoer;
	}
	
	/**
	 * enum which considers three states of results
	 * @author Timeout
	 *
	 */
	public enum AuthorizationResult {
		SUCCESS(0), PASSWORD_FAILED(1), NO_SUDOER(2);
		
		private int id;
		
		private AuthorizationResult(int id) {
			this.id = id;
		}
		
		/**
		 * Returns the state by its id
		 * @author Timeout
		 * 
		 * @param id the id of the state
		 * @return the result or null if the id is invalid
		 */
		@Nullable
		public static AuthorizationResult getResultByID(int id) {
			return id >= 0 && id < 3 ? values()[id] : null;
		}
		
		/**
		 * Returns the id of the state
		 * @author Timeout
		 * 
		 * @return the id of the state
		 */
		public int getID() {
			return id;
		}
	}
}
