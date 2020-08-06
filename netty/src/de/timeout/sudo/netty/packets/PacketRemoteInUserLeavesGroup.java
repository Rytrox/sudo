package de.timeout.sudo.netty.packets;

import de.timeout.sudo.groups.Group;
import de.timeout.sudo.users.User;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

public class PacketRemoteInUserLeavesGroup extends PacketAbstractUser {

	private static final long serialVersionUID = -800726772517978514L;
	
	private String group;
	
	public PacketRemoteInUserLeavesGroup(@NotNull User user, @NotNull Group group) {
		super(user);
		
		Validate.notNull(group, "Group cannot be null");
		this.group = group.getName();
	}

	/**
	 * Returns the group's name which the player leaves
	 * @return the group's name. Cannot be null
	 */
	@NotNull
	public String getGroupName() {
		return group;
	}
}
