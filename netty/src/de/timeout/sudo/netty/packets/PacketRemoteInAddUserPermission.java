package de.timeout.sudo.netty.packets;

import de.timeout.sudo.users.User;

/**
 * Packet which will be sent when an user gets a new permission
 * @author Timeout
 *
 */
public class PacketRemoteInAddUserPermission extends PacketAbstractUserPermission {

	private static final long serialVersionUID = 6083497507139147597L;

	/**
	 * Decoder's constructor
	 */
	public PacketRemoteInAddUserPermission() {
		super();
	}

	/**
	 * Creates a new Packet
	 * @param user the user who get's a new permission
	 * @param permission the permission which will be added to the user
	 */
	public PacketRemoteInAddUserPermission(User user, String permission) {
		super(user, permission);
	}
}
