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

	private String name;
	
	/**
	 * Constructor to create a new Packet. BungeeCord only!
	 * @author Timeout
	 *
	 * @param user the user you want to send
	 */
	public PacketRemoteInLoadUser(@NotNull User user) {
		super(user);
		
		this.name = user.getName();
	}

	/**
	 * Returns the name of the user
	 * @return the name of the user
	 */
	@NotNull
	public String getName() {
		return name;
	}
}
