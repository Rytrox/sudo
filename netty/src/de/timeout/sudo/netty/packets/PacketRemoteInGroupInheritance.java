package de.timeout.sudo.netty.packets;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import de.timeout.sudo.groups.UserGroup;

/**
 * Packet to transfer inheritances of groups
 * @author Timeout
 *
 */
public class PacketRemoteInGroupInheritance extends PacketAbstractGroup {
		
	private static final long serialVersionUID = -7870873454099295181L;
	
	private String inheritance;

	/**
	 * Constructor to create a new Packet. BungeeCord only!
	 * @author Timeout
	 *
	 * @param group the group to convert
	 */
	public PacketRemoteInGroupInheritance(@NotNull UserGroup group, @NotNull String inheritance) {
		super(group);
		
		// Validate
		Validate.notEmpty(inheritance, "Inheritance can neither be null nor empty");
		
		this.inheritance = inheritance;
	}

	@NotNull
	public String getInheritance() {
		return inheritance;
	}
}
