package de.timeout.sudo.netty.packets;

import org.jetbrains.annotations.NotNull;

import de.timeout.sudo.users.User;


/**
 * Packet to send user data 
 * @author Timeout
 *
 */
public class PacketRemoteInLoadUser extends PacketAbstractUser {
		
	private static final long serialVersionUID = 7556749491098352422L;

	/**
	 * Constructor to create a new Packet. BungeeCord only!
	 * @author Timeout
	 *
	 * @param user the user you want to send
	 */
	public PacketRemoteInLoadUser(@NotNull User user) {
		super(user);
	}
	
	/**
	 * Constructor for Decoders
	 * @author Timeout
	 *
	 */
	public PacketRemoteInLoadUser() {
		super();
	}
}
