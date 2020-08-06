package de.timeout.sudo.netty.packets;

import de.timeout.sudo.users.User;

/**
 * Packet which will be sent when an user gets a permission removed
 * @author Timeout
 *
 */
public class PacketRemoteInRemoveUserPermission extends PacketAbstractUserPermission {

	private static final long serialVersionUID = -5931780388231467565L;

	/**
	 * Constructor for decoders
	 */
	public PacketRemoteInRemoveUserPermission() {
		super();
	}

	/**
	 * Creates a new Packet 
	 * @param user the user who gets the permission removed
	 * @param permission the permission which the user loses
	 */
	public PacketRemoteInRemoveUserPermission(User user, String permission) {
		super(user, permission);
	}
}
