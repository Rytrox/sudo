package de.timeout.sudo.netty.packets;

import de.timeout.sudo.groups.UserGroup;

/**
 * Packet which will be sent by BungeeCord when a permission is removed from a group
 * @author Timeout
 *
 */
public class PacketRemoteInRemoveGroupPermission extends PacketAbstractGroupPermission {

	private static final long serialVersionUID = 7486643919033852110L;

	public PacketRemoteInRemoveGroupPermission(UserGroup group, String permission) {
		super(group, permission);
	}
	
	/**
	 * Constructor for decoders
	 */
	public PacketRemoteInRemoveGroupPermission() {
		super();
	}
}
