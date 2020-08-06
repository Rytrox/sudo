package de.timeout.sudo.netty.packets;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import de.timeout.sudo.groups.Group;
import de.timeout.sudo.users.User;

/**
 * Packet which will be sent by BungeeCord when a user joins a group
 * @author Timeout
 *
 */
public class PacketRemoteInUserJoinsGroup extends PacketAbstractUser {
	
	private static final long serialVersionUID = -2497693421169645663L;
	
	private String group;

	/**
	 * Creates a new packet
	 * 
	 * @param user the user who joins a group. Cannot be null
	 * @param group the group which the user joins. Cannot be null
	 * 
	 * @throws IllegalArgumentException if any argument is null
	 */
	public PacketRemoteInUserJoinsGroup(@NotNull User user, @NotNull Group group) {
		super(user);
		
		Validate.notNull(group, "Group cannot be null");
		
		this.group = group.getName();
	}
	/**
	 * Returns the group's name
	 * @return the group's name. Cannot be null
	 */
	@NotNull
	public String getGroup() {
		return group;
	}
}
