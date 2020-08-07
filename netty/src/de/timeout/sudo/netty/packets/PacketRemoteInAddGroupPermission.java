package de.timeout.sudo.netty.packets;

import de.timeout.sudo.groups.UserGroup;

/**
 * Packet which will be sent by bungeecord if a permission is added to a group
 * @author Timeout
 *
 */
public class PacketRemoteInAddGroupPermission extends PacketAbstractGroupPermission {

	private static final long serialVersionUID = 1185384161525081599L;

	public PacketRemoteInAddGroupPermission(UserGroup group, String permission) {
		super(group, permission);
	}

	public PacketRemoteInAddGroupPermission() {
		super();
	}
}
