package de.timeout.sudo.netty.packets;

import org.jetbrains.annotations.NotNull;

import de.timeout.sudo.groups.UserGroup;

/**
 * Packet to initialize a group without their inheritances
 * @author Timeout
 *
 */
public class PacketRemoteInAddGroup extends PacketAbstractGroup {

	private static final long serialVersionUID = 3244696736212833713L;
	
	private boolean isDefault;
	
	/**
	 * Constructor to create a new Packet from Serverside (BungeeCord)
	 * @author Timeout
	 *
	 * @param group the group you want to compile. Cannot be null
	 */
	public PacketRemoteInAddGroup(@NotNull UserGroup group) {
		super(group);
		
		// Validate
		this.isDefault = group.isDefault();
	}
	
	public boolean isDefault() {
		return isDefault;
	}
}
