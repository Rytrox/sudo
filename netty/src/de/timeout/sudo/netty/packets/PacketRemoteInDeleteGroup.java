package de.timeout.sudo.netty.packets;

import de.timeout.sudo.groups.Group;

import org.jetbrains.annotations.NotNull;

/**
 * Packet which will be sent if a certain group is deleted by the proxy
 * @author Timeout
 *
 */
public class PacketRemoteInDeleteGroup extends PacketAbstractGroup {

	private static final long serialVersionUID = -5204106933341599752L;

	/**
	 * Creates a new Packet. 
	 * @author Timeout
	 *
	 * @param groupname the name of the group. Can neither be null nor empty
	 * @throws IllegalArgumentException if the groupname is null, empty or is sudo.
	 */
	public PacketRemoteInDeleteGroup(@NotNull Group group) {
		super(group);
	}
}
