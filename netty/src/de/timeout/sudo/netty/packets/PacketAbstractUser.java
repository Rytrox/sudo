package de.timeout.sudo.netty.packets;

import java.util.UUID;

import de.timeout.sudo.users.User;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

public abstract class PacketAbstractUser implements Packet {

	private static final long serialVersionUID = -469987780570669650L;
	
	protected UUID userID;
	
	public PacketAbstractUser(@NotNull User user) {
		Validate.notNull(user, "User cannot be null");
		
		this.userID = user.getUniqueID();
	}
	
	public PacketAbstractUser() {
		/* EMPTY FOR DECODERS */
	}

	/**
	 * Returns the user's id
	 * @return the user's id. Cannot be null
	 */
	public UUID getUserID() {
		return userID;
	}
}
