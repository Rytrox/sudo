package de.timeout.sudo.netty.packets;

import de.timeout.sudo.groups.Group;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

public abstract class PacketAbstractGroup implements Packet {
	
	private static final long serialVersionUID = 856405612341691584L;
	
	protected String groupname;
	
	public PacketAbstractGroup(@NotNull Group group) {
		// Validate
		Validate.notNull(group, "Group cannot be null");
		this.groupname = group.getName();
	}
	
	public PacketAbstractGroup() {
		/* EMPTY FOR DECODERS */
	}
	
	/**
	 * Returns the group's name. Cannot be null
	 * @return the the group's name. Cannot be null
	 */
	@NotNull
	public String getGroupName() {
		return groupname;
	}	
	
}
