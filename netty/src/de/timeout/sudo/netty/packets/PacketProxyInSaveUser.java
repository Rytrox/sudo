package de.timeout.sudo.netty.packets;

import de.timeout.sudo.users.User;

import org.jetbrains.annotations.NotNull;

/**
 * Packet which will be sent when a user should be saved
 * @author Timeout
 *
 */
public class PacketProxyInSaveUser extends PacketAbstractUser {

	private static final long serialVersionUID = -2466033299746084252L;

	public PacketProxyInSaveUser(@NotNull User user) {
		super(user);
	}
}
